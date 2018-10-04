import java.util.concurrent.Semaphore;

public class Load extends Station implements Runnable{
	
	private Semaphore clk;
	private LB[] load;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	
	public Load(Semaphore clk, int cap, Memory memory, Bus bus) {
		this.clk = clk;
		load = new LB[cap];
		for(int i=0; i<cap; i++) {
			load[i] = new LB();
		}
		this.memory = memory;
		cdb = bus;
	}


	@Override
	public void run() {
		int index, base, value;
		
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			index = check();
			
			// If an LD instruction exists
			if(index >= 0) {
				value = calc(index);
				try {
					cdb.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("Load writing CDB...");
				cdb.set(value, "ROB"+load[index].getDest());
				delete(index);
			}
			
		}

	}

	public int check() {
		int i;
		for(i=0; i<load.length; i++)
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
		load[index] = new LB();
	}
}
