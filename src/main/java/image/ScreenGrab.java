package image;

import http.Upload;
import main.PushClient;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Created by larsg on 26.09.2016.
 */
public class ScreenGrab implements MouseMotionListener, MouseListener {

    private PushClient instance;
    private Point begin, end;
    private JFrame jFrame;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean hasSelected = false;

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

    public void getPartOfScreen() {
        jFrame = new JFrame();
        jFrame.addMouseListener(this);
        jFrame.addMouseMotionListener(this);
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
    }


    private void captureImage() {
        BufferedImage capture = null;

        jFrame.setVisible(false);

        try {
            capture = new Robot().createScreenCapture(new Rectangle(x,y,width,height));
            File imageFile = new File("image.png");
            ImageIO.write(capture, "png", imageFile);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(capture, "png ", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            Upload.uploadDataToServer(is, "test");
        } catch (AWTException | IOException ex) {
            ex.printStackTrace();
        }

        System.exit(0);


    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        begin = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        captureImage();
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(jFrame == null) {
            return;
        }
        final Graphics graphics = jFrame.getGraphics();
        graphics.clearRect(0, 0, jFrame.getWidth(), jFrame.getHeight());
        end = e.getPoint();

        x = begin.x;
        y = begin.y;
        width = end.x - x;
        height = end.y - y;

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
}