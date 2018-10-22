import java.util.concurrent.Semaphore;

public class ROB implements Runnable{
	
	private Clocks clk;
	private ROB_Station rob;
	private Bus cdb;
	private int put_index;		//Indice que indica lugar a escribir
	private int remove_index;	//Indice que indica instruccion a sacar
	private Registers reg; 
	private Memory mem;
	private Semaphore sem;
	
	public ROB(Clocks clk, ROB_Station bufferROB, Bus bus, Registers reg, Memory mem) {
		this.clk = clk;
		cdb = bus;
		put_index = 0;
		remove_index = 0;
		this.reg = reg;
		this.mem = mem;
		this.rob = bufferROB;
		sem = new Semaphore(1);
	}
	
	@Override
	public void run() {
		
		while(true) {
			
			clk.waitClockROB();
			acquire();		//Take semaphore to read bus and update ROB's buffer
			//Write in REG
			writeRegMem();
			
			String UF = "R";
			waitToRead(UF);
			
			//System.out.println("ROB reading CDB...");
			readAndReplace();
			cdb.tryDeleteCDB(); // Delete CDB
			//readReady = true;
			release();		//Free semaphore to Instructions update RS's buffer
		}
	}
	
	public void acquire() {
		try {
			sem.acquire();
			//System.out.println("ROB toma readBusReady");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void release() {
		if(sem.availablePermits() == 0) {
			sem.release();
		}
	}
	
	private void readAndReplace() {
		int index;
		String tag;
		//System.out.println("ROB reading CDB...");
		tag = cdb.getTag();
		if( tag.contains("ROB") ) {
			// Calculate index of ROB and save
			index = Character.getNumericValue( tag.charAt(3) );
			rob.get(index).setValue( ""+cdb.getData() );
			rob.get(index).setReady(true);
			System.out.println("ROB["+index+"] getting "+cdb.getData()+" from CDB...");
			compareValue();
		}
	}

	private void  writeRegMem(){
		int index;
		String value;
		if(rob.get(remove_index).getReady()) {
			if(rob.get(remove_index).getDest().contains("+")) {
				String dest = calcDest();
				rob.get(remove_index).setDest(dest);
			}
			else {
				if(rob.get(remove_index).getDest().contains("R")) {
					index = Character.getNumericValue( rob.get(remove_index).getDest().charAt(1) );
					System.out.println("ROB["+remove_index+"] write "+rob.get(remove_index).getValue()+" in Registers["+index+"]");
					reg.setData(index, Integer.parseInt(rob.get(remove_index).getValue()));
				}
				else {
					index = Character.getNumericValue( rob.get(remove_index).getDest().charAt(0) );
					System.out.println("ROB["+remove_index+"] write "+rob.get(remove_index).getValue()+" in Memory["+index+"]");
					mem.setValue(index, Integer.parseInt(rob.get(remove_index).getValue()));
				}
				delete();
				removeNext();
			}
		}
		//Agrego esto para controlar valores ya cargados y setear Ready a true
		else {
			value = rob.get(remove_index).getValue();
			if( !(value.equals("-1")) && !(value.contains("ROB")) ) {
				rob.get(remove_index).setReady(true);
			}
		}
	}
	
	private void waitToRead(String UF) {
		try {
			//System.out.println("ADD READ ACQUIRE");
			cdb.read_acquire(UF);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String calcDest() {
		int register_index1;
		int register_value1;
		//Get dest
		register_index1 = Character.getNumericValue( rob.get(remove_index).getDest().charAt(3) );
		register_value1 = reg.getData(register_index1);	//Convierte el numero a int y lo pasa como argumento
		String dest = ""+ (register_value1 + Character.getNumericValue( rob.get(remove_index).getDest().charAt(0) ));
		return dest;
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
		int index = -1;
		for(int i=remove_index; i<rob.length(); i++) {
			if(rob.get(i).getDest().equals(operand)) 
				index = i;	
		}
		
		if(remove_index > put_index) {
			for(int i=0; i<put_index; i++) {
				if(rob.get(i).getDest().equals(operand)) 
					index = i;	
			}
		}
		return index;
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
		String table = "\nROB\tput->"+put_index+"\tremove->"+remove_index+"\n";
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
