/**
 * Master program
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MasterServer {
	public static void main(String[] args) throws IOException {

		args = new String[] { "30121" };
		
		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);
		/*
		 * Declare all our containers. 
		 * 
		 * We have two List<Job> to hold incoming jobs from the client before they're apportioned,
		 * and Two OutgoingJobsContainers to hold jobs meant for slave A and slave B once they've been apportioned
		 * 
		 * We also have locks for all our objects, so our threads are synchronized
		 */
		Queue<Job> jobsToComplete = 
				new LinkedList<>();
		Queue<Job> completedJobs = 
				new LinkedList<>();
		Queue<Job> jobsForA = 
				new LinkedList<>();
		Queue<Job> jobsForB = 
				new LinkedList<>();
		Integer slaveAWorkload = 0;
		Integer slaveBWorkload = 0;
		Object slaveAWorkload_LOCK = 
				new Object(); 
		Object slaveBWorkload_LOCK = 
				new Object(); 
		Object jobsToComplete_LOCK = 
				new Object();
		Object completedJobs_LOCK = 
				new Object();
		Object jobsForA_LOCK = 
				new Object();
		Object jobsForB_LOCK = 
				new Object();
		try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				//Accept the sockets from the client and two slaves
				Socket clientSocket = serverSocket.accept();
				Socket slaveASocket = serverSocket.accept();
				Socket slaveBSocket = serverSocket.accept();
				//Send out objects to client and slaves
				ObjectOutputStream responseWriter = 
						new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectOutputStream slaveAOutputStream = 
						new ObjectOutputStream(slaveASocket.getOutputStream());
				ObjectOutputStream slaveBOutputStream = 
						new ObjectOutputStream(slaveBSocket.getOutputStream());
				//Get input from the client and slaves
				ObjectInputStream clientRequestReader = 
						new ObjectInputStream(clientSocket.getInputStream());
				ObjectInputStream slaveARequestReader = 
						new ObjectInputStream(slaveASocket.getInputStream());
				ObjectInputStream slaveBRequestReader = 
						new ObjectInputStream(slaveBSocket.getInputStream());
				) {
				/*
				 * clientInput reads in jobs from client and puts it into list of jobs
				 * 
				 * apportionJobs reads jobs from the list and apportions it out to Slave A or Slave B
				 * 
				 * clientOutput reads completed jobs from the list and sends it back to the client
				 */
				Thread clientInput = 
						new ClientInput(jobsToComplete_LOCK, clientRequestReader, jobsToComplete);
				
				Thread apportionJobs = 
						new ApportionJobs(jobsForA_LOCK, jobsForB_LOCK, jobsToComplete_LOCK, 
								jobsForA, jobsForB, jobsToComplete, slaveAWorkload, slaveAWorkload, 
									slaveAWorkload_LOCK, slaveAWorkload_LOCK);
				Thread outputToSlaveA = 
						new MasterToSlave(jobsForA_LOCK, jobsForA, slaveAOutputStream);
				Thread outputToSlaveB = 
						new MasterToSlave(jobsForB_LOCK, jobsForB, slaveBOutputStream);
				Thread clientOutput = 
						new MasterToClient(completedJobs_LOCK, responseWriter, completedJobs);
				
				Thread slaveAInput = 
						new SlaveInput(slaveARequestReader, completedJobs_LOCK, completedJobs, slaveAWorkload, slaveAWorkload_LOCK);
				Thread slaveBInput = 
						new SlaveInput(slaveBRequestReader, completedJobs_LOCK, completedJobs, slaveBWorkload, slaveBWorkload_LOCK);
				
				clientInput.start();
				apportionJobs.start();
				clientOutput.start();
				outputToSlaveA.start();
				outputToSlaveB.start();
				slaveAInput.start();
				slaveBInput.start();
				
				clientInput.join();
				apportionJobs.join();
				clientOutput.join();
				outputToSlaveA.join();
				outputToSlaveB.join();
				slaveAInput.join();
				slaveBInput.join();
				
				//Close the resources
				serverSocket.close();
				clientSocket.close();
				slaveASocket.close();
				slaveBSocket.close();
				responseWriter.close();
				slaveAOutputStream.close();
				slaveBOutputStream.close();
				clientRequestReader.close();
				slaveARequestReader.close();
				slaveBRequestReader.close();
		} catch (IOException e) {
			System.out.println(
					"Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Program ended, goodbye!");
		System.exit(0);
	}
}
