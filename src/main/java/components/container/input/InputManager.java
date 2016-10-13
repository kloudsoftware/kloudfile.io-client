package components.container.input;

import components.container.IComponent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by fr3d63 on 13/10/16.
 */
public class InputManager implements IInput {

    private final Set<IComponent> componentSet;

    public InputManager() {
        componentSet = new HashSet<>();
    }

    @Override
    public void register(IComponent component) {
        componentSet.add(component);
    }

    @Override
    public void unregister(IComponent component) {
        componentSet.remove(component);
    }

    @Override
    public void handleMouseReleased(MouseEvent event) {
        componentSet.forEach(component -> component.handleMouseReleased(event));
    }

    @Override
    public void handleKeyPressed(KeyEvent event) {
        componentSet.forEach(component -> component.handleKeyPressed(event));
    }

    @Override
    public void handleMouseDragged(MouseEvent event) {
        componentSet.forEach(component -> component.handleMouseDragged(event));
    }

    @Override
    public void handleMousePressed(MouseEvent event) {
        componentSet.forEach(component -> component.handleMousePressed(event));
    }

    @Override
    public void handleDragDropped(DragEvent event) {
        componentSet.forEach(component -> component.handleDragDropped(event));
    }

    @Override
    public void handleDragOver(DragEvent event) {
        componentSet.forEach(component -> component.handleDragOver(event));
    }
}
