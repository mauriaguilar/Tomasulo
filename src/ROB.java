
public class ROB {
	private ROB_Slot [] rob;
	
	public ROB(int cap) {
		//System.out.println("Creando ROB Slot");
		rob = new ROB_Slot[cap];
		for(int i=0; i<cap; i++) {
			rob[i] = new ROB_Slot();
		}
	}
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<rob.length; i++)
			if(rob[i].getType() == null)
				cant++;
		return cant;
	}
}
