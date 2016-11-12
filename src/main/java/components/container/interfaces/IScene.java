package components.container.interfaces;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;

/**
 * Created by fr3d63 on 12/10/16.
 */
public interface IScene {
    IScene build();
    Scene getScene();
    Canvas getCanvas();
}
