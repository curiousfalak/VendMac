package org.example;


/**
 * DAY 1 - STEP 2c: HotBeverage subclass
 * Examples: Tea, Coffee, Hot Chocolate
 */
public class HotBeverage extends Product {

    private int brewTimeSeconds; // How long to brew in seconds

    public HotBeverage(String id, String name, double price, int quantity, int brewTimeSeconds) {
        super(id, name, price, quantity);
        this.brewTimeSeconds = brewTimeSeconds;
    }

    @Override
    public String getCategory() {
        return "HOT";
    }

    public int getBrewTimeSeconds() { return brewTimeSeconds; }
}
