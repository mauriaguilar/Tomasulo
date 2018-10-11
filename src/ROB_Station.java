import java.util.concurrent.Semaphore;

public class ROB_Station {

		private ROB_Entry [] rob;
		private Semaphore available;
		private int cap;
		
		public ROB_Station(int cap){
			rob = new ROB_Entry[cap];
			for(int i=0; i<cap; i++) {
				rob[i] = new ROB_Entry();
			}
			available = new Semaphore(cap); 
			this.cap = cap;
		}
		
		public ROB_Entry get(int i) {
			return rob[i];
		}
		

		public void getResource() {
			try {
				available.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void releaseResource() {
			if(available.availablePermits() < cap)
				available.release();
		}

		public int length() {
			return rob.length;
		}

		public void del(int index) {
			rob[index] = new ROB_Entry();
			releaseResource();
		}
}
