import java.util.concurrent.Semaphore;

public class Instructions implements Runnable{

	private Semaphore clk;
	private static int pc;
	private String [] instruction;
	private static String instructions[][];	
	private LOAD_Station bufferLOAD;
	private Reserve_Station bufferADD;
	private Reserve_Station bufferMUL;
	private ROB_Station bufferROB;
	private ROB rob;
	private Registers reg;
	private ProgramLoader loader;
	private boolean loadInstructions;
	
	public Instructions(LOAD_Station bufferLOAD, Reserve_Station bufferADD, Reserve_Station bufferMUL, ROB_Station bufferROB, ROB rob, Registers reg, ProgramLoader loader) {
		pc = 0;
		instruction = null;
		this.bufferLOAD = bufferLOAD;
		this.bufferADD = bufferADD;
		this.bufferMUL = bufferMUL;
		this.bufferROB = bufferROB;
		this.rob = rob;
		this.reg = reg;
		this.loader = loader;
		Clocks.loading = false;
	}
	
	//@Override
	public void run() {
		
		while(true) {
			
			loadInstructions();
			
			Clocks.waitClockInstructions();
			Clocks.loading = true;
			
			//System.out.println("INSTRUCTIONS PASO CLOCK"); 
			
			String dest;
			
			// 1) Get Instruction
			instruction = getNext();
			
			if(instruction != null) {
				
				// 2) Decode Instruction
				dest = decode_instruction();
				
				// 3) Verify availables slots, renaming and allocate
				checkEmptyes(dest);
			}
			else {
				System.out.println("Instructions readed a HALT. The program was loaded correctly");
				break;
			}
			Clocks.loading = false;
		}
		
	}
	
	private void loadInstructions() {
		if(!loadInstructions){
			instructions = loader.getInstrucions();
			loadInstructions = true;
		}
	}

	public String[] getNext() {
		if(pc < instructions.length) {		//Primero consultamos si hay instrucciones en el buffer
			return instructions[pc++];
		}
		else
			//System.out.println("Instructions readed a HALT. The program was loaded correctly");
			return null;
	}
	
	public boolean isHLT() {
		if(loadInstructions && instruction == null) {
			//System.out.println("instruccion NULLLL");
			return true;
		}
		return false;
	}
	
	private String decode_instruction() {
		String operation;
		if(instruction != null) {
			operation = instruction[0];
			switch (operation) {				
				case "ADD":	return "ADD";
				case "SUB":	return "ADD";
				case "MUL":	return "MUL";
				case "DIV":	return "MUL";
				case "LD":	return "LD";
				case "ST":	return "ST";
				default: return null;
			}
		}
		else
			return null;
	}
	
	private String checkEmptyes(String dest) {
		//Blocks waiting ROB's places
		int indexROB;
		int indexRS, indexLS;

		bufferROB.getResource();
		indexROB = rob.getIndex();
		
		switch (dest) {
			case "ADD":
				// If there is an empty slot in the ADD RS, else block here
				indexRS = bufferADD.getResource();
				allocateROB();
				rob.acquire();	//Take semaphore to write RS
				allocateRS(bufferADD,indexROB,indexRS);
				break;
			case "MUL":
				// If there is an empty slot in the MUL RS, else block here
				indexRS = bufferMUL.getResource();
				allocateROB();
				rob.acquire();	//Take semaphore to write RS
				allocateRS(bufferMUL,indexROB,indexRS);
				break;
			case "LD":
				// If there is an empty slot in the LD RS, else block here
				indexLS = bufferLOAD.getResource();
				allocateROB();
				rob.acquire();	//Take semaphore to write RS
				allocateLS(indexROB,indexLS);
				break;
			case "ST":
				allocateSTinROB();
				break;
		}
		rob.release();		//Free semaphore 
		return "ROB and "+dest+" allocated";
	}

	private void allocateRS(Reserve_Station rs, int indexROB, int indexRS) {
		//rob.acquire();	//Take semaphore to write RS
		//Renaming
		String qj = "";
		String qk = "";
		int vj = -1;
		int vk = -1;
		int index_operand1 = rob.compareOperand(instruction[2]);
		int index_operand2 = rob.compareOperand(instruction[3]);
		
		if(index_operand1 == -1) {
			int register_index = Character.getNumericValue( instruction[2].charAt(1) );
			vj = reg.getData(register_index);	//Convierte el numero a int y lo pasa como argumento
			//System.out.println("Register_index: "+register_index+ " --------------- vj: "+vj);
		}
		else {
			if(rob.getROB(index_operand1).getValue().equals("-1")) {
				qj = "ROB" + index_operand1;
			}
			else {
				vj = Integer.parseInt(rob.getROB(index_operand1).getValue());
			}
		}
		
		if(index_operand2 == -1) {
			int register_index = Character.getNumericValue( instruction[3].charAt(1) );
			vk= reg.getData(register_index);	//Convierte el numero a int y lo pasa como argumento
		}
		else {
			if(rob.getROB(index_operand2).getValue().equals("-1")) {
				qk = "ROB" + index_operand2;
			}
			else {
				vk = Integer.parseInt(rob.getROB(index_operand2).getValue());
			}
		}
		/*else {
			qk = "ROB" + index_operand2;
		}*/
		
		// dest, busy, operation, value j, value k, index qj, index qk
		rs.setData(indexROB,indexRS, true, instruction[0], vj, vk, qj, qk, Clocks.clocks);
	}
	
	private void allocateLS(int indexROB, int indexLS) {
		//rob.acquire(); 		//Take semaphore to write LS

		String tag = "";
		int shift = -1;
		int base = -1;
		int index_operand = rob.compareOperand(instruction[3]);
		
		if(index_operand == -1) {
			int register_index = Character.getNumericValue( instruction[3].charAt(1) );
			shift = reg.getData(register_index);
		}
		else {
			if(rob.getROB(index_operand).getValue().equals("-1")) {
				tag = "ROB" + index_operand;
			}
			else {
				shift = Integer.parseInt(rob.getROB(index_operand).getValue());
			}
		}
		// Calc
		//int register_index = Character.getNumericValue( instruction[3].charAt(1) );
		//int value = reg.getData(register_index);	//Convierte el numero a int y lo pasa como argumento
		//int direction = Integer.parseInt(instruction[2]) + value;
		base = Integer.parseInt(instruction[2]);
		
		bufferLOAD.setData(indexROB, indexLS, true, -1, base, shift, tag,  Clocks.clocks);
	}

	private void allocateROB() {
		rob.setData(instruction[1], "-1", instruction[0], false);
	}
	
	private void allocateSTinROB() {
		// ST 10,R3,R5
		//rob.acquireWrite();
		int register_index1,register_index2;
		int register_value1;
		String register_value2;
		String dest;
		
		int index_operand = rob.compareOperand(instruction[3]);
		
		if(index_operand == -1) {
			//Get value
			register_index2 = Character.getNumericValue( instruction[3].charAt(1) );
			register_value2 = ""+reg.getData(register_index2);	//Convierte el numero a int y lo pasa como argumento			
		}
		else {
			register_value2 = "ROB" + index_operand;
		}
		
		//Verifica si el registro para calcular la direccion donde guardar, se modifica antes
		//de hacer COMMIT en STORE.
		int index_operand1 = rob.compareOperand(instruction[2]);
		if(index_operand1 == -1) {
			register_index1 = Character.getNumericValue( instruction[2].charAt(1) );
			register_value1 = reg.getData(register_index1);	//Convierte el numero a int y lo pasa como argumento
			dest = ""+ (register_value1 + Character.getNumericValue( instruction[1].charAt(0) ));	
		}
		else {
			dest = ""+instruction[1]+"+"+instruction[2];
		}
		//Get dest
		//register_index1 = Character.getNumericValue( instruction[2].charAt(1) );
		//register_value1 = reg.getData(register_index1);	//Convierte el numero a int y lo pasa como argumento
		//dest = ""+ (register_value1 + Character.getNumericValue( instruction[1].charAt(0) ));	
		
		rob.setData(dest, register_value2, instruction[0], false);
	}

	public static int getPC() {
		return pc;
	}

	public static void setInstruction(String[][] instructions_list) {
		instructions = instructions_list;
		//System.out.println(instructions);
	}
	
	public String[] getInstruction(int i) {
		return instructions[i];
	}
	
	public int getSizeProgram() {
		return instructions.length;
	}
}