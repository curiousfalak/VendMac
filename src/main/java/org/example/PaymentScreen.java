package org.example;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * PaymentScreen — coin keypad with:
 *   • Coin bounce animation when inserted
 *   • Live session timer bar (visualises ScheduledExecutorService countdown)
 *   • Thread label showing "JavaFX Thread → insertCoin(₹N)"
 *
 * The timer bar uses a Timeline (JavaFX) to count down.
 * The real SessionTimer uses a ScheduledExecutorService daemon thread;
 * when it fires it calls Platform.runLater() — shown as a flash label here.
 */
public class PaymentScreen extends VBox {

    private VendingMachine machine;
    private Scenemanager   sceneManager;
    private Label          insertedLabel;
    private Label          messageLabel;
    private Label          threadLabel;
    private Label          timerLabel;
    private HBox           timerBarTrack;
    private Region         timerFill;

    // Visual countdown that mirrors SessionTimer's 30s
    private Timeline       visualCountdown;
    private int            visualSeconds = 30;

    public PaymentScreen(VendingMachine machine, Scenemanager sceneManager) {
        this.machine      = machine;
        this.sceneManager = sceneManager;
        buildUI();
        registerCallbacks();
    }

    private void buildUI() {
        setStyle("-fx-background-color: #0e0e18;");
        setSpacing(18);
        setPadding(new Insets(22));
        setAlignment(Pos.CENTER);
        getChildren().addAll(
                buildTitle(),
                buildAmountDisplay(),
                buildThreadIndicator(),
                buildTimerSection(),
                buildCoinKeypad(),
                buildMessageLabel(),
                buildActionButtons()
        );
    }

    private Label buildTitle() {
        Label lbl = new Label("INSERT COINS");
        lbl.setFont(Font.font("Courier New", FontWeight.BOLD, 22));
        lbl.setTextFill(Color.web("#fbbf24"));
        return lbl;
    }

    private VBox buildAmountDisplay() {
        insertedLabel = new Label("₹0");
        insertedLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 52));
        insertedLabel.setTextFill(Color.WHITE);

        Label sub = new Label("inserted so far");
        sub.setFont(Font.font("Courier New", 12));
        sub.setTextFill(Color.web("#666666"));

        VBox box = new VBox(4, insertedLabel, sub);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(18, 44, 18, 44));
        box.setStyle("-fx-background-color: #12122a; -fx-background-radius: 10; " +
                "-fx-border-color: #2a2a50; -fx-border-radius: 10; -fx-border-width: 1;");
        return box;
    }

    private VBox buildThreadIndicator() {
        threadLabel = new Label("[JavaFX Thread] waiting for coin input...");
        threadLabel.setFont(Font.font("Courier New", 10));
        threadLabel.setTextFill(Color.web("#60a5fa"));

        VBox box = new VBox(threadLabel);
        box.setPadding(new Insets(6, 10, 6, 10));
        box.setStyle("-fx-background-color: #101828; -fx-background-radius: 6; " +
                "-fx-border-color: #1e3a60; -fx-border-width: 1; -fx-border-radius: 6;");
        return box;
    }

    private VBox buildTimerSection() {
        timerLabel = new Label("SessionTimer-Thread: inactive (daemon)");
        timerLabel.setFont(Font.font("Courier New", 10));
        timerLabel.setTextFill(Color.web("#555555"));

        timerFill = new Region();
        timerFill.setPrefHeight(4);
        timerFill.setStyle("-fx-background-color: #fbbf24; -fx-background-radius: 3;");
        timerFill.setPrefWidth(0);

        timerBarTrack = new HBox(timerFill);
        timerBarTrack.setStyle("-fx-background-color: #2a2a10; -fx-background-radius: 3;");
        timerBarTrack.setPrefHeight(4);

        VBox box = new VBox(5, timerLabel, timerBarTrack);
        box.setPadding(new Insets(8, 10, 8, 10));
        box.setStyle("-fx-background-color: #18180a; -fx-background-radius: 7; " +
                "-fx-border-color: #3a3a18; -fx-border-width: 1; -fx-border-radius: 7;");
        return box;
    }

    private HBox buildCoinKeypad() {
        int[] coins = {1, 2, 5, 10};
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER);
        for (int coin : coins) row.getChildren().add(createCoinButton(coin));
        return row;
    }

    private Button createCoinButton(int rupees) {
        Button btn = new Button("₹" + rupees);
        btn.setPrefSize(72, 72);
        btn.setFont(Font.font("Courier New", FontWeight.BOLD, 17));
        String base  = "-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 36; -fx-cursor: hand;";
        String hover = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 36; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        btn.setOnAction(e -> {
            machine.insertCoin(rupees);
            animateCoinButton(btn);
            showThreadLabel("[JavaFX Thread] insertCoin(₹" + rupees + ") dispatched");
            resetVisualTimer();
        });
        return btn;
    }

    /** Quick bounce on the pressed coin button */
    private void animateCoinButton(Button btn) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);
        st.setFromX(1); st.setToX(0.88);
        st.setFromY(1); st.setToY(0.88);
        st.setCycleCount(2); st.setAutoReverse(true);
        st.play();
    }

    private void showThreadLabel(String text) {
        threadLabel.setText(text);
        threadLabel.setTextFill(Color.web("#60a5fa"));
        FadeTransition ft = new FadeTransition(Duration.millis(400), threadLabel);
        ft.setFromValue(1); ft.setToValue(0.5);
        ft.setCycleCount(2); ft.setAutoReverse(true);
        ft.setOnFinished(e -> threadLabel.setText("[JavaFX Thread] waiting for coin input..."));
        ft.play();
    }

    private Label buildMessageLabel() {
        messageLabel = new Label("Tap a denomination to insert it.");
        messageLabel.setFont(Font.font("Courier New", 12));
        messageLabel.setTextFill(Color.web("#4ade80"));
        return messageLabel;
    }

    private HBox buildActionButtons() {
        Button backBtn = new Button("Back to Products");
        backBtn.setFont(Font.font("Courier New", 12));
        backBtn.setStyle("-fx-background-color: #1e1e40; -fx-text-fill: #a0a8ff; " +
                "-fx-border-color: #3a3a70; -fx-border-radius: 7; -fx-background-radius: 7; -fx-cursor: hand;");
        backBtn.setOnAction(e -> sceneManager.showHomeScreen());

        Button cancelBtn = new Button("Cancel & Return Coins");
        cancelBtn.setFont(Font.font("Courier New", 12));
        cancelBtn.setStyle("-fx-background-color: #2a1010; -fx-text-fill: #f87171; " +
                "-fx-border-color: #4a2020; -fx-border-radius: 7; -fx-background-radius: 7; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> { machine.cancel(); sceneManager.showHomeScreen(); });

        HBox box = new HBox(12, backBtn, cancelBtn);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Visual timer bar — mirrors the real SessionTimer's 30s countdown.
     * The real timer runs on ScheduledExecutorService daemon thread;
     * this visual runs on the JavaFX Application Thread using Timeline.
     */
    private void resetVisualTimer() {
        if (visualCountdown != null) visualCountdown.stop();
        visualSeconds = 30;
        timerLabel.setText("SessionTimer-Thread: running  [30s → auto-cancel]");
        timerLabel.setTextFill(Color.web("#fbbf24"));

        visualCountdown = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            visualSeconds--;
            double pct = visualSeconds / 30.0;
            timerFill.setPrefWidth(timerBarTrack.getWidth() * pct);
            timerLabel.setText("SessionTimer-Thread: running  [" + visualSeconds + "s remaining]");
            if (visualSeconds <= 0) {
                timerLabel.setText("SessionTimer-Thread: FIRED → Platform.runLater() →");
                timerLabel.setTextFill(Color.web("#f87171"));
            }
        }));
        visualCountdown.setCycleCount(30);
        visualCountdown.play();
    }

    private void registerCallbacks() {
        machine.setOnAmountChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("₹" + (int) amt);
            // Bounce the amount label
            ScaleTransition st = new ScaleTransition(Duration.millis(90), insertedLabel);
            st.setFromX(1); st.setToX(1.08);
            st.setFromY(1); st.setToY(1.08);
            st.setCycleCount(2); st.setAutoReverse(true);
            st.play();
        });
        machine.setOnStateChanged(() -> {
            double amt = machine.getCoinProcessor().getInsertedAmount();
            insertedLabel.setText("₹" + (int) amt);
        });
        machine.setOnMessage(msg -> messageLabel.setText(msg));
    }
}