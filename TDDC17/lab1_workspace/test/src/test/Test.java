package test;

import java.util.LinkedList;
import java.util.Queue;

public class Test {

	public static void main(String[] args) {
		 Queue<Integer> searchQueue = new LinkedList<Integer>();
		 
		 searchQueue.add(1);
		 searchQueue.add(2);
		 searchQueue.add(3);
		 searchQueue.add(4);
		 
		 System.out.println(searchQueue.toString());
		 
		 System.out.println("Removed: " + searchQueue.remove());
		 
		 System.out.println(searchQueue.toString());
		 

	}

}
