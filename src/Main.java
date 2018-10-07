import java.util.concurrent.Semaphore;

public class Main {

	public static Integer clocks = 0;
	
	static Semaphore clk = new Semaphore(1);
	static Semaphore clkInstruction = new Semaphore(1);
	static Semaphore clkLoad = new Semaphore(1);
	static Semaphore clkADD = new Semaphore(1);
	static Semaphore clkMUL = new Semaphore(1);
	static Semaphore clkROB = new Semaphore(1);

	static Bus cdb = new Bus();
	
	static Memory mem = new Memory(9);
	static Registers reg = new Registers(9);
	static Load load = new Load(clkLoad, 3, mem, cdb);
	static ADD add = new ADD(clkADD, 3, cdb);
	static MUL mul = new MUL(clkMUL, 3, cdb);
	static ROB rob = new ROB(clkROB, 9,cdb, reg);
	static Instructions instructions = new Instructions(clkInstruction,load,add,mul,rob,reg);

	static Thread thInstruction = new Thread(instructions);
	static Thread thLoad = new Thread(load);
	static Thread thAdd = new Thread(add);
	static Thread thMul = new Thread(mul);
	static Thread thROB = new Thread(rob);
	

	public static void main (String [ ] args) throws InterruptedException {
		//System.out.println("Tomasulo begging...");
		boolean HLT = false;
		
		clkInstruction.acquire();
		clkLoad.acquire();
		clkADD.acquire();
		clkMUL.acquire();
		clkROB.acquire();
		cdb.write_acquire();

		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
		
		while(true) {
			
			clocks++;
			System.out.println("\nPC: "+instructions.getPC()+" --------Clocks: "+clocks+"-----------------");
			clk.acquire();
			
			clkInstruction.release();
			clkLoad.release();
			clkADD.release();
			clkMUL.release();
			clkROB.release();
			//System.out.println("write_release()");
			cdb.write_release();
			
			//  Time of one clock of execution
			Thread.sleep(1 * 1000);
			
			add.print();
			mul.print();
			load.print();
			rob.print();
			
			HLT = instructions.isHLT() && rob.isEmpty();
			if(!HLT) {
				clk.release();
			}
			else {
				System.out.println("That's all");
				break;
			}
		}
    }	
}
