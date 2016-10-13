package components.container.scene;

import config.Config;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.geom.Rectangle2D;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class ScreenShotScene implements IScene {

    private final Config config;
    private final Stage stage;
    private final Rectangle2D screens;

    public ScreenShotScene(Config config, Stage stage, Rectangle2D screens) {
        this.config = config;
        this.stage = stage;
        this.screens = screens;
    }

    @Override
    public Scene build() {
        final Group root = new Group();
        final Scene scene = new Scene(root);
        Canvas canvas = new Canvas(screens.getWidth(), screens.getHeight());
        root.getChildren().add(canvas);
        canvas.getGraphicsContext2D().setFill(Color.RED);
        canvas.getGraphicsContext2D().fillRect(0,0,100,100);
        return scene;
    }
}
