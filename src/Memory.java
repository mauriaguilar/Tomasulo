
public class Memory {
 
	private Integer[] memory;
	
	public Memory(int cap) {
		memory = new Integer[cap];
		for (int i = 0; i < memory.length; i++) {
			memory[i] = i;
		}
	}
	
	public int getValue(int i) {
		return memory[i];
	}
	
	public void setValue(int index, int value) {
		memory[index] = value;
	}
	
	public void print() {
		String table = "\nMEMORY\n";
		table += "N\t|Vi";
		for(int i=0; i<memory.length; i++)
			table += ("\n" + i + "\t|" + getValue(i));
		System.out.println(table);
	}
}
