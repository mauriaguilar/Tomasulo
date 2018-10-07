import java.util.concurrent.Semaphore;

public class Bus {
	
	//private Integer data_bus = new Integer(0);
	private int data;
	private String tag;
	private Semaphore sem_write;
	private Semaphore sem_read_add;
	private Semaphore sem_read_mul;
	private Semaphore sem_read_rob;
	private int counter;

	public Bus() {
		sem_write = new Semaphore(1);
		sem_read_add = new Semaphore(1);
		sem_read_mul = new Semaphore(1);
		sem_read_rob = new Semaphore(1);
		try {
			sem_read_add.acquire();
			sem_read_mul.acquire();
			sem_read_rob.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		counter = 0;
		data = 0;
		tag = "null";
	}
	
	public void write_acquire() throws InterruptedException {
		sem_write.acquire();
	}
	
	public boolean write_tryAcquire() {
		System.out.println("BUS tryAcquire() ");
		return sem_write.tryAcquire();
	}
	
	public void write_release() {
		sem_write.release();
	}

	public void read_acquire(String unit) throws InterruptedException {
		switch (unit) {
		case "A":
			System.out.println("SEM READ ADD LISTO PARA LEER");
			sem_read_add.acquire();
			System.out.println("SEM READ ADD COMENZANDO A LEER");
			break;
		case "M":
			System.out.println("SEM READ MUL LISTO PARA LEER");
			sem_read_mul.acquire();
			System.out.println("SEM READ MUL COMENZANDO A LEER");
			break;
		case "R":
			System.out.println("SEM READ ROB LISTO PARA LEER");
			sem_read_rob.acquire();
			System.out.println("SEM READ ROB COMENZANDO A LEER");
			break;
		}
		//counter--;
	}
	
	public void write_ready() {
		counter++;
		if(counter==3) {
			sem_read_add.release();
			sem_read_mul.release();
			sem_read_rob.release();
			counter=0;
		}
	}
	
	/*public void read_release(String unit) {
		switch (unit) {
		case "A":
			sem_read_add.release();
			break;
		case "M":
			sem_read_mul.release();
			break;
		case "L":
			sem_read_rob.release();
			break;
		}
	}*/
	
	
	
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
