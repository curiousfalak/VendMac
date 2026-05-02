package org.example;



/**
 * DAY 2 - STEP 2: IdleState
 *
 * Machine is waiting. Screen shows "Insert Coin".
 *
 * ALLOWED:  insertCoin() → moves to HasMoneyState
 * REJECTED: selectProduct() → "Please insert coins first"
 * REJECTED: cancel() → nothing to cancel
 */
public class IdleState implements VendingState {

    // Reference to the machine context — needed to change states
    private VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int rupees) {
        boolean accepted = machine.getCoinProcessor().insertCoin(rupees);
        if (accepted) {
            System.out.println("Coin accepted: ₹" + rupees
                    + " | Total: ₹" + machine.getCoinProcessor().getInsertedAmount());
            // Transition to HasMoneyState
            machine.setState(machine.getHasMoneyState());
        } else {
            System.out.println("Invalid coin: ₹" + rupees + " — returned.");
        }
    }

    @Override
    public void selectProduct(String productId) {
        // Not allowed in idle state
        System.out.println("Please insert coins first!");
        machine.notifyMessage("Please insert coins first!");
    }

    @Override
    public void cancel() {
        // Nothing to cancel
        System.out.println("No transaction to cancel.");
        machine.notifyMessage("No active transaction.");
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
}
