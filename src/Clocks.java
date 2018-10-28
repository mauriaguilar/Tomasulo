import java.util.concurrent.Semaphore;

public class Clocks implements Runnable{
 
	// Cycles counter
	public static Integer clocks = 0;
	public static boolean loading;
	// Minimun Cycles for RS
	private static int cycles_add = 3;
	private static int cycles_load = 2;
	private static int cycles_mul = 5;
	// Current cycles
	private static int current_cycles_add = 0;
	private static int current_cycles_load = 0;
	private static int current_cycles_mul = 0;
	// Semaphores for sync
	private Semaphore clk;
	private static Semaphore clkInstruction;
	private static Semaphore clkLoad;
	private static Semaphore clkADD;
	private static Semaphore clkMUL;
	private static Semaphore clkROB;
	private Bus cdb;
	private Instructions instructions;
	// For Testing
	private boolean isATest;
	private boolean done;
	
	private static ADD add;
	private static MUL mul;
	private static LOAD load;
	private static ROB rob;
	private static Registers reg;
	private static Memory mem;
		
	// Threads
	static Thread thInstruction;
	static Thread thLoad;
	static Thread thAdd;
	static Thread thMul;
	static Thread thROB;
	
	public Clocks(Bus cdb, Instructions instructions, ADD add, MUL mul, LOAD load, ROB rob, Registers reg, Memory mem) {
		clk = new Semaphore(1);
		clkInstruction = new Semaphore(1);
		clkLoad = new Semaphore(1);
		clkADD = new Semaphore(1);
		clkMUL = new Semaphore(1);
		clkROB = new Semaphore(1);
		takeClocks();
		this.cdb = cdb;
		this.instructions = instructions;
		this.add = add;
		this.mul = mul;
		this.load = load;
		this.rob = rob;
		this.reg = reg;
		this.mem = mem;
		// Threads
		thInstruction = new Thread(instructions);
		thLoad = new Thread(load);
		thAdd = new Thread(add);
		thMul = new Thread(mul);
		thROB = new Thread(rob);
		isATest = false;
		done = false;
	}
	
	@Override
	public void run() {
		
		clocks = 0;		
		startExecution();
		//int dead = 0;
		
		while(true) {			
			//Release CDB
			cdb.write_release();
			
			//Enable the execution of a clock
			take();
			
			//Time of execution of one clock 
			pause(150);
			
			//Print tables
			printTables();
			
			//Security Control
			//if( dead++ == 100 ) break;
			
			if(instructions.isHLT() && rob.isEmpty()) {
				//Print Registers and Memory tables
				printMemories();
				done = true;
				break;
			}
			else {
				//Release clock
				if(!isATest)
					release();
			}
		}
		
		// Close all threads and exit
		if(!isATest)
			System.exit(0);	
	}
	

	private static void startExecution() {
		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
	}
	
	private void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void takeClocks() {
		try {
			clkInstruction.acquire();
			clkLoad.acquire();
			clkADD.acquire();
			clkMUL.acquire();
			clkROB.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void releaseThis(Semaphore sem) {
		if(sem.availablePermits() == 0) 
			sem.release();
	}
	
	public void releaseClocks() {
		//System.out.println("-------release---------");
		//System.out.println("AVAILABLES INSTR "+clkInstruction.availablePermits());
		if(!loading)
			releaseThis(clkInstruction);
		releaseThis(clkLoad);
		releaseThis(clkADD);
		releaseThis(clkMUL);
		releaseThis(clkROB);
		//System.out.println("-------release--------- DESPUES");
	}

	public void take() {
		clockNext();
		print();
		
		try {
			clk.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//Release all clocks
		releaseClocks();
	}
	
	public void release() {
		clk.release();
	}
	
	public Semaphore clkInstruction() {
		return clkInstruction;
	}
	
	public Semaphore clkADD() {
		return clkADD;
	}
	
	public Semaphore clkMUL() {
		return clkMUL;
	}
	
	public Semaphore clkLoad() {
		return clkLoad;
	}
	
	public Semaphore clkROB() {
		return clkROB;
	}

	public void setClock(int i) {
		clocks = i;
	}
	
	public void clockNext() {
		clocks++;
	}
	
	public void print() {
		System.out.println("\n---------------------Clock: "+clocks+"   PC: "+Instructions.getPC()+"---------------------");
	}

	private static void waitClock(Semaphore sem) {
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean checkCycles(int current_cycles, int total_cycles) {
		if(current_cycles < current_cycles)
			current_cycles++;
		return (current_cycles == total_cycles);
	}
	
	// ADD Methods
	public static void waitClockADD() {
		waitClock(clkADD);
	}

	public static boolean checkCyclesADD() {
		if(current_cycles_add <= cycles_add)
			current_cycles_add++;
		return (current_cycles_add > cycles_add);
	}
	
	public static void resetCyclesADD() {
		current_cycles_add = 0;
	}
	
	// MUL Methods
	public static void waitClockMUL() {
		waitClock(clkMUL);
	}

	public static boolean checkCyclesMUL() {
		if(current_cycles_mul <= cycles_mul)
			current_cycles_mul++;
		return (current_cycles_mul > cycles_mul);
	}
	
	public static void resetCyclesMUL() {
		current_cycles_mul = 0;
	}
	
	// LOAD Methods
	public static void waitClockLOAD() {
		waitClock(clkLoad);
	}

	public static boolean checkCyclesLOAD() {
		if(current_cycles_load <= cycles_load) {
			current_cycles_load++;
		}
			
		return (current_cycles_load > cycles_load);
	}
	
	public static void resetCyclesLOAD() {
		current_cycles_load = 0;
	}
	
	// ROB Methods
	public static void waitClockROB() {
		waitClock(clkROB);
	}
	
	// Instructions Methods
	public static void waitClockInstructions() {
		waitClock(clkInstruction);
	}

	public void setModeTest(boolean b) {
		isATest = b;
	}
	
	public boolean getDone() {
		return done;
	}
	
	
	static void printTables() {
		add.print();
		mul.print();
		load.print();
		rob.print(); 
	}
	
	static void printMemories() {
		reg.print();
		mem.print();
		System.out.println("\n\nThat's all");
		System.out.println("************************************************************");
	}

	
}
