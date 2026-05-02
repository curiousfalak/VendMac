package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    private VendingMachine machine;
    private Scenemanager   sceneManager;
    private SessionTimer   sessionTimer;

    @Override
    public void start(Stage primaryStage) {

        machine      = new VendingMachine();
        sessionTimer = new SessionTimer(machine);

        // Wire session timer into HasMoneyState via machine
        machine.setSessionTimer(sessionTimer);

        sceneManager = new Scenemanager(primaryStage, machine);

        // No external CSS — all styles are inline in each screen
        Scene scene = new Scene(sceneManager.getHomeScreen(), 520, 660);

        primaryStage.setTitle("Vending Machine");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        primaryStage.setOnCloseRequest(e -> {
            sessionTimer.shutdown();
            Platform.exit();
            System.exit(0);
        });

        primaryStage.show();

        System.out.println("[Main] JavaFX Application Thread: "
                + Thread.currentThread().getName());
        System.out.println("[Main] Machine state: "
                + machine.getCurrentState().getStateName());
    }

    public static void main(String[] args) {
        launch(args);
    }
}