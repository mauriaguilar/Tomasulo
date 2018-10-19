import java.util.concurrent.Semaphore;

public class ROB implements Runnable{
	
	private Clocks clk;
	private ROB_Station rob;
	private Bus cdb;
	private int put_index;		//Indice que indica lugar a escribir
	private int remove_index;	//Indice que indica instruccion a sacar
	private Registers reg; 
	private Memory mem;
	
	public ROB(Clocks clk, ROB_Station bufferROB, Bus bus, Registers reg, Memory mem) {
		this.clk = clk;
		cdb = bus;
		put_index = 0;
		remove_index = 0;
		this.reg = reg;
		this.mem = mem;
		this.rob = bufferROB;
	}
	
	@Override
	public void run() {
		String tag;
		int index;
		
		while(true) {
			clk.waitClockROB();
			
			//Write in REG
			if(rob.get(remove_index).getReady()) {
				if(rob.get(remove_index).getDest().contains("R")) {
					index = Character.getNumericValue( rob.get(remove_index).getDest().charAt(1) );
					reg.setData(index, Integer.parseInt(rob.get(remove_index).getValue()));
				}
				else {
					index = Character.getNumericValue( rob.get(remove_index).getDest().charAt(0) );
					mem.setValue(index, Integer.parseInt(rob.get(remove_index).getValue()));
				}
				delete();
				removeNext();
			}
			
			try {
				cdb.read_acquire("R");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//System.out.println("ROB reading CDB...");
			tag = cdb.getTag();
			if( tag.contains("ROB") ) {
				// Calculate index of ROB and save
				index = Character.getNumericValue( tag.charAt(3) );
				rob.get(index).setValue( ""+cdb.getData() );
				rob.get(index).setReady(true);
				System.out.println("ROB["+index+"] getting "+cdb.getData()+" from CDB...");
				compareValue();
			}else {
				//System.out.println("ROB no encontro nada en el CDB...");
			}
			
			cdb.tryDeleteCDB(); // Delete CDB
			
			
			
		}
	}
	
	private void removeNext() {
		if(remove_index < rob.length()-1)
			remove_index++;
		else
			remove_index = 0;
		
	}
	
	private void putNext() {
		if(put_index < rob.length()-1)
			put_index++;
		else
			put_index = 0;
	}
	
	public int getIndex() {
		return put_index;
	}

	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rob.length(); i++)
			if(rob.get(i).getType().equals(""))
				cant++;
		return cant;
	}
	
	public boolean isEmpty() {
		if(getPlaces() == rob.length()) {
			return true;
		}
		return false;
	}
	
	public void delete() {
		rob.del(remove_index);
	}

	public int compareOperand(String operand) {
		for(int i=0; i<rob.length(); i++) {
			if(rob.get(i).getDest().equals(operand)) {
				//System.out.println("Comparacion entre: "+operand+" y "+rob.get(i).getDest());
				return i;
			}
		}
		return -1;
	}
	
	private void compareValue() {
		int index;
		for(int i=0; i<rob.length(); i++) {
			if(rob.get(i).getValue().contains("ROB")) {
				index = Character.getNumericValue( rob.get(i).getValue().charAt(3) );
				if(rob.get(index).getReady()) {
					rob.get(i).setValue(rob.get(index).getValue());
					rob.get(i).setReady(true);
					break;
				}
			}
		}
	}
	
	public void setData(String dest, String value, String type, boolean ready) {
		System.out.println("Instructions Writing in ROB["+put_index+"] Station...");
		rob.get(put_index).setDest(dest);
		rob.get(put_index).setType(type);
		rob.get(put_index).setValue(value);
		rob.get(put_index).setReady(ready);
		putNext();
	}
	
	public ROB_Entry getROB(int i) {
		return rob.get(i);
	}
	
	public void print() {
		String table = "\nROB\n";
		table += "N\t|DEST\t|VALUE\t|TYPE\t|READY";
		for(int i=0; i<rob.length(); i++)
			if(rob.get(i).getDest() != "-1") {
				table += ("\n" + i + "\t|" + rob.get(i).getDest() + "\t|");
				if(!rob.get(i).getValue().equals("-1")) {
					table += rob.get(i).getValue();
				}
				table	+= ( "\t|" + rob.get(i).getType() + "\t|" + rob.get(i).getReady() );
			}
		System.out.println(table);
		System.out.println("===============================================================");
	}
}
