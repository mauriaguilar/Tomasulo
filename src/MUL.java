
public class MUL extends Station implements Runnable{
	
	private RS[] mul;
	private boolean data;
	
	public MUL(int cap) {
		//System.out.println("Creando Mul");
		mul = new RS[cap];
		for(int i=0; i<cap; i++) {
			mul[i] = new RS();
		}
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
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<mul.length; i++)
			if(mul[i].getOp() == null)
				cant++;
		return cant;
	}
	
	public void setData(int dest, boolean busy, String op, int vj, int vk, String qj, String qk) {
		int pos = -1;
		for(int i=0; i<mul.length; i++) {
			if(mul[i].getOp() == null)
				pos = i;
		}
		if(pos >= 0) {
			mul[pos].setDest(dest);
			mul[pos].setBusy(busy);
			mul[pos].setOp(op);
			mul[pos].setQj(qj);
			mul[pos].setQk(qk);
			mul[pos].setVj(vj);
			mul[pos].setVk(vk);
		}
		else
			System.out.println("ERROR EN setData() de Load");
	}
}