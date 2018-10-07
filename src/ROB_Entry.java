
public class ROB_Entry {
	private String dest;
	private String value;
	private String type; //CONSULTAR
	private boolean ready;
	
	public ROB_Entry() {
		dest = "-1";
		value = "-1";
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
	
	public void setValue(String value) {
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

	public String getValue() {
		return value;
	}
	
}
