package org.example;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

/**
 * DAY 4 - STEP 3: SceneManager
 *
 * Manages navigation between the 3 screens:
 *   HomeScreen → PaymentScreen → DispensingScreen → HomeScreen
 *
 * HOW IT WORKS:
 *   JavaFX has one Stage (window) and one Scene.
 *   To "switch screens" we just replace the Scene's root node.
 *
 *   stage.getScene().setRoot(newScreen) — that's it!
 *
 * All screens hold a reference to SceneManager so they can
 * call showHomeScreen(), showPaymentScreen(), etc.
 */
public class Scenemanager {

    private Stage           stage;
    private VendingMachine  machine;

    // The 3 screen objects (created once, reused)
    private Homescreen homeScreen;
    private PaymentScreen    paymentScreen;
    private Dispensingscreen dispensingScreen;

    public Scenemanager(Stage stage, VendingMachine machine) {
        this.stage   = stage;
        this.machine = machine;

        // Create all screens — pass 'this' so they can navigate
        homeScreen       = new Homescreen(machine, this);
        paymentScreen    = new PaymentScreen(machine, this);
        dispensingScreen = new Dispensingscreen(machine, this);

        // Set machine's dispensing callback to route to DispensingScreen
        machine.setOnDispensing((product, change, coins) -> {
            showDispensingScreen(product, change, coins);
        });
    }

    /**
     * Show the main product selection screen.
     * Also refreshes the grid (stock may have changed after a purchase).
     */
    public void showHomeScreen() {
        homeScreen.refresh();
        switchTo(homeScreen);
    }

    /** Show the coin insertion / payment screen */
    public void showPaymentScreen() {
        switchTo(paymentScreen);
    }

    /** Show the dispensing success screen with product + change info */
    public void showDispensingScreen(Product product, double change, List<Integer> coins) {
        dispensingScreen.show(product, change, coins);
        switchTo(dispensingScreen);
    }

    /**
     * Actually switches the visible screen.
     * Sets the Scene's root to the new Pane.
     */
    private void switchTo(Pane screen) {
        stage.getScene().setRoot(screen);
    }

    // ─── Getters for the screens ──────────────────────────────────────────
    public Homescreen getHomeScreen()       { return homeScreen; }
    public PaymentScreen    getPaymentScreen()    { return paymentScreen; }
    public Dispensingscreen getDispensingScreen() { return dispensingScreen; }
}
