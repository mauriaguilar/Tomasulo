import java.util.concurrent.Semaphore;

public class Load extends Station implements Runnable{
	
	private Semaphore clk;
	private Load_Entry[] load;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	private Semaphore resource;
	
	public Load(Semaphore clk, int cap, Memory memory, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		load = new Load_Entry[cap];
		for(int i=0; i<cap; i++) {
			load[i] = new Load_Entry();
		}
		this.memory = memory;
		cdb = bus;
	}


	@Override
	public void run() {
		int index, base, value,i=0;
		
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			index = check(i,load.length);
			if(index == -1) 
				index = check(0,i-1);
			
			// If an LD instruction exists
			if(index >= 0) {
				if(cdb.write_tryAcquire()) {
					value = calc(index);
					System.out.println("Load writing CDB...");
					cdb.set(value, "ROB"+load[index].getDest());
					delete(index);
				}
			}
			cdb.write_ready(); //escribe o no
		}
	}

	public int check(int ini, int fin) {
		int i;
		for(i=ini; i<fin; i++)
			if(load[i].getBusy() == true)
				return i;
		return -1;
	}
	
	private int calc(int index) {
		int dir = load[index].getDir();
		return memory.getValue( dir );
	}
	
	public boolean getData() {
		data = false;
		for(int i=0; i<load.length; i++)
			if(load[i].getBusy() == false)
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<load.length; i++)
			if(load[i].getDir() == -1)
				cant++;
		return cant;
	}
	
	public void getResource() throws InterruptedException {
		resource.acquire();
	}
	
	public void setData(int dest, boolean busy, int dir) {
		System.out.println("Writing in Load Station...");
		int pos = -1;
		for(int i=0; i<load.length; i++) {
			if(load[i].getDir() == -1)
				pos = i;
		}
		if(pos >= 0) {
			load[pos].setDest(dest);
			load[pos].setBusy(busy);
			load[pos].setDir(dir);
		}
		else
			System.out.println("ERROR EN setData() de Load");
	}

	private void delete(int index) {
		load[index] = new Load_Entry();
	}
}
