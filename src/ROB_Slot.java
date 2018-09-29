
public class ROB_Slot {
	private String dest;
	private float value;
	private String type; //CONSULTAR
	private boolean ready;
	
	public ROB_Slot() {
		dest = null;
		value = -1;
		type = null;
		ready = false;
	}
	
	public String getType() {
		return type;
	}
	
}
