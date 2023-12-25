/**
 * This thread takes jobs to complete and sends them to the respective slave
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class MasterToSlave extends Thread{
	Object jobs_LOCK;						/*Lock for shared memory of outgoing jobs to slave*/
	Queue<Job> jobs;						/*Shared memory of outgoing jobs to slave*/
	ObjectOutputStream outputStream;		/*Output Stream to send jobs on to slaves*/
	
	/**
	 * Constructor
	 * @param jobs_LOCK
	 * @param jobsForA
	 * @param outputStream
	 */
	public MasterToSlave(Object jobs_LOCK, Queue<Job> jobsForA, ObjectOutputStream outputStream)
	{
		this.jobs_LOCK = jobs_LOCK;
		this.jobs = jobsForA;
		this.outputStream = outputStream;
	}
	
	/**
	 * run section
	 */
	@Override
	public void run()
	{
		Job jobToSend;
		boolean cont = true;
		while(cont)
		{
			jobToSend = null;
			//Make sure the jobs list isn't currently in use
			synchronized(jobs_LOCK)
			{
				if(!jobs.isEmpty())
				{

					jobToSend = jobs.remove();
				}
			}
			//Making sure we have an actual job to send
			if(jobToSend != null)
			{
				try {
					//Write that sending job to the slave
					System.out.println("Sending Job " + jobToSend.getID() + " of type " + jobToSend.getType() + " to Slave " + jobToSend.completer());
					//Write out the object to the slave
					outputStream.writeObject(jobToSend);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/*
				 * Check if we want to shut down the system
				 */
				if(jobToSend.shutDownSystem())
				{
					cont = false;
				}
			}
		}
		System.exit(0);
	}
}
