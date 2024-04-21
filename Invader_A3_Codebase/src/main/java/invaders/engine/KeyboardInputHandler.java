package invaders.engine;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyboardInputHandler {
    private final GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private boolean cheatActivated = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();
    private Map<String, MediaPlayer> sounds = new HashMap<>();

    KeyboardInputHandler(GameEngine model) {
        this.model = model;

        // TODO (longGoneUser): Is there a better place for this code?
        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }

    void handlePressed(KeyEvent keyEvent) {
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            if (model.shootPressed()) {
                MediaPlayer shoot = sounds.get("shoot");
                shoot.stop();
                shoot.play();
                model.saveState();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }

        if (keyEvent.getCode().equals(KeyCode.U)) {
            System.out.println("U PRESSED");
            model.restoreState();
        }

        if (!cheatActivated) {
            if (keyEvent.getCode().equals(KeyCode.DIGIT1)) {
                System.out.println("1 PRESSED");
                cheatActivated = true;
                model.cheatRemoveSlowProjectiles();
            }

            if (keyEvent.getCode().equals(KeyCode.DIGIT2)) {
                System.out.println("2 PRESSED");
                cheatActivated = true;
                model.cheatRemoveFastProjectiles();
            }

            if (keyEvent.getCode().equals(KeyCode.DIGIT3)) {
                System.out.println("3 PRESSED");
                cheatActivated = true;
                model.cheatSlowEnemy();
            }

            if (keyEvent.getCode().equals(KeyCode.DIGIT4)) {
                System.out.println("4 PRESSED");
                cheatActivated = true;
                model.cheatFastEnemy();
            }
        }

        if (left) {
            model.leftPressed();
        }

        if(right){
            model.rightPressed();
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }

        if (keyEvent.getCode().equals(KeyCode.U)) {
            System.out.println("U RELEASED");
        }

        if (keyEvent.getCode().equals(KeyCode.DIGIT1)
                || keyEvent.getCode().equals(KeyCode.DIGIT2)
                || keyEvent.getCode().equals(KeyCode.DIGIT3)
                || keyEvent.getCode().equals(KeyCode.DIGIT4)) {
            cheatActivated = false;
        }
    }
}

