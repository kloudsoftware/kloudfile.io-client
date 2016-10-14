package components.container.input;

import components.container.IComponent;
import javafx.event.Event;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Created by fr3d63 on 12/10/16.
 */
interface IInputManager {

    void register(final IComponent component, final InputType... inputTypes);

    void unregister(final IComponent component, final InputType... inputTypes   );

    void forEach(final InputType inputType, final Event event);

}
