package graphEntity;

import java.util.ArrayList;

public class node {

	private int id;
	private String name;
	private Coord pos;
	private double Pt;
	private double sensitivity;
	
	public node (String name, int id, int x, int y, double Pt, double sensitivity) {
		// TODO Auto-generated constructor stub
		this.name = name; // name of this node (just to help the designer to recognize this node)
		this.id = id; // id of this node as shown in the schedule (must be an integer)
		this.pos = new Coord(x,y); // Position in x and y coordinates
		this.Pt = Pt; // Transmitting power (typically up to 5dBm in WSNs)
		this.sensitivity = sensitivity; // Sensitivity in dBm (typically around -90dBm in WSNs)
	}
	
	//////////////////////////// Get Methods /////////////////////////////////
	public int getID(){
		return this.id;
	}
	
	public double getSensitivity(){
		return this.sensitivity;
	}
	
	public double getPower(){
		return this.Pt;
	}
	
	public String getName(){
		return this.name;
	}
	
	public Coord getPos(){
		return this.pos;
	}

	@Override
	public String toString() {
		return "node [id=" + id + ", name=" + name + ", Pt=" + Pt + "]";
	}

}
