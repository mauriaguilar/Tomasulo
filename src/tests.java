import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.Test;

class tests {
	
	private Main main;
	private int ready;
	private Memory memory;
	private Registers register;

	/*
	 * Test del estado del ROB luego de que Instructions leyo tres instrucciones.
	 * Debe cargar las tres instrucciones en el ROB
	 */
	@Test
	void testPutInROB() throws InterruptedException, FileNotFoundException {
		//System.out.println("\n\nTest de estado de ROB en clock 3");
		Clocks clock = new Clocks();
		
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		
		RS bufferADD = new RS(3);
		RS bufferMUL = new RS(3);
		LS bufferLOAD = new LS(3);
		
		Load load = new Load(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, 9,cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,rob,reg);

		Thread thInstruction = new Thread(instructions);
		Thread thROB = new Thread(rob);

		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(1);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);
		
		thInstruction.start();
		thROB.start();
		
		System.out.println("\n");
		clock.take();
		Thread.sleep(1 * 1000);
		clock.release();
		
		add.print();
		rob.print();
		
		assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
				rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		
		System.out.println("\n\n\n");
	}
	
	/*
	 * Cuando se carga la 3ra instruccion, se hace renaming en la RS debido
	 * a que uno de los operandos es calculado por una instruccion anterior
	 */
	@Test
	void testFullROB() throws InterruptedException, FileNotFoundException {
		//System.out.println("\n\nTest de estado de ROB en clock 3");
		Clocks clock = new Clocks();
		
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		
		RS bufferADD = new RS(3);
		RS bufferMUL = new RS(3);
		LS bufferLOAD = new LS(3);
		
		Load load = new Load(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, 1,cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,rob,reg);

		Thread thInstruction = new Thread(instructions);
		Thread thROB = new Thread(rob);
		Thread thAdd = new Thread(add);
		Thread thMul = new Thread(mul);
		Thread thLoad = new Thread(load);
		
		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(2);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);
		
		thInstruction.start();
		thROB.start();
		thAdd.start();
		thLoad.start();
		thMul.start();
		
		Main main;
		int count = 0;
		while(count < 3) {
			count++;
			System.out.println("Clock: "+count);
			clock.take();
			cdb.write_release();
			Thread.sleep(1 * 1000);
			clock.release();
			add.print();
			rob.print();
		}
		assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
				rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		
		
		/*Main.clocks = 1;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
				rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		add.print();
		rob.print();
		System.out.println("\n2");
		
		Main.clocks++;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
				rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		add.print();
		rob.print();
		System.out.println("\n3");
		
		Main.clocks++;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
				rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		add.print();
		rob.print();
		System.out.println("\n4");
		
		Main.clocks++;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		//assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
		//		rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		add.print();
		rob.print();
		System.out.println("\n5");
		
		Main.clocks++;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		//assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
		//		rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		add.print();
		rob.print();
		System.out.println("\n6");
	
		Main.clocks++;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		//assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
		//		rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		add.print();
		rob.print();
		System.out.println("\n7");
		
		Main.clocks++;
		clock.take();
		cdb.write_release();
		Thread.sleep(1 * 1000);
		clock.release();
		add.print();
		rob.print();
		System.out.println("\n\n\n");
		//assertTrue(rob.getROB(0).getDest().equals("R1") && rob.getROB(0).getType().equals("ADD") &&
		//		rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		
		*/
		
	}
	
	
	@Test
	void testRobWriteReg( ) throws InterruptedException, FileNotFoundException {
		System.out.println("\n\nTest de escritura en registros");
		Clocks clock = new Clocks();
		
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		
		RS bufferADD = new RS(3);
		RS bufferMUL = new RS(3);
		LS bufferLOAD = new LS(3);
		
		Load load = new Load(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, 9,cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,rob,reg);

		Thread thInstruction = new Thread(instructions);
		Thread thLoad = new Thread(load);
		Thread thAdd = new Thread(add);
		Thread thMul = new Thread(mul);
		Thread thROB = new Thread(rob);
		
		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(2);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);
		
		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
		
		int count = 0;
		
		while(count < 6) {
			count++;
			System.out.println("Clock: "+count);
			clock.take();
			cdb.write_release();
			Thread.sleep(1 * 1000);
			clock.release();
			add.print();
			rob.print();
		}
		
		reg.print();
		assertTrue(reg.getData(0)==3);
		//assertTrue(reg.getData(1)==3);
		//assertTrue(reg.getData(2)==7);
		//assertTrue(reg.getData(3)==47);
		//assertTrue(reg.getData(4)==42);
		//assertTrue(reg.getData(5)==5);
		//assertTrue(reg.getData(6)==6);
		//assertTrue(reg.getData(7)==7);
		//assertTrue(reg.getData(8)==8);
		//System.exit(0);
		
	}
	
	/*@Test
	void testProgramFinish( ) throws InterruptedException, FileNotFoundException {
		System.out.println("\n\nTest de escritura en registros");
		Clocks clock = new Clocks();
		
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		
		RS bufferADD = new RS(3);
		RS bufferMUL = new RS(3);
		LS bufferLOAD = new LS(3);
		
		Load load = new Load(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, 9,cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,rob,reg);

		Thread thInstruction = new Thread(instructions);
		Thread thLoad = new Thread(load);
		Thread thAdd = new Thread(add);
		Thread thMul = new Thread(mul);
		Thread thROB = new Thread(rob);
		
		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(1);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);
		
		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thROB.start();
		
		int count = 0;
		
		
		
		while(count < 14) {
			count++;
			System.out.println("Clock: "+count);
			clock.take();
			cdb.write_release();
			Thread.sleep(1 * 1000);
			clock.release();
			add.print();
			rob.print();
		}
		
		reg.print();
		assertTrue(reg.getData(0)==3);
		assertTrue(reg.getData(1)==3);
		assertTrue(reg.getData(2)==7);
		assertTrue(reg.getData(3)==47);
		assertTrue(reg.getData(4)==42);
		assertTrue(reg.getData(5)==5);
		assertTrue(reg.getData(6)==6);
		assertTrue(reg.getData(7)==7);
		assertTrue(reg.getData(8)==8);
		
	}*/
	
}
