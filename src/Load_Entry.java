import java.util.concurrent.Semaphore;

public class Load_Entry {
	
	private int dest;
	private boolean busy;
	private int dir;
	private int clock;
	private Semaphore sem;
	
	public Load_Entry() {
		//System.out.println("Creando LB");
		dest = -1;
		busy = false;
		dir = -1; //ver
		clock = 0;
		sem = new Semaphore(1);
	}
	
	public void acquire() throws InterruptedException {
		sem.acquire();
	}
	
	public void release() {
		sem.release();
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
