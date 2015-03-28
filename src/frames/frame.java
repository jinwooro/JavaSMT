package frames;

import graphEntity.node;

import java.util.ArrayList;

public class frame {
	
	private node origin;
	private ArrayList<node> destination;
	private int period;
	private int deadline;
	private int id;
	
	////////////////////////////////// Constructors  ////////////////////////////////////
	public frame(node origin, ArrayList<node> destination, int period, int id) {
		// TODO Auto-generated constructor stub
		this.origin = origin;
		this.destination = new ArrayList<node>(destination);
		this.period = period;
		this.id = id;
		this.deadline = period;
	}
	
	public frame (node origin, ArrayList<node> destination, int period, int id, int deadline){
		this.origin = origin;
		this.destination = new ArrayList<node>(destination);
		this.period = period;
		this.id = id;
		this.deadline = deadline;
	}
	
	public frame(node origin, node destination, int period, int id, int deadline){
		this.origin = origin;
		this.destination = new ArrayList<node>();
		this.destination.add(destination);
		this.period = period;
		this.id = id;
		this.deadline = deadline;
	}
	
	public frame(node origin, node destination, int period, int id){
		this.origin = origin;
		this.destination = new ArrayList<node>();
		this.destination.add(destination);
		this.period = period;
		this.id = id;
		this.deadline = period;
	}
	
	//////////////////////////////// get Methods ///////////////////////////////////////
	public int getID(){
		return id;
	}
	
	public int getPeriod(){
		return period;
	}
	
	public boolean checkDestination(int id){ //return true if the input node id is a destination of this frame
		for (node a : destination){
			if(id == a.getID()) return true;
		}
		return false;
	}
	
	public node getOrigin(){
		return origin;
	}
	
	public ArrayList<node> getDestination(){
		return destination;
	}
	
	public int getDeadline(){
		return deadline;
	}
}
