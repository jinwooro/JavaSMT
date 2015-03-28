package frames;

import graphEntity.edge;
import graphEntity.graph;
import graphEntity.node;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import algorithms.dijkstra;

public class path {
	
	private int id;
	private ArrayList<edge> path = new ArrayList<edge>();

	/////////////////////////// Constructor /////////////////////////
	public path(frame f, graph G) {
		this.id = f.getID();
	
		dijkstra pfder = new dijkstra(G.V, G.E);	// Initialize path finder
		pfder.execute(f.getOrigin()); 																	// Set up the sink node
		try {
			LinkedList<node> N = new LinkedList<node>(pfder.getPath(f.getDestination().get(0)));		// Save nodes in the found path which is the path to the first destination
			addPath(N, G.E);																				// Add to "path"
			
			for (int i=1; i<f.getDestination().size(); i++){
				int min=0;
				LinkedList<node> shortest=null;
				
				for (node n : N){						// We consider all nodes in the existing path. We want to find the shortest path that connects "n" to the next destination node
					int cost=999;
					pfder.reset();													// Reset
					pfder.execute(n);												// Set up the starting node
					LinkedList<node> temp = new LinkedList<node>(pfder.getPath(f.getDestination().get(i)));	// Set up the final node
					cost = computeWeight(temp, G.E); 						// Compute the total cost of this path
					
					if ((min == 0) || (min > cost)){		// If this path is shorter than other path of different n in N, then we select this.
						shortest=temp;
						min=cost;
					}
				}
				
				addPath(shortest, G.E);
				
				for (node m : shortest){
					N.add(m);
				}
			}
			printPath();
		} catch (Exception e){
			System.out.println("No path can be found from " + f.getOrigin().getName() + " to " + f.getDestination().get(0).getName());
			path = null;
		}
	}
	
	private int computeWeight (LinkedList<node> nodes, ArrayList<edge> E){
		int cost = 0;
		for (int a=0; a<nodes.size()-1; a++){
			cost += getEdge(nodes.get(a), nodes.get(a+1), E).getWeight();   // Please consider the direction of the edge. e.g., "from" and "to"
		}
		return cost;
	}
	
	private void addPath (LinkedList<node> nodes, ArrayList<edge> E){
		for (int a=0; a<nodes.size()-1; a++){
			path.add(getEdge(nodes.get(a), nodes.get(a+1), E));   // Please consider the direction of the edge. e.g., "from" and "to"
		}
	}
	
	private edge getEdge (node from, node to, ArrayList<edge> E){
		for (edge e : E){
			if ((e.getSenderID()==from.getID()) && (e.getReceiverID()==to.getID())){
				return e;
			}
		}
		return null;
	}
	
	public ArrayList<edge> getPath (){
		return this.path;
	}
	
	public int getID(){
		return this.id;
	}
	
	public void printPath(){
		System.out.print("Path: ");
		for (edge e : path){
			System.out.print("(" + e.getSenderID() + ", " + e.getReceiverID() + ")"+e.getWeight());
		}
		System.out.println(".");
	}
}
