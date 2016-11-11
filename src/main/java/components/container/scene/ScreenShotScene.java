package components.container.scene;

import components.container.input.InputManager;
import components.container.input.InputType;
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
    private final InputManager inputManager;
    private Scene scene;
    private Canvas canvas;

    public ScreenShotScene(Config config, Stage stage, Rectangle2D screens, InputManager inputManager) {
        this.config = config;
        this.stage = stage;
        this.screens = screens;
        this.inputManager = inputManager;
    }

    @Override
    public IScene build() {
        final Group root = new Group();
        scene = new Scene(root);
        canvas = new Canvas(screens.getWidth(), screens.getHeight());
        root.getChildren().add(canvas);


        scene.setOnKeyPressed(event -> inputManager.forEach(InputType.KEY_PRESSED, event));
        scene.setOnMouseDragged(event -> inputManager.forEach(InputType.MOUSE_DRAGGED, event));
        scene.setOnMouseReleased(event -> inputManager.forEach(InputType.MOUSE_RELEASED, event));
        scene.setOnMousePressed(event -> inputManager.forEach(InputType.MOUSE_PRESSED, event));
        return this;
    }

    @Override
    public Scene getScene() {
        return scene;
    }

    @Override
    public Canvas getCanvas() {
        return canvas;
    }
}
