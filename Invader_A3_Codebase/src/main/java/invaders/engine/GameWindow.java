package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

import invaders.singleton.DifficultyManager;
import invaders.entities.EntityViewImpl;
import invaders.entities.SpaceBackground;
import invaders.Menu;

import invaders.gameobject.GameObject;
import invaders.observer.ScoreBoard;


import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;


public class GameWindow {
    private final int width;
    private final int height;
    private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews = new ArrayList<EntityView>();
    private Renderable background;
    private Menu menu;
    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    private ScoreBoard scoreBoard;
    private HBox buttonPanel;

    // private static final double VIEWPORT_MARGIN = 280.0;

    public GameWindow(GameEngine model) {
        this.model = model;
        this.width = model.getGameWidth();
        this.height = model.getGameHeight();

        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(model, pane);

        KeyboardInputHandler keyboardInputHandler = new KeyboardInputHandler(this.model);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

        this.scoreBoard = new ScoreBoard();
        pane.getChildren().add(scoreBoard.getTimeText());
        pane.getChildren().add(scoreBoard.getScoreText());
        model.registerObserver(scoreBoard);

        initializeDifficultyButtons();

    }

    public void run() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }


    private void draw() {
        model.update();

        List<Renderable> renderables = model.getRenderables();


        //if the game has been restored
        if(model.getRestored()){

            //remove renderables from the screen
            for (Renderable entity : renderables){
                for (EntityView view : entityViews){
                    if (view.matchesEntity(entity)) {
                        pane.getChildren().remove(view.getNode());
                    }
                }
            }

            entityViews.clear();
            renderables.clear();
            model.getGameObjects().clear();

            //create new entity view for the renderables that will be added
            for (Renderable entity : model.getPendingToAddRenderable()) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
                renderables.add(entity);
                model.getGameObjects().add((GameObject) entity);

            }

            model.getPendingToAddRenderable().clear();
            model.getPendingToAddGameObject().clear();

            renderables.add(model.getPlayer());
            model.setRestored(false);
        }


        for (Renderable entity : model.getProjectilesToRemove()) {
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    view.markForDelete();
                    renderables.remove(entity);
                    model.getGameObjects().remove((GameObject) entity);
                }
            }
        }

        for (Renderable entity : model.getEnemiesToRemove()) {
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    view.markForDelete();
                    renderables.remove(entity);
                    model.getGameObjects().remove((GameObject) entity);
                }
            }
        }

        for (Renderable entity : renderables){
            boolean notFound = true;
            for (EntityView view : entityViews){
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
        }

        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }

        }

        if(model.isGameOver()){
            gameOverMessage();
        }
        if(model.isGameWon()){
            gameWonMessage();
        }

        model.getProjectilesToRemove().clear();
        model.getEnemiesToRemove().clear();

        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);

    }

    public Scene getScene() {
        return scene;
    }

    /**
     * Initializes the difficulty selection buttons and adds them to a horizontal panel.
     * The buttons include "Easy", "Medium", and "Hard" options.
     */
    private void initializeDifficultyButtons() {

        buttonPanel = new HBox(10);


        Button easyButton = new Button("Easy");
        easyButton.setFocusTraversable(false);
        easyButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DifficultyManager.getInstance().setDifficulty("easy");
                restartGame(DifficultyManager.getInstance().getDifficulty());

            }
        });

        Button mediumButton = new Button("Medium");
        mediumButton.setFocusTraversable(false);
        mediumButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DifficultyManager.getInstance().setDifficulty("medium");
                restartGame(DifficultyManager.getInstance().getDifficulty());
            }
        });

        Button hardButton = new Button("Hard");
        hardButton.setFocusTraversable(false);
        hardButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DifficultyManager.getInstance().setDifficulty("hard");
                restartGame(DifficultyManager.getInstance().getDifficulty());
            }
        });


        buttonPanel.getChildren().addAll(easyButton, mediumButton, hardButton);


        buttonPanel.layoutXProperty().bind(pane.widthProperty().subtract(buttonPanel.widthProperty()));
        buttonPanel.setLayoutY(10);

        pane.getChildren().add(buttonPanel);
    }



    /**
     * Restarts the game when the player selects a new difficutly
     *
     * @param: difficulty: the difficulty to be restarted
     */
    private void restartGame(String difficulty) {
        Menu menu = new Menu((Stage) pane.getScene().getWindow());
        menu.startGame(difficulty);
    }

    /**
     * Displays a game over message, score, and time on the game pane.
     * The message and score are presented in a centralized manner on the screen.
     */
    private void gameOverMessage() {
        pane.getChildren().clear();
        this.background = new SpaceBackground(model, pane);

        Label gameOverLabel = new Label("Game Over");
        gameOverLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: red;");
        gameOverLabel.layoutXProperty().bind(pane.widthProperty().subtract(gameOverLabel.widthProperty()).divide(2));
        gameOverLabel.layoutYProperty().bind(pane.heightProperty().divide(3));

        Label scoreLabel = new Label("Score: " + scoreBoard.getScoreText().getText());
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.layoutXProperty().bind(pane.widthProperty().subtract(scoreLabel.widthProperty()).divide(2));
        scoreLabel.layoutYProperty().bind(pane.heightProperty().divide(2));

        Label timeLabel = new Label("Time: " + scoreBoard.getTimeText().getText());
        timeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        timeLabel.layoutXProperty().bind(pane.widthProperty().subtract(timeLabel.widthProperty()).divide(2));
        timeLabel.layoutYProperty().bind(pane.heightProperty().divide(2).add(30));

        pane.getChildren().addAll(gameOverLabel, scoreLabel, timeLabel);
    }

    /**
     * Displays a game won message, score, and time on the game pane.
     * The message celebrates the player's victory and the score is
     * presented in a centralized manner on the screen.
     */
    private void gameWonMessage() {
        pane.getChildren().clear();
        this.background = new SpaceBackground(model, pane);

        Label gameWonLabel = new Label("Game Won, Congratulations!");
        gameWonLabel.setStyle("-fx-font-size: 30px; -fx-text-fill: green;");
        gameWonLabel.layoutXProperty().bind(pane.widthProperty().subtract(gameWonLabel.widthProperty()).divide(2));
        gameWonLabel.layoutYProperty().bind(pane.heightProperty().divide(3));

        Label scoreLabel = new Label("Score: " + scoreBoard.getScoreText().getText());
        scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        scoreLabel.layoutXProperty().bind(pane.widthProperty().subtract(scoreLabel.widthProperty()).divide(2));
        scoreLabel.layoutYProperty().bind(pane.heightProperty().divide(2));

        Label timeLabel = new Label("Time: " + scoreBoard.getTimeText().getText());
        timeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
        timeLabel.layoutXProperty().bind(pane.widthProperty().subtract(timeLabel.widthProperty()).divide(2));
        timeLabel.layoutYProperty().bind(pane.heightProperty().divide(2).add(30));

        pane.getChildren().addAll(gameWonLabel, scoreLabel, timeLabel);
    }



}

