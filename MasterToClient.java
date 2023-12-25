/**
 * Master sends completed jobs back to client so the client can see
 * it was completed
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;
import java.util.Queue;

public class MasterToClient extends Thread{
	private ObjectOutputStream responseWriter;
	private Queue<Job> finishedJobs;
	Object completedJobs_LOCK;
	
	public MasterToClient(Object completedJobs_LOCK, ObjectOutputStream p, Queue<Job> jobs)
	{
		responseWriter = p;
		finishedJobs = jobs;
		this.completedJobs_LOCK = completedJobs_LOCK;
	}
	
	@Override
	public void run()
	{
		 Job job;
         try 
		 {
				while (true) 
				{
					job = null;
					//must be synchronized so no two threads access the queue at once
					synchronized(completedJobs_LOCK)
			        {
						if(!finishedJobs.isEmpty())
						{
							job = finishedJobs.remove();
						}	
			        }
					
					//Only send the job if you've accessed a valid one 
					if(job != null)
					{
					    responseWriter.writeObject(job); // send job back to client
					    /*
					     * Check if we need to 
					     * end loop and
					     * shut down thread
					     */
					    if(job.shutDownSystem())
					    {
					    	break;
					    }
					    if(job.isComplete())
					    {
					    	System.out.println("Job " + job.getID() + " is completed, sent to client");
					    }else
					    {
					    	System.out.println("Job " + job.getID() + " wasn't completed, sending back to client");
					    }
					}
			    }
				System.exit(0);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
