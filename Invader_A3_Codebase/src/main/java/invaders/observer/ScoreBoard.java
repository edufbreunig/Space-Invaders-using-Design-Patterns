package invaders.observer;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Represents a scoreboard display in the game.
 * The scoreboard shows the current time and score, updating in response
 * to changes in game state.
 * <p>
 * This class implements the {@code Observer} interface, allowing it to
 * react to state changes in the game and update the display accordingly.
 *
 */
public class ScoreBoard implements Observer {

    private Text timeText = new Text();
    private Text scoreText = new Text();

    /**
     * Initializes a new ScoreBoard instance with default settings.
     * Default settings  position, font, and color for time and score texts.
     */
    public ScoreBoard() {
        timeText.setX(10);
        timeText.setY(20);

        scoreText.setX(10);
        scoreText.setY(40);

        timeText.setFont(new Font(20));
        timeText.setFill(Color.WHITE);

        scoreText.setFont(new Font(20));
        scoreText.setFill(Color.WHITE);

    }

    /**
     * Updates the displayed time and score based on provided values.
     * The time is formatted as MM:SS.
     *
     * @param time  The current game time in seconds.
     * @param score The current game score.
     */

    @Override
    public void update(int time, int score) {
        timeText.setText(String.format("%02d:%02d", time / 60, time % 60));
        scoreText.setText("Score: " + score);
    }


    public Text getTimeText() {
        return timeText;
    }

    public Text getScoreText() {
        return scoreText;
    }
}
