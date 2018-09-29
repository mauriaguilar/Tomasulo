
public class ROB_Slot {
	private String dest;
	private int value;
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
	
	public String getDest() {
		return dest;
	}
	
	public void setDest(String dest) {
		this.dest = dest;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	
}
