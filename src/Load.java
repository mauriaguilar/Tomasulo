import java.util.concurrent.Semaphore;

public class Load implements Runnable{
	
	private Clocks clk;
	private Load_Station load;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	private int pos;
	
	
	public Load(Clocks clk, Load_Station bufferLOAD, Memory memory, Bus bus) {
		this.clk = clk;
		load = bufferLOAD;
		this.memory = memory;
		cdb = bus;
		pos = 0;
	}


	@Override
	public void run() {
		boolean cdbWrited = false;
		while(true) {
			
			//System.out.println("LOAD pide clock");
			clk.waitClockLOAD();
			//System.out.println("LOAD obtuvo clock");
			
			//System.out.println("LOAD va a escribir");
			//System.out.println("LOAD Calculating instructions...");
			cdbWrited = tryCalculate(pos,load.length());
			if(!cdbWrited) 
				tryCalculate(0,pos-1);
			//System.out.println("LOAD escribio");

			//System.out.println("LOAD LIBERA...");
			writingReady();	
			//System.out.println("LOAD esperando lectura");
			waitToRead("L");	//solo por sincronizacion
			System.out.println("LOAD reading CDB...");
		}
	}
	
	private void check() {
		int index, value,i=0;
		
		// Get position of instruction, if exists
		
		
		// If an instruction exists
		/*if(index >= 0) {
			if(clk.checkCyclesLOAD()) {
				if(cdb.write_tryAcquire()) {
					pos++;
					clk.resetCyclesLOAD();
					value =  calc(index);
					System.out.println("LOAD["+i+"] writing "+value+" CDB...");
					cdb.set(value, "ROB"+load.get(index).getDest());
					delete(index);
				}
			}
		}*/
		
	}

	private void writingReady() {
		cdb.write_ready();
	}
	
	private void waitToRead(String UF) {
		try {
			//System.out.println("MUL READ ACQUIRE");
			cdb.read_acquire(UF);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public boolean tryCalculate(int ini, int fin) {
		int i, value;
		// 
		for(i=ini; i<fin; i++) {
			pos = i;
			// If an instruction exists
			if(load.get(i).getBusy()) {
				if(clk.checkCyclesLOAD()) {
					if(cdb.write_tryAcquire()) {
						pos = i+1;
						clk.resetCyclesLOAD();
						value =  calc(i);
						System.out.println("LOAD["+i+"] writing "+value+" CDB...");
						cdb.set(value, "ROB"+load.get(i).getDest());
						delete(i);
						return true;
					}
					else {
						System.out.println("CDB is Busy. LOAD["+i+"] Waiting...");
						return true;
					}
				}
				else {
					System.out.println("LOAD["+i+"] waiting clocks...");
					return false;
				}
			}		
		}
		return false;
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
