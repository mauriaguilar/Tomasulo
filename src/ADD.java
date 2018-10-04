import java.util.concurrent.Semaphore;

public class ADD extends Station implements Runnable{
	
	private Semaphore clk;
	private RS[] add;
	private boolean data;
	private Bus cdb;
	
	public ADD(Semaphore clk, int cap, Bus bus) {
		this.clk = clk;
		//System.out.println("Creando Add");
		add = new RS[cap];
		for(int i=0; i<cap; i++) {
			add[i] = new RS();
		}
		cdb = bus;
	}
	
	@Override
	public void run() {
		int index, result;
		
		while(true) {

			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("Reemplacing operands...");
			// Read data bus and replace operands
			for(int i=0; i<add.length; i++){
				if( add[i].getBusy() ) {
					if( cdb.getTag().equals(add[i].getQj()) ){
						add[i].setQj("");
						add[i].setVj(cdb.getData());
					}
					if( cdb.getTag().equals(add[i].getQk()) ){
						add[i].setQk("");
						add[i].setVk(cdb.getData());
					}
				}
			}
			
			System.out.println("Calculating instructions...");
			// Calculate instructions
			for(int i=0; i<add.length; i++)
				
				// If an ADD instruction exists
				if( add[i].getBusy() ) {
					if(checkOperands(i)) {
						result = calc(i);
						try {
							cdb.acquire();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("ADD writing CDB...");
						cdb.set(result, "ROB"+add[i].getDest());
						delete(i);
						break; // ver si el remplazo de los operandos se hace en todos los clocks
					}
				}
			
		}
	
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
		add[index] = new RS();
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
