package org.example;

import javafx.application.Platform;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * DAY 5 - STEP 1: SessionTimer
 *
 * Implements the 30-second timeout in HasMoneyState.
 *
 * PROBLEM: If a user inserts coins and walks away,
 * the machine is stuck in HasMoneyState forever.
 *
 * SOLUTION: A background timer. If no action happens
 * within 30 seconds, auto-cancel and return coins.
 *
 * KEY THREADING CONCEPTS:
 *
 * 1. ScheduledExecutorService
 *    - Java's timer that runs tasks after a delay
 *    - schedule(task, 30, TimeUnit.SECONDS) → runs once after 30s
 *
 * 2. Thread Safety Issue:
 *    - The timer runs on a BACKGROUND thread
 *    - JavaFX UI can only be updated from the JavaFX Application Thread
 *    - If you update UI from background thread → crash or glitches!
 *
 * 3. Platform.runLater()
 *    - Queues your UI update to run on the JavaFX thread
 *    - THIS IS MANDATORY for any UI update from a background thread
 *
 * USAGE:
 *   timer.start()  → called when entering HasMoneyState
 *   timer.reset()  → called when user inserts coin / selects product
 *   timer.stop()   → called when leaving HasMoneyState
 */
public class SessionTimer {

    private static final int TIMEOUT_SECONDS = 30;

    private VendingMachine machine;
    private ScheduledExecutorService executor;
    private ScheduledFuture<?>       currentTask; // Reference to cancel it

    public SessionTimer(VendingMachine machine) {
        this.machine  = machine;
        // Single-threaded scheduler (we only need one timer at a time)
        this.executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "SessionTimer-Thread");
            t.setDaemon(true); // Dies when the app closes — important!
            return t;
        });
    }

    /**
     * Start (or restart) the 30-second countdown.
     * If already running, cancel the old one first.
     */
    public void start() {
        stop(); // Cancel any existing timer

        currentTask = executor.schedule(this::onTimeout, TIMEOUT_SECONDS, TimeUnit.SECONDS);
        System.out.println("[Timer] Session timer started — " + TIMEOUT_SECONDS + "s");
    }

    /**
     * Reset the timer (call this when user inserts a coin or interacts).
     * Effectively: stop + start.
     */
    public void reset() {
        System.out.println("[Timer] Session timer reset.");
        start();
    }

    /**
     * Stop and discard the current timer (call when leaving HasMoneyState).
     */
    public void stop() {
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(false); // false = don't interrupt if running
            System.out.println("[Timer] Session timer stopped.");
        }
    }

    /**
     * Called when timer fires (30s elapsed with no user action).
     *
     * CRITICAL: This runs on the background timer thread.
     * We MUST use Platform.runLater() to touch anything JavaFX-related.
     */
    private void onTimeout() {
        System.out.println("[Timer] Session TIMED OUT — returning coins.");

        // Platform.runLater: safely executes on the JavaFX Application Thread
        Platform.runLater(() -> {
            double returned = machine.getCoinProcessor().getInsertedAmount();

            if (returned > 0) {
                machine.getTransactionLog().logTimeout(returned);
                machine.getCoinProcessor().reset();
                machine.notifyCoinsReturned(returned);
                machine.notifyMessage("Session timed out. ₹" + (int) returned + " returned.");
            }

            // Force state back to IDLE
            machine.setState(machine.getIdleState());
        });
    }

    /**
     * Shut down the executor (call on app close).
     */
    public void shutdown() {
        executor.shutdownNow();
    }
}
