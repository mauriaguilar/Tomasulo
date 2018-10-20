import java.util.concurrent.Semaphore;

public class Load_Entry {
	
	private int dest;
	private boolean busy;
	private int dir;
	private int base;
	private int shift;
	private String shift_tag;
	private int clock;
	private Semaphore sem;
	private boolean ready;
	
	public Load_Entry() {
		//System.out.println("Creando LB");
		dest = -1;
		busy = false;
		dir = -1; //ver
		base = -1;
		shift = -1;
		shift_tag = "";
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

	public void setReady(boolean b) {
		ready = b;
	}

	public boolean getReady() {
		return ready;
	}
	
	public int getBase() {
		return base;
	}
	
	public int getShift() {
		return shift;
	}
	
	public String getShiftTag() {
		return shift_tag;
	}
	
	public void setBase(int value) {
		base = value;
	}
	
	public void setShift(int value) {
		shift = value;
	}
	
	public void setShiftTag(String value) {
		shift_tag = value;
	}
}
