package frames;

import fileManager.writer;
import graphEntity.edge;
import graphEntity.node;

public class finstance {
	
	//private int x; // number of transmissions that this frame instance must have
	private edge e; 
	private frame f;

	public finstance(frame _f, edge _e) {
		// TODO Auto-generated constructor stub
		f=_f;
		e=_e;
		//this.x = e.getWeight() * (LCM / this.f.getPeriod());
	}
	
	public edge getEdge(){
		return this.e;
	}
	
	public frame getFrame(){
		return this.f;
	}
	
	//////////////////////////// is Methods /////////////////////////////////
	public boolean isFirst(){
		if (f.getOrigin().getID() == e.getSenderID()) return true;
		else {return false;}
	}
	
	public boolean isLast(){
		int ID = e.getReceiverID();

		for (node n : f.getDestination()){
			if (n.getID() == ID) return true;
		}
		return false;
	}
}
