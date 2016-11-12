package components.container.interfaces;

import components.container.input.InputType;
import javafx.event.Event;

/**
 * Created by fr3d63 on 12/10/16.
 */
public interface IInputManager {

    void register(final IComponent component, final InputType... inputTypes);

    void unregister(final IComponent component, final InputType... inputTypes   );

    void handle(final InputType inputType, final Event event);

}
