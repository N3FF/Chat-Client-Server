package network.client;

import java.awt.Color;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ChirpyTheBird extends JFrame {
  private static final long serialVersionUID = 1L;

  ChirpyTheBird(){
    this.setUndecorated(true); // Removes Title Bar and Enables transparency
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.setBackground(new Color(1.0f,1.0f,1.0f,0.0f)); // Sets background transparent
    this.setType(Type.UTILITY); // Changes window type so it's not displayed on the askbar
    this.add(new JLabel(new ImageIcon("images\\chirpy.png")));
    this.pack();
    this.addWindowFocusListener(new WindowAdapter() {
      public void windowGainedFocus(WindowEvent e) {
        Window.getWindows()[0].toFront(); // If Chirpy gets focus then set the main window as the new focus
      }
    });
  }
}
