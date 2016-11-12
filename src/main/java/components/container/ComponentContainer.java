package components.container;

import components.container.interfaces.IComponent;
import components.container.input.InputManager;
import components.container.input.InputType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fr3d63 on 10/10/16.
 */
public class ComponentContainer {

    private final Map<Class<? extends IComponent>, IComponent> componentMap;
    private final InputManager inputManager;
    private IComponent activeComponent;

    public ComponentContainer(InputManager inputManager) {
        this.inputManager = inputManager;
        this.componentMap = new HashMap<>();
    }

    public void add(final IComponent component, final InputType... inputTypes) {
        componentMap.put(component.getClass(), component);

        for (InputType inputType : inputTypes) {
            inputManager.register(component, inputType);
        }
    }

    public IComponent get(final Class<? extends IComponent> key) {
        return componentMap.get(key);
    }

    public IComponent getActiveComponent() {
        return activeComponent;
    }

    public void setActiveComponent(IComponent activeComponent) {
        this.activeComponent = activeComponent;
    }
}
