
public class Main {

	static Bus bus = new Bus();	
	
	static Load load = new Load(3);
	static Store store = new Store(3);
	static ADD add = new ADD(3);
	static MUL mul = new MUL(3);
	static Instructions instructions = new Instructions();
	
	static Thread thLoad = new Thread(load);
	static Thread thStore = new Thread(store);
	static Thread thAdd = new Thread(add);
	static Thread thMul = new Thread(mul);

	public static void main (String [ ] args) {
		//System.out.println("Tomasulo begging...");
		thLoad.start();
		thStore.start();
		thAdd.start();
		thMul.start();
		System.out.println(
				instructions.getNext()[0] + " " +
				instructions.getNext()[1] + " " +
				instructions.getNext()[2] + " " +
				instructions.getNext()[3]
				);
    }
	
	
}
