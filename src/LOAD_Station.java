import java.util.concurrent.Semaphore;

public class LOAD_Station {

	private Load_Entry[] ls;
	private Semaphore available;
	private int cap;
	
	public LOAD_Station(int cap) {
		ls = new Load_Entry[cap];
		for(int i=0; i<cap; i++) {
			ls[i] = new Load_Entry();
		}
		available = new Semaphore(cap);
		this.cap = cap;
	}
	
	public int getResource() {
		try {
			available.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < ls.length; i++) {
			if(!ls[i].getBusy())
				return i;
		}
		return -1;
	}
	
	public void releaseResource() {
		if(available.availablePermits() < cap)
			available.release();
	}

	public int length() {
		return ls.length;
	}

	public Load_Entry get(int j) {
		return ls[j];
	}

	public void del(int index) {
		ls[index] = new Load_Entry();
		releaseResource();
	}

	public void setData(int indexROB, int indexLS, boolean busy, int dir, int base, int shift, String shift_tag, int clock) {
		System.out.println("Instructions Writing in LOAD["+indexLS+"] Station...");
		ls[indexLS].setDest(indexROB);
		ls[indexLS].setBusy(busy);
		ls[indexLS].setDir(dir);
		ls[indexLS].setBase(base);
		ls[indexLS].setShift(shift);
		ls[indexLS].setShiftTag(shift_tag);
		ls[indexLS].setClock(clock);
		ls[indexLS].setReady(false);
	}

}
