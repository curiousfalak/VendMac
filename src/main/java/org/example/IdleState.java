package org.example;

public class IdleState implements VendingState {

    private VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int rupees) {
        boolean accepted = machine.getCoinProcessor().insertCoin(rupees);
        if (accepted) {
            System.out.printf("[%s] Coin ₹%d accepted | Total ₹%.0f%n",
                    Thread.currentThread().getName(), rupees,
                    machine.getCoinProcessor().getInsertedAmount());
            machine.setState(machine.getHasMoneyState());
            // Start the 30s session timer daemon thread
            if (machine.getSessionTimer() != null) machine.getSessionTimer().start();
            machine.notifyAmountChanged();
        } else {
            System.out.println("Invalid coin ₹" + rupees + " — returned.");
            machine.notifyMessage("Invalid coin ₹" + rupees + " — returned.");
        }
    }

    @Override
    public void selectProduct(String productId) {
        System.out.println("Please insert coins first!");
        machine.notifyMessage("Insert coins first!");
    }

    @Override
    public void cancel() {
        System.out.println("No transaction to cancel.");
        machine.notifyMessage("No active transaction.");
    }

    @Override
    public String getStateName() { return "IDLE"; }
}