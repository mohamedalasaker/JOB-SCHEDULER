package finaleVersion;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



public class LoadToReadyThread implements Runnable {

	// queues to store the address of the queues that came from the main thread
	private ConcurrentLinkedQueue<PCB>readyQueue;
	private ConcurrentLinkedQueue<PCB> jobQueue;
	private PriorityQueue<PCB> sjfQueue;
	private int choiceOfQueue; // to choose between sjfQueue (1) & jobQueue (0)

	usedMemoryInt usedMemory; // to store the address of the object that 
							  //monitor the usedMemory which is received by the main method (main thread)
	
	static int count; // (size of the jobQueue after finishing the transfer to ready queue)

	
	
	public LoadToReadyThread(ConcurrentLinkedQueue<PCB> rq, ConcurrentLinkedQueue<PCB> jobQueue, usedMemoryInt um, PriorityQueue<PCB> sjfq,
			int choice) {
		
		// some initializations
		count = jobQueue.size(); 
		this.jobQueue = jobQueue;
		readyQueue = rq;
		sjfQueue = sjfq;
		choiceOfQueue = choice;
		usedMemory = um;
	}

	
	// the method that runs when the thread is started
		@Override
		public void run() {
			
			// if we choose FCFS OR Round Robin
			if (choiceOfQueue == 0) {
				
				// continuously check for available space then add the job to the ready queue 
				// until the jobs are finished			
				PCB p = jobQueue.poll();
				while (count != 0) {
					if (p != null && loadToReadyQueue(p)) {
						p = jobQueue.poll();
					}
				}

			} else {
				
				// if we choose SJF
				
				// continuously check for available space then add the job to the ready queue 
				// until the jobs are finished

				PCB p = sjfQueue.poll();

				while (count != 0) {
					if (p != null && loadToReadyQueue(p)) {
						p = sjfQueue.poll();

					}
				}

			}
		}
	
	
	// to check if there is available memory
	public double checkFreeMemory() {
		return MainThread.MEMORY_SIZE - usedMemory.getUm();
	}
	
	
	// this method loads the processes from the job queue to the ready queue
	public boolean loadToReadyQueue(PCB p) {
		
		// if there is enough space load it to the ready queue  
		double freeMem = checkFreeMemory();
		if (p.getSize() <= freeMem) {			
			usedMemory.add(p.getSize());
			readyQueue.add(p);
			p.setState("Ready");
			count--; 
			return true;
			
		} else {
			// no available space in memory
			return false;
		}
		

	}

	// getter
	public static int getCount() {
		return count;
	}

	
	

}
