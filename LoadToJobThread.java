package finaleVersion;

import java.io.*;
import java.util.*;

import java.util.concurrent.*;

public class LoadToJobThread implements Runnable {

	private String filename; // the file name that contains the jobs
	private Scanner stream1; // to read from file
	private ConcurrentLinkedQueue<PCB> jobQueue; // the job queue which is from the main to store the 
												// processes in it. 
	
	static int numOfProcesses; // number of processes in the jobQueue

	public LoadToJobThread(String filename, ConcurrentLinkedQueue<PCB> jq) {
		
		// initializations
		this.filename = filename;
		stream1 = null;
		jobQueue = jq;
		numOfProcesses = 0;
	}

	@Override
	// it runs when the thread starts
	public void run() {
		readFile();
	}
	// methods that load the jobs from the file to the job Queue
	public void readFile() {
		
		try {
			stream1 = new Scanner(new File(filename));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("the file could not be opened successfully");
			System.exit(0);
		}

		
		// file is ready to be read from

		int pid; // process id
		int burstTime; // process burst time
		double size; // process required memory

		Scanner inline;
		String ReadLine = null; 
		// read each processes from the file and add it to the job queue if its size is acceptable
		while (stream1.hasNextLine()) {
			numOfProcesses++;
			ReadLine = stream1.nextLine();
			inline = new Scanner(ReadLine);
			// set delimiters to ; and :
			inline.useDelimiter(":|;");
			if (inline.hasNext()) {
				pid = Integer.parseInt(inline.next());
				burstTime = Integer.parseInt(inline.next());
				size = Double.parseDouble(inline.next());
				if (size <= MainThread.MEMORY_SIZE) {
					loadPCBToJobQueue(pid, burstTime, size);
				}
			}
		}
		stream1.close();

	}

	
	// creates PCB's for processes and loads them into job queue
	public void loadPCBToJobQueue(int id, int burstTime, double size) {
		PCB process = new PCB(id, burstTime, size);
		process.setState("new");	
		jobQueue.add(process); 
	}

	// getter
	public static int getCountOfProcesses() {
		return numOfProcesses;
	}

	
}
