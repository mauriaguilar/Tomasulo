import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

public class Main {

	// Cycles counter
	public static Integer clocks = 0;
	
	// Minimun Cycles for RS
	static int cycles_add = 3;
	static int cycles_load = 2;
	static int cycles_mul = 5;

	// Common Data Bus
	static Bus cdb = new Bus();
	
	// Clocks for RS, ROB and Instruction
	static Clocks clock = new Clocks();
	
	// Objects of Memory and Registers
	static Memory mem = new Memory(9);
	static Registers reg = new Registers(9);
	
	static RS bufferADD = new RS(3);
	static RS bufferMUL = new RS(3);
	static LS bufferLOAD = new LS(3);
	
	// Objects of RS, ROB and Instruction
	static Load load = new Load(clock.clkLoad(), bufferLOAD, mem, cdb, cycles_load);
	static ADD add = new ADD(clock.clkADD(), bufferADD, cdb, cycles_add);
	static MUL mul = new MUL(clock.clkMUL(), bufferMUL, cdb, cycles_mul);
	static ROB rob = new ROB(clock.clkROB(), 9,cdb, reg, mem);
	static Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,rob,reg);

	// Threads
	static Thread thInstruction = new Thread(instructions);
	static Thread thLoad = new Thread(load);
	static Thread thAdd = new Thread(add);
	static Thread thMul = new Thread(mul);
	static Thread thROB = new Thread(rob);
	
	public static void main (String [ ] args) throws InterruptedException, FileNotFoundException {
		
		boolean HLT = false;
		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(1);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);

		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
		
		while(true) {
			clocks++;
			System.out.println("\n---------------------Clock: "+clocks+"   PC: "+instructions.getPC()+"---------------------");

			//Enable the execution of a clock
			clock.take();

			//Release CDB
			if(!cdb.haveAvailables())
				cdb.write_release();
			
			//Time of execution of one clock 
			Thread.sleep(1 * 1000);
			
			//Print tables
			printTables();
			
			HLT = instructions.isHLT() && rob.isEmpty();
			if(!HLT) {
				//Release clock
				clock.release();
			}
			else {
				//Print Registers and Memory tables
				printMemories();
				System.out.println("\n\nThat's all");
				System.out.println("************************************************************");
				break;
			}
		}
		System.exit(0);		
    }
	
	

	public Registers getRegister() {
		return reg;
	}
	
	public void setClock(int i) {
		clocks = i;
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
	}
	
}
