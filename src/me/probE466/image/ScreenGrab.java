package me.probE466.image;

import me.probE466.PushClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by larsg on 26.09.2016.
 */
public class ScreenGrab {

    private PushClient instance;

    public ScreenGrab(PushClient pushClient) {
        this.instance = pushClient;
    }

    public BufferedImage getFullScreen() {
        Rectangle screenRect = new Rectangle(0, 0, 0, 0);
        for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
        }
        BufferedImage capture = null;
        try {
            capture = new Robot().createScreenCapture(screenRect);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        return capture;
    }

    public BufferedImage getPartOfScreen() {
        final Point[] begin = new Point[1];
        final Point[] end = new Point[1];

        JFrame jFrame = new JFrame();
        jFrame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                begin[0] = e.getPoint();

            }

            @Override
            public void mouseReleased(MouseEvent e) {

                jFrame.getGraphics().setColor(Color.red);

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        jFrame.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                final Graphics graphics = jFrame.getGraphics();
                graphics.clearRect(0, 0, jFrame.getWidth(), jFrame.getHeight());
                end[0] = e.getPoint();

                int x = begin[0].x;
                int y = begin[0].y;
                final int x2 = end[0].x;
                final int y2 = end[0].y;
                int width = end[0].x - x;
                int height = end[0].y - y;

                if (width < 0) {
                    width *= -1;
                    x = x - width;
                }

                if (height < 0) {
                    height *= -1;
                    y = y - height;
                }


                graphics.setColor(new Color(1f,1f,1f,.5f));
                graphics.drawRect(x, y, width, height);
                graphics.fillRect(x, y, width, height);
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
        jFrame.setUndecorated(true);
        jFrame.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.1f));
        Rectangle2D result = new Rectangle2D.Double();
        GraphicsEnvironment localGE = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (GraphicsDevice gd : localGE.getScreenDevices()) {
            for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
                Rectangle2D.union(result, graphicsConfiguration.getBounds(), result);
            }
        }
        jFrame.setSize((int) result.getWidth(), (int) result.getHeight());
        jFrame.setVisible(true);
        jFrame.setLocation(instance.getOffset(), 0);
        return null;
    }


}

class MyMouse implements MouseMotionListener {

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("dragged: x: " + e.getX() + " y: " + e.getY());
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}