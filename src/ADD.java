
public class ADD {
	
	private RS[] add;
	
	public ADD(int cap) {
		add = new RS[cap];
	}
	
	private float calc(int i) {
		float res = 0;
		
		if(add[i].getOp() == "add") {
			res = add[i].getVj() * add[i].getVk();
		}
		
		return res;
	}
	
}
