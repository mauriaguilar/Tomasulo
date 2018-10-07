import java.util.concurrent.Semaphore;

public class ADD extends Station implements Runnable{
	
	private Semaphore clk;
	private RS_Entry[] add;
	private boolean data;
	private Bus cdb;
	private Semaphore resource;
	private int pos;
	private RS_Entry[] rs;
	
	public ADD(Semaphore clk, int cap, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		add = new RS_Entry[cap];
		for(int i=0; i<cap; i++) {
			add[i] = new RS_Entry();
		}
		cdb = bus;
		pos = 0;
		rs = add;
	}
	
	@Override
	public void run() {
		boolean cdbWrited=false;
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("ADD Calculating instructions... in clock "+Main.clocks);
			cdbWrited = tryCalculate(pos,add.length);
			if(!cdbWrited)
				tryCalculate(0,pos-1);
				
			try {
				System.out.println("ADD WRITE READY");
				cdb.write_ready();
				System.out.println("ADD READ ACQUIRE");
				cdb.read_acquire("A");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("ADD reading CDB..."+Main.clocks);
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
		//System.out.println("ini "+ini+" fin "+fin);
		int result;
		// Try calculate instructions
		for(int i=ini; i<fin; i++) {
			pos = i+1; // Save the next index
			//System.out.println("....."+i);
			// If an ADD instruction exists
			if( add[i].getBusy() ) {
				if(checkOperands(i) && (Main.clocks > add[i].getClock())) {
					if(cdb.write_tryAcquire()) {
					//cdb.acquire();
						result = calc(i);
						System.out.println("ADD["+i+"] writing CDB...");
						cdb.set(result, "ROB"+add[i].getDest());
						delete(i);
						return true;
					}
					else
						System.out.println("CDB is Busy. Waiting...");
				}
			}
		}
			//else
			//	System.out.println("add["+i+"] is False");
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
	
	public void setData(int dest, boolean busy, String op, int vj, int vk, String qj, String qk, int clock) {
		for(int i=0; i<add.length; i++) {
			if(add[i].getOp().equals("")) {
				System.out.println("Instructions Writing in ADD["+i+"] Station...");
				add[i].setDest(dest);
				add[i].setBusy(busy);
				add[i].setOp(op);
				add[i].setQj(qj);
				add[i].setQk(qk);
				add[i].setVj(vj);
				add[i].setVk(vk);
				add[i].setClock(clock);
				break;
			}
		}
	}
	
	public void print() {
		System.out.print("===============================================================");
		String table = "\nADD\n";
		table += "N\t|DEST\t|OP\t|Vj\t|Vk\t|Qj\t|Qk\t|Busy";
		for(int i=0; i<rs.length; i++)
			if(rs[i].getBusy()) {
				table += ("\n" + i + "\t|" + rs[i].getDest() + "\t|" + rs[i].getOp() + "\t|"
						+ rs[i].getVj() + "\t|" + rs[i].getVk() 
						+ "\t|" + rs[i].getQk() + "\t|" + rs[i].getQk() + "\t|" + rs[i].getBusy());
			}
			//else
			//	table += ("\n" + i + "\t|\t|\t|\t|\t|\t|\t|");
		System.out.println(table);
	}
}
