
public class MUL implements Runnable{
	
	private RS[] mul;
	private boolean data;
	
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

	@Override
	public void run() {
		while(getData()) {
			System.out.print("MUL");
		}
	}

	public boolean getData() {
		data = false;
		for(int i=0; i<mul.length; i++)
			if(mul[i].getBusy())
				data = true;
		return data;
	}
	
}