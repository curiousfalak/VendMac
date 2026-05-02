package org.example;

public class HasMoneyState implements VendingState {

    private VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int rupees) {
        boolean accepted = machine.getCoinProcessor().insertCoin(rupees);
        if (accepted) {
            System.out.printf("[%s] Coin ₹%d accepted | Total ₹%.0f%n",
                    Thread.currentThread().getName(), rupees,
                    machine.getCoinProcessor().getInsertedAmount());
            // Reset the 30s timeout on each new coin
            if (machine.getSessionTimer() != null) machine.getSessionTimer().reset();
            machine.notifyAmountChanged();
        } else {
            System.out.println("Invalid coin ₹" + rupees + " rejected.");
            machine.notifyMessage("Invalid coin ₹" + rupees + " — returned.");
        }
    }

    @Override
    public void selectProduct(String productId) {
        Product product = machine.getInventory().getProduct(productId);
        if (product == null) {
            machine.notifyMessage("Invalid selection."); return;
        }
        if (!product.isInStock()) {
            machine.notifyMessage(product.getName() + " is out of stock!"); return;
        }
        if (!machine.getCoinProcessor().canAfford(product.getPrice())) {
            double needed = product.getPrice() - machine.getCoinProcessor().getInsertedAmount();
            machine.notifyMessage(String.format("Need ₹%.0f more.", needed)); return;
        }
        // Stop timer before dispensing
        if (machine.getSessionTimer() != null) machine.getSessionTimer().stop();
        machine.setSelectedProduct(product);
        machine.setState(machine.getDispensingState());
        machine.getDispensingState().dispense();
    }

    @Override
    public void cancel() {
        if (machine.getSessionTimer() != null) machine.getSessionTimer().stop();
        double returned = machine.getCoinProcessor().getInsertedAmount();
        machine.getCoinProcessor().reset();
        machine.getTransactionLog().logCancel(returned);
        System.out.printf("Cancelled. Returning ₹%.0f%n", returned);
        machine.notifyCoinsReturned(returned);
        machine.setState(machine.getIdleState());
    }

    @Override
    public String getStateName() { return "HAS_MONEY"; }
}