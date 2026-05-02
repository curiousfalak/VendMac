package org.example;


import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DAY 6 — JUnit 5 Tests
 *
 * Tests cover all the critical behaviours the interviewer will ask about:
 *
 *  1. testInsufficientFundsRejected
 *  2. testExactChangeDispenses
 *  3. testChangeCalculationCorrect
 *  4. testOutOfStockThrowsException
 *  5. testStateTransitionSequence
 *  6. testSessionTimeoutResetsState  (conceptual — tested via direct method call)
 *
 * HOW TO RUN:
 *   Maven:  mvn test
 *   Gradle: ./gradlew test
 *   IntelliJ: Right-click the test class → Run
 *
 * JUNIT 5 ANNOTATIONS:
 *   @Test           - marks a test method
 *   @BeforeEach     - runs before EVERY test (setup/reset)
 *   @DisplayName    - human-readable test name in reports
 *   assertEquals()  - checks two values are equal
 *   assertThrows()  - checks that an exception IS thrown
 *   assertTrue()    - checks a condition is true
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VendingMachineTest {

    private VendingMachine machine;

    /**
     * Fresh machine before each test.
     * This ensures tests are INDEPENDENT — one failure won't affect another.
     */
    @BeforeEach
    void setUp() {
        machine = new VendingMachine();
        // Note: loadDefaultProducts() is called inside VendingMachine constructor via Inventory
    }

    // ─── Test 1: Insufficient Funds Rejected ──────────────────────────────

    @Test
    @Order(1)
    @DisplayName("Should reject product selection when funds insufficient")
    void testInsufficientFundsRejected() {
        // Insert ₹10, try to buy Coca-Cola (₹25)
        machine.insertCoin(10);

        // Machine should be in HasMoneyState now
        assertTrue(machine.getCurrentState() instanceof HasMoneyState,
                "After inserting coin, should be in HasMoneyState");

        // Try to select Coca-Cola (A1 = ₹25, we only have ₹10)
        machine.selectProduct("A1");

        // Machine should STILL be in HasMoneyState (purchase failed)
        assertTrue(machine.getCurrentState() instanceof HasMoneyState,
                "Should remain in HasMoneyState when funds insufficient");

        // Inserted amount should still be ₹10 (not consumed)
        assertEquals(10.0, machine.getCoinProcessor().getInsertedAmount(), 0.001,
                "Coins should not be consumed on failed purchase");
    }

    // ─── Test 2: Exact Change Dispenses Successfully ──────────────────────

    @Test
    @Order(2)
    @DisplayName("Exact payment should dispense product and return no change")
    void testExactChangeDispenses() {
        // Tea costs ₹10 — insert exactly ₹10
        machine.insertCoin(10);
        machine.selectProduct("C1"); // C1 = Tea ₹10

        // After dispensing, should be back in IdleState
        assertTrue(machine.getCurrentState() instanceof IdleState,
                "Should return to IdleState after dispensing");

        // Coin processor should be reset
        assertEquals(0.0, machine.getCoinProcessor().getInsertedAmount(), 0.001,
                "Coin processor should be reset after sale");
    }

    // ─── Test 3: Change Calculation Correct ──────────────────────────────

    @Test
    @Order(3)
    @DisplayName("Change calculation should be correct")
    void testChangeCalculationCorrect() {
        CoinProcessor cp = new CoinProcessor();

        // Insert ₹50 (two ₹10 + two ₹10 + two ₹5 = ₹50)
        cp.insertCoin(10);
        cp.insertCoin(10);
        cp.insertCoin(10);
        cp.insertCoin(10);
        cp.insertCoin(10);
        // Total = ₹50

        // Product costs ₹25
        double change = cp.calculateChange(25.0);
        assertEquals(25.0, change, 0.001, "Change should be ₹25");

        // Verify coin breakdown: ₹25 = ₹10 + ₹10 + ₹5
        var coins = cp.getChangeCoins(change);
        assertEquals(3, coins.size(), "Should return 3 coins for ₹25 change");
        assertTrue(coins.contains(10), "Should include ₹10 coin");
        assertTrue(coins.contains(5),  "Should include ₹5 coin");
    }

    // ─── Test 4: Out of Stock Throws Exception ────────────────────────────

    @Test
    @Order(4)
    @DisplayName("Dispensing out-of-stock item should throw IllegalStateException")
    void testOutOfStockThrowsException() {
        Inventory inventory = new Inventory();
        inventory.loadDefaultProducts();

        // B2 = Kurkure is out of stock (qty=0 in loadDefaultProducts)
        assertThrows(IllegalStateException.class, () -> {
            inventory.dispense("B2");
        }, "Should throw when dispensing out-of-stock item");
    }

    // ─── Test 5: State Transition Sequence ───────────────────────────────

    @Test
    @Order(5)
    @DisplayName("State should transition: Idle → HasMoney → Idle (after cancel)")
    void testStateTransitionSequence() {
        // Start: IDLE
        assertTrue(machine.getCurrentState() instanceof IdleState, "Start: Idle");

        // Insert coin → HasMoneyState
        machine.insertCoin(5);
        assertTrue(machine.getCurrentState() instanceof HasMoneyState, "After coin: HasMoney");

        // Cancel → back to IdleState
        machine.cancel();
        assertTrue(machine.getCurrentState() instanceof IdleState, "After cancel: Idle");

        // Verify coins returned (amount = 0)
        assertEquals(0.0, machine.getCoinProcessor().getInsertedAmount(), 0.001,
                "Coins should be returned on cancel");
    }

    // ─── Test 6: Session Timeout Resets State ─────────────────────────────

    @Test
    @Order(6)
    @DisplayName("Timeout simulation should reset machine to Idle with coins returned")
    void testSessionTimeoutResetsState() {
        // Simulate: user inserted ₹20, then walked away (timeout fires)
        machine.insertCoin(10);
        machine.insertCoin(10);

        double amountBeforeTimeout = machine.getCoinProcessor().getInsertedAmount();
        assertEquals(20.0, amountBeforeTimeout, 0.001, "Should have ₹20 inserted");

        // Simulate what SessionTimer.onTimeout() does:
        double returned = machine.getCoinProcessor().getInsertedAmount();
        machine.getTransactionLog().logTimeout(returned);
        machine.getCoinProcessor().reset();
        machine.setState(machine.getIdleState()); // Force back to idle

        // Assertions
        assertTrue(machine.getCurrentState() instanceof IdleState,
                "Should be in IdleState after timeout");
        assertEquals(0.0, machine.getCoinProcessor().getInsertedAmount(), 0.001,
                "All coins should be returned after timeout");

        // Check transaction log recorded the timeout
        var log = machine.getTransactionLog().getAll();
        assertFalse(log.isEmpty(), "Timeout should be logged");
        assertEquals("TIMEOUT", log.get(log.size() - 1).getType(),
                "Last log entry should be TIMEOUT");
    }

    // ─── Test 7: Invalid Coin Rejected ───────────────────────────────────

    @Test
    @Order(7)
    @DisplayName("Invalid coin denomination should be rejected")
    void testInvalidCoinRejected() {
        CoinProcessor cp = new CoinProcessor();

        boolean accepted = cp.insertCoin(3); // ₹3 is not a valid Indian coin
        assertFalse(accepted, "₹3 coin should be rejected");
        assertEquals(0.0, cp.getInsertedAmount(), "No amount should be added for invalid coin");
    }

    // ─── Test 8: Cannot Select In Idle State ─────────────────────────────

    @Test
    @Order(8)
    @DisplayName("Selecting product in IdleState should not change state")
    void testCannotSelectInIdleState() {
        // Machine is idle, no coins inserted
        machine.selectProduct("A1"); // Should be ignored

        // Must still be in IdleState
        assertTrue(machine.getCurrentState() instanceof IdleState,
                "Should remain in IdleState when no coins inserted");
        assertEquals(0.0, machine.getCoinProcessor().getInsertedAmount());
    }
}
