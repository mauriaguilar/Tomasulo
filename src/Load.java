import java.util.concurrent.Semaphore;

public class Load extends Station implements Runnable{
	
	private Semaphore clk;
	private Load_Entry[] load;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	private Semaphore resource;
	private int pos;
	
	public Load(Semaphore clk, int cap, Memory memory, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		load = new Load_Entry[cap];
		for(int i=0; i<cap; i++) {
			load[i] = new Load_Entry();
		}
		this.memory = memory;
		cdb = bus;
		pos = 0;
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
			
			//System.out.println("LOAD Calculating instructions...");
			index = check(pos,load.length);
			if(index == -1) 
				index = check(0,pos-1);		
			// If an LD instruction exists
			if(index >= 0  && (Main.clocks > load[index].getClock())) {
				if(cdb.write_tryAcquire()) {
					value = calc(index);
					System.out.println("LOAD["+index+"] writing CDB...");
					cdb.set(value, "ROB"+load[index].getDest());
					delete(index);
				}
			}
			System.out.println("LOAD WRITE READY");
			cdb.write_ready(); //escribe o no
		}
	}

	public int check(int ini, int fin) {
		int i;
		for(i=ini; i<fin; i++) {
			pos = i+1;
			if(load[i].getBusy() == true)
				return i;
		}
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
	
	public void setData(int dest, boolean busy, int dir, int clock) {
		for(int i=0; i<load.length; i++) {
			if(load[i].getDir() == -1)
				System.out.println("Instructions Writing in Load["+i+"] Station...");
				load[i].setDest(dest);
				load[i].setBusy(busy);
				load[i].setDir(dir);
				load[i].setClock(clock);
				break;
		}
	}

	private void delete(int index) {
		load[index] = new Load_Entry();
		resource.release();
	}

	public void print() {
		String table = "\nLOAD\n";
		table += "N\t|DEST\t|DIR\t|Busy";
		for(int i=0; i<load.length; i++)
			if(load[i].getBusy()) {
				table += ("\n" + i + "\t|" + load[i].getDest() + "\t|" 
					+ load[i].getDir() + "\t|" + load[i].getBusy());
			}
		System.out.println(table);
	}
}
