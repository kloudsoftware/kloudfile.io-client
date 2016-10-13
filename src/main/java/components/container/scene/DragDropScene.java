package components.container.scene;

import config.Config;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static javafx.scene.paint.Color.GREY;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class DragDropScene implements IScene {

    private final Config config;
    private final Stage stage;
    private final SceneManager sceneManager;

    public DragDropScene(Config config, Stage stage, SceneManager sceneManager) {
        this.config = config;
        this.stage = stage;
        this.sceneManager = sceneManager;
    }

    @Override
    public Scene build() {
        final Group root = new Group();
        final Scene dragDropScene = new Scene(root);
        stage.hide();
        stage.setScene(dragDropScene);
        stage.setX(Integer.valueOf(config.getProperties().getProperty("offset")));
        stage.setWidth(100);
        stage.setHeight(200);
        stage.setY(sceneManager.getScreens().getHeight() / 2 - stage.getHeight());

        stage.setOpacity(1);

        stage.show();
        stage.setAlwaysOnTop(true);


        dragDropScene.setFill(GREY);

        return dragDropScene;
    }
}
