package finaleVersion;

public class PCB {

	
	private int id; // process ID
	private String state; // process state
	private int burstTime; // process burst time
	private double size; // process required memory
	private double turnaroundTime; // total turnAround time
	private double waitingTime; // total waiting time
	private int remainingBurst; // the remaining burst time of the processes

	
	public PCB(int id, int burstTime, double size) {
		// initializations
		this.id = id;
		state = null; 
		this.burstTime = burstTime;
		this.size = size;
		remainingBurst = burstTime;
	}

	// getters and setters
	public int getRemainingBurst() {
		return remainingBurst;
	}

	public void setRemainingBurst(int remainingBurst) {
		this.remainingBurst = remainingBurst;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getBurstTime() {
		return burstTime;
	}

	public void setBurstTime(int burstTime) {
		this.burstTime = burstTime;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public double getTurnaroundTime() {
		return turnaroundTime;
	}

	public void setTurnaroundTime(double turnaroundTime) {
		this.turnaroundTime = turnaroundTime;
	}

	public double getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(double waitingTime) {
		this.waitingTime = waitingTime;
	}

}
