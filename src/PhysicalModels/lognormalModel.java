package PhysicalModels;


public class lognormalModel extends physicalModelBase {

	// Log-normal model specific parameters
	private double n = 4; // Typically, indoor = 3.3 (2.1 ~ 4.5), outdoor = 4.7 (4.3 ~ 5.1). Reference: Zuniga07, ACM transactions on Sensor networks.
	private double sigma = 5.5; // Typically, indoor = 5.5 (4.6 ~ 6.8), outdoor = 3.2 (2.6 ~ 3.8). Reference: Zuniga07,  ACM transactions on Sensor networks.
	private double PLd0 = 40;
	private double pathloss = 0;
	
	public lognormalModel(double distance, double TXpower, double RXsens) {
		super(distance, TXpower, RXsens);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void getRSSI() {
		// TODO Auto-generated method stub
		pathloss = PLd0 + 10 * n * Math.log10(super.distance / super.d0);
		this.RSSI = TXpower - pathloss;
	}
	
	public void setParameters(double PathLossExponent, double ShadowingDeviation, double ReferenceDistancePower){
		n = PathLossExponent; // Pathloss exponents
		sigma = ShadowingDeviation; // Shadowing standard deviation (we set to 0 to average it?)
		PLd0 = ReferenceDistancePower; // Path loss at reference distance
				
		this.getRSSI();
	}

}
