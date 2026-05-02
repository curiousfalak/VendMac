package org.example;


import java.util.List;


/**
 * DAY 2 - STEP 5: VendingMachine (Context Class)
 *
 * This is the CONTEXT in the State Pattern.
 * It holds a reference to the current state and delegates
 * all actions to it.
 *
 * It also:
 *  - Holds shared resources: Inventory, CoinProcessor, TransactionLog
 *  - Pre-creates all 3 state objects (once, reused)
 *  - Provides observer callbacks so JavaFX UI can update
 *
 * HOW STATE PATTERN WORKS HERE:
 *   machine.insertCoin(5)  →  currentState.insertCoin(5)
 *   The machine doesn't know what will happen — the STATE does.
 */
public class VendingMachine {

    // ─── Shared Resources ────────────────────────────────────────────────
    private Inventory       inventory;
    private CoinProcessor   coinProcessor;
    private TransactionLog  transactionLog;

    // ─── State objects (created ONCE, reused — no GC pressure) ───────────
    private IdleState       idleState;
    private HasMoneyState   hasMoneyState;
    private DispensiongState dispensingState;

    // ─── Current active state ─────────────────────────────────────────────
    private VendingState currentState;

    // ─── Currently selected product (set in HasMoneyState) ───────────────
    private Product selectedProduct;

    // ─── Observer / Callbacks for JavaFX UI ──────────────────────────────
    // We use simple functional interfaces so JavaFX screens can hook in.
    private Runnable          onStateChanged;
    private Runnable          onAmountChanged;
    private MessageListener   onMessage;
    private DispensingListener onDispensing;
    private CoinReturnListener onCoinsReturned;

    @FunctionalInterface
    public interface MessageListener {
        void onMessage(String message);
    }

    @FunctionalInterface
    public interface DispensingListener {
        void onDispensing(Product product, double change, List<Integer> changeCoins);
    }

    @FunctionalInterface
    public interface CoinReturnListener {
        void onCoinsReturned(double amount);
    }

    // ─── Constructor ──────────────────────────────────────────────────────
    public VendingMachine() {
        // Initialize resources
        inventory      = new Inventory();
        coinProcessor  = new CoinProcessor();
        transactionLog = new TransactionLog();

        // Seed with default products
        inventory.loadDefaultProducts();

        // Create state objects — pass 'this' so states can call machine methods
        idleState       = new IdleState(this);
        hasMoneyState   = new HasMoneyState(this);
        dispensingState = new DispensiongState(this);

        // Start in IDLE state
        currentState = idleState;

        System.out.println("Vending Machine ready. State: " + currentState.getStateName());
    }

    // ─── Delegating actions to current state ─────────────────────────────

    public void insertCoin(int rupees) {
        currentState.insertCoin(rupees);
    }

    public void selectProduct(String productId) {
        currentState.selectProduct(productId);
    }

    public void cancel() {
        currentState.cancel();
    }

    // ─── State management ────────────────────────────────────────────────

    public void setState(VendingState newState) {
        System.out.println("State: " + currentState.getStateName()
                + " → " + newState.getStateName());
        currentState = newState;
        if (onStateChanged != null) onStateChanged.run();
    }

    public VendingState getCurrentState()    { return currentState; }
    public IdleState    getIdleState()        { return idleState; }
    public HasMoneyState getHasMoneyState()  { return hasMoneyState; }
    public DispensiongState getDispensingState() { return dispensingState; }

    // ─── Getters for shared resources ────────────────────────────────────

    public Inventory       getInventory()      { return inventory; }
    public CoinProcessor   getCoinProcessor()  { return coinProcessor; }
    public TransactionLog  getTransactionLog() { return transactionLog; }

    // ─── Selected product ─────────────────────────────────────────────────

    public void    setSelectedProduct(Product p) { selectedProduct = p; }
    public Product getSelectedProduct()          { return selectedProduct; }

    // ─── Notify UI callbacks ─────────────────────────────────────────────

    public void notifyStateChanged()    { if (onStateChanged != null)  onStateChanged.run(); }
    public void notifyAmountChanged()   { if (onAmountChanged != null) onAmountChanged.run(); }

    public void notifyMessage(String msg) {
        if (onMessage != null) onMessage.onMessage(msg);
    }

    public void notifyDispensing(Product p, double change, List<Integer> coins) {
        if (onDispensing != null) onDispensing.onDispensing(p, change, coins);
    }

    public void notifyCoinsReturned(double amount) {
        if (onCoinsReturned != null) onCoinsReturned.onCoinsReturned(amount);
    }

    // ─── Register UI callbacks ────────────────────────────────────────────

    public void setOnStateChanged(Runnable r)           { onStateChanged = r; }
    public void setOnAmountChanged(Runnable r)          { onAmountChanged = r; }
    public void setOnMessage(MessageListener l)         { onMessage = l; }
    public void setOnDispensing(DispensingListener l)   { onDispensing = l; }
    public void setOnCoinsReturned(CoinReturnListener l){ onCoinsReturned = l; }
}
