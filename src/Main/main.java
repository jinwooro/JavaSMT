package Main;

import fileManager.ILPconstraints;
import fileManager.constraints;
import fileManager.writer;
import frames.finstance;
import frames.frame;
import frames.path;
import algorithms.*;
import graphEntity.edge;
import graphEntity.graph;
import graphEntity.node;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import Main.*;
import PhysicalModels.ModulationTypes;
import PhysicalModels.PathlossModes;


public class main {

	private static ArrayList<node> V = new ArrayList<node>(); // Set of vertices
	private static ArrayList<edge> E = new ArrayList<edge>(); // Set of edges
	private static ArrayList<frame> F = new ArrayList<frame>(); // Set of frames
	private static ArrayList<path> P = new ArrayList<path>(); // Set of paths
	private static ArrayList<finstance> I = new ArrayList<finstance>(); // Set of frame instances
	
	static int LCM; // this is the hyper period of the messages
	static double transmissionPower = -6; //dBm
	static double sensitivityPower = -95; //dBm
	
	public static void main(String[] args) {
		
		// Test purpose:
//		pdfChecker tester1 = new pdfChecker(0, -90, ModulationTypes.FSK, PathlossModes.ITU, "ITUPDF.csv");
//		tester1.genetratePDF(1);
//		pdfChecker tester2 = new pdfChecker(0, -90, ModulationTypes.FSK, PathlossModes.LOGNORMAL, "LognormalPDF.csv");
//		tester2.genetratePDF(1);
//		pdfChecker tester3 = new pdfChecker(0, -90, ModulationTypes.FSK, PathlossModes.FREE_SPACE, "FreeSpacePDF.csv");
//		tester3.genetratePDF(1);
		
		// 1. given inputs (nodes). We assume that all nodes are deployed in a correct position as given.
		//    Definition of a node ( name , id , x , y , transmitting power, sensitivity ).
		// 	  The system designer must provide information according to the Node's definition.
		int nid = 0;
		V.add(new node("v1", nid++, 0, 0, transmissionPower, sensitivityPower));
		V.add(new node("v2", nid++, 0, 10, transmissionPower, sensitivityPower));
		V.add(new node("v3", nid++, 10, 0, transmissionPower, sensitivityPower));
		V.add(new node("v4", nid++, 10, 10, transmissionPower, sensitivityPower));
		
		
		// 2. computing the edges. The instance "connector" returns the edges.
		//    Now, we have V and E to form a graph G.
//		linker connector = new linker(V, 0.9, PathlossModes.FREE_SPACE);

		E.add(new edge(V.get(0), V.get(1), 2));
		E.add(new edge(V.get(1), V.get(0), 2));
		E.add(new edge(V.get(0), V.get(2), 1));
		E.add(new edge(V.get(2), V.get(0), 1));
		E.add(new edge(V.get(1), V.get(3), 2));
		E.add(new edge(V.get(3), V.get(1), 2));
		E.add(new edge(V.get(2), V.get(3), 2));
		E.add(new edge(V.get(3), V.get(2), 2));
		
		// 3. Now the program will generate a graph which representing the network.
		graph G = new graph(V,E);
		// Generate Graphviz's "dot" script to visualize the graph G.
		// The argument is "true" if you want to have the graph in scale. Otherwise, the visualized graph will place nodes according to their connection.
		G.outputGraph(true);
		
		// 4. Defining frames ( origin, destination, period, id, deadline (optional) )
		//    Another input to the system is the frames (i.e., messages)
		int fid = 0;
		F.add(new frame (V.get(0), V.get(3), 8, fid++));
		ArrayList<node> dest = new ArrayList<node>();
		dest.add(V.get(1));
		dest.add(V.get(2));
		F.add(new frame (V.get(3), dest, 16, fid++));
		F.add(new frame (V.get(2), V.get(1), 16, fid++));

		// 5. Compute the path from the source node to the destination node for all frames.
		//    A path is a set of edges.
		// 	  LCM is the size of the schedule (in number of slots).
		ArrayList<Integer> periods = new ArrayList<Integer>();
		
		for (frame f : F){
			path p = new path(f, G);
			if (p.getPath() == null) continue;  // If the path cannot be found, then we do not consider this frame for scheduling
			P.add(p);
			periods.add(f.getPeriod());
		}
		lcm math = new lcm();
		LCM = math.getLCM(periods);
		System.out.println("LCM is: " + LCM);
		
		// 6. We create a frame instance which we associate it with an edge in a path.
		//    Definition of a frame instance is: a set of slot allocations (i.e., slot index).
		for (int i=0; i< P.size(); i++){
			for (edge e : P.get(i).getPath()){
				I.add(new finstance(F.get(i), e));
			}
		}
		// Debugging purpose printing functions
		for (finstance fi : I){
			System.out.println("f" + fi.getFrame().getID() + "[" + fi.getEdge().getSenderID() + "," + fi.getEdge().getReceiverID() + "]");
		}

		System.out.println("Indicate what solver is going to be used: Type 1 for SMT, 2 for ILP, otherwise, 0 to exit this program");
		while (true){
			Scanner keyboard = new Scanner(System.in);
			int mode = keyboard.nextInt();
			if ((mode == 0) || (mode == 1) || (mode == 2)) {
				
				// 7. Now, we create constraints for each frame instance.
				switch (mode){
					case 1:
						//SMT solve
						SMT();
						log();
						break;
					case 2:
						//ILP solve
						ILP();
						log();
						break;
					case 0:
						//exit the program
						break;
					default:		
						break;
				}
				
				break;
			}
		}	
		System.out.println("Program finished...");
	}
	
	static void SMT(){
		System.out.print("Generating constraints for SMT solvers...\nNotes: \n");
		constraints cons = new constraints();
		cons.generateConstraints(I, LCM, E);
		System.out.println("Done!");
	}
	
	static void ILP(){
		System.out.print("Generating constraints for ILP solvers...\nNotes: \n");
		ILPconstraints cons = new ILPconstraints(I, LCM, E);
		System.out.println("Done!");
	}
	
	static void log(){
		// 7.5. Making a log of this network. A file called simulation.txt is written to store all the useful detail of this network.
		System.out.println("Logging...");
		writer myWriter = new writer("simulation.txt",false);

		myWriter.write("Nodes: (Name, ID, x, y) \n");
		for (node n: V){
			myWriter.write(n.getName() + "," + n.getID() + "," + n.getPos().getX() + "," + n.getPos().getY() + "\n");
		}
		myWriter.write("*\nEdges: (SenderID, ReceiverID) \n");
		for (edge e: E){
			myWriter.write(e.getSenderID() + "," + e.getReceiverID() + "\n");
		}
		myWriter.write("*\nFrame Instances: (FrameID, SenderID, ReceiverID) \n");
		for (finstance fi: I){
			myWriter.write( fi.getFrame().getID() + "," + fi.getEdge().getSenderID() + "," + fi.getEdge().getReceiverID() + "\n");			
		}

		// 8. Leave other details 
		int sum = 0;
		for (finstance fi : I){
			sum += fi.getEdge().getWeight() * LCM / fi.getFrame().getPeriod();
		}
		myWriter.write("\n\n--------------------------------------------------------------------------------------\n");
		myWriter.write(";The total number of frames: "+F.size()+"\n");
		myWriter.write(";The total number of frame instances: "+I.size()+"\n");
		myWriter.write(";The total number of allocated slots over schedule over total slots: "+ sum + "/" + LCM + "\n");
		myWriter.write(";Node numbers: "+V.size()+"\n");
		myWriter.write(";Edge numbers: "+E.size()+"\n");
		System.out.println("Done!");
	}
}


