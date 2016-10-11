package components;

import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Created by fr3d63 on 10/10/16.
 */
public interface IComponent {

    void handleMouseReleased(MouseEvent event);

    void handleKeyPressed(KeyEvent event);

    void handleMouseDragged(MouseEvent event);

    void handleMousePressed(MouseEvent event);

    void handleDragDropped(DragEvent event);

    void handleDragOver(DragEvent event);
}
