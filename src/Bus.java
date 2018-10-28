import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Bus {
	
	//private Integer data_bus = new Integer(0);
	private int data;
	private String tag; 
	private Semaphore sem_write;
	private Semaphore write_ready;
	private Semaphore sem_read;
	private int counter;
	private int reads;
	private Semaphore sem_del;

	public Bus() {
		sem_write = new Semaphore(1);
		write_ready = new Semaphore(0);
		counter = 0;
		data = -1;
		tag = "null";
		write_acquire();
		reads = 0;
		sem_del = new Semaphore(0);
		sem_read = new Semaphore(0);
	}
	
	public void write_acquire() {
		try {
			sem_write.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean write_tryAcquire() {
		boolean result = false;
		result = sem_write.tryAcquire();
		//System.out.println("Result write_tryAcquire: "+result);
		return result;
	}
	
	public void write_release() {
		//delete();
		if( !haveAvailables() ) {
			//System.out.println("Write_release");
			sem_write.release();
		}
	}
	
	public boolean haveAvailables() {
		//System.out.println("Disponibles: "+sem_write.availablePermits());
		if(sem_write.availablePermits() > 0) return true;
		else return false;
	}

	public void delete() {
		data = -1;
		tag = "null";
	}	
	
	public void write_ready() {
		
		write_ready.release();		
		if(write_ready.tryAcquire(3)) {
			sem_read.release();
		}		
	}	

	public void read_acquire() throws InterruptedException {
		
		sem_read.acquire();
	}
	
	public void read_release() {
		
		sem_read.release();
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

	public void tryDeleteCDB() {
		sem_del.release(1);
	}

	public void acquireDelete(int i) {
		try {
			sem_del.acquire(i);
			sem_read.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
