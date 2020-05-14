package network.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import network.common.ChatProtocol;

public class Server {
  /**
   * The set of all the print writers for all the clients. This set is kept so we can easily
   * broadcast messages.
   */
  // private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
  private static Map<String, HashSet<String>> friends = new HashMap<String, HashSet<String>>();
  private static Map<String, PrintWriter> writers = new HashMap<>();

  public static void main(String[] args) throws Exception {
    System.out.println("The chat server is running.");
    ServerSocket listener = new ServerSocket(ChatProtocol.PORT);
    try {
      while (true) {
        new Handler(listener.accept()).start();
      }
    } finally {
      listener.close();
    }
  }

  private static class Handler extends Thread {
    private String name; // the accepted name
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    /**
     * Constructs a handler thread, squirreling away the socket. All the interesting work is done in
     * the run method once the thread is spawned.
     */
    public Handler(Socket socket) {
      this.socket = socket;
    }

    /**
     * Services this thread's client by repeatedly requesting a screen name until a unique one has
     * been submitted, then acknowledges the name and registers the output stream for the client in
     * a global set, then repeatedly gets inputs and broadcasts them.
     */
    public void run() {
      try {

        // Create character streams for the socket.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Request a name from this client. Keep requesting until
        // a name is submitted that is not already used. Note that
        // checking for the existence of a name and adding the name
        // must be done while locking the set of names.
        while (true) {
          out.println(ChatProtocol.NICK);
          name = in.readLine();
          if (name == null || !name.startsWith(ChatProtocol.NICK)) {
            return;
          } else {
            // remove the command
            name = name.substring(ChatProtocol.TRIM_COMMAND);

            synchronized (writers) { // make sure no-one else is changing
              // the set
              if (!writers.containsKey(name)) {
                synchronized (writers) {
                  writers.put(name, out);
                }
                synchronized (friends) {
                  friends.put(name, new HashSet<String>());
                }
                break; // can stop requesting the name now
              }
            }
          }
        }

        // Now that a successful name has been chosen, add the
        // socket's print writer to the set of all writers so
        // this client can receive broadcast messages.
        send(true, ChatProtocol.JOIN + "!" + name);

        // Builds a String of online users to send to new connections
        Iterator<String> iter = writers.keySet().iterator();
        StringBuilder users = new StringBuilder();
        users.append(ChatProtocol.ONLINE + "!");
        while (iter.hasNext()) {
          users.append(iter.next());
          users.append(',');
        }
        out.println(users.toString());

        // Accept messages from this client and broadcast them.
        // Ignore other clients that cannot be broadcasted to.
        while (true) {
          String input = in.readLine();
          if (input == null || !input.startsWith(ChatProtocol.MESSAGE)) {
            return;
          }
          // Trims off the command from the front of the String
          String command = input.substring(0, ChatProtocol.TRIM_COMMAND - 1).trim();
          String subCommand = "";

          // Sub Command was created to handle creating friends lists. Since I had an issue
          // retrieving the information from the JavaFX Panel. If you type !PALS followed by
          // a space and then the comma seperated name values it will create a freinds list for the
          // sender. ie "!PALS Tim,Sue,Mark"

          if (input.length() > (ChatProtocol.TRIM_COMMAND * 2) + 2 + name.length())
            subCommand = input.substring(input.indexOf('@') + 2,
                input.indexOf('@') + ChatProtocol.TRIM_COMMAND + 1);
          if (subCommand.equals(ChatProtocol.FRIENDS)) {
            System.out.print(name + "'s friends are: ");
            HashSet<String> set = new HashSet<>();
            String[] names =
                input.substring(input.indexOf('@') + ChatProtocol.TRIM_COMMAND + 2).split(",");
            for (String name : names) {
              System.out.print(name + " ");
              set.add(name);
            }
            synchronized (friends) {
              friends.put(name, set);
            }
            // If you receive a PART Command remove the user from the list
          } else if (command.equals(ChatProtocol.PART)) {
            send(true, ChatProtocol.MESSAGE + "!" + name);
            if (name != null) {
              send(true, ChatProtocol.PART + "!" + name);
              synchronized (writers) {
                writers.remove(name);
              }
            }
            try {
              socket.close();
            } catch (IOException e) {
            }
            return;
          } else {
            send(true, input);
          }
        }
      } catch (IOException e) {
        System.out.println(e);
      } finally {
        // This client is going down! Remove its name and its print
        // writer from the sets, and close its socket.
        if (name != null) {
          send(true, ChatProtocol.PART + "!" + name);
          synchronized (writers) {
            writers.remove(name);
          }
        }
        try {
          socket.close();
        } catch (IOException e) {
        }
      }
    }

    private void send(boolean toAll, String input) {
      // Iterates through PrintWriters
      Iterator<Entry<String, PrintWriter>> users = writers.entrySet().iterator();
      while (users.hasNext()) {
        Entry<String, PrintWriter> user = users.next();
        // Sends to everyone. In the console it will show A: show it was sent to everyone
        if (toAll) {
          user.getValue().println(input);
          System.out.println("A:" + name + "->" + user.getKey());
          // Only Sends to people on the friends list
        } else if (friends.containsKey(name)) {
          if (friends.get(name).contains(user.getKey()) || name.equals(user.getKey())) {
            user.getValue().println(input);
            System.out.println("F:" + name + "->" + user.getKey());
          }
        }
      }
    }
  }
}
