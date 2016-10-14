package components.container;

import components.container.input.InputType;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Created by fr3d63 on 10/10/16.
 */
public interface IComponent {

    Scene getScene();

    void handle(final InputType inputType, final Event event);
}
