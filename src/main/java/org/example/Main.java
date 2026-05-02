package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * REDESIGNED Main.java
 *
 * Replaces the old prototype (raw buttons + 1 Label) with
 * the full 3-screen UI managed by SceneManager.
 *
 * Flow:
 *   HomeScreen  ──[Insert Coins]──►  PaymentScreen
 *   PaymentScreen ──[select product on HomeScreen]──► DispensingScreen
 *   DispensingScreen ──[← Back]──► HomeScreen
 *
 * Window size: 480 × 600 (fits all 3 screens comfortably)
 */
public class Main extends Application {

    // ── Shared machine instance ──────────────────────────────────────────
    private VendingMachine machine;

    // ── Scene manager controls navigation ───────────────────────────────
    private Scenemanager sceneManager;

    @Override
    public void start(Stage primaryStage) {

        // 1. Create the vending machine (loads default inventory automatically)
        machine = new VendingMachine();

        // 2. Build the HomeScreen first so we have a root node for the Scene
        //    SceneManager creates all 3 screens internally.
        sceneManager = new Scenemanager(primaryStage, machine);

        // 3. Create the JavaFX Scene with HomeScreen as the starting root
        //    Width=480, Height=620 gives enough room for the 3-column product grid
        Scene scene = new Scene(sceneManager.getHomeScreen(), 480, 620);

        // 4. Window settings
        primaryStage.setTitle("🥤 Vending Machine");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Fixed size — prevents layout breakage

        // 5. Graceful shutdown: stop background threads when window closes
        primaryStage.setOnCloseRequest(e -> {
            // If you add SessionTimer to VendingMachine later, call shutdown() here
            // machine.getSessionTimer().shutdown();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        // 6. Log startup state to console (helpful for debugging)
        System.out.println("===========================================");
        System.out.println("  Vending Machine Started");
        System.out.println("  State: " + machine.getCurrentState().getStateName());
        System.out.println("  Products loaded: "
                + machine.getInventory().getAllProducts().size());
        System.out.println("===========================================");
    }

    public static void main(String[] args) {
        launch(args);
    }
}