import java.util.concurrent.Semaphore;

public class Bus {
	
	//private Integer data_bus = new Integer(0);
	private int data;
	private String tag;
	private Semaphore mutex;

	public Bus() {
		mutex = new Semaphore(1);
		data = 0;
		tag = "";
	}
	
	public void acquire() throws InterruptedException {
		mutex.acquire();
	}
	
	public boolean tryAcquire() {
		return mutex.tryAcquire();
	}
	
	public void release() {
		mutex.release();
	}
	
	public void set(int data, String tag) {
		this.data = data;
		this.tag = tag;
	}

	public int getData() {
		return data;
	}
	
	public String getTag() {
		return tag;
	}
}
