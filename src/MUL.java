import java.util.concurrent.Semaphore;

public class MUL implements Runnable{

	private Clocks clk;
	private boolean data;
	private Bus cdb;
	private int pos;
	private Reserve_Station rs;
	
	public MUL(Clocks clk, Reserve_Station bufferMUL, Bus bus) {
		this.clk = clk;
		rs = bufferMUL;
		cdb = bus;
		pos = 0;
	}
	
	@Override
	public void run() { 
		boolean cdbWrited = false;
		while(true) {
			
			clk.waitClockMUL();
			
			//System.out.println("MUL Calculating instructions...");
			cdbWrited = tryCalculate(pos,rs.length());
			if(!cdbWrited)
				tryCalculate(0,pos-1);
			
			String UF = "M";
			//System.out.println("MUL LIBERA...");
			writingReady();
			waitToRead(UF);
			
			// Read data bus and replace operands
			System.out.println("MUL reading CDB...");
			readAndReplace();		
			System.out.println("MUL reading CDB... AFTER");
		}
	}
	
	private void writingReady() {
		//System.out.println("MUL WRITE READY");
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

	private void readAndReplace() {
		for(int j=0; j<rs.length(); j++){
			if( rs.get(j).getBusy() ) {
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
			}
		}
	}


	private boolean tryCalculate(int ini,int fin){
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++) {
			pos = i; // Save the next index
			// If an instruction exists
			if( rs.get(i).getBusy() ) {
				if(checkOperands(i)) {
					if(clk.checkCyclesMUL()) {
						if(cdb.write_tryAcquire()) {
							pos = i+1;
							clk.resetCyclesMUL();
							result = calc(i);
							System.out.println("MUL["+i+"] writing "+result+" CDB...");
							cdb.set(result, "ROB"+rs.get(i).getDest());
							delete(i);
							return true;
						}
						else{
							System.out.println("CDB is Busy. MUL Waiting...");
							return true;
						}
					}
					else {
						System.out.println("MUL waiting clocks...");
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