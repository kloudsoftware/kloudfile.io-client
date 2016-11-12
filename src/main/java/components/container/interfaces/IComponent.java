package components.container.interfaces;

import components.container.input.InputType;
import javafx.event.Event;
import javafx.scene.Scene;

/**
 * Created by fr3d63 on 10/10/16.
 */
public interface IComponent {

    Scene getScene();

    boolean handle(final InputType inputType, final Event event);
}
