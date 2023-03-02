package finaleVersion;

import java.util.Collection;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;



// a class to manage usedMemory by the processes.
class usedMemoryInt {
	
	// to store the value of used memory
	double um;
	
	// methods to edit the value with synchronization 
	public synchronized void add(double val) {
		um += val;
	}
	public synchronized void substract(double val) {
		um -= val;
	}
	public synchronized double getUm() {
		return um;
	}
	
}

// main thread
public class MainThread {
	
	// here is some constants variables
	final static int MEMORY_SIZE = 1024;
	final static int TIME_QUANTUM = 10;


	public static void main(String[] args) {
		
		// these next lines to handle the user requests and run the algorithms
		Scanner input = new Scanner(System.in);
		int choice;
		do {
			System.out.println("------------------- Welcome to Job Schedular program -------------------");
			System.out.println("1-First Come First Serve");
			System.out.println("2-Round Robin");
			System.out.println("3-Shortest job first (non-preemptive)");
			System.out.println("4-Quit");
			System.out.print("Please enter the choice of the algorithm you want to run :");
			choice = input.nextInt();
			
			switch(choice) {
			
				case 1:{
					
						//here we initialize usedMomory object 
						//to share it among "JobSchedular and LoadToReadyThread classes"   
						usedMemoryInt usedMemory;
						usedMemory = new usedMemoryInt();
						
						// initialize the queues 
						ConcurrentLinkedQueue<PCB> readyQueue = new ConcurrentLinkedQueue<PCB>();
						ConcurrentLinkedQueue<PCB> jobQueue = new ConcurrentLinkedQueue<PCB>();
						// load jobs to job queue
						LoadToJobThread ltjob = new LoadToJobThread("job.txt", jobQueue);
						Thread ltj1 = new Thread(ltjob);
						ltj1.start();
						
						// here we wait for the job queue to load all processes to handle them after they are loaded
						try {
							ltj1.join();
						} catch (InterruptedException e) {
							System.out.println("failed to wait ltj1");
						}
						
						// then start the LoadToTeady thread and run the CPU scheduling algorithm
						LoadToReadyThread ltready = new LoadToReadyThread(readyQueue, jobQueue, usedMemory, null , 0);
						Thread ltr1 = new Thread(ltready);
						ltr1.start();
						JobScheduler js = new JobScheduler(readyQueue, usedMemory);
						double [] arr = js.fcfs();
						
						// print the average times
						System.out.println("\nAverage Waiting Time Of FCFS : " + arr[0] +"ms");
						System.out.println("Average Turn Around Time of FCFS : " + arr[1] +"ms");
						System.out.println();					
						
					
						break;
				}
				
				case 2:{
					
						
						// same explanations as case 1
						usedMemoryInt usedMemory = new usedMemoryInt();
						ConcurrentLinkedQueue<usedMemoryInt> a = new ConcurrentLinkedQueue<>();
						a.add(usedMemory);
						
						ConcurrentLinkedQueue<PCB> readyQueue = new ConcurrentLinkedQueue<PCB>();
						ConcurrentLinkedQueue<PCB> jobQueue = new ConcurrentLinkedQueue<PCB>();
						LoadToJobThread ltjob = new LoadToJobThread("job.txt", jobQueue);
						Thread ltj1 = new Thread(ltjob);
						ltj1.start();
						
						try {
							ltj1.join();
						} catch (InterruptedException e) {
							System.out.println("failed to wait ltj1");
						}
						
						LoadToReadyThread ltready = new LoadToReadyThread(readyQueue, jobQueue, usedMemory, null , 0);
						Thread ltr1 = new Thread(ltready);
						ltr1.start();
						JobScheduler js = new JobScheduler(readyQueue, usedMemory);
						double [] arr = js.roundRobin(TIME_QUANTUM);
						System.out.println("\nAverage Waiting Time Of Round Robin : " + arr[0] +"ms");
						System.out.println("Average Turn Around Time of Round Robin : " + arr[1] +"ms");
						System.out.println();					
						
						break;
					
				}
				case 3:{
						
						// same explanations as case 1
						//difference is using a priority queue for SJF
						usedMemoryInt usedMemory;
						usedMemory = new usedMemoryInt();
						
						ConcurrentLinkedQueue<PCB> readyQueue = new ConcurrentLinkedQueue<PCB>();
						ConcurrentLinkedQueue<PCB> jobQueue = new ConcurrentLinkedQueue<PCB>();
						LoadToJobThread ltjob = new LoadToJobThread("job.txt", jobQueue);
						
						Thread ltj1 = new Thread(ltjob);
						ltj1.start();
						
						try {
							ltj1.join();
						} catch (InterruptedException e) {
							System.out.println("failed to wait ltj1");
						}
						
						PriorityQueue<PCB> sjfQueue = new PriorityQueue<>(new comparator1()); 
						Utility.copyJobQueue(sjfQueue, jobQueue);
						
						LoadToReadyThread ltready = new LoadToReadyThread(readyQueue, jobQueue, usedMemory, sjfQueue , 1);
						Thread ltr1 = new Thread(ltready);
						ltr1.start();
						JobScheduler js = new JobScheduler(readyQueue, usedMemory);
						double [] arr = js.sjf();
						System.out.println("\nAverage Waiting Time Of Shortest job first : " + arr[0] +"ms");
						System.out.println("Average Turn Around Time of Shortest job first : " + arr[1]+"ms");
						System.out.println();
					
						break;
				}
				case 4:{
					System.out.println("GOODBYE!");
					break;
				}	
				default:
					System.out.println("Wrong input!");
			}
		}while(choice != 4);
		
		input.close();
	}

}
