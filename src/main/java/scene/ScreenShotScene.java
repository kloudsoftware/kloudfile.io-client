package scene;

import config.Config;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class ScreenShotScene extends Scene implements IScene {

    private final Parent root;
    private final Config config;
    private final Stage stage;

    public ScreenShotScene(Parent root, Config config, Stage stage) {
        super(root);
        this.root = root;
        this.config = config;
        this.stage = stage;
    }

    @Override
    public Scene build() {



        mainScene.setOnMousePressed(event -> componentContainer.getActiveComponent().handleMousePressed(event));

        mainScene.setOnMouseDragged(event -> componentContainer.getActiveComponent().handleMouseDragged(event));

        mainScene.setOnMouseReleased(event -> componentContainer.getActiveComponent().handleMouseReleased(event));

        mainScene.setOnKeyPressed(event -> componentContainer.getActiveComponent().handleKeyPressed(event));

        mainScene.setOnDragDropped(event -> componentContainer.getActiveComponent().handleDragDropped(event));

        mainScene.setOnDragOver(event -> componentContainer.getActiveComponent().handleDragOver(event));

    }
}
