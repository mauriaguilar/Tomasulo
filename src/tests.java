import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class tests {
	  
	/*
	 * Test del estado del ROB luego de que Instructions leyó una instruccion.
	 * Debe cargar la instruccion en ROB y en la RS correspondiente
	 */
	@Test
	void testPutInROB() throws InterruptedException {
		System.out.println("==============\nPUT_IN_ROB\n==============\n");
		Clocks clock = new Clocks();
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		 
		Reserve_Station bufferADD = new Reserve_Station(3);
		Reserve_Station bufferMUL = new Reserve_Station(3);
		LOAD_Station bufferLOAD = new LOAD_Station(3);
		ROB_Station bufferROB = new ROB_Station(9);
		
		int programNumber = 1;
		ProgramLoader loader = new ProgramLoader(programNumber);
		LOAD load = new LOAD(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, bufferROB, cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg,loader);

		Thread thInstruction = new Thread(instructions);
		Thread thLoad = new Thread(load);
		Thread thAdd = new Thread(add);
		Thread thMul = new Thread(mul);
		Thread thRob = new Thread(rob);
		

		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thRob.start();
		
		clock.take();
		Thread.sleep(1 * 1000);
		clock.release();
		
		add.print();
		rob.print();
		
		assertTrue( (rob.getROB(0).getDest().equals("R0") )
				&&  (rob.getROB(0).getType().equals("ADD") ) 
				&& 	(rob.getROB(0).getReady()==false )
				&&	(rob.getROB(0).getValue().equals("-1") )
				);
		assertTrue( (bufferADD.get(0).getDest()== 0) 
				&& 	(bufferADD.get(0).getOp().equals("ADD")) 
				&&	(bufferADD.get(0).getVj()==1) 
				&& 	(bufferADD.get(0).getVk()==2)
				&& 	(bufferADD.get(0).getBusy()==true)
				);
		System.out.println("\n\n\n");
	}
	
	/*
	 * Cuando se carga la 3ra instruccion, se hace renaming en la RS debido
	 * a que uno de los operandos es calculado por una instruccion anterior
	 */
	@Test
	void testFullROB() throws InterruptedException {
		System.out.println("==============\nFULL_ROB\n==============\n");
		Clocks clock = new Clocks();
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		
		Reserve_Station bufferADD = new Reserve_Station(3);
		Reserve_Station bufferMUL = new Reserve_Station(3);
		LOAD_Station bufferLOAD = new LOAD_Station(3);
		ROB_Station bufferROB = new ROB_Station(1);
		
		int programNumber = 1;
		ProgramLoader loader = new ProgramLoader(programNumber);
		LOAD load = new LOAD(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, bufferROB, cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg,loader);

		Thread thInstruction = new Thread(instructions);
		Thread thLoad = new Thread(load);
		Thread thAdd = new Thread(add);
		Thread thMul = new Thread(mul);
		Thread thRob = new Thread(rob);
		

		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thRob.start();
		
		int clocks = 0;
		while(clocks < 6) {
			clocks++;
			cdb.write_release();
			clock.take();
			Thread.sleep(1 * 1000);
			clock.release();
			cdb.acquireDelete(4);
			cdb.delete();
			add.print();
			load.print();
			rob.print();
			
			if(clocks==1) {
				assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
						rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
			}
			if(clocks==6) {
				assertTrue(rob.getROB(0).getDest().equals("R1") && rob.getROB(0).getType().equals("LD") &&
						rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
			}
		}	
	}
	
	
	@Test
	void testRobWriteReg( ) throws InterruptedException {
		System.out.println("\n==============\nROB_WRITE_IN_REG\n==============\n");
		Clocks clock = new Clocks();
		Bus cdb = new Bus();
		
		Registers reg = new Registers(9);
		Memory mem = new Memory(9);
		
		Reserve_Station bufferADD = new Reserve_Station(3);
		Reserve_Station bufferMUL = new Reserve_Station(3);
		LOAD_Station bufferLOAD = new LOAD_Station(3);
		ROB_Station bufferROB = new ROB_Station(1);
		
		int programNumber = 1;
		ProgramLoader loader = new ProgramLoader(programNumber);
		LOAD load = new LOAD(clock, bufferLOAD, mem, cdb);
		ADD add = new ADD(clock, bufferADD, cdb);
		MUL mul = new MUL(clock, bufferMUL, cdb);
		ROB rob = new ROB(clock, bufferROB, cdb, reg, mem);
		Instructions instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg,loader);

		Thread thInstruction = new Thread(instructions);
		Thread thLoad = new Thread(load);
		Thread thAdd = new Thread(add);
		Thread thMul = new Thread(mul);
		Thread thRob = new Thread(rob);
		

		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thRob.start();
		
		int clocks = 0;
		while(clocks < 6) {
			clocks++;
			cdb.write_release();
			clock.take();
			Thread.sleep(1 * 1000);
			clock.release();
			cdb.acquireDelete(4);
			cdb.delete();
			add.print();
			load.print();
			rob.print();
		}
		
		reg.print();
		assertTrue(reg.getData(0)==3);
	}
}