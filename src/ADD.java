
public class ADD implements Runnable{
	
	private RS[] add;
	private boolean data;
	
	public ADD(int cap) {
		//System.out.println("Creando Add");
		add = new RS[cap];
		for(int i=0; i<cap; i++) {
			add[i] = new RS();
		}
	}
	
	private float calc(int i) {
		float res = 0;
		
		if(add[i].getOp() == "add") {
			res = add[i].getVj() * add[i].getVk();
		}
		
		return res;
	}

	@Override
	public void run() {
		while(getData()) {
			System.out.print("ADD");
		}
	}
	
	public boolean getData() {
		data = false;
		for(int i=0; i<add.length; i++)
			if(add[i].getBusy())
				data = true;
		return data;
	}
	
}
