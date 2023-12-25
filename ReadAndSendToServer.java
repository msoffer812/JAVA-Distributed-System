
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.InputMismatchException;

public class ReadAndSendToServer extends Thread{
	private ObjectOutputStream requestWriter;
	private BufferedReader stdIn;
	private int jobCount;
	
	public ReadAndSendToServer(ObjectOutputStream requestWriter, BufferedReader stdIn)
	{
		this.requestWriter = requestWriter;
		this.stdIn = stdIn;
	}
	
	@Override
	public void run()
	{
		String userInput;
        Job job = new Job();
        boolean cont;
        try {
        	System.out.println("Waiting for input...");
        	   while (((userInput = stdIn.readLine()) != null)) {
        		   System.out.println("Input Received");
        			do
					{
						cont = false;
						try
						{
							job = new Job(jobCount, userInput);
						}catch(InputMismatchException e)
						{
							System.out.println("Invalid input, please reenter with a job type of a or b, or 'END' to end the program");
							userInput = stdIn.readLine();
							cont = true;
						}
					}while(cont);
				    requestWriter.writeObject(job); // send request to server
				    System.out.println("Job " + job.getID() + ", Type " + job.getType() + " sent to server");
				    jobCount++;
				    /*
				     * check if we should shut down the thread
				     */
				    if(job.shutDownSystem())
        			{
        				break;
        			}
			}
        	   System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
