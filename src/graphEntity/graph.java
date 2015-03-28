package graphEntity;

import java.util.ArrayList;

import fileManager.writer;

public class graph {
	public ArrayList<node> V;
	public ArrayList<edge> E;
	
	
	public graph(ArrayList<node> v, ArrayList<edge> e) {
		V = v;
		E = e;
	}
	
	public void outputGraph(boolean IntuitiveDiagramIfTrue){
		// automatic graph layout drawer
		
		// if intuitive mode is on (true), then the position of each node is "pinned" at the exact location.
		// If not in the intuitive mode, then all the nodes will be places in such a way that the user can easily recognize the connection between nodes.
		String marker1 = "";
		if (IntuitiveDiagramIfTrue) marker1 = "!";
		
		
		writer myWriter = new writer("graph.dot",false);
		
		myWriter.write("digraph testgraph{ \n {node [shape=none, fontsize=23] \"A network with " + V.size() + " node(s).\"}\n");
		
		for (node n : V){
			myWriter.write(n.getName() + "[pos=\"" + n.getPos().getX() + "," + n.getPos().getY() + marker1 +"\"];\n");
		}
		
		for (edge e : E){
			
			String myColor = "black";
			
			if (e.getWeight() == 1){
				myColor = "green";	
			}
			else if (e.getWeight() == 2){
				myColor = "yellow";
			}
			else{
				myColor = "red";
			}
			
			myWriter.write(e.getSender().getName() + "->" + e.getReceiver().getName() + " [label=" + e.getWeight() + ", color=\"" + myColor + "\"];\n");	
		}
		
		myWriter.write("}");
	}

}
