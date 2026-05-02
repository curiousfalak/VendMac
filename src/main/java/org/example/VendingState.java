package org.example;



/**
 * DAY 2 - STEP 1: VendingState Interface
 *
 * This is the heart of the State Design Pattern.
 *
 * WHAT IS STATE PATTERN?
 * The machine behaves differently based on its current "state":
 *   - IDLE: Waiting. Only action = insert coin.
 *   - HAS_MONEY: Coin inserted. Can select product or cancel.
 *   - DISPENSING: Product selected. Dispensing in progress.
 *
 * Instead of giant if-else blocks like:
 *   if(state == IDLE) { ... }
 *   else if(state == HAS_MONEY) { ... }
 *
 * Each state is its OWN CLASS that implements this interface.
 * The VendingMachine just calls state.insertCoin() and the
 * correct behavior runs automatically.
 *
 * This is the Open/Closed Principle: adding a new state
 * = add a new class, don't modify existing code.
 */
public interface VendingState {

    /**
     * User inserts a coin (₹1, ₹2, ₹5, ₹10)
     */
    void insertCoin(int rupees);

    /**
     * User selects a product by its ID (e.g., "A1")
     */
    void selectProduct(String productId);

    /**
     * User presses the Cancel / Return Coins button
     */
    void cancel();

    /**
     * Returns the name of this state (for UI display)
     */
    String getStateName();
}
