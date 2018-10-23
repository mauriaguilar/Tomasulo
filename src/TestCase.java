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
	
	/*
	 * TEST_01
	 * Test del estado del ROB luego de que Instructions leyó una instruccion.
	 * Debe cargar la instruccion en ROB y en la RS correspondiente
	 */
	@Test
	//void testPutInROB() throws InterruptedException {
	void test_01() throws InterruptedException {
		System.out.println("::::::::::::::::::::::::::::::\n\t\tPUT_IN_ROB\n::::::::::::::::::::::::::::::\n");
				
		int sizeRob = 9;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		start();

		clock.take();
		Thread.sleep(1 * 100);
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
	 * TEST_02
	 * Test del bloqueo de la carga de instrucciones cuando el buffer de ROB
	 * se llenó. 
	 * Se debe esperar a que se liberer un lugar para luego cargar otra instruccion.
	 */
	@Test
	//void testFullROB() throws InterruptedException {
	void test_02() throws InterruptedException {
		System.out.println("::::::::::::::::::::::::::::::\n\t\tFULL_ROB\n::::::::::::::::::::::::::::::\n");

		int sizeRob = 1;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		start();
		
		int clocks = 0;
		while(clocks < 6) {
			clocks++;
			cdb.write_release();
			clock.take();
			Thread.sleep(1 * 100);
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
	
	/*
	 * TEST_03
	 * Test de escritura de Rob en Registers. Cuando Rob tiene la entrada de la cabecera
	 * con valor y flag Ready en True, debe escribir el valor en el registro correspondiente.
	 */
	@Test
	//void testRobWriteReg( ) throws InterruptedException {
	void test_03( ) throws InterruptedException {
		System.out.println("\n::::::::::::::::::::::::::::::\n\t\tROB_WRITE_IN_REG\n::::::::::::::::::::::::::::::\n");
		
		int sizeRob = 9;
		int programNumber = 1;
		initialize(sizeRob,programNumber);
		start();
		
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
	 * TEST_04
	 * Test de escritura en orden de Rob. Cuando una instruccion se completo, pero no 
	 * se encuentra en la cabecera de Rob, no debe escribirse en registro o memoria segun corresponda,
	 * hasta que la se encuentre en la cabecera de Rob.
	 */
	
	@Test
	//void testRobOrderWrite( ) throws InterruptedException {
	void test_04( ) throws InterruptedException {
		System.out.println("\n::::::::::::::::::::::::::::::\n\t\tROB_WRITE_IN_ORDER\n::::::::::::::::::::::::::::::\n");
		
		int sizeRob = 9;
		int programNumber = 6;
		initialize(sizeRob,programNumber);
		start();
		
		int clocks = 0;
		while(clocks < 14) {
			clocks++;
			cdb.write_release();
			clock.take();
			Thread.sleep(1 * 100);
			clock.release();
			cdb.acquireDelete(4);
			cdb.delete();
			rob.print();
			
			if(clocks == 3) {
				assertTrue(rob.getRemoveIndex()==0);
				assertTrue(rob.getROB(0).getType().equals("ADD") && rob.getROB(0).getReady()==false);
				assertTrue(rob.getROB(1).getType().equals("MUL") && rob.getROB(1).getReady()==false);
				assertTrue(rob.getROB(2).getType().equals("LD") && rob.getROB(2).getReady()==false);
			}
			if(clocks == 6) {
				assertTrue(rob.getRemoveIndex()==1);
				assertTrue(rob.getROB(1).getType().equals("MUL") && rob.getROB(1).getReady()==false);
				assertTrue(rob.getROB(2).getType().equals("LD") && rob.getROB(2).getReady()==true);
			}
			if(clocks == 11) {
				assertTrue(rob.getRemoveIndex()==1);
				assertTrue(rob.getROB(1).getType().equals("MUL") && rob.getROB(1).getReady()==true);
				assertTrue(rob.getROB(2).getType().equals("LD") && rob.getROB(2).getReady()==true);
			}
			if(clocks == 12) {
				assertTrue(rob.getRemoveIndex()==2);
				assertTrue(rob.getROB(2).getType().equals("LD") && rob.getROB(2).getReady()==true);
			}
			if(clocks == 13) {
				assertTrue(rob.getRemoveIndex()==3);
				assertFalse(rob.getROB(2).getType().equals("LD") && rob.getROB(2).getReady()==true);
			}
		}
	}
	
	/*
	 * Test que ejecuta un programa en particular, y verifica que el resultado
	 * final de los registros y memoria sea el correcto.
	 */
	@Test
	//void testProgramFinishGood() throws InterruptedException {
	void test_05() throws InterruptedException {
		System.out.println("\n::::::::::::::::::::::::::::::\n\t\tPROGRAM_FINISH_GOOD\n::::::::::::::::::::::::::::::\n");
		
		int sizeRob = 9;
		int programNumber = 4;
		initialize(sizeRob,programNumber);
		start();
		
		while(true) {
			//Release CDB
			cdb.write_release();
			//Enable the execution of a clock
			clock.take();
			//Time of execution of one clock 
			Thread.sleep(1 * 150);
			//Print tables
			printTables();
			
			if(instructions.isHLT() && rob.isEmpty()) {
				//Print Registers and Memory tables
				printMemories();
				break;
			}
			else {
				//Release clock
				clock.release();
				cdb.acquireDelete(4);
				cdb.delete();
			}
		}
		
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
		clock = new Clocks();
		cdb = new Bus();
		
		reg = new Registers(9);
		mem = new Memory(9);
		 
		bufferADD = new Reserve_Station(3);
		bufferMUL = new Reserve_Station(3);
		bufferLOAD = new LOAD_Station(3);
		bufferROB = new ROB_Station(robSize);
		
		loader = new ProgramLoader(programNumber);
		load = new LOAD(clock, bufferLOAD, mem, cdb);
		add = new ADD(clock, bufferADD, cdb);
		mul = new MUL(clock, bufferMUL, cdb);
		rob = new ROB(clock, bufferROB, cdb, reg, mem);
		instructions = new Instructions(clock.clkInstruction(),bufferLOAD,bufferADD,bufferMUL,bufferROB,rob,reg,loader);

		thInstruction = new Thread(instructions);
		thLoad = new Thread(load);
		thAdd = new Thread(add);
		thMul = new Thread(mul);
		thRob = new Thread(rob);
	}
	
	private void start() {
		thInstruction.start();
		thLoad.start();
		thAdd.start();
		thMul.start();
		thRob.start();
	}
	
	private void printTables() {
		add.print();
		mul.print();
		load.print();
		rob.print(); 
	}
	
	private void printMemories() {
		reg.print();
		mem.print();
		System.out.println("\n\nThat's all");
		System.out.println("************************************************************");
	}
	
}
