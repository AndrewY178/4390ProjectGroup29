import java.io.*;
import java.net.*;

class Client {

    public static void main(String argv[]) throws Exception
    {
        String sentence;
        String modifiedSentence;

        System.out.println("Client is running: " );

        // create client socket
        Socket clientSocket = new Socket("127.0.0.1", 6789);

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
          while(!sentence.equals("EXIT"))
            {
                System.out.println("Enter equation: ");  
                sentence = inFromUser.readLine(); // waits for user input

                outToServer.writeBytes("CALC|" + sentence + '\n');
                modifiedSentence = inFromServer.readLine();
                System.out.println("FROM SERVER: " + modifiedSentence);
            }

            clientSocket.close();

        }
      }

