
public class Load implements Runnable{
	
	private LB[] load;
	private boolean data;
	
	public Load(int cap) {
		load = new LB[cap];
	}
	
	private void calc(int origen, int dest) {
		//reg[dest] = mem[origen];
	}

	@Override
	public void run() {
		System.out.print("Load Starting...");
		while(getData()) {
			System.out.print("Load");
		}
	}

	public boolean getData() {
		data = false;
		for(int i=0; i<load.length; i++)
			if(load[i].getBusy())
				data = true;
		return data;
	}

}
