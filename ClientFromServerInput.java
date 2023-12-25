
import java.io.IOException;
import java.io.ObjectInputStream;

public class ClientFromServerInput extends Thread{
	ObjectInputStream responseReader;
	
	public ClientFromServerInput(ObjectInputStream responseReader)
	{
		this.responseReader = responseReader;
	}
	
	@Override
	public void run()
	{
		Job completedJob;
		boolean cont = true;
		try {
			while (((completedJob = (Job)responseReader.readObject()) != null) && cont) {
				/*
				 * check if we want to shut down thread
				 */
				if(completedJob.shutDownSystem())
				{
					break;
				}
				if(completedJob.isComplete())
			    {
					System.out.println("Job " + completedJob.getID() + " of type " + 
								completedJob.getType() + " was completed");
			    }else
			    {
			    	System.out.println("Job " + completedJob.getID() + " of type " + 
			    				completedJob.getType() + " wasn't completed, but was resent");
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
