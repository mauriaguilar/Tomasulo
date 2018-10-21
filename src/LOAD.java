public class LOAD implements Runnable{
	
	private Clocks clk;
	private LOAD_Station rs;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	private int pos;
	
	
	public LOAD(Clocks clk, LOAD_Station bufferLOAD, Memory memory, Bus bus) {
		this.clk = clk;
		rs = bufferLOAD;
		this.memory = memory;
		cdb = bus;
		pos = 0;
	}


	@Override
	public void run() {
		boolean cdbWrited = false;
		while(true) {
			
			
			clk.waitClockLOAD();
			
			//System.out.println("LOAD Calculating instructions...");
			cdbWrited = tryCalculate(pos,rs.length());
			if(!cdbWrited) 
				tryCalculate(0,pos-1);

			//System.out.println("LOAD LIBERA...");
			String UF="L";
			writingReady();	
			waitToRead(UF); // case acquire

			//System.out.println("LOAD reading CDB... AFTER");
			
			readAndReplace();
			
			cdb.tryDeleteCDB(); // Delete CDB

		}
	}
	
	private void waitToRead(String UF) {
		try {
			//System.out.println("ADD READ ACQUIRE");
			cdb.read_acquire(UF);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	private boolean checkOperands(int i) {
		if(rs.get(i).getShiftTag().equals(""))
			return true;
		else
			return false;
	}

	private void writingReady() {
		cdb.write_ready();
	}
	
	public boolean tryCalculate(int ini, int fin) {
		int i, value;
		// 
		for(i=ini; i<fin; i++) {
				pos = i;
				if( rs.get(i).getBusy() ) {//&& rs.get(i).getReady()) {
					if(checkOperands(i)) {
						if(clk.checkCyclesLOAD()) {
							//System.out.println("LOAD Disponibles: "+cdb.haveAvailables());
							if(cdb.write_tryAcquire()) {
								pos = i+1;
								clk.resetCyclesLOAD();
								value =  calc(i);
								System.out.println("LOAD["+i+"] writing "+value+" in CDB...");
								cdb.set(value, "ROB"+rs.get(i).getDest());
								delete(i);
							}
							else {
							System.out.println("CDB is Busy. LOAD["+i+"] Waiting...\"");
							}
							return true;
						}
						else {
							System.out.println("LOAD["+i+"] waiting clocks...");
							return false;
						}
					}
				}
				
		}
		return false;
	}
	
	private int calc(int index) {
		int dir = rs.get(index).getBase() + rs.get(index).getShift();
		rs.get(index).setDir(dir);
		int value = memory.getValue(dir);
		return value;
	}
	
	public boolean getData() {
		data = false;
		for(int i=0; i<rs.length(); i++)
			if(rs.get(i).getBusy() == false)
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rs.length(); i++)
			if(rs.get(i).getDir() == -1)
				cant++;
		return cant;
	}

	private void delete(int index) {
		rs.del(index);
	}
	
	private void readAndReplace() {
		for(int j=0; j<rs.length(); j++){
			if( rs.get(j).getBusy() ) {
				// Replacing operands
				if( cdb.getTag().equals(rs.get(j).getShiftTag()) ){
					System.out.println("LOAD["+j+"] getting "+cdb.getData()+" from CDB...");
					rs.get(j).setShiftTag("");
					rs.get(j).setShift(cdb.getData());
				}
				// Setting ready for instructions in station
				if( !rs.get(j).getReady() )
					rs.get(j).setReady(true);
			}
		}
	}

	public void print() {
		String table = "\nLOAD\n";
		table += "N\t|DEST\t|BASE\t|SHIFT\t|TAG\t|Busy";
		for(int i=0; i<rs.length(); i++)
			if(rs.get(i).getBusy()) {
				table += ("\n" + i + "\t|" + rs.get(i).getDest() + "\t|" 
					+ rs.get(i).getBase() + "\t|" + rs.get(i).getShift() + "\t|" 
					+ rs.get(i).getShiftTag() + "\t|" + rs.get(i).getBusy());
			}
		System.out.println(table);
	}
}
