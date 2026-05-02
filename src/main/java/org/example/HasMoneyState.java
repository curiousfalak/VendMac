package org.example;



/**
 * DAY 2 - STEP 3: HasMoneyState
 *
 * Coins have been inserted. Awaiting product selection.
 *
 * ALLOWED:  insertCoin()     → add more coins
 * ALLOWED:  selectProduct()  → check stock + funds → DispensingState
 * ALLOWED:  cancel()         → return all coins → IdleState
 *
 * Day 5 adds: 30-second timeout → auto-cancel → IdleState
 */
public class HasMoneyState implements VendingState {

    private VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertCoin(int rupees) {
        boolean accepted = machine.getCoinProcessor().insertCoin(rupees);
        if (accepted) {
            System.out.println("More coins: ₹" + rupees
                    + " | Total: ₹" + machine.getCoinProcessor().getInsertedAmount());
            machine.notifyAmountChanged();
        } else {
            System.out.println("Invalid coin ₹" + rupees + " rejected.");
        }
    }

    @Override
    public void selectProduct(String productId) {
        // 1. Check if product exists
        Product product = machine.getInventory().getProduct(productId);
        if (product == null) {
            System.out.println("Invalid product ID: " + productId);
            machine.notifyMessage("Invalid selection.");
            return;
        }

        // 2. Check if in stock
        if (!product.isInStock()) {
            System.out.println(product.getName() + " is OUT OF STOCK.");
            machine.notifyMessage(product.getName() + " is out of stock!");
            return;
        }

        // 3. Check if user has enough money
        if (!machine.getCoinProcessor().canAfford(product.getPrice())) {
            double needed = product.getPrice() - machine.getCoinProcessor().getInsertedAmount();
            System.out.printf("Need ₹%.0f more for %s%n", needed, product.getName());
            machine.notifyMessage(String.format("Need ₹%.0f more.", needed));
            return;
        }

        // 4. All checks passed — set the selected product and move to DispensingState
        machine.setSelectedProduct(product);
        machine.setState(machine.getDispensingState());

        // Trigger dispensing
        machine.getDispensingState().dispense();
    }

    @Override
    public void cancel() {
        double returned = machine.getCoinProcessor().getInsertedAmount();
        machine.getCoinProcessor().reset();
        machine.getTransactionLog().logCancel(returned);

        System.out.printf("Cancelled. Returning ₹%.0f%n", returned);
        machine.notifyCoinsReturned(returned);

        // Go back to idle
        machine.setState(machine.getIdleState());
    }

    @Override
    public String getStateName() {
        return "HAS_MONEY";
    }
}