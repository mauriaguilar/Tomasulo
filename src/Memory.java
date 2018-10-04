
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
}
