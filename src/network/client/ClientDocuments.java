package network.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;

/**
 * Holds all of the JFrame content
 * @author Justin
 *
 */
public class ClientDocuments {

  private WebEngine friendEngine;
  private WebEngine chatEngine;
  private String name;

  public ClientDocuments(WebEngine friendEngine, WebEngine chatEngine) {
    this.friendEngine = friendEngine;
    this.chatEngine = chatEngine;

    // Creates the base layout to have nodes appended to for the friends panel.
    friendEngine.loadContent(
        "<html><head></head><body id='body'><div id='title'>ONLINE</div></body></html>");
    friendEngine.setUserStyleSheetLocation(getClass().getResource("users.css").toExternalForm());

    // Creates the base layout to have nodes appended to for the chat panel.
    chatEngine.loadContent("<html><head></head><body id='body'></body></html>");
    chatEngine.setUserStyleSheetLocation(getClass().getResource("chat.css").toExternalForm());

  }
  /**
   * Takes in a user name to add to the JFX Panel
   * @param user
   */
  public void addUserToPanel(String user) {

    // Loads the page to enable writing to the DOM.
    friendEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == State.SUCCEEDED) {
        Document userDoc = friendEngine.getDocument(); // Get Panal Contents
        Element body = userDoc.getElementById("body"); // Grab the body tag

        Element box = userDoc.createElement("div");// Create a new Div
        box.setAttribute("id", user); // Add an id to the Div equal to the username
        box.setAttribute("class", "user"); // Set the Div class

        box.appendChild(userDoc.createTextNode(user)); // Create a text node inside the Div
        
        // Go through the current list of the body's child nodes and insert the new Div in the
        // correct spot alphabetically.
        NodeList child = body.getChildNodes();
        for (int i = 1; i <= child.getLength(); i++) {
          if (child.getLength() == i) {
            body.insertBefore(box, child.item(i));
            break;
          }
          String userDiv = child.item(i).getAttributes().getNamedItem("id").getNodeValue();
          if (0 > user.compareTo(userDiv)) {
            body.insertBefore(box, child.item(i));
            break;
          }
        }
      }

    });

  }

  /**
   * Takes in a user to remove from the JFXPanel
   * @param user
   */
  public void removeFromUserPanel(String user) {

    friendEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
      if (newState == State.SUCCEEDED) {
        Document userDoc = friendEngine.getDocument(); // Get document
        Element body = userDoc.getElementById("body"); // Get body
        Element userDiv = userDoc.getElementById(user);
        body.removeChild(userDiv); // Remove Body's child with the id of the username
      }
    });
  }

  /**
   * Takes in some paramaters to determing the format of the text that is placed on the JFXPanel
   * 
   * @param isMe is the message from me
   * @param name name of the sender
   * @param text from the sender
   */
  public void addTextToChat(boolean isMe, String name, String text) {
    String arrow; // Holds the image direction style of the arrow from the chat bubble
    String bubble; // Sets id for Bubble's CSS style.
    String span; // Holds and positions the arrow image
    String image; // Image location for the
    String userImage; // File Location of the Face Icon
    this.name = name;
    
    // Switches between the style for my messages and the incoming messages
    if (isMe) {
      this.name = "";
      arrow = "imgArrowLeft";
      bubble = "myChatBubble";
      span = "myChatSpan";
      image = "images\\leftArrow.png";
      userImage = "images\\face1.png";
    } else {
      arrow = "imgArrowRight";
      bubble = "otherChatBubble";
      span = "otherChatSpan";
      image = "images\\rightArrow.png";
      userImage = "images\\face8.png";
    }
    try {
      chatEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
        if (newState == State.SUCCEEDED) {
          Document chatDoc = chatEngine.getDocument(); // Get chat panel document
          Element body = chatDoc.getElementById("body"); // Get body Element by it's id

          Element userIcon = chatDoc.createElement("img"); // Creates an image node
          // Sets the image source equal to the userImage file location. (Currently face1-face9.png exist).
          userIcon.setAttribute("src", getClass().getResource(userImage).toExternalForm());
          userIcon.setAttribute("class", "userIcon");

          Element iconSpan = chatDoc.createElement("span"); // Creates a span to hold the user Icon

          Element chatSpan = chatDoc.createElement("span"); // Creates a spen to hold all of the chat content
          chatSpan.setAttribute("class", span);
          
          Element timeStamp = chatDoc.createElement("div");
          timeStamp.setAttribute("id", "timeStamp");
          
          timeStamp.appendChild(chatDoc.createTextNode(getTimeStamp()));

          Element arrowSpan = chatDoc.createElement("span"); // Holds the arrow image for the direction
          arrowSpan.setAttribute("class", "chatArrow");

          Element img = chatDoc.createElement("img"); // The Arrow Image
          img.setAttribute("src", getClass().getResource(image).toExternalForm());
          img.setAttribute("class", arrow);

          Element nameDiv = chatDoc.createElement("div");
          nameDiv.setAttribute("class", "username");
          nameDiv.appendChild(chatDoc.createTextNode(name.toUpperCase()));
          
          Element bubbleDiv = chatDoc.createElement("div"); // The bubble that holds the Chat text
          bubbleDiv.setAttribute("class", bubble);

          // Adds all of the nodes together
          iconSpan.appendChild(userIcon);
          arrowSpan.appendChild(img);
          bubbleDiv.appendChild(chatDoc.createTextNode(text));

          if (isMe) {
            chatSpan.appendChild(iconSpan);
            chatSpan.appendChild(arrowSpan);
            chatSpan.appendChild(bubbleDiv);
            chatSpan.appendChild(timeStamp);
            body.appendChild(chatSpan);
          } else {
            chatSpan.appendChild(timeStamp);
            chatSpan.appendChild(bubbleDiv);
            chatSpan.appendChild(arrowSpan);
            chatSpan.appendChild(iconSpan);
            body.appendChild(chatSpan);
            body.appendChild(nameDiv);
          }
          
          //Scrolls to the bottom of the window.
          chatEngine.executeScript("window.scrollTo(0,document.body.scrollHeight)");
        }
      }); // addListener()
    } catch (Exception e) {
      System.out.println(e);
    }
  }
  
  // Time stamp the chat
  public String getTimeStamp() {
    DateFormat dateTime = new SimpleDateFormat(" h:mm a");
    String date = dateTime.format(new Date());

    return date;
  }
}
