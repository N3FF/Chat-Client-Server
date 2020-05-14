package network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import network.common.ChatProtocol;
import network.common.MessageHandler;

public class Client {
  private JFrame ui;
  private JTextField textBox;
  private BufferedReader in;
  private PrintWriter out;
  private String name;
  private WebEngine friendEngine;
  private WebEngine chatEngine;
  private JFXPanel friendsPanel;
  private JFXPanel textPane;
  private MessageHandler msgFormat = new MessageHandler();
  private Set<String> users = new HashSet<>();

  /**
   * Starts Swing thread
   */
  private void go() {
    // Starts Swing Thread content
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        
        ui = new ClientUI(); // Builds the window
        ui.setVisible(true); // Once it's completed make it visible
        textPane = ((ClientUI) ui).getTextPane();
        friendsPanel = ((ClientUI) ui).getFriendPanel();
        textBox = ((ClientUI) ui).getTextBox();
        textBox.addActionListener((e) -> toServer(textBox.getText().trim()));
        Platform.runLater(() -> startFX()); // Start FX Thread
      }

    });
    // Prompts for IP Address.
    getServerAddress();
    try {
      // Open connection to the server
      openConnection();
    } catch (IOException e) {
      // If there was a problem connecting to the server do this.
      textBox.setEnabled(false);
      textBox.setText("You are not currently connected to the server");
    }
  }

  /**
   * Starts Main Thread
   * @param args
   */
  public static void main(String[] args) {
    Client client = new Client();
    client.go();
  }

  private void startFX() {
    //Creates WebViews to have HTML written to
    WebView friendsView = new WebView();
    friendEngine = friendsView.getEngine();
    WebView chatView = new WebView();
    chatEngine = chatView.getEngine();

    // Create FX Scenes
    friendsPanel.setScene(new Scene(friendsView));
    textPane.setScene(new Scene(chatView));

    //Start
    new ClientDocuments(friendEngine, chatEngine);
  }

  // Add text to chatbox through the JavaFX Thread
  private void toTextBox(boolean isMe, String name, String text) {
    if (text != "") {
      Platform
          .runLater(() -> new ClientDocuments(friendEngine, chatEngine).addTextToChat(isMe, name, text));
    }
  }

  // Add Users to User Panel through the JavaFX Thread
  private void toUserList(String text) {
    if (text != "") {
      Platform
          .runLater(() -> new ClientDocuments(friendEngine, chatEngine).addUserToPanel(text));
    }
  }
  
  // Remove User from User Panel through the JavaFX Thread
  private void removeFromUserList(String text) {
    if (text != "") {
      Platform
          .runLater(() -> new ClientDocuments(friendEngine, chatEngine).removeFromUserPanel(text));
    }
  }

  // Sends Messages to the server
  private void toServer(String text) {
    textBox.setText("");
    out.println(ChatProtocol.MESSAGE + "!" + name + "@" + text);
  }

  // Opens the connection to the server
  private void openConnection() throws IOException {
    // Make connection and initialize streams
    String serverAddress = getServerAddress();
    @SuppressWarnings("resource") //Must suppress for my sanity
    Socket socket = new Socket(serverAddress, ChatProtocol.PORT);
    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    out = new PrintWriter(socket.getOutputStream(), true);

    String lineIn = in.readLine();
    // If the message that is received is NICK, then prompt for name
    if (lineIn.equals(ChatProtocol.NICK)) {
      name = getUserName(); // Store Username picked
      out.println(ChatProtocol.NICK + " " + name); // Send name to server
    }
    while (!lineIn.startsWith(ChatProtocol.ONLINE)) {
      lineIn = in.readLine(); // Wait to see if others are online
    }
    // Reads in the list of online users
    String[] onlineUsers = (lineIn.substring(ChatProtocol.TRIM_COMMAND)).split(",");
    for (String user : onlineUsers) {
      users.add(user);
      // Sends the new list to the online users panel
      SwingUtilities.invokeLater(() -> toUserList(user));
    }

    while (true) {
      String line = in.readLine();
      //If someone Joins add them to the online user list
      if (line.startsWith(ChatProtocol.JOIN)
          && users.add(line.substring(ChatProtocol.TRIM_COMMAND))) {
        SwingUtilities
            .invokeLater(() -> toUserList(line.substring(ChatProtocol.TRIM_COMMAND)));
      }
      //If someone leaves remove them from the online user list
      if (line.startsWith(ChatProtocol.PART)) {
        SwingUtilities
            .invokeLater(() -> removeFromUserList(line.substring(ChatProtocol.TRIM_COMMAND)));
      }
      //If it is a message determine if it was sent from me or someone else.
      if(line.startsWith(ChatProtocol.MESSAGE)){
        String user = msgFormat.getName(line); // Picks the username out of the String.
        boolean isMe = user.equals(name) ? true : false;

        SwingUtilities.invokeLater(() -> toTextBox(isMe, user, msgFormat.getMessage(line)));
      }
    }
    // socket.close();
  }

  private String getServerAddress() {
    return "localhost"; /* Set To localhost to I don't have to keep typing it in. Uncomment for prompt
    
                         * JOptionPane.showInputDialog(frame, "Enter IP Address of the Server:",
                         * "Welcome to Chirply", JOptionPane.QUESTION_MESSAGE);
                         */
  }

  // Username prompt used to create username for the server
  private String getUserName() {
    String user = "";
    while (user.length() < 1 || user.contains("@")) { //Make sure there is no "@" in the name and it's not empty
      user = JOptionPane.showInputDialog(ui, "Choose a screen name:", "Screen name selection",
          JOptionPane.PLAIN_MESSAGE);
      // If you hit cancel just exit the program.
      if (user == null) {
        System.exit(0);
      }
    }
    // Set window Title
    ui.setTitle(user + "'s Chat Client");
    
    // Once the username has been set, enable the textbox and give it focus
    ((ClientUI) ui).getTextBox().setEditable(true);
    ((ClientUI) ui).getTextBox().requestFocus();
    return user;
  }
}
