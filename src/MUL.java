import java.util.concurrent.Semaphore;

public class MUL extends Station implements Runnable{

	private Semaphore clk;
	private RS_Entry[] mul;
	private boolean data;
	private Bus cdb;
	private Semaphore resource;
	
	public MUL(Semaphore clk, int cap, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		mul = new RS_Entry[cap];
		for(int i=0; i<cap; i++) {
			mul[i] = new RS_Entry();
		}
		cdb = bus;
	}
	
	@Override
	public void run() {
		int index, i=0;
		boolean cdbWrited;
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Calculating instructions...");
			cdbWrited = tryCalculate(i,mul.length);
			if(!cdbWrited)
				tryCalculate(0,i-1);
			
			try {
				cdb.write_ready();
				cdb.read_acquire("M");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Reemplacing operands...");
			// Read data bus and replace operands
			for(int j=0; j<mul.length; j++){
				if( mul[j].getBusy() ) {
					if( cdb.getTag().equals(mul[j].getQj()) ){
						mul[j].setQj("");
						mul[j].setVj(cdb.getData());
					}
					if( cdb.getTag().equals(mul[j].getQk()) ){
						mul[j].setQk("");
						mul[j].setVk(cdb.getData());
					}
				}
			}			
		}
	}
	


	private boolean tryCalculate(int ini,int fin) {
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++)
			// If an ADD instruction exists
			if( mul[i].getBusy() ) {
				if(checkOperands(i)) {
					if(cdb.write_tryAcquire()) {
						result = calc(i);
						System.out.println("ADD writing CDB...");
						cdb.set(result, "ROB"+mul[i].getDest());
						delete(i);
						return true;
					}
					else
						System.out.println("CDB is Busy. Waiting...");
				}
				else
					System.out.println("mul["+i+"] haven't operands");
			}
			else
				System.out.println("mul["+i+"] is False");
		return false;
	}

	private void delete(int index) {
		mul[index] = new RS_Entry();	
		resource.release();
	}

	private boolean checkOperands(int i) {
		if(mul[i].getQj().equals("") && mul[i].getQk().equals(""))
			return true;
		else
			return false;
	}

	public int check() {
		int i;
		for(i=0; i<mul.length; i++)
			if(mul[i].getBusy() == true)
				return i;
		return -1;
	}

	private int calc(int i) {
		int res = 0;
		
		if(mul[i].getOp() == "mul") {
			res = mul[i].getVj() * mul[i].getVk();
		}
		
		return res;
	}

	public boolean getData() {
		data = false;
		for(int i=0; i<mul.length; i++)
			if(mul[i].getBusy())
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<mul.length; i++)
			if(mul[i].getOp() == null)
				cant++;
		return cant;
	}
	
	public void getResource() throws InterruptedException {
		resource.acquire();
	}
	
	public void setData(int dest, boolean busy, String op, int vj, int vk, String qj, String qk) {
		System.out.println("Writing in MUL Station...");
		int pos = -1;
		for(int i=0; i<mul.length; i++) {
			if(mul[i].getOp() == null)
				pos = i;
		}
		if(pos >= 0) {
			mul[pos].setDest(dest);
			mul[pos].setBusy(busy);
			mul[pos].setOp(op);
			mul[pos].setQj(qj);
			mul[pos].setQk(qk);
			mul[pos].setVj(vj);
			mul[pos].setVk(vk);
		}
		else
			System.out.println("ERROR EN setData() de Load");
	}
}