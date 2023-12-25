
import java.io.Serializable;
import java.util.InputMismatchException;
/*
 * This is a 'Job' class, which represents jobs to complete
 * It's serializable, so we can send it over input streams to other programs
 * 
 * It contains an id(it's job number), a 
 */
public class Job implements Serializable{
	private static final long serialVersionUID = 1L;	/* To serialize it over pipeline */
	private final int id;								/* Job's unique id which identifies it, auto increments in the implementation*/
	private boolean completed;							/* Whether job has been completed, etc. */
	private String type;								/* A, B, or end */
	private int timeToComplete;							/* How long thread should sleep while completing */
	private String completer;							/* Which slave completed the job */
	private boolean jobEnder;							/* Is this a job that shuts down the program? */
	
	public Job(int id, String type)
	{
		if(!(type.toUpperCase().equals("A")) && !(type.toUpperCase().equals("B")) 
				&& !(type.toUpperCase().equals("END")))
		{
			throw new InputMismatchException("Must be either 'a', 'b', or 'end");
		}else
		{
			this.id = id;
			this.type = type.toUpperCase();
			completed = false;
			if(this.type.equals("END"))
			{
				jobEnder = true;
			}
		}
	}
	public Job()
	{
		this.id =0;
		this.type = "";
		completed = false;
	}
	public void setTime(int time)
	{
		this.timeToComplete = time;
	}
	public void setCompleter(String slave)
	{
		this.completer = slave;
	}
	public String completer()
	{
		return this.completer;
	}
	public int getTime()
	{
		return timeToComplete;
	}
	public void setType(String type)
	{
		if(!(type.toUpperCase().equals("A")) && !(type.toUpperCase().equals("B")))
		{
			throw new InputMismatchException("Must be either 'a' or 'b'");
		}else
		{
			this.type = type.toUpperCase();
		}
	}
	public void complete()
	{
		try {
			Thread.sleep(timeToComplete);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		completed = true;
	}
	public int getID()
	{
		return this.id;
	}
	public String getType()
	{
		return this.type;
	}
	public boolean isComplete()
	{
		return completed;
	}
	public boolean shutDownSystem()
	{
		return this.jobEnder;
	}
}
