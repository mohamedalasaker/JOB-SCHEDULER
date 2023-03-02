package finaleVersion;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Utility {

	
	// copies elements from jobQueue to sjf queue
	public static void copyJobQueue(PriorityQueue<PCB> sjf, Queue<PCB> jobQueue) {
		for (PCB p : jobQueue) {
			sjf.add(p);
		}

	}

	/*
	 * List L have pairs of process ID, time
	 *
	 * {
	 * 
	 * {1, left of burst at start, left of burst at end}, {2, time2}, ...
	 * 
	 * }
	 * 
	 */
	public static void printGanttChart(List<int[]> L) {

		// first line of "-"
		for (int i = 0; i < 21 * L.size(); i++) {
			System.out.print("-");
		}

		System.out.println();

		
		// middle line (processes)
		for (int[] a : L) {

			System.out.printf("|   [%d,%d]ms P%d   ", a[1], a[2], a[0]);

		}

		System.out.print(" |");

		System.out.println();

		// last line of "-"
		for (int i = 0; i < 21 * L.size(); i++) {
			System.out.print("-");
		}

		System.out.println();

		// line of intervals of Gantt chart
		int temp = 0, counter = 0;
		int[] aa;

		for (int ll = 0; ll < L.size() + 1; ll++) {

			if (ll == L.size()) {
				System.out.print(temp);
			} else {

				aa = L.get(counter);

				// no space
				System.out.print(temp);

				for (int ss = 0; ss < 16; ss++) {
					System.out.print(" ");
				}

				counter++;
				temp += aa[1] - aa[2];

			}

		}

		System.out.println("");
	}

}

class comparator1 implements Comparator<PCB> {

	@Override
	public int compare(PCB p1, PCB p2) {
		// TODO Auto-generated method stub
		if (p1.getBurstTime() < p2.getBurstTime()) {
			return -1;
		} else if (p1.getBurstTime() >= p1.getBurstTime()) { // added =
			return 1;
		} else {
			return 0;
		}

	}

}
