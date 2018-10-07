
public class Load_Entry {
	
	private int dest;
	private boolean busy;
	private int dir;
	private int clock;
	
	public Load_Entry() {
		//System.out.println("Creando LB");
		dest = -1;
		busy = false;
		dir = -1; //ver
		clock = 0;
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

	public void setClock(int value) {
		clock = value;
	}
	
	public int getClock() {
		return clock;
	}
}
