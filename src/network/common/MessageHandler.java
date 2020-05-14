package network.common;

/**
 * Formats and extracts information from the string passed in. Not enough time to fully implement.
 * 
 * @author Justin
 *
 */
public class MessageHandler {

  // Takes in the line from the server and returns the text
  public String getMessage(String text) {
    return text.substring(text.indexOf('@') + 1);
  }

  // Takes in a string and returns the sender's name
  public String getName(String text) {
    int a = text.indexOf('!');
    int b = text.indexOf('@');
    return (a > 0 && b > a) ? text.substring(a + 1, b) : "";
  }
}
