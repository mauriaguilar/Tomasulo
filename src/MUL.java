import java.util.concurrent.Semaphore;

public class MUL extends Station implements Runnable{

	private Semaphore clk;
	private boolean data;
	private Bus cdb;
	private Semaphore resource;
	private int pos;
	private RS_Entry[] rs;
	
	public MUL(Semaphore clk, int cap, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		rs = new RS_Entry[cap];
		for(int i=0; i<cap; i++) {
			rs[i] = new RS_Entry();
		}
		cdb = bus;
		pos = 0;
	}
	
	@Override
	public void run() {
		boolean cdbWrited;
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//System.out.println("MUL Calculating instructions...");
			cdbWrited = tryCalculate(pos,rs.length);
			if(!cdbWrited)
				tryCalculate(0,pos-1);
			
			try {
				System.out.println("MUL WRITE READY");
				cdb.write_ready();
				cdb.read_acquire("M");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("MUL reading CDB...");
			// Read data bus and replace operands
			for(int j=0; j<rs.length; j++){
				if( rs[j].getBusy() ) {
					if( cdb.getTag().equals(rs[j].getQj()) ){
						rs[j].setQj("");
						rs[j].setVj(cdb.getData());
					}
					if( cdb.getTag().equals(rs[j].getQk()) ){
						rs[j].setQk("");
						rs[j].setVk(cdb.getData());
					}
				}
			}			
		}
	}
	


	private boolean tryCalculate(int ini,int fin)  {
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++) {
			pos = i+1;
			// If an ADD instruction exists
			if( rs[i].getBusy() ) {
				if(checkOperands(i) && (Main.clocks > rs[i].getClock())) {
					if(cdb.write_tryAcquire()) {
						result = calc(i);
						System.out.println("MUL["+i+"] writing CDB...");
						cdb.set(result, "ROB"+rs[i].getDest());
						delete(i);
						return true;
					}
					else
						System.out.println("CDB is Busy. Waiting...");
				}
			}
			//else
			//	System.out.println("mul["+i+"] is False");
		}
		return false;
	}

	private void delete(int index) {
		rs[index] = new RS_Entry();	
		resource.release();
	}

	private boolean checkOperands(int i) {
		if(rs[i].getQj().equals("") && rs[i].getQk().equals(""))
			return true;
		else
			return false;
	}

	public int check() {
		int i;
		for(i=0; i<rs.length; i++)
			if(rs[i].getBusy() == true)
				return i;
		return -1;
	}

	private int calc(int i) {
		int res = 0;
		
		if(rs[i].getOp() == "mul") {
			res = rs[i].getVj() * rs[i].getVk();
		}
		
		return res;
	}

	public boolean getData() {
		data = false;
		for(int i=0; i<rs.length; i++)
			if(rs[i].getBusy())
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rs.length; i++)
			if(rs[i].getOp() == null)
				cant++;
		return cant;
	}
	
	public void getResource() throws InterruptedException {
		resource.acquire();
	}
	
	public void setData(int dest, boolean busy, String op, int vj, int vk, String qj, String qk, int clock) {
		for(int i=0; i<rs.length; i++) {
			if(rs[i].getOp().equals("")) {
				System.out.println("Instructions Writing in MUL["+i+"] Station...");
				rs[i].setDest(dest);
				rs[i].setBusy(busy);
				rs[i].setOp(op);
				rs[i].setQj(qj);
				rs[i].setQk(qk);
				rs[i].setVj(vj);
				rs[i].setVk(vk);
				rs[i].setClock(clock);
				break;
			}
		}
	}
	
	public void print() {
		String table = "\nMUL\n";
		table += "N\t|DEST\t|OP\t|Vj\t|Vk\t|Qj\t|Qk\t|Busy";
		for(int i=0; i<rs.length; i++)
			if(rs[i].getBusy()) {
				table += ("\n" + i + "\t|" + rs[i].getDest() + "\t|" + rs[i].getOp() + "\t|"
						+ rs[i].getVj() + "\t|" + rs[i].getVk() 
						+ "\t|" + rs[i].getQk() + "\t|" + rs[i].getQk() + "\t|" + rs[i].getBusy());
			}
		System.out.println(table);
	}
}