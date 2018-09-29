
public class ADD extends Station implements Runnable{
	
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
	
	public int getPlaces() {
		int cant = 0;
		for(int i=0; i<add.length; i++)
			if(add[i].getOp() == null)
				cant++;
		return cant;
	}
	
	public void setData(int dest, boolean busy, String op, int vj, int vk, String qj, String qk) {
		int pos = -1;
		for(int i=0; i<add.length; i++) {
			if(add[i].getOp() == null)
				pos = i;
		}
		if(pos >= 0) {
			add[pos].setDest(dest);
			add[pos].setBusy(busy);
			add[pos].setOp(op);
			add[pos].setQj(qj);
			add[pos].setQk(qk);
			add[pos].setVj(vj);
			add[pos].setVk(vk);
		}
		else
			System.out.println("ERROR EN setData() de ADD");
	}
}
