package me.probE466.image;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by larsg on 26.09.2016.
 */
public class ScreenGrab {

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
        JFrame jFrame = new JFrame();
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
        jFrame.setLocation(-1080,0);
        System.out.println(result.getWidth());
        System.out.println(result.getHeight());
        return null;
    }


}
