
public class ROB implements Runnable{
	
	private ROB_Slot [] rob;
	private Bus cdb;
	private int put_index;	//Indice donde se escriben las instrucciones
	private int remove_index;	//Indice que indica instruccion a sacar
	
	public ROB(int cap, Bus bus) {
		//System.out.println("Creando ROB Slot");
		cdb = bus;
		put_index = 0;
		remove_index = 0;
		rob = new ROB_Slot[cap];
		for(int i=0; i<cap; i++) {
			rob[i] = new ROB_Slot();
		}
	}
	
	@Override
	public void run() {
		
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rob.length; i++)
			if(rob[i].getType() == null)
				cant++;
		return cant;
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
		//COMPLETAR
	}
}
