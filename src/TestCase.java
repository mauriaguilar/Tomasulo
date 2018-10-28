import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestCase {
	
	private Clocks clock;
	private Bus cdb;
	private Registers reg;
	private Memory mem;
	private Reserve_Station bufferADD;
	private Reserve_Station bufferMUL;
	private LOAD_Station bufferLOAD;
	private ROB_Station bufferROB;
	private ProgramLoader loader;
	private LOAD load;
	private ADD add;
	private MUL mul;
	private ROB rob;
	private Instructions instructions;
	private Thread thInstruction;
	private Thread thLoad;
	private Thread thAdd;
	private Thread thMul;
	private Thread thRob;
	private Thread thClock;
	
	/*
	 * TEST_01: Carga en ROB
	 * Test del estado del ROB luego de que Instructions leyo una instruccion.
	 * Debe cargar la instruccion en ROB y en la RS correspondiente
	 */
	@Test
	//void testPutInROB() throws InterruptedException {
	void test_01() throws InterruptedException {
		System.out.println("::::::::::::::::::::::::::::::\n\t\tPUT_IN_ROB\n::::::::::::::::::::::::::::::\n");
				
		int sizeRob = 9;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		thClock.start();

		clock.take();
		Thread.sleep(1 * 100);
		//clock.release();
		
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
	 * TEST_02: STALL
	 * Test del bloqueo de la carga de instrucciones cuando el buffer de ROB
	 * se lleno. 
	 * Se debe esperar a que se libere un lugar para luego cargar otra instruccion.
	 */
	@Test
	//void testFullROB() throws InterruptedException {
	void test_02() throws InterruptedException {
		System.out.println("::::::::::::::::::::::::::::::\n\t\tFULL_ROB\n::::::::::::::::::::::::::::::\n");

		int sizeRob = 1;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		
		clock.setModeTest(true);
		thClock.start();
		
		int clocks = 0;
		while(clocks < 6) {
			clocks++;
			pause(250);
			
			if(clocks==1) {
				assertTrue(rob.getROB(0).getDest().equals("R0") && rob.getROB(0).getType().equals("ADD") &&
						rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
			}
			if(clocks==6) {
				assertTrue(rob.getROB(0).getDest().equals("R1") && rob.getROB(0).getType().equals("LD") &&
						rob.getROB(0).getReady()==false && rob.getROB(0).getValue().equals("-1"));
			}

			clock.release();
			
			add.print();
			load.print();
			rob.print();
		}	
	}
	
	/*
	 * TEST_03: Escritura en Registros
	 * Test de escritura de ROB en Registers. Cuando ROB tiene la entrada de la cabecera
	 * con valor y flag Ready en True, debe escribir el valor en el registro correspondiente.
	 */
	@Test
	//void testRobWriteReg( ) throws InterruptedException {
	void test_03( ) throws InterruptedException {
		System.out.println("\n::::::::::::::::::::::::::::::\n\t\tROB_WRITE_IN_REG\n::::::::::::::::::::::::::::::\n");
		
		int sizeRob = 9;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		thClock.start();
		
		int clocks = 0;
		while(clocks < 7) {
			clocks++;
			cdb.write_release();
			clock.take();
			Thread.sleep(1 * 100);
			clock.release();
			cdb.acquireDelete(4);
			cdb.delete();
			//add.print();
			//load.print();
			//rob.print();
		}
		
		reg.print();
		assertTrue(reg.getData(0)==3);
	}
	
	
	
	/*
	 * TEST_04: Lectura y Ejecucion en orden.
	 * Test que comprueba que las instrucciones commiteadas en el ROB,
	 * se realizan en el mismo orden en el que fueron leidas. 
	 */
	
	@Test
	//void testRobOrderWrite( ) throws InterruptedException {
	void test_04() throws InterruptedException {
		System.out.println("\n::::::::::::::::::::::::::::::\n\t\tROB_WRITE_IN_ORDER\n::::::::::::::::::::::::::::::\n");
		
		int sizeRob = 9;
		int programNumber = 2;
		initialize(sizeRob,programNumber);
		clock.setModeTest(true);
		thClock.start();
		
		int pc = 0;
		ROB_Entry instruction_to_remove;
		String instruction;
		
		int clocks = 0;
		while(clocks < 14) {
			clocks++;
			pause(250);
			rob.print();
			
			instruction_to_remove = rob.getROB(rob.getRemoveIndex());	// ROB
			instruction = instructions.getInstruction(pc)[0];			// Instructions
			
			System.out.println("GET READY "+instruction_to_remove.getReady());
			System.out.println("INSTRUCTION "+instruction);
			System.out.println("GET TYPE  "+instruction_to_remove.getType());
			
			// If instruction to remove is Ready
			if(	instruction_to_remove.getReady() ) {
				assertTrue(
						// Check if this ROB instruction match with the order on the Instructions instruction
						instruction.equals( instruction_to_remove.getType() )
				);
				
				pc++;
			}

			clock.release();
		}
		

	}
	
	/*
	 * TEST 05: Resultados
	 * Test que ejecuta un programa en particular, y verifica que el resultado
	 * final de los registros y memoria sea el correcto.
	 */
	@Test
	//void testProgramFinishGood() throws InterruptedException {
	void test_05() throws InterruptedException {
		System.out.println("\n::::::::::::::::::::::::::::::\n\t\tPROGRAM_FINISH_GOOD\n::::::::::::::::::::::::::::::\n");
		
		int sizeRob = 9;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		clock.setModeTest(true);
		thClock.start();
		
		while(!clock.getDone()) {
			pause(250);
						
			clock.release();	
			System.out.print(":");
		}

		System.out.print("______________________________________");
		
		if(programNumber==1) {
			assertTrue(reg.getData(0)==3);
			assertTrue(reg.getData(1)==3);
			assertTrue(reg.getData(2)==7);
			assertTrue(reg.getData(3)==47);
			assertTrue(reg.getData(4)==42);
			assertTrue(reg.getData(5)==5);
			assertTrue(reg.getData(6)==6);
			assertTrue(reg.getData(7)==7);
			assertTrue(reg.getData(8)==8);
			
			assertTrue(mem.getValue(0)==0);
			assertTrue(mem.getValue(1)==1);
			assertTrue(mem.getValue(2)==2);
			assertTrue(mem.getValue(3)==3);
			assertTrue(mem.getValue(4)==4);
			assertTrue(mem.getValue(5)==3);
			assertTrue(mem.getValue(6)==6);
			assertTrue(mem.getValue(7)==42);
			assertTrue(mem.getValue(8)==8);
		}
		if(programNumber==2) {
			assertTrue(reg.getData(0)==3);
			assertTrue(reg.getData(1)==5);
			assertTrue(reg.getData(2)==7);
			assertTrue(reg.getData(3)==3);
			assertTrue(reg.getData(4)==11);
			assertTrue(reg.getData(5)==13);
			assertTrue(reg.getData(6)==6);
			assertTrue(reg.getData(7)==7);
			assertTrue(reg.getData(8)==8);
			
			assertTrue(mem.getValue(0)==0);
			assertTrue(mem.getValue(1)==1);
			assertTrue(mem.getValue(2)==2);
			assertTrue(mem.getValue(3)==3);
			assertTrue(mem.getValue(4)==4);
			assertTrue(mem.getValue(5)==3);
			assertTrue(mem.getValue(6)==6);
			assertTrue(mem.getValue(7)==7);
			assertTrue(mem.getValue(8)==8);
		}
		if(programNumber==3) {
			assertTrue(reg.getData(0)==3);
			assertTrue(reg.getData(1)==4);
			assertTrue(reg.getData(2)==8);
			assertTrue(reg.getData(3)==53);
			assertTrue(reg.getData(4)==48);
			assertTrue(reg.getData(5)==5);
			assertTrue(reg.getData(6)==6);
			assertTrue(reg.getData(7)==7);
			assertTrue(reg.getData(8)==8);
			
			assertTrue(mem.getValue(0)==0);
			assertTrue(mem.getValue(1)==1);
			assertTrue(mem.getValue(2)==2);
			assertTrue(mem.getValue(3)==3);
			assertTrue(mem.getValue(4)==4);
			assertTrue(mem.getValue(5)==4);
			assertTrue(mem.getValue(6)==6);
			assertTrue(mem.getValue(7)==7);
			assertTrue(mem.getValue(8)==8);
		}
		if(programNumber==4) {
			assertTrue(reg.getData(0)==5);
			assertTrue(reg.getData(1)==5);
			assertTrue(reg.getData(2)==3);
			assertTrue(reg.getData(3)==3);
			assertTrue(reg.getData(4)==3);
			assertTrue(reg.getData(5)==15);
			assertTrue(reg.getData(6)==9);
			assertTrue(reg.getData(7)==9);
			assertTrue(reg.getData(8)==45);
			
			assertTrue(mem.getValue(0)==0);
			assertTrue(mem.getValue(1)==1);
			assertTrue(mem.getValue(2)==2);
			assertTrue(mem.getValue(3)==45);
			assertTrue(mem.getValue(4)==4);
			assertTrue(mem.getValue(5)==9);
			assertTrue(mem.getValue(6)==6);
			assertTrue(mem.getValue(7)==7);
			assertTrue(mem.getValue(8)==8);
		}
	}
	
	private void initialize(int robSize, int programNumber) {

		// Common Data Bus
		cdb = new Bus();
		
		// Memory and Registers
		reg = new Registers(9);
		mem = new Memory(9);
		
		// Reserve Stations
		bufferADD = new Reserve_Station(3);
		bufferMUL = new Reserve_Station(3);
		bufferLOAD = new LOAD_Station(3);
		bufferROB = new ROB_Station(robSize);

		// Functional Units
		loader = new ProgramLoader(programNumber);
		load = new LOAD(bufferLOAD, mem, cdb);
		add = new ADD(bufferADD, cdb);
		mul = new MUL(bufferMUL, cdb);
		rob = new ROB(bufferROB, cdb, reg, mem);
		instructions = new Instructions(bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg,loader);		

		// Clock Unit
		clock = new Clocks(cdb, instructions, add, mul, load, rob, reg, mem);
		thClock = new Thread(clock);
	}
	
	
	private void pause(int i) {
		try {
			Thread.sleep(i);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
