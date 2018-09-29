import java.security.spec.RSAKeyGenParameterSpec;

public class Instructions implements Runnable{

	private int pc;
	private String [] instruction;
	private String instructions[][] = {
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"}
	};
	private Bus ndb;
	private Load load;
	private ADD add;
	private MUL mul;
	private ROB rob;
	private Registers reg;
	
	public Instructions(Bus bus, Load load, ADD add, MUL mul, ROB rob, Registers reg) {
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
		//Ver excepciones
		String dest;
		dest = decode_instruction();
		allocate(dest);
		
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
	
	private void allocate(String dest) {
		Boolean isFreeRS = false;
		Boolean isFreeROB = false;
		int index_operand1;
		int index_operand2;
		Station rs = null;
		
		int index = -1;
		if(rob.getPlaces() >= 1) {
			isFreeROB = true;
			index = rob.getIndex();
			
			switch (dest) {
				case "ADD": 
					if(add.getPlaces() >= 1) {	
						isFreeRS = true;
						//rs = add;
						setear(add,index);
					}
					break;
				case "MUL":
					if(mul.getPlaces() >= 1) {	
						isFreeRS = true;
						//rs = mul;
						setear(mul,index);
					}
					break;
				case "LD":
					if(load.getPlaces() >= 1) {	
						isFreeRS = true;
						rs = load;
						String register_index = instruction[3].valueOf(1);	//Obtiene el valor del registro
						int valor = reg.getData(Integer.parseInt(register_index));	//Convierte el numero a int y lo pasa como argumento
						int direction = Integer.parseInt(instruction[2]) + valor;
						load.setData(index, true, direction);
					}
					break;
			}
		}
		
		//PARA REGISTROS DE 2 DIGITOS -> VERIFICAR SI EXISTE UN valueOf(2) --> ver si es NULL
		
		/*
		if(isFreeRS && isFreeROB) {
			//Renaming
			String qj = "";
			String qk = "";
			int vj = -1;
			int vk = -1;
			index_operand1 = rob.compareOperand(instruction[2]);
			index_operand2 = rob.compareOperand(instruction[3]);
			
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
			
			
			rs.setData(index,true, instruction[0], vj, vk, qj, qk);
		}*/
			

	}
	
	private void setear(Station rs, int index) {
		
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
		
		
		rs.setData(index,true, instruction[0], vj, vk, qj, qk);
	}
	
	private String decode_instruction() {
		instruction = getNext();
		String operation;
		if(instruction != null) {
			operation = instruction[0];
			switch (operation) {				
				case "ADD":	return "ADD";
				case "SUB":	return "ADD";
				case "MUL":	return "MUL";
				case "DIV":	return "MUL";
				case "LD":	return "LD";
				case "ST":	return "ROB";
				case "BNE":	return "ROB";
				default: return null;
			}
		}
		else
			return null;
	}
}
