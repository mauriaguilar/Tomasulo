import java.util.concurrent.Semaphore;

public class ROB implements Runnable{
	
	private Semaphore clk;
	private ROB_Entry [] rob;
	private Bus cdb;
	private int put_index;	//Indice donde se escriben las instrucciones
	private int remove_index;	//Indice que indica instruccion a sacar
	private Registers reg; 
	private Memory mem;
	 
	
	public ROB(Semaphore clk, int cap, Bus bus, Registers reg, Memory mem) {
		this.clk = clk;
		cdb = bus;
		put_index = 0;
		remove_index = 0;
		rob = new ROB_Entry[cap];
		for(int i=0; i<cap; i++) {
			rob[i] = new ROB_Entry();
		}
		this.reg = reg;
		this.mem = mem;
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
			
			//Write in REG
			if(rob[remove_index].getReady()) {
				if(rob[remove_index].getDest().contains("R")) {
					index = Character.getNumericValue( rob[remove_index].getDest().charAt(1) );
					reg.setData(index, Integer.parseInt(rob[remove_index].getValue()));
				}
				else {
					index = Character.getNumericValue( rob[remove_index].getDest().charAt(0) );
					mem.setValue(index, Integer.parseInt(rob[remove_index].getValue()));
				}
				delete();
				//System.out.println("  GET READY  "+remove_index);
				if(remove_index == rob.length)
					remove_index = 0;
				else
					remove_index++;
			}
			
			try {
				//System.out.println("ROB READ ACQUIRE");
				cdb.read_acquire("R");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("ROB reading CDB...");
			tag = cdb.getTag();
			if( tag.contains("ROB") ) {
				// Calculate index of ROB and save
				index = Character.getNumericValue( tag.charAt(3) );
				rob[index].setValue( ""+cdb.getData() );
				rob[index].setReady(true);
				compareValue();
			}
			
			
			
		}
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rob.length; i++)
			if(rob[i].getType().equals(""))
				cant++;
		return cant;
	}
	
	public boolean isEmpty() {
		if(getPlaces() == rob.length) {
			return true;
		}
		return false;
	}
	
	public void delete() {
		rob[remove_index] = new ROB_Entry();
		//System.out.println("ELIMINANDO "+remove_index);
		rob[remove_index].release();
	}

	public int getIndex() {
		return put_index;
	}

	public int compareOperand(String operand) {
		for(int i=0; i<rob.length; i++) {
			if(rob[i].getDest().equals(operand)) {
				//System.out.println("Comparacion entre: "+operand+" y "+rob[i].getDest());
				return i;
			}
		}
		return -1;
	}
	
	private void compareValue() {
		int index;
		for(int i=0; i<rob.length; i++) {
			if(rob[i].getValue().contains("ROB")) {
				index = Character.getNumericValue( rob[i].getValue().charAt(3) );
				if(rob[index].getReady()) {
					rob[i].setValue(rob[index].getValue());
					rob[i].setReady(true);
					break;
				}
			}
		}
	}
	
	public void setData(String dest, String value, String type, boolean ready) {
		System.out.println("Instructions Writing in ROB["+put_index+"] Station...");
		rob[put_index].setDest(dest);
		rob[put_index].setType(type);
		rob[put_index].setValue(value);
		rob[put_index].setReady(ready);
		if(put_index == rob.length)
			put_index = 0;
		else
			put_index++;		
	}
	
	public ROB_Entry getROB(int i) {
		return rob[i];
	}
	
	public void print() {
		String table = "\nROB\n";
		table += "N\t|DEST\t|VALUE\t|TYPE\t|READY";
		for(int i=0; i<rob.length; i++)
			if(rob[i].getDest() != "-1") {
				table += ("\n" + i + "\t|" + rob[i].getDest() + "\t|");
				if(!rob[i].getValue().equals("-1")) {
					table += rob[i].getValue();
				}
				table	+= ( "\t|" + rob[i].getType() + "\t|" + rob[i].getReady() );
			}
		System.out.println(table);
		System.out.println("===============================================================");
	}
}
