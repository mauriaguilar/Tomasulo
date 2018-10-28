import java.util.concurrent.Semaphore;

public class ADD implements Runnable{
	
	private boolean data;
	private Bus cdb;
	private int pos;
	private Reserve_Station rs;
	
	public ADD(Reserve_Station bufferADD, Bus bus) {
		cdb = bus;
		pos = 0;
		rs = bufferADD;
	}
	
	@Override
	public void run() {
		boolean cdbWrited = false;
		while(true) {
			
			Clocks.waitClockADD();
			
			cdbWrited = tryCalculate(pos,rs.length());
			if(!cdbWrited)
				tryCalculate(0,pos-1);			
			write_ready();  // ++ release
			
			waitToRead(); // case acquire
			// Read data bus and replace operands
			readAndReplace();
			
			read_ready();
			
			cdb.tryDeleteCDB(); // Delete CDB
			
		}
	}


	private boolean tryCalculate(int ini,int fin){ 
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++) {
			pos = i; // Save the next index
			// If an instruction exists
			if( rs.get(i).getBusy()){// && rs.get(i).getReady()) {
			//if( rs.get(i).getBusy() ) {
				if(checkOperands(i)) {
					if(Clocks.checkCyclesADD()) {
						//System.out.println("ADD Disponibles: "+cdb.haveAvailables());
						if(cdb.write_tryAcquire()) {
							pos = i+1;
							Clocks.resetCyclesADD();
							result = calc(i);
							System.out.println("ADD["+i+"] writing "+result+" CDB...");
							cdb.set(result, "ROB"+rs.get(i).getDest());
							delete(i);
							return true;
						}
						else {
							System.out.println("CDB is Busy. ADD["+i+"] Waiting...");
							return true;
						}
					}
					else {
						System.out.println("ADD["+i+"] waiting clocks...");
						return false;
					}
			   }
			}
		}
		return false;
	}

	private boolean checkOperands(int i) {
		if(rs.get(i).getQj().equals("") && rs.get(i).getQk().equals(""))
			return true;
		else
			return false;
	}
	
	private int calc(int i) {
		int res = 0;
		
		if(rs.get(i).getOp().equals("ADD")) {
			res = rs.get(i).getVj() + rs.get(i).getVk();
		}
		
		return res;
	}
	
	private void delete(int index) {
		rs.del(index);
	}
	

	private void write_ready() {
		cdb.write_ready();
	}
	
	private void read_ready() {
		cdb.read_release();
	}
	

	private void waitToRead() {
		try {
			cdb.read_acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void readAndReplace() {
		for(int j=0; j<rs.length(); j++){
			if( rs.get(j).getBusy() ) {
				
				// Replacing operands
				if( cdb.getTag().equals(rs.get(j).getQj()) ){
					System.out.println("ADD["+j+"] getting "+cdb.getData()+" from CDB...");
					rs.get(j).setQj("");
					rs.get(j).setVj(cdb.getData());
				}
				if( cdb.getTag().equals(rs.get(j).getQk()) ){
					System.out.println("ADD["+j+"] getting "+cdb.getData()+" from CDB...");
					rs.get(j).setQk("");
					rs.get(j).setVk(cdb.getData());
				}
				
				// Setting ready for instructions in station
				//if( !rs.get(j).getReady() )
				//	rs.get(j).setReady(true);
			}
		}
	} 
	
	
	
	
	
	
	public boolean getData() {
		data = false;
		for(int i=0; i<rs.length(); i++)
			if(rs.get(i).getBusy())
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rs.length(); i++)
			if(!rs.get(i).getBusy())
				cant++;
		return cant;
	}
	
	public void print() {
		System.out.print("===============================================================");
		String table = "\nADD\n";
		table += "N\t|DEST\t|OP\t|Vj\t|Vk\t|Qj\t|Qk\t|Busy";
		for(int i=0; i<rs.length(); i++)
			if(rs.get(i).getBusy()) {
				table += ("\n" + i + "\t|" + rs.get(i).getDest() + "\t|" + rs.get(i).getOp() + "\t|"
						+ rs.get(i).getVj() + "\t|" + rs.get(i).getVk() 
						+ "\t|" + rs.get(i).getQj() + "\t|" + rs.get(i).getQk() + "\t|" + rs.get(i).getBusy());
			}
			//else
			//	table += ("\n" + i + "\t|\t|\t|\t|\t|\t|\t|");
		System.out.println(table);
	}
	
}
