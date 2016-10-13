package components.container.scene;

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
        return this;
    }
}
