
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Client {
	public static void main(String[] args) throws IOException, InterruptedException {
        
		// Hardcode in IP and Port here if required
    	args = new String[] {"127.0.0.1", "30121"};
    	
        if (args.length != 2) {
            System.err.println(
                "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        try (
            Socket clientSocket = new Socket(hostName, portNumber);
        	ObjectOutputStream requestWriter = // stream to write jobs to server
                new ObjectOutputStream(clientSocket.getOutputStream());
        	ObjectInputStream responseReader= // stream to read completed Jobs from server
                new ObjectInputStream(clientSocket.getInputStream());
            BufferedReader stdIn = // standard input stream to get user's requests
                new BufferedReader(
                    new InputStreamReader(System.in))
        ) {
        	Thread out = new ReadAndSendToServer(requestWriter, stdIn);
            Thread masterIn = new ClientFromServerInput(responseReader);
            
            masterIn.start();
            out.start();
            
            out.join();
            masterIn.join();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException | InterruptedException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
        System.out.println("Program ended, goodbye!");
    }
}
