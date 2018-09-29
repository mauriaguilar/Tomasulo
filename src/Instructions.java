
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
	
	public Instructions(Bus bus, Load load, ADD add, MUL mul, ROB rob) {
		System.out.println("Creando Instructions...");
		pc = 0;
		ndb = bus;
		instruction = null;
		this.load = load;
		this.add = add;
		this.mul = mul;
		this.rob = rob;
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
		Object rs;
		
		if(rob.getPlaces() >= 1) {
			isFreeROB = true;
			switch (dest) {
				case "ADD": 
					if(add.getPlaces() >= 1)	
						isFreeRS = true;
					rs = add;
					break;
				case "MUL":
					if(mul.getPlaces() >= 1)	
						isFreeRS = true;
					rs = mul;
					break;
				case "LD":
					if(load.getPlaces() >= 1)	
						isFreeRS = true;
					rs = load;
					break;
			}
		}
		
		if(isFreeRS && isFreeROB) {
			//add.setData(true, instruction[0], vj, vk, qj, qk);
		}
			

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
