package fileManager;

public class ns3adapter extends writer {

	private String filename;
	
	public ns3adapter (String filename){
		this.filename = filename;
		write ("", filename,false);
	}
	
	public void writeNode (){
		
	}
}
