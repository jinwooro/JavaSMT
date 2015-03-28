package PhysicalModels;

public class ItuModel extends physicalModelBase {

	// The value of distance power loss coefficient (N) can be found in this table.
	//	Frequency band	Residential area	Office area		Commercial area
	//	900 MHz			N/A					33				20
	//	1.2 GHz			N/A					32				22
	//	1.3 GHz			N/A					32				22
	//	1.8 GHz			28					30				22
	//	4 GHz			N/A					28				22
	//	5.2 GHz			N/A					31				N/A
	//	60 GHz			N/A					22				17
	// Please check:
	// Chrysikos, T.; Georgopoulos, G.; Kotsopoulos, S., "Site-specific validation of ITU indoor path loss model at 2.4 GHz," World of Wireless, Mobile and Multimedia Networks & Workshops, 2009. WoWMoM 2009. IEEE International Symposium on a, vol., no., pp.1,6, 15-19 June 2009

	private int N = 0; // The distance power loss coefficient.
	
	// The value of floor penetration loss factor (pn) can be found.
	//Frequency band	Number of floors	Residential area	Office area		Commercial area
	//900 MHz			1					N/A					9				N/A
	//900 MHz			2					N/A					19				N/A
	//900 MHz			3					N/A					24				N/A
	//1.8 GHz			n					4n					15+4(n-1)		6 + 3(n-1)
	//2.0 GHz			n					4n					15+4(n-1)		6 + 3(n-1)
	//5.2 GHz			1					N/A					16				N/A
	private int pn = 0; // The floor loss penetration factor.
	
	public ItuModel(double distance, double TXpower, double RXsens) {
		super(distance, TXpower, RXsens);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void getRSSI() {
		// TODO Auto-generated method stub
		super.RSSI = TXpower - 20 * Math.log10(super.Bandwidth / 1000000) - N * Math.log10(super.distance) + pn + 28;
	}
	
	public void setParameters(int DistancePowerLossCoef, int FloorPenetrationLossFactor){
		this.N = DistancePowerLossCoef; // Pathloss exponents
		this.pn = FloorPenetrationLossFactor; // Shadowing standard deviation (we set to 0 to average it?)
		
		this.getRSSI();
		calcProbability();
	}

}
