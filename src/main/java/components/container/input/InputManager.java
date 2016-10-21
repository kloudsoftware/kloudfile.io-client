package components.container.input;

import components.container.IComponent;
import javafx.event.Event;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Created by fr3d63 on 13/10/16.
 */
public class InputManager implements IInputManager {

    private static final Logger LOGGER = Logger.getLogger(InputManager.class);
    private final Map<InputType, Set<IComponent>> typeSetMap;

    public InputManager() {
        typeSetMap = new HashMap<>();
    }

    @Override
    public void register(final IComponent component, final InputType... inputTypes) {
        for (final InputType inputType : inputTypes) {
            Set<IComponent> componentSet = typeSetMap.get(inputType);

            if (componentSet == null) {
                componentSet = new HashSet<>();
                componentSet.add(component);
                typeSetMap.put(inputType, componentSet);
            } else {
                componentSet.add(component);
            }
        }

    }

    @Override
    public void unregister(final IComponent component, final InputType... inputTypes) {
        for (final InputType inputType : inputTypes) {
            Set<IComponent> componentSet = typeSetMap.get(inputType);
            if (componentSet == null) {
                return;
            }
            componentSet.remove(component);
        }
    }

    @Override
    public void forEach(InputType inputType, final Event event) {
        Set<IComponent> componentSet = typeSetMap.get(inputType);
        if (componentSet == null) {
            return;
        }
        final boolean[] handled = {false};
        componentSet.forEach(iComponent -> {
            if (iComponent.handle(inputType, event)) {
                handled[0] = true;
            }
        });

        if (!handled[0]) {
            LOGGER.info("Input not handeld, exited");
            System.exit(0);
        }
    }

}
