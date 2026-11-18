import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class GUIIMAGE extends JFrame {
    private final JLabel imageLabel;
    private final JButton browseButton;

    public GUIIMAGE() {
        setTitle("Image Loader GUI");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        imageLabel = new JLabel("No image selected", JLabel.CENTER);
        imageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        imageLabel.setForeground(Color.GRAY);

        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.CENTER);

        browseButton = new JButton("Browse Image...");
        browseButton.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(browseButton);
        add(bottomPanel, BorderLayout.SOUTH);

        browseButton.addActionListener(e -> chooseAndDisplayImage());
    }

    private void chooseAndDisplayImage() {
        JFileChooser fileChooser = new JFileChooser();

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif"
        );
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            displayImage(selectedFile.getAbsolutePath());
        }
    }

    private void displayImage(String imagePath) {
        ImageIcon originalIcon = new ImageIcon(imagePath);

        if (originalIcon.getIconWidth() == -1) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load the selected image.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int panelWidth = imageLabel.getWidth();
        int panelHeight = imageLabel.getHeight();

        if (panelWidth <= 0) panelWidth = 550;
        if (panelHeight <= 0) panelHeight = 400;

        Image scaledImage = originalIcon.getImage().getScaledInstance(
                panelWidth, panelHeight, Image.SCALE_SMOOTH
        );

        imageLabel.setIcon(new ImageIcon(scaledImage));
        imageLabel.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GUIIMAGE().setVisible(true);
        });
    }
}