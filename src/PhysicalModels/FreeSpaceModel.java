package PhysicalModels;

public class FreeSpaceModel extends physicalModelBase {

	// For now, it is a faked model which produce a "looking-good" connection.
	// This will be corrected later on.
	
	// Log-normal model specific parameters
	private double n = 4; // Typically, indoor = 3.3 (2.1 ~ 4.5), outdoor = 4.7 (4.3 ~ 5.1). Reference: Zuniga07, ACM transactions on Sensor networks.
	private double sigma = 5.5; // Typically, indoor = 5.5 (4.6 ~ 6.8), outdoor = 3.2 (2.6 ~ 3.8). Reference: Zuniga07,  ACM transactions on Sensor networks.
	private double PLd0 = 55;
	
	
	public FreeSpaceModel(double distance, double TXpower, double RXsens) {
		super(distance, TXpower, RXsens);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void getRSSI() {
		// TODO Auto-generated method stub
		double PathLoss = PLd0 + 10 * n * Math.log10(super.distance / super.d0) + sigma;
		super.RSSI = super.TXpower - PathLoss;
	}
	
	public void setParameters(double PathLossExponent, double ShadowingDeviation, double ReferenceDistancePower){
		n = PathLossExponent; // Pathloss exponents
		sigma = ShadowingDeviation; // Shadowing standard deviation (we set to 0 to average it?)
		PLd0 = ReferenceDistancePower; // Path loss at reference distance
		
		super.setThermalNoise(-77);

		this.getRSSI();
		calcProbability();
	}

	@Override
	protected void calcProbability() {
		// TODO Auto-generated method stub
		
	}

}
