
public class Load_Entry {
	
	private int dest;
	private boolean busy;
	private int dir;
	
	public Load_Entry() {
		//System.out.println("Creando LB");
		dest = -1;
		busy = false;
		dir = -1; //ver
	}
	
	public boolean getBusy() {
		return busy;
	}
	
	public int getDir() {
		return dir;
	}
	
	public void setDest(int dest) {
		this.dest = dest;
	}
	
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	
	public void setDir(int dir) {
		this.dir = dir;
	}

	public int getDest() {
		return dest;
	}
}
