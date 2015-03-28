package Main;

import java.io.File;

import fileManager.writer;
import PhysicalModels.ItuModel;
import PhysicalModels.ModulationTypes;
import PhysicalModels.PathlossModes;
import PhysicalModels.lognormalModel;

public class pdfChecker extends writer{
	
	double testingTransmitPoewr = 0; //dBm
	double testingSensitivity = -90; //dBm
	ModulationTypes mode = ModulationTypes.FSK;
	PathlossModes model = PathlossModes.LOGNORMAL;
	
	public pdfChecker(double txp, double rxp, ModulationTypes mode, PathlossModes model, String fname) {
		// TODO Auto-generated constructor stub
		this.testingTransmitPoewr = txp;
		this.testingSensitivity = rxp;
		this.mode = mode;
		this.model = model;
		
		// The file name must have CSV extension
		write ("", fname, false);
	}
	
	public void genetratePDF(double resolution){
		// Create all files
		double distance = 0;
		double probability = 1;
		
		while (probability > 0.001){
			probability = calcProbability(distance);
			write(distance + "," + probability + "\n");
			distance+=resolution;
			System.out.println("Distance:" + distance);
		}
	}
	
	public double calcProbability(double distance){
		
		double p = 0;
		
		switch (model){
		case FREE_SPACE:
			break;
		case LOGNORMAL:
			lognormalModel lmodel = new lognormalModel(distance, testingTransmitPoewr, testingSensitivity);
			lmodel.setDataRate(250);
			lmodel.setParameters(2.8, 0, 0);
			lmodel.setFrameSize(56);
			lmodel.setBroadband(2.4 * Math.pow(10, 9));
			lmodel.setModulation(ModulationTypes.FSK);
			p = lmodel.getProbability();
			break;
		case RAYLEIGH:
			break;
		case ITU:
			ItuModel imodel = new ItuModel(distance, testingTransmitPoewr, testingSensitivity);
			imodel.setParameters(29, 15);
			imodel.setDataRate(250000);
			imodel.setFrameSize(56);
			imodel.setBroadband(2.4 * Math.pow(10, 9));
			imodel.setModulation(ModulationTypes.FSK);
			p = imodel.getProbability();
			break;
		default:
			break;
		}
		
		return p;
	}

}
