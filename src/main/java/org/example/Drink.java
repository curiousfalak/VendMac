package org.example;



/**
 * DAY 1 - STEP 2a: Drink subclass
 * Examples: Coca-Cola, Pepsi, Water
 */
public class Drink extends Product {

    private boolean isCarbonated; // Extra field specific to drinks

    public Drink(String id, String name, double price, int quantity, boolean isCarbonated) {
        super(id, name, price, quantity); // Call parent constructor
        this.isCarbonated = isCarbonated;
    }

    @Override
    public String getCategory() {
        return "DRINK";
    }

    public boolean isCarbonated() { return isCarbonated; }
}
