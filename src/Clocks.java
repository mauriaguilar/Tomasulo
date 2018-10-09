import java.util.concurrent.Semaphore;

public class Clocks {

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
	
	public void freeClocks() {
		clkInstruction.release();
		clkLoad.release();
		clkADD.release();
		clkMUL.release();
		clkROB.release();
	}
	
	public void take() throws InterruptedException {
		clk.acquire();
		//Release all clocks
		freeClocks();
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
}
