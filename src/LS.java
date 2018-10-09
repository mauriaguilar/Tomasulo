import java.util.concurrent.Semaphore;

public class LS {

	private Load_Entry[] ls;
	private Semaphore available;
	
	public LS(int cap) {
		ls = new Load_Entry[cap];
		for(int i=0; i<cap; i++) {
			ls[i] = new Load_Entry();
		}
		available = new Semaphore(cap);
	}
	
	public int getResource() throws InterruptedException {
		available.acquire();
		for (int i = 0; i < ls.length; i++) {
			if(!ls[i].getBusy())
				return i;
		}
		return -1;
	}
	
	public void releaseResource() {
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

	public void setData(int indexROB, int indexLS, boolean busy, int dir, int clock) {
		System.out.println("Instructions Writing in ADD["+indexLS+"] Station...");
		ls[indexLS].setDest(indexROB);
		ls[indexLS].setBusy(busy);
		ls[indexLS].setDir(dir);
		ls[indexLS].setClock(clock);
	}

}
