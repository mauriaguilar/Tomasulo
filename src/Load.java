
public class Load implements Runnable{
	
	private LB[] load;
	private boolean data;
	
	public Load(int cap) {
		//System.out.println("Creando Load");
		load = new LB[cap];
		for(int i=0; i<cap; i++) {
			load[i] = new LB();
		}
	}
	
	private void calc(int origen, int dest) {
		//reg[dest] = mem[origen];
	}

	@Override
	public void run() {
		int it = 10;
		System.out.print("Load Starting...\n");
		while(getData() && it>0) {
			it--;
			System.out.println("Load");
		}
	}

	public boolean getData() {
		data = false;
		for(int i=0; i<load.length; i++)
			if(load[i].getBusy() == false)
				data = true;
		return data;
	}

}
