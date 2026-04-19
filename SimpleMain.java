import javax.swing.*;
import java.awt.*;

public class SimpleMain extends JFrame {
    
    public SimpleMain() {
        setTitle("Hotel Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        
        // Simple colored panel to see if anything shows
        JPanel panel = new JPanel();
        panel.setBackground(new Color(212, 175, 55));
        panel.add(new JLabel("Hotel Reservation System - Working!"));
        panel.add(new JButton("Test Button"));
        
        add(panel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimpleMain());
    }
}