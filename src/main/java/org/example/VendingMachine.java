package org.example;

import java.util.List;

public class VendingMachine {

    private Inventory        inventory;
    private CoinProcessor    coinProcessor;
    private TransactionLog   transactionLog;
    private SessionTimer     sessionTimer;   // NEW: stored here so states can access it

    private IdleState        idleState;
    private HasMoneyState    hasMoneyState;
    private DispensiongState dispensingState;
    private VendingState     currentState;
    private Product          selectedProduct;

    private Runnable           onStateChanged;
    private Runnable           onAmountChanged;
    private MessageListener    onMessage;
    private DispensingListener onDispensing;
    private CoinReturnListener onCoinsReturned;

    @FunctionalInterface public interface MessageListener    { void onMessage(String m); }
    @FunctionalInterface public interface DispensingListener { void onDispensing(Product p, double change, List<Integer> coins); }
    @FunctionalInterface public interface CoinReturnListener { void onCoinsReturned(double amt); }

    public VendingMachine() {
        inventory      = new Inventory();
        coinProcessor  = new CoinProcessor();
        transactionLog = new TransactionLog();
        inventory.loadDefaultProducts();
        idleState       = new IdleState(this);
        hasMoneyState   = new HasMoneyState(this);
        dispensingState = new DispensiongState(this);
        currentState    = idleState;
    }

    // ── Delegate to current state ────────────────────────────────────────
    public void insertCoin(int r)       { currentState.insertCoin(r); }
    public void selectProduct(String id){ currentState.selectProduct(id); }
    public void cancel()                { currentState.cancel(); }

    // ── State management ─────────────────────────────────────────────────
    public void setState(VendingState s) {
        System.out.printf("[Thread:%s] State: %s → %s%n",
                Thread.currentThread().getName(),
                currentState.getStateName(), s.getStateName());
        currentState = s;
        if (onStateChanged != null) onStateChanged.run();
    }

    public VendingState    getCurrentState()    { return currentState; }
    public IdleState       getIdleState()       { return idleState; }
    public HasMoneyState   getHasMoneyState()   { return hasMoneyState; }
    public DispensiongState getDispensingState(){ return dispensingState; }

    // ── Resources ────────────────────────────────────────────────────────
    public Inventory      getInventory()      { return inventory; }
    public CoinProcessor  getCoinProcessor()  { return coinProcessor; }
    public TransactionLog getTransactionLog() { return transactionLog; }
    public SessionTimer   getSessionTimer()   { return sessionTimer; }
    public void           setSessionTimer(SessionTimer t){ sessionTimer = t; }

    // ── Selected product ─────────────────────────────────────────────────
    public void    setSelectedProduct(Product p){ selectedProduct = p; }
    public Product getSelectedProduct()         { return selectedProduct; }

    // ── Notify UI ────────────────────────────────────────────────────────
    public void notifyStateChanged()  { if (onStateChanged  != null) onStateChanged.run(); }
    public void notifyAmountChanged() { if (onAmountChanged != null) onAmountChanged.run(); }
    public void notifyMessage(String m)            { if (onMessage      != null) onMessage.onMessage(m); }
    public void notifyDispensing(Product p, double c, List<Integer> coins) {
        if (onDispensing != null) onDispensing.onDispensing(p, c, coins);
    }
    public void notifyCoinsReturned(double a)      { if (onCoinsReturned != null) onCoinsReturned.onCoinsReturned(a); }

    public void setOnStateChanged(Runnable r)        { onStateChanged  = r; }
    public void setOnAmountChanged(Runnable r)       { onAmountChanged = r; }
    public void setOnMessage(MessageListener l)      { onMessage       = l; }
    public void setOnDispensing(DispensingListener l){ onDispensing     = l; }
    public void setOnCoinsReturned(CoinReturnListener l){ onCoinsReturned = l; }
}