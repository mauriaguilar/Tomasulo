import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

public class Main {

	// Common Data Bus
	static Bus cdb = new Bus();
	
	// Clocks for RS, ROB and Instruction
	static Clocks clock = new Clocks();
	
	// Objects of Memory and Registers
	static Memory mem = new Memory(9);
	static Registers reg = new Registers(9);
	
	static Reserve_Station bufferADD = new Reserve_Station(3);
	static Reserve_Station bufferMUL = new Reserve_Station(3);
	static LOAD_Station bufferLOAD = new LOAD_Station(3);
	static ROB_Station bufferROB = new ROB_Station(9);
	
	// Objects of RS, ROB and Instruction
	static LOAD load = new LOAD(clock, bufferLOAD, mem, cdb);
	static ADD add = new ADD(clock, bufferADD, cdb);
	static MUL mul = new MUL(clock, bufferMUL, cdb);
	static ROB rob = new ROB(clock, bufferROB, cdb, reg, mem);
	static Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg);

	// Threads
	static Thread thInstruction = new Thread(instructions);
	static Thread thLoad = new Thread(load);
	static Thread thAdd = new Thread(add);
	static Thread thMul = new Thread(mul);
	static Thread thROB = new Thread(rob);
	
	public static void main (String [ ] args) throws InterruptedException, FileNotFoundException {
		
		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(1);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(1 * 1000);

		startExecution();
		int dead = 0;
		while(true) {	 		
			//Enable the execution of a clock
			clock.take();
			dead++;
			//Release CDB
			cdb.write_release();
			
			//Time of execution of one clock 
			Thread.sleep(1 * 150);
			
			//Print tables
			printTables();
			if( dead == 25 ) break;
			if(instructions.isHLT() && rob.isEmpty()) {
				//Print Registers and Memory tables
				printMemories();
				break;
			}
			else {
				//Release clock
				clock.release();
				System.out.println("antes acquireDelete");
				cdb.acquireDelete(3);
				System.out.println("despues acquireDelete");
				cdb.delete();
			}
		}
		// Close all threads and exit
		System.exit(0);		
    }
	
	

	private static void startExecution() {
		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
	}

	public Registers getRegister() {
		return reg;
	}
	
	private static void printTables() {
		add.print();
		mul.print();
		load.print();
		rob.print(); 
	}
	
	private static void printMemories() {
		reg.print();
		mem.print();
		System.out.println("\n\nThat's all");
		System.out.println("************************************************************");
	}
	
}
