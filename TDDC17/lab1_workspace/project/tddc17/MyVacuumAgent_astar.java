package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
/*
class Coord{
	public int x;
	public int y;
	
	public Coord(int x, int y){
		this.x = x;
		this.y = y;
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
		Coord other = (Coord) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
*/
class MyAgentState_b
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

class MyAgentProgram_b implements AgentProgram {

	private int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 1800;
	public MyAgentState state = new MyAgentState();
	
	public boolean followGoal = false;
	public int goal_x_position;
	public int goal_y_position;
	public boolean cleaningDone = false;
	public int eastWall = 30, southWall = 30;
	public Queue<Coord> searchQueue = new LinkedList<Coord>();
	public Stack<Coord> goalStack = new Stack<Coord>();
	public Set<Coord> visited = new HashSet<Coord>();
	public Set<Coord> obstacles = new HashSet<Coord>();
	
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
	    
	    //state.agent_last_action=state.ACTION_NONE;
    	//return NoOpAction.NO_OP;
	    
	    // Next action selection based on the percept value
	    System.out.println("SearchQueue: " + searchQueue);
	    
	    visited.add(new Coord(state.agent_x_position, state.agent_y_position));
	    queueNearbyCoords();
	    
	    if(!followGoal){
	    	try{
	    		Coord goal = searchQueue.remove();
	    		System.out.println("New goal: " + goal);
	    		setGoal(goal);
	    	}catch(Exception e){
	    		cleaningDone = true;
	    		setGoal(new Coord(1,1));
	    	}
	    } else {
	    	Coord goal = new Coord(goal_x_position, goal_y_position);
	    	System.out.println("Current goal: " + goal);
	    }
	    
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    
	    if (home && cleaningDone){
	    	state.agent_last_action=state.ACTION_NONE;
	    	return NoOpAction.NO_OP;
	    }
	    
	    if (bump) {
			System.out.println("BUMPED");
			int next_x_position = state.agent_x_position;
			int next_y_position = state.agent_y_position;
			
			if(state.agent_direction == MyAgentState.EAST ){
				next_x_position = state.agent_x_position+1;
				next_y_position = state.agent_y_position;
			}
			if(state.agent_direction == MyAgentState.NORTH){
				next_x_position = state.agent_x_position;
				next_y_position = state.agent_y_position-1;
			}
			if(state.agent_direction == MyAgentState.WEST){
				next_x_position = state.agent_x_position-1;
				next_y_position = state.agent_y_position;
			}
			if(state.agent_direction == MyAgentState.SOUTH){
				next_x_position = state.agent_x_position;
				next_y_position = state.agent_y_position+1;
			}
			
			if(goal_x_position == next_x_position && goal_y_position == next_y_position){
				followGoal = false;
			}
			
			obstacles.add(new Coord(next_x_position, next_y_position));
			
			/*if (state.agent_direction == MyAgentState.EAST)
				eastWall = state.agent_x_position + 1;
			else if (state.agent_direction == MyAgentState.SOUTH)
				southWall = state.agent_y_position + 1;*/
			
			
			state.agent_direction = (state.agent_direction +1)%4;
			state.agent_last_action=state.ACTION_TURN_RIGHT;
			return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		}
		/*else
		{
			if(forwardCheck() == 2){
				state.agent_direction = (state.agent_direction +1)%4;
	    		state.agent_last_action=state.ACTION_TURN_RIGHT;
	    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
			}else{
				state.agent_last_action=state.ACTION_MOVE_FORWARD;
				return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
			}
			
		}*/
	    
	    if(followGoal){
	    	if(state.agent_x_position < goal_x_position){
	    		if(state.agent_direction != MyAgentState.EAST){
	    			state.agent_direction = (state.agent_direction +1)%4;
		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
		    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    		}
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
    			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    	else if(state.agent_x_position > goal_x_position){
	    		if(state.agent_direction != MyAgentState.WEST){
	    			state.agent_direction = (state.agent_direction +1)%4;
		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
		    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    		}
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
    			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    	else if(state.agent_y_position > goal_y_position){
	    		if(state.agent_direction != MyAgentState.NORTH){
	    			state.agent_direction = (state.agent_direction +1)%4;
		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
		    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    		}
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
    			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    	else if(state.agent_y_position < goal_y_position){
	    		if(state.agent_direction != MyAgentState.SOUTH){
	    			state.agent_direction = (state.agent_direction +1)%4;
		    		state.agent_last_action=state.ACTION_TURN_RIGHT;
		    		return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
	    		}
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
    			return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    	else{
	    		//goal was found
	    		followGoal = false;
	    	}
	    }
	    
	    /*if(isCorneredCheck()){
	    	if (isDone()){
	    		cleaningDone = true;
	    		setGoal(1,1);
	    	}
	    }*/
	    
	    state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	Queue<Coord> calculatePath(Coord start, Coord goal){
		Set<Coord> closedSet = new HashSet<Coord>();
		Set<Coord> openSet = new HashSet<Coord>();
		openSet.add(start);
		
		Map<Coord, Coord> cameFrom = new HashMap<Coord, Coord>();
		Map<Coord, Integer> gScore = new HashMap<Coord, Integer>();
		gScore.put(start, 0);
		
		Map<Coord, Double> fScore = new HashMap<Coord, Double>();
		fScore.put(start, heuristicCost(start,goal));
		
		Coord[] nearby = new Coord[] {
				new Coord(start.x+1, start.y),
				new Coord(start.x-1, start.y),
				new Coord(start.x, start.y+1),
				new Coord(start.x, start.y-1)
		};
		
		while(!openSet.isEmpty()){
			Coord current = getLowestScore(openSet, fScore);
			
			if(current == goal){
				return reconstructPath(cameFrom, current);
			}
			
			openSet.remove(current);
			closedSet.add(current);
			
			for(Coord neighbour : nearby){
				if(closedSet.contains(neighbour) || obstacles.contains(neighbour)){
					continue;
				}
				
				if(!openSet.contains(neighbour)){
					openSet.add(neighbour);
				}
				
				//fortsätt här
				//tentativeGScore = 
			}
			
		}
		
		return null;
	}
	
	Coord[] getNeighbours(Coord coord){
		return new Coord[] {
				new Coord(coord.x+1, coord.y),
				new Coord(coord.x-1, coord.y),
				new Coord(coord.x, coord.y+1),
				new Coord(coord.x, coord.y-1)
		};
		
	}
	
	Coord getLowestScore(Set<Coord> set, Map<Coord, Double> score){
		Coord minNode = null;
		
		double minScore = 1000;
		for(Coord coord : set){
			if(score.get(coord) < minScore){
				minNode = coord;
			}
		}
		
		return null;
	}
	
	double heuristicCost(Coord a, Coord b){
		int dx = Math.abs(a.x - b.x);
		int dy = Math.abs(a.y - b.y);
		
		return Math.sqrt((dx*dx) + (dy*dy));
	}
	
	Queue<Coord> reconstructPath(Map<Coord, Coord> cameFrom, Coord current){
		
		
		return null;
	}
	
	int forwardCheck() {
		switch (state.agent_direction) {
		case MyAgentState.NORTH:
			return state.world[state.agent_x_position][state.agent_y_position-1];
		case MyAgentState.EAST:
			return state.world[state.agent_x_position+1][state.agent_y_position];
		case MyAgentState.SOUTH:
			return state.world[state.agent_x_position][state.agent_y_position+1];
		case MyAgentState.WEST:
			return state.world[state.agent_x_position-1][state.agent_y_position];
		}
		return -1;
	}
	
	boolean isCorneredCheck() {
		if(state.world[state.agent_x_position][state.agent_y_position-1] != 0 &&
			state.world[state.agent_x_position][state.agent_y_position+1] != 0 &&
			state.world[state.agent_x_position-1][state.agent_y_position] != 0 &&
			state.world[state.agent_x_position+1][state.agent_y_position] != 0)
			return true;
		return false;
	}
	
	void removeGoalIfBlocked(){
		//TODO
	}
	
	void setGoal(int x, int y) {
		followGoal = true;
		goal_x_position = x;
		goal_y_position = y;
	}
	
	void setGoal(Coord coord) {
		followGoal = true;
		goal_x_position = coord.x;
		goal_y_position = coord.y;
	}
	
	boolean isDone(){
		for(int i = 1; i < eastWall-1;i++){
			for(int j = 1; j < southWall-1;j++ ){
				if (state.world[i][j] == 0){
					setGoal(i,j);
					return false;
				}
			}
		}
		return true;
	}
	
	void queueNearbyCoords(){
		int x = state.agent_x_position;
		int y = state.agent_y_position;
		
		queueCoord(new Coord(x,y-1));
		//queueCoord(new Coord(x+1,y-1));
		queueCoord(new Coord(x+1,y));
		//queueCoord(new Coord(x+1,y+1));
		queueCoord(new Coord(x,y+1));
		//queueCoord(new Coord(x-1,y+1));
		queueCoord(new Coord(x-1,y));
		//queueCoord(new Coord(x-1,y-1));
		
	}
	
	void queueCoord(Coord coord){
		if(!visited.contains(coord) && !searchQueue.contains(coord) && !obstacles.contains(coord)){
			searchQueue.add(coord);
		} else {
			System.out.println("Tried to add visisted: " + coord);
		}
	}
}

public class MyVacuumAgent_astar extends AbstractAgent {
    public MyVacuumAgent_astar() {
    	super(new MyAgentProgram());
	}
}
