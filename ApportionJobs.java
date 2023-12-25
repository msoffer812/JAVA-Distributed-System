
import java.util.List;
import java.util.Queue;
/*
 * This thread apportions out jobs to go either to slave a or slave b
 */
public class ApportionJobs extends Thread{
	private Queue<Job> jobsForA;
	private Queue<Job> jobsForB;
	private Queue<Job> jobs;
	private Object jobs_LOCK;
	private Object outgoingA_LOCK;
	private Object outgoingB_LOCK;
	Integer slaveAWorkload;
	Integer slaveBWorkload;
	Object slaveAWorkload_LOCK; 
	Object slaveBWorkload_LOCK;
	
	public ApportionJobs(Object outgoingA_LOCK, Object outgoingB_LOCK, 
			Object jobs_LOCK, Queue<Job> a, Queue<Job> b, Queue<Job> jobs, 
				Integer slaveAWorkload, Integer slaveBWorkload, Object slaveAWorkload_LOCK, 
					Object slaveBWorkload_LOCK)
	{
		this.jobsForA = a;
		this.jobsForB = b;
		this.jobs = jobs;
		this.jobs_LOCK = jobs_LOCK;
		this.outgoingA_LOCK = outgoingA_LOCK;
		this.outgoingB_LOCK = outgoingB_LOCK;
		this.slaveAWorkload = slaveAWorkload;
		this.slaveBWorkload = slaveBWorkload;
		this.slaveAWorkload_LOCK = slaveAWorkload_LOCK; 
		this.slaveBWorkload_LOCK = slaveBWorkload_LOCK;
	}
	
	@Override
	public void run()
	{
		boolean cont = true;
		Job newJob;
		while(cont)
		{
			newJob = null;
			/*
			 * Get a job from the list of jobs
			 */
			synchronized(jobs_LOCK)
			{
				if(!jobs.isEmpty())
				{
					newJob = jobs.remove();
				}
			}

		/*
		 * 
		 *Below is the algorithm to apportion out a new job
		 *Put simply, we are assuming that the outgoing threads to the slaves will get about the same amount of time to run, so the jobs get sent out at an about equal rate.
		 *Therefore, we'll add jobs to the outgoing job containers in a way that they're just about equal - give a job to the one with the least time needed to complete jobs 
		 *
		 */
		if(newJob != null)
		{
			/*
			 * Check if it's the type of job to shut down the system, 
			 * then send out to both slaves, and break the loop
			 */
			if(newJob.shutDownSystem())
	        {
				synchronized(outgoingA_LOCK)
				{
					jobsForA.add(newJob);
				}
				synchronized(outgoingB_LOCK)
				{
					jobsForB.add(newJob);
				}
				cont = false;
				continue;
	        }
			String jobType = newJob.getType();
			String sentContainer = null;
			int timeToComplete;
					/*
					 * We need to apportion jobs based off of jobs time.
					 * 
					 * So first see whether it's a job of type a or b, 
					 * since the time it will take to complete the job varies per slave depending on the job type 
					 * 
					 * Then we check which slave has a smaller workload, and give it to them
					 * 
					 * We also, in doing so, assign the time it will take to complete to the job, 
					 * which it will use both when it's removed from the container and when it is "completed"
					 */
					if(jobType.equals("A"))
					{
						synchronized(slaveAWorkload_LOCK)
						{
							synchronized(slaveBWorkload_LOCK)
							{
								if(slaveAWorkload + 2000 <= slaveBWorkload + 10000)
								{
									slaveAWorkload += 2000;
									timeToComplete = 2000;
									sentContainer = "A";
								}else
								{
									slaveBWorkload += 10000;
									timeToComplete = 10000;
									sentContainer = "B";
								}
							}
						}
					} else
					{
						synchronized(slaveAWorkload_LOCK)
						{
							synchronized(slaveBWorkload_LOCK)
							{
								if(slaveBWorkload + 2000 <= slaveAWorkload + 10000)
								{
									slaveBWorkload += 2000;
									timeToComplete = 2000;
									sentContainer = "B";
								}else
								{
									slaveAWorkload += 10000;
									timeToComplete = 10000;
									sentContainer = "A";
								}
							}
						}
					}
			newJob.setCompleter(sentContainer);
			newJob.setTime(timeToComplete);
			/*
			 * Lock down on outgoing jobs because don't want errors
			 */
			if(sentContainer.equals("A"))
			{
				synchronized(outgoingA_LOCK)
				{
					jobsForA.add(newJob);
				}
			}else
			{
				synchronized(outgoingB_LOCK)
				{
					jobsForB.add(newJob);
				}
			}

			//Print out which job container the job was sent to, for debugging purposes
			System.out.println("Job " + newJob.getID() + " of type " + newJob.getType() + " sent to be sent out to slave " + sentContainer);
			}
		}
		System.exit(0);
	}

}
