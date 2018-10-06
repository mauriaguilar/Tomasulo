import java.util.concurrent.Semaphore;

public class ADD extends Station implements Runnable{
	
	private Semaphore clk;
	private RS_Entry[] add;
	private boolean data;
	private Bus cdb;
	private Semaphore resource;
	
	public ADD(Semaphore clk, int cap, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		//System.out.println("Creando Add");
		add = new RS_Entry[cap];
		for(int i=0; i<cap; i++) {
			add[i] = new RS_Entry();
		}
		cdb = bus;
	}
	
	@Override
	public void run() {
		int index, i=0;
		boolean cdbWrited=false;
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Calculating instructions...");
			cdbWrited = tryCalculate(i,add.length);
			if(!cdbWrited)
				tryCalculate(0,i-1);
				
			try {
				cdb.write_ready();
				cdb.read_acquire("A");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Reemplacing operands...");
			// Read data bus and replace operands
			for(int j=0; j<add.length; j++){
				if( add[j].getBusy() ) {
					if( cdb.getTag().equals(add[j].getQj()) ){
						add[j].setQj("");
						add[j].setVj(cdb.getData());
					}
					if( cdb.getTag().equals(add[j].getQk()) ){
						add[j].setQk("");
						add[j].setVk(cdb.getData());
					}
				}
			}
		}
	}

	private boolean tryCalculate(int ini,int fin){
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++)
			// If an ADD instruction exists
			if( add[i].getBusy() ) {
				if(checkOperands(i)) {
					if(cdb.write_tryAcquire()) {
					//cdb.acquire();
						result = calc(i);
						System.out.println("ADD writing CDB...");
						cdb.set(result, "ROB"+add[i].getDest());
						delete(i);
						return true;
					}
					else
						System.out.println("CDB is Busy. Waiting...");
				}
				else
					System.out.println("add["+i+"] haven't operands");
			}
			else
				System.out.println("add["+i+"] is False");
		return false;
	}

	private boolean checkOperands(int i) {
		if(add[i].getQj().equals("") && add[i].getQk().equals(""))
			return true;
		else
			return false;
	}
	
	private int calc(int i) {
		int res = 0;
		
		if(add[i].getOp() == "ADD") {
			res = add[i].getVj() + add[i].getVk();
		}
		
		if(add[i].getOp() == "SUB") {
			res = add[i].getVj() - add[i].getVk();
		}
		
		return res;
	}
	
	private void delete(int index) {
		add[index] = new RS_Entry();
		resource.release();
	}
	
	public boolean getData() {
		data = false;
		for(int i=0; i<add.length; i++)
			if(add[i].getBusy())
				data = true;
		return data;
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<add.length; i++)
			if(add[i].getOp() == null)
				cant++;
		return cant;
	}
	
	public void getResource() throws InterruptedException {
		resource.acquire();
	}
	
	public void setData(int dest, boolean busy, String op, int vj, int vk, String qj, String qk) {
		System.out.println("Writing in ADD Station...");
		int pos = -1;
		for(int i=0; i<add.length; i++) {
			if(add[i].getOp() == null)
				pos = i;
		}
		if(pos >= 0) {
			add[pos].setDest(dest);
			add[pos].setBusy(busy);
			add[pos].setOp(op);
			add[pos].setQj(qj);
			add[pos].setQk(qk);
			add[pos].setVj(vj);
			add[pos].setVk(vk);
		}
		else
			System.out.println("ERROR EN setData() de ADD");
	}
}
