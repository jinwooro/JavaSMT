package fileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class writer {
	
	protected File name;
	protected BufferedWriter writer = null;
	
	public writer() {
		// TODO Auto-generated constructor stub
	}
	
	public writer (String filename, boolean mode) {
		
		//mode is false if no append
		
		// TODO Auto-generated constructor stub
		name = new File(filename);
		if (!name.exists()) {
			try {
				name.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		write("", mode);
	}
	
	protected void write(String text, String filename, boolean mode){
		// TODO Auto-generated constructor stub
		name = new File(filename);
		if (!name.exists()) {
			try {
				name.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		write(text,mode);
	}
	
	public void write(String text){
		// TODO Auto-generated constructor stub
		write(text,true);
	}
	
	protected void write(String text, boolean mode){
		// TODO Auto-generated constructor stub
		try{
			writer = new BufferedWriter(new FileWriter(name,mode));
			writer.write(text);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (Exception e){}
		}
	}
	
}
