package scene;

import config.Config;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class SceneManager {

    private final Map<Class<? extends IScene>, Scene> sceneMap;
    private Scene activeScene;

    public SceneManager(Parent root, Config config, Stage stage, Rectangle2D screens) {
        sceneMap = new HashMap<>();
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    private void setActiveScene(Scene activeScene) {
        this.activeScene = activeScene;
    }

    public void switchTo(IScene scene) {
        final Scene nextScene = sceneMap.get(scene.getClass());
        if (nextScene == null) {
            throw new IllegalArgumentException("Scene not registerd");
        }
        scene.build();
        setActiveScene(nextScene);
    }
}

