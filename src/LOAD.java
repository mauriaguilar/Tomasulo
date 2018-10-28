public class LOAD implements Runnable{
	
	private LOAD_Station rs;
	private boolean data;
	private Memory memory;
	private Registers registers;
	private Bus cdb;
	private int pos;
	
	
	public LOAD(LOAD_Station bufferLOAD, Memory memory, Bus bus) {
		rs = bufferLOAD;
		this.memory = memory;
		cdb = bus;
		pos = 0;
	}


	@Override
	public void run() {
		boolean cdbWrited = false;
		while(true) {
			
			
			Clocks.waitClockLOAD();
			
			cdbWrited = tryCalculate(pos,rs.length());
			if(!cdbWrited) 
				tryCalculate(0,pos-1);
			write_ready();
			
			waitToRead(); // case acquire
			readAndReplace();
			
			read_ready();
			cdb.tryDeleteCDB(); // Delete CDB

		}
	}


	private void waitToRead() {
		try {
			cdb.read_acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void read_ready() {
		cdb.read_release();
	}


	private boolean checkOperands(int i) {
		if(rs.get(i).getShiftTag().equals(""))
			return true;
		else
			return false;
	}

	private void write_ready() {
		cdb.write_ready();
	}
	
	public boolean tryCalculate(int ini, int fin) {
		int i, value;
		// 
		for(i=ini; i<fin; i++) {
				pos = i;
				if( rs.get(i).getBusy() ) {//&& rs.get(i).getReady()) {
					if(checkOperands(i)) {
						if(Clocks.checkCyclesLOAD()) {
							if(cdb.write_tryAcquire()) {
								pos = i+1;
								Clocks.resetCyclesLOAD();
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
