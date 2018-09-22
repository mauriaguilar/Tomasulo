
public class MUL {
	
	private RS[] mul;
	
	public MUL(int cap) {
		mul = new RS[cap];
	}
	
	private float calc(int i) {
		float res = 0;
		
		if(mul[i].getOp() == "mul") {
			res = mul[i].getVj() * mul[i].getVk();
		}
		
		return res;
	}
}