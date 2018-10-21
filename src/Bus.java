import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Bus {
	
	//private Integer data_bus = new Integer(0);
	private int data;
	private String tag; 
	private Semaphore sem_write;
	private Semaphore sem_read_add;
	private Semaphore sem_read_mul;
	private Semaphore sem_read_rob;
	private Semaphore sem_read_load;
	private int counter;
	private int reads;
	private Semaphore sem_del;
	private Semaphore sem_read;

	public Bus() {
		sem_write = new Semaphore(1);
		sem_read_add = new Semaphore(1);
		sem_read_mul = new Semaphore(1);
		sem_read_rob = new Semaphore(1);
		sem_read_load = new Semaphore(1);

		try {
			sem_read_add.acquire();
			sem_read_mul.acquire();
			sem_read_rob.acquire();
			sem_read_load.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		counter = 0;
		data = -1;
		tag = "null";
		write_acquire();
		reads = 0;
		sem_del = new Semaphore(4);
		acquireDelete(4);
		sem_read = new Semaphore(4);
		try {
			sem_read.acquire(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

	public void read_acquire(String unit) throws InterruptedException {
		switch (unit) {
			case "A":
				sem_read_add.acquire();
				break;
			case "M":
				sem_read_mul.acquire();
				break;
			case "R":
				sem_read_rob.acquire();
				break;
			case "L":
				sem_read_load.acquire();
				break;
		}
	}
	
	public void write_ready() {
		sem_read.release();		
		if(sem_read.tryAcquire(3)) {
			sem_read_add.release();
			sem_read_mul.release();
			sem_read_rob.release();
			sem_read_load.release();
		}		
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
