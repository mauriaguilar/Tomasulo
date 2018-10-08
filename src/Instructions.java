import java.security.spec.RSAKeyGenParameterSpec;
import java.util.concurrent.Semaphore;

public class Instructions implements Runnable{

	private Semaphore clk;
	private int pc;
	private String [] instruction;
	private static String instructions[][];
	/*= {
			{"ADD", "R0", "R1", "R2"},			// R0 = R1 + R2 = 1 + 2 = 3
			{"LD", "R1", "1", "R2"},			// R1 = 1 + (R2) = 1 + 2 = M(3) = 3 
			{"ADD", "R2", "R1", "R4"},			// R2 = 3 + 4 = 7
			{"ST", "1", "R4", "R1"},			// M5 = 3
			{"MUL", "R4", "R6", "R2"},			// R4 = 6 * 7 = 42
			{"ADD", "R3", "R4", "R5"},			// R3 = 42 + 5 = 47
	};*/
	
	private Load load;
	private ADD add;
	private MUL mul;
	private ROB rob;
	private Registers reg;
	
	public Instructions(Semaphore clk, Load load, ADD add, MUL mul, ROB rob, Registers reg) {
		this.clk = clk;
		pc = 0;
		instruction = null;
		this.load = load;
		this.add = add;
		this.mul = mul;
		this.rob = rob;
		this.reg = reg;
	}
	
	//@Override
	public void run() {
		
		while(true) {
			
			try {
				clk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			String dest, result = null;
			
			// 1) Get Instruction
			instruction = getNext();
			
			if(instruction != null) {
				//System.out.println("Instruction...");
				
				// 2) Decode Instruction
				dest = decode_instruction();
				
				// 3) Verify availables slots, renaming and allocate
				try {
					result = checkEmptyes(dest);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			else {
				System.out.println("Instructions HLT");
				break;
			}
		}
		
	}
	
	public String[] getNext() {
		//System.out.println("pc:"+pc+"   "+instructions[0]);
		if(pc < instructions.length) {		//Primero consultamos si hay instrucciones en el buffer
			return instructions[pc++];
		}
		else
			System.out.println("No hay mas instrucciones en el buffer");
			return null;
	}
	
	public boolean isHLT() {
		if(instruction == null) {
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
				case "ST":	return "ST"; // ver
				//case "BNE":	return "ROB"; // ver
				default: return null;
			}
		}
		else
			return null;
	}
	
	private String checkEmptyes(String dest) throws InterruptedException {
		int index_operand1;
		int index_operand2;
		Station rs = null;
		int index = -1;
		
		// If there is an empty slot in the ROB
		//if(rob.getPlaces() >= 1) {
		rob.getResource();	//Blocks waiting ROB's places
		index = rob.getIndex();
		
		switch (dest) {
			case "ADD": 
				// If there is an empty slot in the ADD RS
				add.getResource();
				allocate(add,index);
				allocateROB();
				break;
			case "MUL":
				// If there is an empty slot in the MUL RS	
				mul.getResource();
				allocate(mul,index);
				allocateROB();
				break;
			case "LD":
				// If there is an empty slot in the LD RS
				load.getResource();
				int register_index = Character.getNumericValue( instruction[3].charAt(1) );
				int valor = reg.getData(register_index);	//Convierte el numero a int y lo pasa como argumento
				int direction = Integer.parseInt(instruction[2]) + valor;
				load.setData(index, true, direction, Main.clocks);
				allocateROB();
				break;
			case "ST":
				allocateSTinROB();
				break;
		}
		
		return "ROB and "+dest+" allocated";
		
		//PARA REGISTROS DE 2 DIGITOS -> VERIFICAR SI EXISTE UN valueOf(2) --> ver si es NULL


	}
	
	private void allocate(Station rs, int index) {
		
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
			//System.out.println("Renombrado*******************************");
			qj = "ROB" + index_operand1;
		}
		
		if(index_operand2 == -1) {
			int register_index = Character.getNumericValue( instruction[3].charAt(1) );
			vk= reg.getData(register_index);	//Convierte el numero a int y lo pasa como argumento
		}
		else {
			qk = "ROB" + index_operand2;
		}
		
		// dest, busy, operation, value j, value k, index qj, index qk
		rs.setData(index,true, instruction[0], vj, vk, qj, qk, Main.clocks);
	}

	private void allocateROB() {
		rob.setData(instruction[1], "-1", instruction[0], false);
	}
	
	private void allocateSTinROB() {
		// ST 10,R3,R5
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
		
		//Get dest
		register_index1 = Character.getNumericValue( instruction[2].charAt(1) );
		register_value1 = reg.getData(register_index1);	//Convierte el numero a int y lo pasa como argumento
		dest = ""+ (register_value1 + Character.getNumericValue( instruction[1].charAt(0) ));	
		
		rob.setData(dest, register_value2, instruction[0], false);
	}

	public int getPC() {
		return pc;
	}

	public static void setInstruction(String[][] instructions_list) {
		instructions = instructions_list;
		//System.out.println(instructions);
	}
}