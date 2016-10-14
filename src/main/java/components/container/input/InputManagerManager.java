package components.container.input;

import components.container.IComponent;
import javafx.event.Event;

import java.util.*;

/**
 * Created by fr3d63 on 13/10/16.
 */
public class InputManagerManager implements IInputManager {

    private final Map<InputType, Set<IComponent>> typeSetMap;

    public InputManagerManager() {
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
        componentSet.forEach(iComponent -> iComponent.handle(inputType, event));
    }

}
