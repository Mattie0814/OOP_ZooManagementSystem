import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String resourcePath) {
        setBackgroundResource(resourcePath);
    }

    public BackgroundPanel(URL resourceUrl) {
        setBackgroundImage(resourceUrl != null ? new ImageIcon(resourceUrl).getImage() : null);
    }

    public BackgroundPanel(Image backgroundImage) {
        setBackgroundImage(backgroundImage);
    }

    public void setBackgroundResource(String resourcePath) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            setBackgroundImage((Image) null);
            return;
        }
        URL url = getClass().getResource(resourcePath);
        setBackgroundImage(url != null ? new ImageIcon(url).getImage() : null);
    }

    public void setBackgroundFile(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            setBackgroundImage((Image) null);
            return;
        }
        File file = new File(filePath);
        if (!file.isFile()) {
            setBackgroundImage((Image) null);
            return;
        }

        setBackgroundImage(new ImageIcon(file.getAbsolutePath()).getImage());
    }

    public void setBackgroundImage(Image newImage) {
        this.backgroundImage = newImage;
        setOpaque(this.backgroundImage == null);
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}