package helper;

import javafx.geometry.Point2D;

import java.util.Optional;

/**
 * Created by fr3d63 on 11/10/16.
 */
public class ScreenHelper {


    /**
     * Calculates the Start Point of a dragged Rectangle
     * @param begin The startpoint of the drag
     * @param end The endpoint of the drag
     * @param width The width of the drag
     * @param height The height of the drag
     * @return Correct startpoint
     */
    public static Optional<Point2D> calculateStartPoint(final Point2D begin, Point2D end, final double width, final double height) {
        if (end == null || begin == null) {
            return Optional.empty();
        }

        if (begin.getX() > end.getX() && begin.getY() > end.getY()) {
            return Optional.of(new Point2D(begin.getX() - width, begin.getY() - height));
        }
        if (begin.getX() > end.getX()) {
            return Optional.of(new Point2D(begin.getX() - width, begin.getY()));
        }
        if (begin.getY() > end.getY()) {
            return Optional.of(new Point2D(begin.getX(), begin.getY() - height));
        }
        return Optional.of(begin);
    }
}
