package PhysicalModels;


public abstract class physicalModelBase {
	
	// Universal constants
	double BoltzmanConstant = 1.3806504 * Math.pow(10, -23);		// Boltzman constant
	double LightSpeed = 3 * Math.pow(10, 8);
	
	// Radio specific variables
	double Bandwidth = 2.4 * Math.pow(10, 9);
	double DataRate = 250 * Math.pow(10, 3);
	double SignalWavelength = LightSpeed / Bandwidth;
	ModulationTypes modulation = ModulationTypes.FSK; // We set default modulation to be FSK.
	double TXpower; // Reception sensitivity
	double RXsens; // Transmitting power
	int d0 = 1;	// reference distance
	int f = 56; // frame size (number of bytes).

	// Environmental variables
	double Temperature = 300;		// Temperature in degree K (i.e., 300 is 20 degree celcius)
	double NoiseFigure = 16;		// Noise figure in dB
	int NoiseBandwidth = 1000000;		// Noise bandwidth
	
	// Distance between two nodes
	double distance;
	
	// On calculation variables.
	double ThermalNoise = -105; // This is typical value of noise in dB at 300K.
	double RSSI; // Received signal strength in dB scale
	double SNR; // SNR is in dB scale
	double probability;
	double minSNR;
	
	public physicalModelBase(double distance, double TXpower, double RXsens) {
		// TODO Auto-generated constructor stub
		this.distance = distance;
		this.TXpower = TXpower;
		this.RXsens = RXsens;
	}

	public void setMinSNRbyProbability (double MinimumProbability){
		// This is the SNR value which 
		this.minSNR =  10 * Math.log10(-1.28 * Math.log((2 * (1 - Math.pow(0.9, 1/ (8 * f))))));
	}
	
	public void setThermalNoise(double NoiseFigure_dB, int NoiseBandWidth, double TemperatureInKelvin){
		this.NoiseFigure = NoiseFigure_dB;
		this.NoiseBandwidth = NoiseBandWidth;
		this.Temperature = TemperatureInKelvin;
		
		this.ThermalNoise = 10 * Math.log10(BoltzmanConstant * Temperature * NoiseBandwidth) + NoiseFigure + 1 + 30; // adding 30 to convert dB to dBm
	}
	
	public void setThermalNoise(double ThermalNoise_dB){
		this.ThermalNoise = ThermalNoise_dB;
	}
	
	protected void calcSNR(){
		this.SNR = RSSI - ThermalNoise;
	}
	
	public double getProbability(){
		calcProbability();
		return this.probability;
	} 
	
	public void setModulation(ModulationTypes ModulationType){
		this.modulation = ModulationType;
	}
	
	public void setDataRate(double DataRateBitPerSecond){
		this.DataRate = DataRateBitPerSecond;
	}
	
	public void setBroadband(double BroadbandFrequencyInHz){
		this.Bandwidth = BroadbandFrequencyInHz;
	}
	
	public void setFrameSize(int FrameSizeInBytes){
		this.f = FrameSizeInBytes;
	}
	
	protected void calcProbability() {
		// TODO Auto-generated method stub
		// Calculate signal-to-noise ratio
		calcSNR();
		
		double pe = 1;	 // pe is the packet error rate (PER) value. Initially, it is 1.
		this.probability = 0; // PRR, packet receiving rate, is initially 0.
		//System.out.println(SNR);
		SNR = Math.pow(10, SNR/10);	// Convert the SNR scale into linear
		
		switch(modulation){
			case FSK:
				if (SNR <= 0) break;
				double temp = Math.pow((SNR), 0.5) / Math.pow(2, 0.5);
				ErrorFunction erf = new ErrorFunction();
				pe = 0.5 * (1 - erf.erf(temp));
				this.probability = Math.pow((1-pe), (8*f));
				break;
			case PSK:
				break;
			default:
				break;
		}
	}
	
	abstract protected void getRSSI();
}
