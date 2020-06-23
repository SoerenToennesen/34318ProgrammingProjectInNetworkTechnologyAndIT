import javax.swing.*;

public class ImagePane extends JPanel {
    private JLabel label;
    public ImagePane() {
        super();
        label = new JLabel();
        add(label);
    }
    public void setImage (Object object) {
        label.setIcon(new ImageIcon("Files/" + object));
    }
}
