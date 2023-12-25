
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.List;
import java.util.Queue;
//gets input from clients
public class ClientInput extends Thread{
	ObjectInputStream requestReader;
	Queue<Job> jobs;
	Object jobs_LOCK;
	
	public ClientInput(Object jobsl, ObjectInputStream requestReader, Queue<Job> jobs)
	{
		this.requestReader = requestReader;
		this.jobs = jobs;
		jobs_LOCK = jobsl;
	}
	
	@Override
	public void run()
	{
		Job newJob;
		try {
			boolean cont = true;
			while (((newJob = (Job)requestReader.readObject()) != null) && cont) {
			        System.out.println("Job " + newJob.getID() + " received, of type " + newJob.getType());
			        /*
			         * Check if we should shut down program
			         */
			        if(newJob.shutDownSystem())
			        {
			        	cont = false;
			        }
			        synchronized(jobs_LOCK)
			        {
			        	jobs.add(newJob);
			        }
			}
			System.exit(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
