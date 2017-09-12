package tddc17;


import aima.core.environment.liuvacuum.*;
import sun.management.resources.agent;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
class Coordinate{
	public int x;
	public int y;
	public Coordinate parent;
	
	public Coordinate(int x, int y, Coordinate parent){
		this.x = x;
		this.y = y;
		this.parent = parent;
	}
	
	@Override
	public String toString() {
		return "Coord [x=" + x + ", y=" + y + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Coordinate other = (Coordinate) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 1800;
	public MyAgentState state = new MyAgentState();
	
	public Queue<Coordinate> path = new LinkedList<Coordinate>();
	public HashSet<Coordinate> unknownNodes = new HashSet<Coordinate>();
	
	Coordinate goal;
	Coordinate current;
	
	public boolean cleaningDone = false;
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	
	@Override
	public Action execute(Percept percept) {
		current = new Coordinate(state.agent_x_position, state.agent_y_position, null);
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else if(!home)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	    state.printWorldDebug();
	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    addNearbyCoords();
	    Coordinate c = findUnknown();
	    if (home && c == null){
	    	state.agent_last_action=state.ACTION_NONE;
	    	return NoOpAction.NO_OP;
	    }
	    
	    if (path.isEmpty() || bump) {
	    	
	    	if (c == null) {
	    		path = pathFinder(new Coordinate(1,1, null));
	    		goal = path.remove();
	    	}
	    	else {
	    		
	    		path = pathFinder(c);
	    		goal = path.remove();
	    	}
		}
	    if (state.agent_x_position == goal.x && state.agent_y_position == goal.y) {
	    	goal = path.remove();
	    }
		if (state.agent_x_position < goal.x ) {
			if (state.agent_direction != MyAgentState.EAST) {
				return rotate(MyAgentState.EAST);
			}
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		} else if (state.agent_x_position > goal.x) {
			if (state.agent_direction != MyAgentState.WEST) {
				return rotate(MyAgentState.WEST);
			}
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		} else if (state.agent_y_position > goal.y) {
			if (state.agent_direction != MyAgentState.NORTH) {
				return rotate(MyAgentState.NORTH);
			}
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		} else if (state.agent_y_position < goal.y) {
			if (state.agent_direction != MyAgentState.SOUTH) {
				return rotate(MyAgentState.SOUTH);
			}
			state.agent_last_action = state.ACTION_MOVE_FORWARD;
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		} else {
			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		}

	}
	/***
	 * Performs a breadth first search to to find the shortest path to coordinate c.
	 * @param c Goal coordinate.
	 * @return Returns a queue of coordinates that is the path to coordinate c. 
	 */
	Queue<Coordinate> pathFinder(Coordinate c) {
		Queue<Coordinate> queue = new LinkedList<Coordinate>();
		HashSet<Coordinate> visited = new HashSet<Coordinate>();
	    queue.add(new Coordinate(state.agent_x_position,  state.agent_y_position, null));
		
	    while(!queue.isEmpty()){
	        Coordinate node = queue.remove();
	        visited.add(node);
	        
	        if (node.x == c.x && node.y == c.y) {
	        	return getPathFromChild(node);
	        }
	        
	        if (node.x!=30) {
	        	Coordinate t = new Coordinate(node.x+1,node.y,node);
	        	if (state.world[t.x][t.y] != 1 && !visited.contains(t))
	        		queue.add(t);
	        }
	        if (node.x!=0){
	        	Coordinate t = new Coordinate(node.x-1,node.y,node);
	        	if (state.world[t.x][t.y] != 1 && !visited.contains(t))
	        		queue.add(t);
	        }
	        if (node.y!=30){
	        	Coordinate t = new Coordinate(node.x,node.y+1,node);
	        	if (state.world[t.x][t.y] != 1 && !visited.contains(t))
	        		queue.add(t);
	        }
	        if (node.y!=0){
	        	Coordinate t = new Coordinate(node.x,node.y-1,node);
	        	if (state.world[t.x][t.y] != 1 && !visited.contains(t))
	        		queue.add(t);
	        }
	    }
	    return null;
	}
	/***
	 * Backtracks the path from a child to the start node.
	 * @param child The goal coordinate.
	 * @return Returns the queue of coordinates.
	 */
	Queue<Coordinate> getPathFromChild(Coordinate child){
		Stack<Coordinate> stack = new Stack<Coordinate>();
		while (child.parent != null) {
			stack.push(child);
			child = child.parent;
		}
		Queue<Coordinate> queue = new LinkedList<Coordinate>();
		while(!stack.isEmpty()) {
			queue.add(stack.pop());
		}
		return queue;
	}
	/***
	 * Finds the closest unknown node.
	 * Uses pythagoras theorem to estimate the closest node.
	 * @return
	 */
	Coordinate findUnknown() {
		Coordinate closest = null;
		double minDistance = 1000;
		HashSet<Coordinate> remove = new HashSet<Coordinate>();
		for (Coordinate c : unknownNodes) {
			if (state.world[c.x][c.y] != 0) {
				remove.add(c);
				continue;
			}
			double d = distance(c, new Coordinate(state.agent_x_position, state.agent_y_position, null));
			if (d < minDistance){
				closest = c;
				minDistance = d;
			}
		}
		for (Coordinate c : remove) {
			unknownNodes.remove(c);
		}
		return closest;
	}
	/***
	 * Adds nearby unvisited nodes to a set.
	 */
	void addNearbyCoords(){
		int x = state.agent_x_position;
		int y = state.agent_y_position;
		
		if (state.world[x+1][y] == 0)
			unknownNodes.add((new Coordinate(x+1,y, null)));
		if (state.world[x-1][y] == 0)
			unknownNodes.add((new Coordinate(x-1,y, null)));
		if (state.world[x][y+1] == 0)
			unknownNodes.add((new Coordinate(x,y+1, null)));
		if (state.world[x][y-1] == 0)
			unknownNodes.add((new Coordinate(x,y-1, null)));
		
		
	}
	/***
	 * Calculates the distance between two coordinates using pythagoras theorem.
	 * @param a
	 * @param b
	 * @return
	 */
	double distance(Coordinate a, Coordinate b){
		int dx = Math.abs(a.x - b.x);
		int dy = Math.abs(a.y - b.y);
		
		return Math.sqrt((dx*dx) + (dy*dy));
	}
	/***
	 * Rotates towards a goal direction by calculating the quickest way to rotate.
	 * @param goalDirection
	 * @return
	 */
	Action rotate(int goalDirection) {
		int currDirection = state.agent_direction;
		int left = currDirection, right = currDirection;
		int leftTurns = 0, rightTurns = 0;
		
		while(left != goalDirection) {
			left = (left == 0 ? 3 : left - 1);
			leftTurns++;
		}
		while(right != goalDirection) {
			right = (right + 1) % 4;
			rightTurns++;
		}
		
		if(leftTurns < rightTurns) {
			state.agent_direction = (state.agent_direction == 0 ? 3 : state.agent_direction - 1);
			state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		}else {
			state.agent_direction = (state.agent_direction + 1) % 4;
			state.agent_last_action = state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
	}
	
}
public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram());
	}
}
