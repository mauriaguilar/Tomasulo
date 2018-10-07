
public class ROB_Entry {
	private String dest;
	private int value;
	private String type; //CONSULTAR
	private boolean ready;
	
	public ROB_Entry() {
		dest = "-1";
		value = -1;
		type = "";
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

	public boolean getReady() {
		return ready;
	}

	public int getValue() {
		return value;
	}
	
}
