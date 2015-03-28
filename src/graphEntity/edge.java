package graphEntity;

import java.util.ArrayList;

public class edge {
	//A directed edge from vi to vj
	private node vi;
	private node vj;
	private int weight;
	
	public edge(node nodei, node nodej, int weight) {
		// TODO Auto-generated constructor stub
		this.vi=nodei;
		this.vj=nodej;
		this.weight=weight;
	}	
	
	/////////////////////////////// get Methods ////////////////////////////////
	public int getSenderID(){
		return vi.getID();
	}
	
	public int getReceiverID(){
		return vj.getID();
	}
	
	public node getSender(){
		return vi;
	}
	
	public node getReceiver(){
		return vj;
	}
	
	public int getWeight(){
		return weight;
	}
	
	///////////////////////////// print Methods ////////////////////////////////
	public void printEdge(){
		String info = "Edge: from " + vi.getName() + " to " + vj.getName() + " weight=" + weight;
		System.out.println(info);
	}
}
