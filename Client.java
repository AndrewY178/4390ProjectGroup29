import java.io.*;
import java.net.*;

class Client {

    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;

        System.out.println("Client is running: " );

        // create client socket
        int port;
        try {
          port = Integer.parseInt(argv[0]);
        }
        catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + argv[0]);
            System.exit(1);
            return;
        }

        Socket clientSocket = new Socket("127.0.0.1", port);

        // allows input from the user
        BufferedReader inFromUser =
          new BufferedReader(new InputStreamReader(System.in));

        // allows input from the server
        BufferedReader inFromServer =
                new BufferedReader(new
                InputStreamReader(clientSocket.getInputStream()));

        // allows send data to the server
        DataOutputStream outToServer =
          new DataOutputStream(clientSocket.getOutputStream());

          // initial message from client to server
          System.out.println("Enter client name: ");  // ask user for username
          sentence = inFromUser.readLine(); // waits for user input
          // prepend "JOIN" to username to match message format
          sentence = "JOIN|" + sentence; 
          
          // sends inital message to server
          outToServer.writeBytes(sentence + '\n');

          // initial response from server
          modifiedSentence = inFromServer.readLine();

          // if ACK is not received, resend sentence 
          // might change, use timer instead
          /* 
          while(modifiedSentence.compareTo("ACK" ) != 0)
          {
              outToServer.writeBytes(sentence + '\n');
              modifiedSentence = inFromServer.readLine();
              //displays response from server
              System.out.println("FROM SERVER: " + modifiedSentence);
          } */

          // if one server doesn't return ACK, failed connection
          if(!modifiedSentence.startsWith("ACK"))
          {
              System.out.println("Connection to server failed");
              clientSocket.close();
              return; // end program
          }
          else // successful connection
          {
            //displays response from server
            System.out.println("Successful connection");
            System.out.println("FROM SERVER: " + modifiedSentence);
          }
            
          // loop for equations
          while(true)
            {
                System.out.println("Enter equation: ");  
                sentence = inFromUser.readLine(); // waits for user input

                if (sentence.equalsIgnoreCase("EXIT")) {
                  outToServer.writeBytes("EXIT\n");
                  modifiedSentence = inFromServer.readLine(); // read BYE|name
                  System.out.println("FROM SERVER: " + modifiedSentence);
                  break;
                }
          
                outToServer.writeBytes("CALC|" + sentence + '\n');
                modifiedSentence = inFromServer.readLine();
                System.out.println("FROM SERVER: " + modifiedSentence);
            }

            clientSocket.close();

        }
      }

