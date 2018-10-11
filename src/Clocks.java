import java.util.concurrent.Semaphore;

public class Clocks {
 
	// Cycles counter
	public static Integer clocks = 0;
	// Minimun Cycles for RS
	private int cycles_add = 3;
	private int cycles_load = 2;
	private int cycles_mul = 5;
	// Current cycles
	private int current_cycles_add = 0;
	private int current_cycles_load = 0;
	private int current_cycles_mul = 0;
	// Semaphores for sync
	private Semaphore clk;
	private Semaphore clkInstruction;
	private Semaphore clkLoad;
	private Semaphore clkADD;
	private Semaphore clkMUL;
	private Semaphore clkROB;
	
	public Clocks() {
		clk = new Semaphore(1);
		clkInstruction = new Semaphore(1);
		clkLoad = new Semaphore(1);
		clkADD = new Semaphore(1);
		clkMUL = new Semaphore(1);
		clkROB = new Semaphore(1);
		takeClocks();
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
		releaseThis(clkInstruction);
		releaseThis(clkLoad);
		releaseThis(clkADD);
		releaseThis(clkMUL);
		releaseThis(clkROB);
	}

	public void take() throws InterruptedException {
		clockNext();
		print();
		
		clk.acquire();
		
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

	private void waitClock(Semaphore sem) {
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
	public void waitClockADD() {
		waitClock(clkADD);
	}

	public boolean checkCyclesADD() {
		if(current_cycles_add <= cycles_add)
			current_cycles_add++;
		return (current_cycles_add >= cycles_add+1);
	}
	
	public void resetCyclesADD() {
		current_cycles_add = 0;
	}
	
	// MUL Methods
	public void waitClockMUL() {
		waitClock(clkMUL);
	}

	public boolean checkCyclesMUL() {
		if(current_cycles_mul <= cycles_mul)
			current_cycles_mul++;
		return (current_cycles_mul >= cycles_mul+1);
	}
	
	public void resetCyclesMUL() {
		current_cycles_mul = 0;
	}
	
	// LOAD Methods
	public void waitClockLOAD() {
		waitClock(clkLoad);
	}

	public boolean checkCyclesLOAD() {
		if(current_cycles_load <= cycles_load)
			current_cycles_load++;
		return (current_cycles_load >= cycles_load+1);
		//return checkCycles(current_cycles_load,cycles_load);
	}
	
	public void resetCyclesLOAD() {
		current_cycles_load = 0;
	}

	public void waitClockROB() {
		waitClock(clkROB);
	}
}
