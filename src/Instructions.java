
public class Instructions {

	private int pc;
	private String instructions[][] = {
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"},
			{"ADD", "F0", "R1", "R2"}
	};
	
	public Instructions() {
		System.out.println("Creando Instructions...");
		pc = 0;
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
	
}
