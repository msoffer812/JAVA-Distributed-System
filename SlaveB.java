
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SlaveB {
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
            Socket slaveSocket = new Socket(hostName, portNumber);
        		ObjectOutputStream outputStream =		/*Send completed jobs back out*/
        				new ObjectOutputStream(slaveSocket.getOutputStream());
        		ObjectInputStream inputStream = 		/*Send completed jobs back out*/
        				new ObjectInputStream(slaveSocket.getInputStream());
        ) {
        	/*
        	 * No need for threads, as we want the whole program to sleep when completing a job
        	 * we get a job, complete it, and send it back
        	 */
        	Job job;
        	while ((job = (Job)inputStream.readObject()) != null) {
        		/*
        		 * Write out that got the job
        		 */
        		System.out.println("Job " + job.getID() + " of type " + job.getType() + " received");
        		
        		/*
        		 * Complete the job
        		 */
				job.complete();
				
				/*
				 * Write out that job was completed
				 */
				System.out.println("Job " + job.getID() + " of type " + job.getType() + " completed, sending back to server");
				/*
				 * Send the job back over to the server
				 */
				outputStream.writeObject(job);
				/*
				 * Check if the job is saying to shut down system
				 */
				if(job.shutDownSystem())
				{
					break;
				}
        	}
        	/*
        	 * Close resources and say goodbye
        	 */
        	inputStream.close();
        	outputStream.close();
        	slaveSocket.close();
        	System.out.println("Program ended, goodbye!");
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }	
}
