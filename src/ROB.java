import java.util.concurrent.Semaphore;

public class ROB implements Runnable{
	
	private Semaphore clk;
	private Semaphore resource;
	private ROB_Entry [] rob;
	private Bus cdb;
	private int put_index;	//Indice donde se escriben las instrucciones
	private int remove_index;	//Indice que indica instruccion a sacar
	
	
	public ROB(Semaphore clk, int cap, Bus bus) {
		this.clk = clk;
		resource = new Semaphore(cap);
		System.out.println("Creando ROB Slot");
		cdb = bus;
		put_index = 0;
		remove_index = 0;
		rob = new ROB_Entry[cap];
		for(int i=0; i<cap; i++) {
			rob[i] = new ROB_Entry();
		}
	}
	
	@Override
	public void run() {
		String tag;
		int index;
		
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			tag = cdb.getTag();
			if( tag.contains("ROB") ) {
				System.out.println("CDB TAG: "+tag);
				// Calculate index of ROB and save
				index = Integer.parseInt( tag.valueOf(3) );
				rob[index].setValue( cdb.getData() );
			}
			
		}
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rob.length; i++)
			if(rob[i].getType() == null)
				cant++;
		return cant;
	}
	
	public void getResource() throws InterruptedException {
		resource.acquire();
	}
	
	public void delete() {
		resource.release();
	}

	public int getIndex() {
		return put_index;
	}

	public int compareOperand(String operand) {
		for(int i=0; i<rob.length; i++) {
			if(rob[i].getDest().equals(operand))
				return i;
		}
		return -1;
		
	}
	
	public void setData(String dest, int value, String type, boolean ready) {
		System.out.println("Writing in ROB Station...");
		
		int i;
		for(i=0; i<rob.length; i++)
			if(rob[i].getType() == null)
				break;
		rob[i].setDest(dest);
		rob[i].setType(type);
		rob[i].setValue(value);
		rob[i].setReady(ready);
	}
}
