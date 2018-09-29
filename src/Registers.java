
public class Registers {
	private Register_Entry[] registers;
	
	public Registers(int cap) {
		registers = new Register_Entry[cap];
		for(int i=0; i<cap; i++) {
			registers[i] = new Register_Entry();
		}
	}
	
	public int getData(int index) {
		return registers[index].getVi();
	}
}
