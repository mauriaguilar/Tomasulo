
public class Bus {
	
	//private Integer data_bus = new Integer(0);
	private int data;
	private String tag;
	
	public Bus() {
		data = 0;
		tag = "";
	}
	
	public void set(int data, String tag) {
		this.data = data;
		this.tag = tag;
	}

	public int getData() {
		return data;
	}
	
	public String getTag() {
		return tag;
	}
}
