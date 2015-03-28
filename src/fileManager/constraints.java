package fileManager;

import frames.finstance;
import graphEntity.edge;
import graphEntity.node;

import java.io.File;
import java.util.ArrayList;

import Main.main;

public class constraints extends writer{

	private int LCM;
	private ArrayList<finstance> Finst; 
	String file = "SMTconstraints.txt";
			
	public constraints() {
		write ("", file,false);
	}
	
	public void writeComments (String txt){
		write(txt);
	}
	
	public void generateConstraints(ArrayList<finstance> _Finst, int _LCM, ArrayList<edge> E){
		Finst= _Finst;
		LCM = _LCM;
		
		writeDeclarations();

		// Intra path dependent constraints
		int index = 0;
		for (finstance i : Finst){
			writeRetransmissionDependency(i, index++);
		}
		
		// Inter path dependent constraints
		writeInterPathDependent();
		
		// Multi-receiver constraints
		writeMultiReceiver();
		
		
		// Collision-free constraints
		writeCollisionFree(E);
		
		
		writeReporter();
	}
	
	private void writeDeclarations(){
		boolean debug = true;
		
		if (debug) write("(declare-datatypes () ((PATH (mk_A (id Int) (from Int) (to Int)))))\n");
		for (int a=0; a<Finst.size();a++){
			write("(declare-const R"+a+" (Array Int Int))\n");
			if (debug) {
				write("(declare-const INFO"+a+" PATH)\n");
				String temp = "(= (id INFO"+a+") "+Finst.get(a).getFrame().getID()+") ";
				temp+= "(= (from INFO"+a+") "+Finst.get(a).getEdge().getSenderID()+") ";
				temp+= "(= (to INFO"+a+") "+Finst.get(a).getEdge().getReceiverID()+") ";
				write("(assert (and "+temp+"))\n");
			}
		}
	}
	
	private void writeCollisionFree (ArrayList<edge> E){
		for (int a=0; a<Finst.size();a++){
			for (int b=a+1; b<Finst.size();b++){
				if ((isExistEdge(Finst.get(a).getEdge().getSender(), Finst.get(b).getEdge().getReceiver(), E)) || (isExistEdge(Finst.get(b).getEdge().getSender(), Finst.get(a).getEdge().getReceiver(), E))){
					if ((Finst.get(a).getEdge().getSenderID() == Finst.get(b).getEdge().getSenderID()) && (Finst.get(a).getFrame().getID() == Finst.get(b).getFrame().getID())){
						continue;
					}
					else if (isExistEdge(Finst.get(b).getEdge().getSender(), Finst.get(a).getEdge().getReceiver(), E) && isExistEdge(Finst.get(a).getEdge().getSender(), Finst.get(b).getEdge().getReceiver(), E)){
						continue;
					}
					else{
						String R1 = "(select R"+a+" a)";
						String R2 = "(select R"+b+" b)";
						write("(assert (forall ((a Int) (b Int)) (distinct "+R1+" "+R2+")))\n");
					}
				}
			}
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
	
	private void writeRetransmissionDependency (finstance fi, int b){
		int repetition = LCM / fi.getFrame().getPeriod(); // Number of repetition due to the periodic nature within hyper period
		int retransmission = fi.getEdge().getWeight(); // weight
		int total = retransmission * repetition; // total transmission
		
		// Sequential constraints =>>  Ra < Rb, if a+1 = b.
		String text;
		for (int a=0; a<total-1; a++){
			text= "(assert (< (select R"+b+" "+a+") (select R"+b+" "+(a+1)+")))\n";
			write(text);
			text="";
		}
		
		// if it is the first edge in the path: constraints =>> R of the very first transmission >= 0
		if (fi.isFirst()){
			for (int n=0; n<repetition; n++){
				text= "(assert (<= " + (n*fi.getFrame().getPeriod()) + " (select R"+b+" "+(n*retransmission)+")))\n";
				write(text);
				text="";
			}
		} 
		
		// if it is the last edge in the path: constraints =>> R of the very last transmission < Period
		if (fi.isLast()){
			for (int n=0; n<repetition; n++){
				text= "(assert (< (select R"+b+" "+((n+1)*retransmission-1)+") "+ ((n+1)* fi.getFrame().getPeriod()) +"))\n";
				write(text);
				text="";
			}
		}
	}
	
	private void writeInterPathDependent (){
				
				//Between two consecutive frame instances
				for (int a=0; a<Finst.size();a++){
					for (int b=a+1; b<Finst.size();b++){
						if (Finst.get(a).getFrame().getID() !=Finst.get(b).getFrame().getID() ) continue; // If two frames have different ID, then no constraints.
						
						int repetition1 = LCM / Finst.get(a).getFrame().getPeriod();
						int repetition2 = LCM / Finst.get(b).getFrame().getPeriod();
						int retransmission1 = Finst.get(a).getEdge().getWeight();
						int retransmission2 = Finst.get(b).getEdge().getWeight(); 
						
						// If the frame instances are consecutive, then we need to make a "cascading relationship" between them
						if (isNext(Finst.get(a), Finst.get(b))){
							for (int x=0; x<repetition1; x++){ 				// If the frame is one after another, e.g., [a b] [b c]
								String text = "(assert (< ";
								text += "(select R"+a+"(- (* (+ "+x+" 1) "+ retransmission1 +") 1)) ";
								text += "(select R"+b+" (* "+x+" "+ retransmission2 +"))))\n";	
								write(text);
							}
						}
						
						// If the frame instances are inverse consecutive, then we need to make a "cascading relationship" between them inversely.
						if (isNext(Finst.get(b), Finst.get(a))){							// If the frame is one after another, e.g., [b c] [a b]
							for (int x=0; x<repetition2; x++){
								String text = "(assert (< ";
								text += "(select R"+b+"(- (* (+ "+x+" 1) "+retransmission2+") 1)) ";
								text += "(select R"+a+" (* "+x+" "+retransmission1+"))))\n";
								write(text);
							}
						}
						
		
					}
				}
	}
	
	private void writeMultiReceiver (){
		
		boolean debug = true;
		
		for (int a=0; a<Finst.size();a++){
			for (int b=a+1; b<Finst.size();b++){
				if (Finst.get(a).getFrame().getID() !=Finst.get(b).getFrame().getID() ) continue; // if not same frame id, then skip. Broadcast means the same message
				if (Finst.get(a).getEdge().getSenderID() != Finst.get(b).getEdge().getSenderID() ) continue; // If two frames are having the same sender, then multi-receiver constraints must be created
				
				int repetition  = LCM / Finst.get(a).getFrame().getPeriod();
				int retransmission1 = Finst.get(a).getEdge().getWeight();
				int retransmission2 = Finst.get(b).getEdge().getWeight();
			
				for (int x=0; x<repetition; x++){
					for (int y=0; y< min(retransmission1, retransmission2); y++){
						String text = "(assert (= ";
						text += "(select R"+a+" "+ (x*retransmission1 + y) +")";
						text += "(select R"+b+" "+ (x*retransmission2 + y) +")))\n";
						write(text);
					}
					
				}
	
				if (debug){
					System.out.println("Overlapped edges: [" + Finst.get(a).getEdge().getSenderID() + "," + Finst.get(a).getEdge().getReceiverID() + "] - [" + Finst.get(b).getEdge().getSenderID() + ","+Finst.get(b).getEdge().getReceiverID() +"] " + Finst.get(a).getEdge().getWeight() + " , " + Finst.get(b).getEdge().getWeight());
					System.out.println("Overlapped Constraints R index: " + a + "," + b);
				}
				
			}
		}
	}
	private boolean isNext(finstance a, finstance b){
		if (a.getEdge().getReceiverID() == b.getEdge().getSenderID() ){
			return true;
		}
		return false;
	}
	
	private void writeReporter(){
		write("(check-sat)\n");
		for (int a=0; a<Finst.size();a++){
			write("(get-value ((id INFO"+a+") (from INFO"+a+") (to INFO"+a+")))\n");
			
			int repetition1 = LCM / Finst.get(a).getFrame().getPeriod();
			int retransmission1 = Finst.get(a).getEdge().getWeight();

			for (int b=0; b<repetition1 * retransmission1; b++){
				write("(get-value ((select R"+a+" "+b+")))\n");
			}
		}
	}
	
	private int min(int a, int b){
		if (a < b){
			return a;
		}
		return b;
	}
}
