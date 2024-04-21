package invaders;


import invaders.singleton.DifficultyManager;
import invaders.engine.GameEngine;
import invaders.engine.GameWindow;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;


public class Menu {
    private Stage stage;

    public Menu(Stage stage) {
        this.stage = stage;
    }

    public void display() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Text welcomeText = new Text("WELCOME TO SPACE INVADERS");
        welcomeText.setFont(Font.font(null, FontWeight.BOLD, 30));
        welcomeText.setFill(javafx.scene.paint.Color.GREEN);

        ComboBox<String> modeSelection = new ComboBox<>();
        modeSelection.getItems().addAll("Easy", "Medium", "Hard");
        modeSelection.getSelectionModel().selectFirst();

        Button startBtn = new Button("Start Game");
        startBtn.setOnAction(e ->{
            String difficulty = modeSelection.getValue().toLowerCase();
            DifficultyManager.getInstance().setDifficulty(difficulty);
            startGame(DifficultyManager.getInstance().getDifficulty());
        });

        Scene scene = new Scene(layout, 500, 600);

        Button easyButton = new Button("Easy");
        Button mediumButton = new Button("Medium");
        Button hardButton = new Button("Hard");


        Label instruction = new Label("Instructions: ");
        instruction.setTextFill(javafx.scene.paint.Color.WHITE);

        Label instruction0 = new Label("U. Undo");
        instruction0.setTextFill(javafx.scene.paint.Color.WHITE);

        Label instruction1 = new Label("1: Cheat Slow Projectiles");
        instruction1.setTextFill(javafx.scene.paint.Color.WHITE);

        Label instruction2 = new Label("2. Cheat Fast Projectiles");
        instruction2.setTextFill(javafx.scene.paint.Color.WHITE);

        Label instruction3 = new Label("3. Cheat Slow Aliens");
        instruction3.setTextFill(javafx.scene.paint.Color.WHITE);

        Label instruction4 = new Label("4. Cheat Fast Aliens");
        instruction4.setTextFill(javafx.scene.paint.Color.WHITE);

        Region region = new Region();
        region.setPrefHeight(30);

        layout.getChildren().addAll(welcomeText, modeSelection, startBtn, region, instruction,instruction0, instruction1, instruction2, instruction3, instruction4);

        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(easyButton, mediumButton, hardButton);
        buttonBox.setAlignment(Pos.TOP_CENTER);

        stage.setScene(scene);
        stage.show();
    }

    public void startGame(String difficulty) {

        String configPath = "src/main/resources/config_" + difficulty + ".json";
        GameEngine model = new GameEngine(configPath);
        DifficultyManager.getInstance().setGameEngine(model);
        GameWindow window = new GameWindow(DifficultyManager.getInstance().getGameEngine());
        window.run();

        stage.setTitle("Space Invaders");
        stage.setScene(window.getScene());
        stage.show();

        window.run();
    }
}