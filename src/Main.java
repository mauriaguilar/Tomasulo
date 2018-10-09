import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

public class Main {

	// Cycles counter
	public static Integer clocks = 0;
	
	// Cycles min for RS
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
	
	//  Objects of RS, ROB and Instruction
	static Load load = new Load(clock.clkLoad(), 3, mem, cdb, cycles_load);
	static ADD add = new ADD(clock.clkADD(), 3, cdb, cycles_add);
	static MUL mul = new MUL(clock.clkMUL(), 3, cdb, cycles_mul);
	static ROB rob = new ROB(clock.clkROB(), 9,cdb, reg, mem);
	static Instructions instructions = new Instructions(clock.clkInstruction(),load,add,mul,rob,reg);

	// Threads
	static Thread thInstruction = new Thread(instructions);
	static Thread thLoad = new Thread(load);
	static Thread thAdd = new Thread(add);
	static Thread thMul = new Thread(mul);
	static Thread thROB = new Thread(rob);
	
	public static void main (String [ ] args) throws InterruptedException, FileNotFoundException {
		//System.out.println("Tomasulo begging...");
		boolean HLT = false;

		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(1);
		//String[][] instructions_list = program.getInstrucions(2);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);
		
		clock.takeClocks();
		cdb.write_acquire();

		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
		
		while(true) {
			clocks++;
			System.out.println("\nPC: "+instructions.getPC()+" --------Clocks: "+clocks+"-----------------");
			//Acquire main clock
			clock.takeMainClk();
			
			//Release all clocks
			clock.freeClocks();

			//Release CDB
			cdb.write_release();
			
			//  Time of one clock of execution
			Thread.sleep(1 * 1000);
			
			//Print tables
			add.print();
			mul.print();
			load.print();
			rob.print(); 
			
			HLT = instructions.isHLT() && rob.isEmpty();
			if(!HLT) {
				//Release main clock
				clock.freeMainClk();
			}
			else {
				//Print Registers and Memory tables
				reg.print();
				mem.print();
				System.out.println("\n\nThat's all");
				System.out.println("***********************************************************************************");
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
}
