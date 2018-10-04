import java.util.concurrent.Semaphore;

public class Main {

	
	static Semaphore clk = new Semaphore(1);
	static Semaphore clkInstruction = new Semaphore(1);
	static Semaphore clkLoad = new Semaphore(1);
	static Semaphore clkADD = new Semaphore(1);
	static Semaphore clkMUL = new Semaphore(1);
	static Semaphore clkROB = new Semaphore(1);

	static Bus cdb = new Bus();	
	static Bus ndb = new Bus();	
	
	static Memory mem = new Memory(9);
	static Registers reg = new Registers(9);
	static Load load = new Load(clkLoad, 3, mem, cdb);
	static Store store = new Store(3);
	static ADD add = new ADD(clkADD, 3, cdb);
	static MUL mul = new MUL(clkMUL, 3, cdb);
	static ROB rob = new ROB(clkROB, 6,cdb);
	static Instructions instructions = new Instructions(clkInstruction, ndb,load,add,mul,rob,reg);

	static Thread thInstruction = new Thread(instructions);
	static Thread thLoad = new Thread(load);
	static Thread thStore = new Thread(store);
	static Thread thAdd = new Thread(add);
	static Thread thMul = new Thread(mul);
	static Thread thROB = new Thread(rob);

	public static void main (String [ ] args) throws InterruptedException {
		//System.out.println("Tomasulo begging...");
		boolean HLT = false;
		
		clkInstruction.acquire();
		clkLoad.acquire();
		clkROB.acquire();
		clkADD.acquire();

		thInstruction.start();
		thLoad.start();
		thStore.start();
		thAdd.start();
		thMul.start();
		thROB.start();
		
		int pc=0;
		while(true) {
			
			System.out.println("PC: "+pc+" -----------------");
			pc++;
			clk.acquire();
			
			clkInstruction.release();
			clkLoad.release();
			clkROB.release();
			clkADD.release();
			cdb.release();
			
			//  Time of one clock of execution
			Thread.sleep(3 * 1000);
			
			if(!HLT)
				clk.release();
		}
		
    }
	
	
}
