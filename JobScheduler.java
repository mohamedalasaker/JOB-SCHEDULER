package finaleVersion;

import java.util.Collection;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.*;


//HERE ARE THE CPU SHCEDULING ALGORITHMS
public class JobScheduler {

	// takes the reference of the queue from main thread and store it here
	private ConcurrentLinkedQueue<PCB> readyQueue;
	
	// here we store the usedMemory object which came from the main thread 
	// to monitor the used Memory
	usedMemoryInt usedMemory;

	
	public JobScheduler(ConcurrentLinkedQueue<PCB> rq, usedMemoryInt um) {
		
		// initializations
		readyQueue = rq;
		usedMemory = um;
	}

	
	// FIRST COME FIRST SERVED ALGORITHM
	// it returns a list which has at index[0] the average waiting time
	// and at index[1] the average turn around time
	public double[] fcfs() {
		
		// a list to contain running processes intervals to print the Gantt chart
		List<int[]> L = new LinkedList<int[]>(); // list of three-element arrays

		// to store WT & TA times: wt[0] is waiting time, wt[1] is turn around time
		double[] wt = new double[2];
		
		// counters for calculating the averages
		int countwt = 0, countta = 0;
		
		// to store the time-line of the Scheduling
		double time = 0; 
		
		// if there are remaining processes in the ready or job queues
		// continue Scheduling
		while (LoadToReadyThread.getCount() != 0 || readyQueue.size() != 0) {
			
			PCB p = null;

			// array of three elements which 
			//stores (proccesesId, BurstTime before the processes becomes running, 
			//and the remaining burst time after running )
			int[] arr; 
			
			// while there are processes in the ready queue 
			while (readyQueue.size() != 0) {
				
				// take a processes and run it.
				p = readyQueue.poll();
				
				if(p != null) {
					
					// note we set the whole burst time because it is FCFS 
					
					// set waiting time
					p.setWaitingTime(time);
					
					// add the burst Time to the time-line
					time += p.getBurstTime();	
					
					// set The turn around time
					p.setTurnaroundTime(time);
					
					//add the informations of the processes to the array
					arr = new int[3];
					arr[0] = p.getId();
					arr[1] = p.getBurstTime();
					arr[2] = 0;
					
					// process is done
					usedMemory.substract(p.getSize());
					
					// add the array to the list to print it in the Gantt chart					
					L.add(arr);
					
					// set stats to wt
					wt[0] += p.getWaitingTime();
					wt[1] += p.getTurnaroundTime();
					countwt++;
					countta++;
					
				}
			}
		}

		// print Gantt chart
		if (L.size() != 0) {
			System.out.println("\nFirst Come First Serve Gantt chart");
			Utility.printGanttChart(L);
		}
		
		// calculating the averages.
		wt[0] = wt[0] / countwt;
		wt[1] = wt[1] / countta;
		return wt;

	}

	
	//input q is quantum time
	public double[] roundRobin(int q) {
		
		// list of arrays to print them later
		List<int[]> L = new LinkedList<int[]>(); 
		
		
		// processes that are preempted go here if job queue is still not
		// empty
		Queue<PCB> waitBuffer = new LinkedList<PCB>();
		
		
		 // to store WT & TA times
		double[] wt = new double[2];
		// counters
		int countwt = 0, countta = 0;

		// array to store the times at which each process is preempted
		int[] preemptionTimes = new int[LoadToJobThread.getCountOfProcesses()];
		
		// the store the time-line of the Scheduling
		int time = 0;

		
		// if there are remaining processes in the ready or job queues
		// continue Scheduling
		while (LoadToReadyThread.getCount() != 0 || readyQueue.size() != 0) {

			PCB p = null;

			// array of three elements which 
			//stores (proccesesId, BurstTime before each run, and 
			//the remaining burst time after the run )
			int[] arr; 
			
			// to terminate quantum loop 
			//(the loop that runs the process for a maximum of one time quantum)
			int counter = 0; 
			
			PCB w = null;

			
			// continue scheduling while there is still unserved jobs (processes didn't complete their burst times)
			while (readyQueue.size() != 0 || waitBuffer.size() != 0) {

				// if all job queue processes are all in the ready queue or the waitBuffer queue 
				if (LoadToReadyThread.getCount() == 0) {
					
					// loop through the buffer's processes 
					while (waitBuffer.size() != 0) {
						
						// check if we can send it back to the running queue
						double freeMem = MainThread.MEMORY_SIZE - usedMemory.um;
						if (waitBuffer.peek().getSize() <= freeMem) {
							w = waitBuffer.poll();
							usedMemory.add(w.getSize());
							readyQueue.add(w);
						} else {

							// no available space in memory
							// then continue serving the ready queue
							p = readyQueue.poll();

							if (p != null) {
								
								// set waiting time 
								//(the formula shown below gives us the waiting time after its last burst time and before now )
								p.setWaitingTime(p.getWaitingTime() + (time - preemptionTimes[p.getId() - 1]));

								// to terminate the if quantum time has finished
								
								// continue serving the processes until 
								// either quantum is done or processes finished 
								counter = 0;
								while (counter < q && p.getRemainingBurst() > 0) {
									p.setRemainingBurst(p.getRemainingBurst() - 1);
									counter++;
								}

								// add how much time taken by the processes to the time-line
								time += counter; 
								
								// if it has not finished sent it to the waitBuffer 
								if (p.getRemainingBurst() > 0) {
									
									// set the preemption time of the processes 
									preemptionTimes[p.getId() - 1] = time;
									
									usedMemory.substract(p.getSize());;
									waitBuffer.add(p);
									
								}
								

								// if it has finished
								if (p.getRemainingBurst() == 0) {
									
									//because all processes arrived at time 0 we set the turn around time directly
									p.setTurnaroundTime(time); 
									usedMemory.substract(p.getSize());
									// to calculate the average later
									wt[0] += p.getWaitingTime();
									wt[1] += p.getTurnaroundTime();
									countwt++;
									countta++;
								}

								// initialize the array as explained above to use it to print the gantt chart
								arr = new int[3];
								arr[0] = p.getId();
								arr[1] = p.getRemainingBurst() + counter;
								arr[2] = p.getRemainingBurst();

								L.add(arr);

							}

						}

					}
				}

				
				// here is the same procedure but if there were remaining jobs in the job queue	
				p = readyQueue.poll();
				
				if( p != null ) {
					p.setWaitingTime(p.getWaitingTime() + (time - preemptionTimes[p.getId() - 1]));
					counter = 0;
					while (counter < q && p.getRemainingBurst() > 0) {
						
						p.setRemainingBurst(p.getRemainingBurst() - 1);
						counter++;
					}
					
					time += counter;
					if (p.getRemainingBurst() > 0) {
						
						preemptionTimes[p.getId() - 1] = time;
						waitBuffer.add(p);
						usedMemory.substract(p.getSize());
						
					}
					
					
					if (p.getRemainingBurst() == 0) {
						p.setTurnaroundTime(time); 
						usedMemory.substract(p.getSize());
						wt[0] += p.getWaitingTime(); 
						wt[1] += p.getTurnaroundTime();
						countwt++;
						countta++;
					}
					
					arr = new int[3];
					arr[0] = p.getId();
					arr[1] = p.getRemainingBurst() + counter;
					arr[2] = p.getRemainingBurst();
					
					L.add(arr);
					
				}

			}

		}

		if (L.size() != 0) {

			System.out.println("\nRound Robin Gantt chart");
			Utility.printGanttChart(L);
		}
		wt[0] = wt[0] / countwt; 
		wt[1] = wt[1] / countta;

		return wt;

	}

	public double[] sjf() {

		// a list to contain a running processes interval to print the Gantt chart
		List<int[]> L = new LinkedList<int[]>(); // list of arrays
		
		double[] wt = new double[2]; // to store WT & TA times
		
		// counters
		int countwt = 0, countta = 0;
		// time-line
		double time = 0;

		// if there is unserved processes continue
		while (LoadToReadyThread.getCount() != 0 || readyQueue.size() != 0) {

			PCB p = null;

			// array of three elements which 
			//stores (proccesesId, BurstTime before the processes becomes running, the remaining burst time after running )
			int[] arr; 

			// while there are remaining processes in the ready queue
			while (readyQueue.size() != 0) {
				
				// take the process with the lowest burst time and serve it
				p = readyQueue.poll();
				if(p != null) {
					
					// update stats

					p.setWaitingTime(time);
					time += p.getBurstTime();
					
					p.setTurnaroundTime(time);
					arr = new int[3];
					arr[0] = p.getId();
					arr[1] = p.getBurstTime();
					arr[2] = 0;
					
					// finished serving
					usedMemory.substract(p.getSize());
					L.add(arr);
					
					
					wt[0] += p.getWaitingTime();
					wt[1] += p.getTurnaroundTime();
					countwt++;
					countta++;
				}


			}

		}

		// print gantt chart
		if (L.size() != 0) {
			System.out.println("\nShortest Job First Gantt chart");
			Utility.printGanttChart(L);
		}

		wt[0] = wt[0] / countwt;
		wt[1] = wt[1] / countta;

		return wt;
	}
}
