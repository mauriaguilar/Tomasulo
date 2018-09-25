
public class Store implements Runnable{

	private SB[] store;
	private boolean data;
	
	public Store(int cap) {
		store = new SB[cap];
		for(int i=0; i<cap; i++) {
			store[i] = new SB();
		}
	}
	
	private void calc(int origen, int dest) {
		//mem[dest] = reg[origen];
	}

	@Override
	public void run() {
		while(getData()) {
			System.out.print("Store");
		}
	}

	public boolean getData() {
		data = false;
		for(int i=0; i<store.length; i++)
			if(store[i].getBusy())
				data = true;
		return data;
	}

}
