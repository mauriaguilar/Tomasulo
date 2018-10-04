import java.security.spec.RSAKeyGenParameterSpec;
import java.util.concurrent.Semaphore;

public class Instructions implements Runnable{

	private Semaphore clk;
	private int pc;
	private String [] instruction;
	private String instructions[][] = {
			{"ADD", "F0", "R1", "R2"},
			{"LD", "F1", "1", "R2"},
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"}
	};
	private Bus ndb;
	private Load load;
	private ADD add;
	private MUL mul;
	private ROB rob;
	private Registers reg;
	
	public Instructions(Semaphore clk, Bus bus, Load load, ADD add, MUL mul, ROB rob, Registers reg) {
		this.clk = clk;
		System.out.println("Creando Instructions...");
		pc = 0;
		ndb = bus;
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
				
				System.out.println("Loading instruction: " + result);
			}
			else {
				System.out.println("HLT");
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
				case "ST":	return "ROB"; // ver
				case "BNE":	return "ROB"; // ver
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
				String register_index = instruction[3].valueOf(1);	//Obtiene el valor del registro
				int valor = reg.getData(Integer.parseInt(register_index));	//Convierte el numero a int y lo pasa como argumento
				int direction = Integer.parseInt(instruction[2]) + valor;
				load.setData(index, true, direction);
				allocateROB();
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
			String register_index = instruction[2].valueOf(1);	//Obtiene el valor del registro
			vj = reg.getData(Integer.parseInt(register_index));	//Convierte el numero a int y lo pasa como argumento
		}
		else {
			qj = "ROB" + index_operand1;
		}
		
		if(index_operand2 == -1) {
			String register_index = instruction[3].valueOf(1);	//Obtiene el valor del registro
			vk = reg.getData(Integer.parseInt(register_index));	//Convierte el numero a int y lo pasa como argumento
		}
		else {
			qk = "ROB" + index_operand2;
		}
		
		// dest, busy, operation, value j, value k, index qj, index qk
		rs.setData(index,true, instruction[0], vj, vk, qj, qk);
	}

	private void allocateROB() {
		rob.setData(instruction[1], -1, instruction[0], false);
	}
}