package graphEntity;

public class Coord {
	
	int x;
	int y;
	//int z;
	//int yaw;
	
	public Coord(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return this.x;
	}

	public int getY(){
		return this.y;
	}
	@Override
	public String toString() {
		return "Coord [x=" + x + ", y=" + y + "]";
	}
	
}
