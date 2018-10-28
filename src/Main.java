public class Main {

	// Common Data Bus
	static Bus cdb = new Bus();
	
	// Memory and Registers
	static Memory mem = new Memory(9);
	static Registers reg = new Registers(9);
	
	// Reserve Stations
	static Reserve_Station bufferADD = new Reserve_Station(3);
	static Reserve_Station bufferMUL = new Reserve_Station(3);
	static LOAD_Station bufferLOAD = new LOAD_Station(3);
	static ROB_Station bufferROB = new ROB_Station(9);
	
	// Program Loader
	static int programNumber = 2;
	static ProgramLoader loader = new ProgramLoader(programNumber);
	
	// Functional Units
	static LOAD load = new LOAD(bufferLOAD, mem, cdb);
	static ADD add = new ADD(bufferADD, cdb);
	static MUL mul = new MUL(bufferMUL, cdb);
	static ROB rob = new ROB(bufferROB, cdb, reg, mem);
	static Instructions instructions = new Instructions(bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg,loader);

	// Clock Unit
	static Clocks clock = new Clocks(cdb, instructions, add, mul, load, rob, reg, mem);
	static Thread thClock = new Thread(clock);
	
	public static void main (String [ ] args) throws InterruptedException {
		thClock.start();
    }
	
}
