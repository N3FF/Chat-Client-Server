package network.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.MatteBorder;

import javafx.embed.swing.JFXPanel;
/**
 * 
 * @author Justin
 *
 * Swing JFrame
 */
public class ClientUI extends JFrame {

  private ChirpyTheBird chirpy = new ChirpyTheBird();
  private static ImageIcon icon = new ImageIcon("images\\chirpy-icon.png"); // Taskbar Icon
  
  private static final long serialVersionUID = 1L;
  private static final int WIDTH = 800;
  private static final int HEIGHT = 600;
  private static final Dimension WINDOW_SIZE = new Dimension(WIDTH, HEIGHT);
  private static final Dimension MIN_WINDOW_SIZE =
      new Dimension((int) (WIDTH * (2 / 3.0)), (int) (HEIGHT * (2 / 3.0)));
  private JFXPanel friendsPanel;
  private JTextField textBox;
  private JFXPanel textPane;

  ClientUI() {
    setSize(WINDOW_SIZE);
    setMinimumSize(MIN_WINDOW_SIZE);
    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setIconImage(icon.getImage());

    JPanel mainPanel = new JPanel(); // Holds everything
    mainPanel.setLayout(new BorderLayout());
    mainPanel.setBorder(ClientFormat.SOLID_TOP_BORDER);

    JPanel chatPanel = buildChatPanel(); // Holds the JFX and Textbox

    friendsPanel = new JFXPanel(); // Contains all of the Chat content
    friendsPanel.setBorder(ClientFormat.SOLID_RIGHT_BORDER);
    friendsPanel.setBackground(ClientFormat.LIGHT_GRAY);
    friendsPanel.setPreferredSize(new Dimension(200, chatPanel.getHeight()));
    
    /* Holds the content on the Title Panel
    JPanel titlePanel = new JPanel(); // Nothing currently in the title
    titlePanel.setLayout(new BorderLayout());
    titlePanel.setBorder(ClientFormat.SOLID_BOTTOM_BORDER);
    titlePanel.setBackground(new Color(56, 156, 252));
    
    JLabel testJLabel = new JLabel(); // Was on the titlePanel but I removed
    testJLabel.setText("Chat");
    testJLabel.setForeground(Color.WHITE);
    testJLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
    testJLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    titlePanel.add(testJLabel);

    mainPanel.add(titlePanel, BorderLayout.NORTH);
    */
    
    mainPanel.add(friendsPanel, BorderLayout.WEST);
    mainPanel.add(chatPanel, BorderLayout.CENTER);

    addListeners(); // Listeners() used for Chirpy above the window.
    chirpy.setLocation(getX() + 10, getY() - 70); // Attaches the bird to the window location
    chirpy.setVisible(true);
    
    setContentPane(mainPanel);
  }
  
  private JPanel buildChatPanel() {
    JPanel chatPanel = new JPanel(); // Holds JFXPanel & Text box
    chatPanel.setLayout(new BorderLayout());

    textBox = new JTextField(); // Text Box at the bottom.

    textBox.setBorder(new MatteBorder(10, 15, 10, 5, ClientFormat.LIGHT_GRAY));
    textBox.setMargin(new Insets(5, 10, 10, 5));
    textBox.setBackground(ClientFormat.LIGHT_GRAY);

    textPane = new JFXPanel(); // Center Panel that holds all of the Chat content
    textPane.setBorder(ClientFormat.SOLID_BOTTOM_BORDER);

    chatPanel.add(textPane, BorderLayout.CENTER);
    chatPanel.add(textBox, BorderLayout.SOUTH);

    return chatPanel;
  }

  public JTextField getTextBox() {
    return textBox;
  }
  public JFXPanel getTextPane() {
    return textPane;
  }
  public JFXPanel getFriendPanel() {
    return friendsPanel;
  }
  
  // Listeners used to control the location of the bird above the main window.
  private void addListeners() {

    // Makes bird follow window
    addComponentListener(new ComponentAdapter() {
      // 
      @Override
      public void componentMoved(ComponentEvent e) {
        chirpy.setLocation(getX() + 10, getY() - 70);
      }
    });

    // Hide bird when minimized or maximized. Show if not.
    addWindowStateListener(new WindowAdapter() {
      public void windowStateChanged(WindowEvent e) {
        if ((e.getOldState() & Frame.ICONIFIED) == 0 && (e.getNewState() & Frame.ICONIFIED) != 0) {
          chirpy.setVisible(false);
        } else if ((e.getOldState() & Frame.ICONIFIED) != 0
            && (e.getNewState() & Frame.ICONIFIED) == 0) {
          chirpy.setVisible(true);
          toFront();
        }
        if ((e.getOldState() & Frame.MAXIMIZED_BOTH) == 0
            && (e.getNewState() & Frame.MAXIMIZED_BOTH) != 0) {
          chirpy.setVisible(false);
        } else if ((e.getOldState() & Frame.MAXIMIZED_BOTH) != 0
            && (e.getNewState() & Frame.MAXIMIZED_BOTH) == 0) {
          chirpy.setVisible(true);
          toFront();
        }
      }
    });

    // If if you click on the bird it will give the main window focus
    addWindowFocusListener(new WindowAdapter() {
      public void windowGainedFocus(WindowEvent e) {
        // if the last window that had focus was the bird set the focus on the main window
        if (e.getOppositeWindow() != chirpy) {
          chirpy.toFront();
          toFront();
        }
      }
    });
  }
}
