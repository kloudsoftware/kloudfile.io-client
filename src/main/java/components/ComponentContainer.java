package components;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fr3d63 on 10/10/16.
 */
public class ComponentContainer {

    private final Map<Class<? extends IComponent>, IComponent> componentMap;
    private IComponent activeComponent;

    public ComponentContainer() {
        this.componentMap = new HashMap<>();
    }

    public void add(final IComponent... components) {
        for (IComponent iComponent : components) {
            componentMap.put(iComponent.getClass(), iComponent);
        }
    }

    public IComponent get(final Class<? extends IComponent> key) {
        return componentMap.get(key);
    }

    public IComponent getActiveComponent() {
        return activeComponent;
    }
}
