package components.container.scene;

import components.container.input.InputManager;
import config.Config;
import helper.ScreenHelper;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class SceneManager {

    public static final Logger LOGGER = Logger.getLogger(SceneManager.class);
    private final Map<Class<? extends IScene>, Scene> sceneMap;
    private final InputManager inputManager;
    private Scene activeScene;

    public SceneManager(Config config, Stage stage, InputManager inputManager) {
        this.inputManager = inputManager;

        stage.setX(Integer.valueOf(config.getProperties().getProperty("offset")));
        stage.setY(0);
        stage.setOpacity(.1);
        stage.setTitle("Push");
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        final ScreenShotScene screenShotScene = new ScreenShotScene(config, stage, ScreenHelper.getScreens(), inputManager);
        final Scene mainScene = screenShotScene.build();

        stage.setScene(mainScene);

        stage.show();

        sceneMap = new HashMap<>();
        sceneMap.put(ScreenShotScene.class, mainScene);
        activeScene = (sceneMap.get(ScreenShotScene.class));

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



    public Scene getScene(final Class<? extends IScene> key) {
        return sceneMap.get(key);
    }

}

