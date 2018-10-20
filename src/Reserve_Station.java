import java.util.concurrent.Semaphore;

public class Reserve_Station {

	private RS_Entry[] rs;
	private Semaphore available;
	private int cap;
	
	public Reserve_Station(int cap) { 
		rs = new RS_Entry[cap];
		for(int i=0; i<cap; i++) {
			rs[i] = new RS_Entry();
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
		for (int i = 0; i < rs.length; i++) {
			if(!rs[i].getBusy())
				return i;
		}
		return -1;
	}
	
	public void releaseResource() {
		if(available.availablePermits() < cap)
			available.release();
	}

	public int length() {
		return rs.length;
	}

	public RS_Entry get(int j) {
		return rs[j];
	}

	public void del(int index) {
		rs[index] = new RS_Entry();
		releaseResource();
	}

	public void setData(int dest, int indexRS, boolean busy, String op, int vj, int vk, String qj, String qk, int clock) {
		System.out.println("Instructions Writing in RS["+indexRS+"] Station...");
		rs[indexRS].setDest(dest);
		rs[indexRS].setBusy(busy);
		rs[indexRS].setOp(op);
		rs[indexRS].setQj(qj);
		rs[indexRS].setQk(qk);
		rs[indexRS].setVj(vj);
		rs[indexRS].setVk(vk);
		rs[indexRS].setClock(clock);
		rs[indexRS].setReady(false);
	}
	
	
}
