import java.util.concurrent.Semaphore;

public class Load implements Runnable{
	
	private Clocks clk;
	private LS load;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	private int pos;
	
	
	public Load(Clocks clk, LS bufferLOAD, Memory memory, Bus bus) {
		this.clk = clk;
		load = bufferLOAD;
		this.memory = memory;
		cdb = bus;
		pos = 0;
	}


	@Override
	public void run() {
		 
		while(true) {
			
			clk.waitClockLOAD();
			
			System.out.println("LOAD Calculating instructions...");
			tryCalculate();

			writingReady();
		}
	}
	
	private void tryCalculate() {
		int index, value,i=0;
		
		// Get position of instruction, if exists
		index = check(pos,load.length());
		if(index == -1) 
			index = check(0,pos-1);		
		
		// If an instruction exists
		if(index >= 0) {
			if(clk.checkCyclesLOAD())
				if(cdb.write_tryAcquire()) {
					pos++;
					clk.resetCyclesLOAD();
					value =  calc(index);
					System.out.println("LOAD["+i+"] writing "+value+" CDB...");
					cdb.set(value, "ROB"+load.get(index).getDest());
					delete(index);
				}
		}
		
	}

	private void writingReady() {
		//System.out.println("ADD WRITE READY");
		cdb.write_ready();
	}
	
	public int check(int ini, int fin) {
		int i;
		for(i=ini; i<fin; i++) {
			pos = i;
			if(load.get(i).getBusy() == true)
				return i;
		}
		return -1;
	}
	
	private int calc(int index) {
		int dir = load.get(index).getDir();
		return memory.getValue( dir );
	}
	
	public boolean getData() {
		data = false;
		for(int i=0; i<load.length(); i++)
			if(load.get(i).getBusy() == false)
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<load.length(); i++)
			if(load.get(i).getDir() == -1)
				cant++;
		return cant;
	}

	private void delete(int index) {
		load.del(index);
	}

	public void print() {
		String table = "\nLOAD\n";
		table += "N\t|DEST\t|DIR\t|Busy";
		for(int i=0; i<load.length(); i++)
			if(load.get(i).getBusy()) {
				table += ("\n" + i + "\t|" + load.get(i).getDest() + "\t|" 
					+ load.get(i).getDir() + "\t|" + load.get(i).getBusy());
			}
		System.out.println(table);
	}
}
