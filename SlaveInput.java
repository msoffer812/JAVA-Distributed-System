/**
 * Receives the completed jobs from slave and puts into shared memory to send to client
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Queue;

public class SlaveInput extends Thread{
	ObjectInputStream jobReader;		/*Reads completed jobs from slaves*/
	Object completedJobs_LOCK;			/*Dummy Object to lock down on completed jobs*/
	Queue<Job> completedJobs;			/*Actual shared memory to put complete jobs in to send to client*/
	Integer slaveWorkload;
	Object slaveWorkload_LOCK;
	
	/**
	 * Constructor
	 * @param jobReader
	 * @param completedJobs_LOCK
	 * @param completedJobs
	 * @param slaveWorkload
	 * @param slaveWorkload_LOCK
	 */
	public SlaveInput(ObjectInputStream jobReader, 
			Object completedJobs_LOCK, Queue<Job> completedJobs, 
				Integer slaveWorkload, Object slaveWorkload_LOCK)
	{
		this.jobReader = jobReader;
		this.completedJobs_LOCK = completedJobs_LOCK;
		this.completedJobs = completedJobs;
		this.slaveWorkload = slaveWorkload;
		this.slaveWorkload_LOCK = slaveWorkload_LOCK;
	}
	
	/**
	 * Running section
	 */
	@Override
	public void run()
	{
		Job job;	/*Job declaration, this is the job that you get from the slave*/
    	try {
			while ((job = (Job)jobReader.readObject()) != null) {
				System.out.println("Job received from Slave " + job.completer());
				/*
				 * Put received job into shared memory
				 */
				synchronized(completedJobs_LOCK)
				{
					completedJobs.add(job);
				}
				/*
				 * If job is to shut down system, 
				 * end loop
				 */
				if(job.shutDownSystem())
				{
					break;
				}
				synchronized(slaveWorkload_LOCK)
				{
					slaveWorkload -= job.getTime();
				}
			}
			System.exit(0);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
	