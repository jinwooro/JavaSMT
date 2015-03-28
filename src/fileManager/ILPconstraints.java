package fileManager;

import java.util.ArrayList;

import frames.finstance;
import graphEntity.edge;
import graphEntity.node;
import gurobi.*;
import gurobi.GRB.DoubleAttr;

import java.util.List;

public class ILPconstraints {

	private int LCM;
	private ArrayList<finstance> Finst; 
	private GRBEnv env;
	private GRBModel model;
	private List<List<GRBVar>> var;
	private ArrayList<edge> E;
	private GRBVar latency;
	GRBLinExpr expr;
	
	private int BVnametag = 0; 
	private int Cnametag = 0;
	
	public ILPconstraints(ArrayList<finstance> Finst, int LCM, ArrayList<edge> E) {
		// TODO Auto-generated constructor stub
		this.Finst= Finst;
		this.LCM = LCM;
		this.E = E;
		
		// All the transmissions are variables. Therefore, we create a 2d array to express each transmission. The Row of this array is "frame" and each column is the GRBVar which is a variable declaration for Gurobi ILP solver. 
		var = new ArrayList<List<GRBVar>>(Finst.size());
		for (int i=0; i<Finst.size(); i++){ // initialization
			var.add(new ArrayList<GRBVar>());
		}
		
		try {
			// Create an environment and a model for ILP solve
			env = new GRBEnv("ILPresults.log");
			model = new GRBModel(env); 
			
			// Create variables
			for (int i=0; i<Finst.size(); i++){
				for (int j=0; j<(LCM/Finst.get(i).getFrame().getPeriod()); j++){
					for (int k=0; k<Finst.get(i).getEdge().getWeight(); k++){
						// Need to check here whether the lower and the upper bounds are inclusive??????
						double ub = (j+1) * Finst.get(i).getFrame().getPeriod()-1;
						double lb = j * Finst.get(i).getFrame().getPeriod();
						var.get(i).add(model.addVar(lb, ub, 0, GRB.INTEGER, getVariableName(Finst.get(i), j, k)));
					}
				}
			}
			
			// An optimization variable
			latency = model.addVar(0.0, (double) LCM-1, 0, GRB.INTEGER, "latency");
			
			// Binary variables
			model.update();
			
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("ILP Error: cannot initiate environment/model/variables.");
			e.printStackTrace();
		}
		
		setConstraints();
	}
	
	private String getVariableName(finstance fi, int periodic, int count){
		String name = "f" + fi.getFrame().getID() + "s" + fi.getEdge().getSenderID() + "d" + fi.getEdge().getReceiverID() + "r" + count + "p" + periodic;
		return name;
	}
	
	private void setObjective(){
		GRBLinExpr expr = new GRBLinExpr();
	    expr.addTerm(1.0, latency);
	    try {
			model.setObjective(expr, GRB.MINIMIZE);
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("ILP Error: cannot set objective.");
			e.printStackTrace();
		}
	}
	
	public void setConstraints(){
		setObjective();
		
		Cnametag=1;
		// Collision Free Constraints
		for (int a=0; a<Finst.size();a++){
			for (int b=a+1; b<Finst.size();b++){
				if (Finst.get(a).getFrame().getID() == Finst.get(b).getFrame().getID()) continue; //if two frame instances have the same ID, then we do not create collision constraints. They will be constrained by path dependent constraints.
				// Check if two frame instance can interfere each other. Check possible connection across two transmissions
				if ((isExistEdge(Finst.get(a).getEdge().getSender(), Finst.get(b).getEdge().getReceiver(), E)) || (isExistEdge(Finst.get(b).getEdge().getSender(), Finst.get(a).getEdge().getReceiver(), E))){
					CollisionFree (a,b);
				}
				else if ((Finst.get(a).getEdge().getSenderID() == Finst.get(b).getEdge().getReceiverID()) || (Finst.get(b).getEdge().getSenderID() == Finst.get(a).getEdge().getReceiverID())){
					CollisionFree(a,b);
				}
			}
		}
		
		// Path dependent constraints within one frame instance
		Cnametag=1;
		for (int a=0; a<Finst.size();a++){
			PathDependent(a);
		}
		
		Cnametag=1;
		// Path Dependent Constraints
		for (int a=0; a<Finst.size();a++){
			for (int b=a+1; b<Finst.size(); b++){
				if (Finst.get(a).getFrame().getID() != Finst.get(b).getFrame().getID()) continue;
				if (Finst.get(a).getEdge().getReceiverID() == Finst.get(b).getEdge().getSenderID()){
					PathDependent(a,b);
				}
				else if (Finst.get(b).getEdge().getReceiverID() == Finst.get(a).getEdge().getSenderID()){
					PathDependent(b,a);
				}
			}
		}
		
		Cnametag=1;
		// Latency optimization constraints
		for (int a=0; a<Finst.size();a++){
			// If this frame instance is the "last" of its frame path, then we constrain the transmissions such a way that the latency to be minimized.
			if (Finst.get(a).getFrame().checkDestination(Finst.get(a).getEdge().getReceiverID())){
				LatencyConstraints(a);
			}
		}
		
		try {
			model.update();
			model.write("Debug.lp");
			model.optimize();
			
			// Log results
			writer myWriter = new writer("ILPresults.csv", false);
			myWriter.write("Transmissions,Slot Index\n");
			
			for (int a=0; a<var.size(); a++){
				for (int b=0; b<var.get(a).size(); b++){
					myWriter.write(var.get(a).get(b).get(GRB.StringAttr.VarName)+","+var.get(a).get(b).get(GRB.DoubleAttr.X)+"\n");
				}
			}
			
			model.dispose();
			env.dispose();
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void LatencyConstraints(int a){
		int weight = Finst.get(a).getEdge().getWeight();
		for (int i=0; i<(LCM/Finst.get(a).getFrame().getPeriod());i++){
			expr = new GRBLinExpr();
			expr.addTerm(-1.0, latency);
			expr.addTerm(1.0, var.get(a).get((i+1)*weight-1));
			try {
				model.addConstr(expr, GRB.LESS_EQUAL, (i*Finst.get(a).getFrame().getPeriod()), "Latency"+(Cnametag++));
				model.update();
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
	
	private void PathDependent(int a){
		for (int x=0; x<var.get(a).size()-1; x++){
			expr = new GRBLinExpr();
			expr.addTerm(1.0, var.get(a).get(x)); 
			expr.addTerm(-1.0, var.get(a).get(x+1)); 
			try {
				model.addConstr(expr, GRB.LESS_EQUAL, -1, "Intrapath"+(Cnametag++));
				model.update();
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				System.out.println("ILP error: Path dependent constraints within one frame instance cannot be created");
				e.printStackTrace();
			}

		}
	}
	
	private void PathDependent(int a, int b){
		int weight_a = Finst.get(a).getEdge().getWeight();
		int weight_b = Finst.get(b).getEdge().getWeight();

		for (int i=0; i<(LCM/Finst.get(a).getFrame().getPeriod()); i++){
			expr = new GRBLinExpr();
			expr.addTerm(1.0, var.get(a).get((i+1)*weight_a-1)); 
			expr.addTerm(-1.0, var.get(b).get(i*weight_b)); 
			try {
				model.addConstr(expr, GRB.LESS_EQUAL, -1, "Interpath"+(Cnametag++));
				model.update();
			} catch (GRBException e) {
				// TODO Auto-generated catch block
				System.out.println("ILP error: Path dependent constraints between two frame instances cannot be created");
				e.printStackTrace();
			}
		}
	}
	
	private void CollisionFree (int a, int b){ // a and b are the indices of frame instance
		// Here we generate collision free constraints for all "interfering" frame instances
		try {
			int infinite = Integer.MAX_VALUE;
			
			// Now we have two frame instances to build constraints.
			for (int i=0; i<var.get(a).size(); i++){
				for(int j=0; j<var.get(b).size(); j++){
					GRBVar BV = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "BV"+(BVnametag++));
					model.update();

					expr = new GRBLinExpr();
					expr.addTerm(1.0, var.get(a).get(i)); 
					expr.addTerm(-1.0, var.get(b).get(j)); 
					expr.addTerm(-infinite, BV);
					model.addConstr(expr, GRB.LESS_EQUAL, -1, "Collision"+(Cnametag++));

					expr = new GRBLinExpr();
					expr.addTerm(1.0, var.get(b).get(j)); 
					expr.addTerm(-1.0, var.get(a).get(i)); 
					expr.addTerm(infinite, BV);
					model.addConstr(expr, GRB.LESS_EQUAL, (-1+infinite), "Collision"+(Cnametag++));
				}	
			}
		} catch (GRBException e) {
			// TODO Auto-generated catch block
			System.out.println("ILP error: Binary variable generation for (A != B) has failed at BV:"+BVnametag);
			e.printStackTrace();
		}
	}
	
	private boolean isExistEdge (node from, node to, ArrayList<edge> E){
		for (edge e : E){
			if ((e.getSenderID()==from.getID()) && (e.getReceiverID()==to.getID())){
				return true;
			}
		}
		return false;
	}
}
