import java.util.concurrent.Semaphore;

public class MUL implements Runnable{

	private boolean data;
	private Bus cdb;
	private int pos;
	private Reserve_Station rs;
	
	public MUL(Reserve_Station bufferMUL, Bus bus) {
		rs = bufferMUL;
		cdb = bus;
		pos = 0;
	}
	
	@Override
	public void run() { 
		boolean cdbWrited = false;
		while(true) {
			
			Clocks.waitClockMUL();
			
			cdbWrited = tryCalculate(pos,rs.length());
			if(!cdbWrited)
				tryCalculate(0,pos-1);
			writingReady();
			
			String UF = "M";
			waitToRead(UF);
			// Read data bus and replace operands
			readAndReplace();
			
			cdb.tryDeleteCDB(); // Delete CDB
		}
	}
	
	private void writingReady() {
		cdb.write_ready();
	}
	

	private void waitToRead(String UF) {
		try {
			cdb.read_acquire(UF);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void readAndReplace() {
		for(int j=0; j<rs.length(); j++){
			if( rs.get(j).getBusy() ) {
				
				// Replacing operands
				if( cdb.getTag().equals(rs.get(j).getQj()) ){
					System.out.println("MUL["+j+"] getting "+cdb.getData()+" from CDB...");
					rs.get(j).setQj("");
					rs.get(j).setVj(cdb.getData());
				}
				if( cdb.getTag().equals(rs.get(j).getQk()) ){
					System.out.println("MUL["+j+"] getting "+cdb.getData()+" from CDB...");
					rs.get(j).setQk("");
					rs.get(j).setVk(cdb.getData());
				}
				
				// Setting ready for instructions in station
				if( !rs.get(j).getReady() )
					rs.get(j).setReady(true);
			}
		}
	}


	private boolean tryCalculate(int ini,int fin){
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++) {
			pos = i; // Save the next index
			// If an instruction exists
			if( rs.get(i).getBusy() ){//&& rs.get(i).getReady()) {
			//if( rs.get(i).getBusy() ) {
				if(checkOperands(i)) {
					if(Clocks.checkCyclesMUL()) {
						//System.out.println("MUL Disponibles: "+cdb.haveAvailables());
						if(cdb.write_tryAcquire()) {
							pos = i+1;
							Clocks.resetCyclesMUL();
							result = calc(i);
							System.out.println("MUL["+i+"] writing "+result+" CDB...");
							cdb.set(result, "ROB"+rs.get(i).getDest());
							delete(i);
							return true;
						}
						else{
							System.out.println("CDB is Busy. MUL["+i+"] Waiting...");
							return true;
						}
					}
					else {
						System.out.println("MUL["+i+"] waiting clocks...");
						return false;
					}
				}
			}
		}
		return false;
	}

	private void delete(int index) {
		rs.del(index);
	}

	private boolean checkOperands(int i) {
		if(rs.get(i).getQj().equals("") && rs.get(i).getQk().equals(""))
			return true;
		else
			return false;
	}

	private int calc(int i) {
		int res = 0;
		
		if(rs.get(i).getOp().equals("MUL")) {
			res = rs.get(i).getVj() * rs.get(i).getVk();
		}
		
		return res;
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
		String table = "\nMUL\n";
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