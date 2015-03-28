// this algorithm creates communication links based on the position of the nodes.
// Input: the position of the sender and the position of the receiver.
// Computation: Considering the transmitting power and the distance in between nodes, the probability of receiving a message is computed.
// Output: the probability in double data type.

package algorithms;
import java.util.ArrayList;
import java.lang.Object;

import PhysicalModels.ErrorFunction;
import PhysicalModels.FreeSpaceModel;
import PhysicalModels.ItuModel;
import PhysicalModels.ModulationTypes;
import PhysicalModels.PathlossModes;
import PhysicalModels.lognormalModel;
import fileManager.writer;
import graphEntity.edge;
import graphEntity.node;

public class linker {

	private ArrayList<node> V;
	double pi = 0.99; // The default threshold probability for each communication link is 0.99
	double pmin = 0.7; // minimum probability which we make the edge. If less, then we do not create edge in the model
	PathlossModes model = PathlossModes.LOGNORMAL;  // The pathloss model which this link must use to estimate the probability
													// Default is lognormal shadowing model
	
	public linker (ArrayList<node> V, double p, PathlossModes mode){
		this.V = V;
		this.pi = p; // This probability is the value which the designer wants to achieve
		this.model = mode; // The designer must explicitly indicate what type of pathloss model for this link
	}
	
	public linker (ArrayList<node> V){
		this.V = V;
	}
	
	public edge getEdge(node sender, node receiver){
		int x1 = sender.getPos().getX();
		int y1 = sender.getPos().getY();
		double Pt1 = sender.getPower();
		int x2 = receiver.getPos().getX();
		int y2 = receiver.getPos().getY();
		double Sens2 = receiver.getSensitivity();
		
		double distance = Math.pow( Math.pow((x1-x2),2) + Math.pow((y1-y2), 2), 0.5);
		
		double probability = 0;
		
		switch (model){
			case FREE_SPACE:
				FreeSpaceModel fmodel = new FreeSpaceModel(distance, Pt1, Sens2);
				fmodel.setParameters(4, 0, 40);
				probability = fmodel.getProbability();
				break;
			case LOGNORMAL:
				lognormalModel lmodel = new lognormalModel(distance, Pt1, Sens2);
				lmodel.setParameters(4, 0, 45);
				probability = lmodel.getProbability();
				break;
			case RAYLEIGH:
				break;
			case ITU:
				ItuModel imodel = new ItuModel(distance, Pt1, Sens2);
				imodel.setParameters(37, 0);
				imodel.setDataRate(250000);
				imodel.setFrameSize(56);
				imodel.setBroadband(2.4 * Math.pow(10, 9));
				imodel.setModulation(ModulationTypes.FSK);
				probability = imodel.getProbability();
				break;
			default:
				break;
		}
		
		//System.out.println("distance="+ distance + " and probability="+probability);
		
		// if probability is 0, then the number of retransmissions required is infinite.
		// Therefore, we do not create an edge for these two nodes.
		if (probability <= pmin) return null;
		
		int weight = getWeight(probability);
		edge newEdge = new edge(sender,receiver,weight);
	
		return newEdge;
	}
	
	public void setThreshold(double threshold){
		if (threshold >= 0 && threshold <= 1){
			pi = threshold;
		}
	}
	
	private int getWeight(double p){
		int attempt = 1;
		double temp = p;
		
		while (temp<pi){
			temp+=p * Math.pow((1-p),attempt);
			attempt++;
		}
			
		return attempt;
	}

	public ArrayList<edge> getOutput (){
		ArrayList<edge> E = new ArrayList<edge>();
		
		for (node n : V){
			for (node m : V){
				if (n != m){
					if (getEdge(n,m) != null){
						E.add(getEdge(n, m));
					}
				}
			}
		}
		return E;
	}
}
