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
	void testROB() throws InterruptedException, FileNotFoundException {
		System.out.println("\n\nTest de estado de ROB en clock 3");
		Semaphore clk = new Semaphore(1);
		Semaphore clkInstruction = new Semaphore(1);
		Semaphore clkLoad = new Semaphore(1);
		Semaphore clkADD = new Semaphore(1);
		Semaphore clkMUL = new Semaphore(1);
		Semaphore clkROB = new Semaphore(1);
		
		Bus cdb = new Bus();
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		Load load = new Load(clkLoad, 3, mem, cdb, 2);
		ADD add = new ADD(clkADD, 3, cdb, 2);
		MUL mul = new MUL(clkMUL, 3, cdb, 5);
		ROB rob = new ROB(clkROB, 9,cdb, reg, mem);
		Instructions instructions = new Instructions(clkInstruction,load,add,mul,rob,reg);

		Thread thInstruction = new Thread(instructions);
		Thread thROB = new Thread(rob);

		ProgramLoader program = new ProgramLoader();
		String[][] instructions_list = program.getInstrucions(1);
		Instructions.setInstruction(instructions_list);
		Thread.sleep(3 * 1000);
		
		thInstruction.start();
		thROB.start();
		
		int counter = 0;
		while(counter < 2) {
			clkInstruction.release();
			clkROB.release();
			Thread.sleep(1 * 1000);
			counter++;
		}
		rob.print();
		
		assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
				rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
		assertTrue(rob.getROB(1).getDest().equals("R1") && rob.getROB(1).getType().equals("LD") &&
				rob.getROB(1).getReady()==false && rob.getROB(1).getValue().equals("-1"));
		assertTrue(rob.getROB(2).getDest().equals("R2") && rob.getROB(2).getType().equals("ADD") &&
				rob.getROB(2).getReady()==false && rob.getROB(2).getValue().equals("-1"));
	}
	
	/*
	 * Cuando se carga la 3ra instruccion, se hace renaming en la RS debido
	 * a que uno de los operandos es calculado por una instruccion anterior
	 */
	@Test
	void testRENAMING() throws InterruptedException, FileNotFoundException {
		System.out.println("\n\nTest de funcionamiento de 'Renaming'");
		Semaphore clk = new Semaphore(1);
		Semaphore clkInstruction = new Semaphore(1);
		Semaphore clkLoad = new Semaphore(1);
		Semaphore clkADD = new Semaphore(1);
		Semaphore clkMUL = new Semaphore(1);
		Semaphore clkROB = new Semaphore(1);
		
		Bus cdb = new Bus();
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		Load load = new Load(clkLoad, 3, mem, cdb, 2);
		ADD add = new ADD(clkADD, 3, cdb, 2);
		MUL mul = new MUL(clkMUL, 3, cdb, 5);
		ROB rob = new ROB(clkROB, 9,cdb, reg, mem);
		Instructions instructions = new Instructions(clkInstruction,load,add,mul,rob,reg);

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
		
		int counter = 0;
		while(counter < 2) {
			clkInstruction.release();
			clkLoad.release();
			clkADD.release();
			clkMUL.release();
			clkROB.release();
			Thread.sleep(1 * 1000);
			counter++;
		}
		add.print();
		rob.print();
		System.out.println(add.getRS(1).getQj());
		assertTrue(add.getRS(1).getQj().equals("ROB1"));
	}
	
	@Test
	void testRegisters( ) throws InterruptedException, FileNotFoundException {
		/*System.out.println("\n\nTest de escritura en registros");
		Semaphore clk = new Semaphore(1);
		Semaphore clkInstruction = new Semaphore(1);
		Semaphore clkLoad = new Semaphore(1);
		Semaphore clkADD = new Semaphore(1);
		Semaphore clkMUL = new Semaphore(1);
		Semaphore clkROB = new Semaphore(1);
		
		Bus cdb = new Bus();
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		Load load = new Load(clkLoad, 3, mem, cdb, 2);
		ADD add = new ADD(clkADD, 3, cdb, 2);
		MUL mul = new MUL(clkMUL, 3, cdb, 5);
		ROB rob = new ROB(clkROB, 9,cdb, reg, mem);
		Instructions instructions = new Instructions(clkInstruction,load,add,mul,rob,reg);

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
		
		int counter = 0;
		Main main = new Main();
		main.setClock(0);
		while(counter < 14) {
			main.setClock(counter);
			//clk.acquire();
			
			clkInstruction.release();
			clkLoad.release();
			clkADD.release();
			clkMUL.release();
			clkROB.release();
			
			cdb.write_release();
			Thread.sleep(1 * 1000);
			clk.release();
			counter++;
		}
		*/
		
		Main main = new Main();
		String [] args = {};
		Main.main(args);
		Registers reg = main.getRegister();
		reg.print();
		assertTrue(reg.getData(0)==0);
		assertTrue(reg.getData(1)==3);
		assertTrue(reg.getData(2)==3);
		assertTrue(reg.getData(3)==42);
		assertTrue(reg.getData(4)==47);
		assertTrue(reg.getData(5)==5);
		assertTrue(reg.getData(6)==6);
		assertTrue(reg.getData(7)==7);
		assertTrue(reg.getData(8)==8);
	}
	
}
