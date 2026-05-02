package org.example;


import java.util.List;

/**
 * DAY 2 - STEP 4: DispensingState
 *
 * Product is being dispensed. Machine is BUSY.
 *
 * REJECTED: insertCoin()    → "Please wait"
 * REJECTED: selectProduct() → "Already dispensing"
 * REJECTED: cancel()        → "Too late!"
 *
 * dispense() is called internally (not from the interface)
 * to perform the actual dispensing logic.
 */
public class DispensingState implements VendingState {

    private VendingMachine machine;

    public DispensingState(VendingMachine machine) {
        this.machine = machine;
    }

    /**
     * Main dispensing logic — called right after transitioning into this state.
     * This is where money is deducted, change is calculated, inventory decremented.
     */
    public void dispense() {
        Product product = machine.getSelectedProduct();

        // Calculate change
        double change = machine.getCoinProcessor().calculateChange(product.getPrice());
        List<Integer> changeCoins = machine.getCoinProcessor().getChangeCoins(change);

        // Deduct from inventory
        machine.getInventory().dispense(product.getId());

        // Log the transaction
        machine.getTransactionLog().logSale(
                product.getId(),
                product.getName(),
                machine.getCoinProcessor().getInsertedAmount(),
                change
        );

        // Reset coin processor
        machine.getCoinProcessor().reset();

        System.out.println("Dispensing: " + product.getName());
        System.out.printf("Change: ₹%.0f in coins: %s%n", change, changeCoins);

        // Notify the UI (JavaFX screen)
        machine.notifyDispensing(product, change, changeCoins);

        // Return to idle after dispensing
        machine.setState(machine.getIdleState());
    }

    @Override
    public void insertCoin(int rupees) {
        System.out.println("Please wait, dispensing in progress...");
        machine.notifyMessage("Please wait...");
    }

    @Override
    public void selectProduct(String productId) {
        System.out.println("Already dispensing. Please wait.");
        machine.notifyMessage("Already dispensing!");
    }

    @Override
    public void cancel() {
        System.out.println("Cannot cancel — dispensing in progress.");
        machine.notifyMessage("Cannot cancel now!");
    }

    @Override
    public String getStateName() {
        return "DISPENSING";
    }
}
