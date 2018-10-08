
public class Registers {
	private Register_Entry[] registers;
	
	public Registers(int cap) {
		registers = new Register_Entry[cap];
		for(int i=0; i<cap; i++) {
			registers[i] = new Register_Entry();
			registers[i].setVi(i);
		}
	}
	
	public int getData(int index) {
		return registers[index].getVi();
	}

	public void setData(int index, int value) {
		registers[index].setVi(value);
	}
	
	public void print() {
		String table = "\nREGISTERS\n";
		table += "N\t|Vi";
		for(int i=0; i<registers.length; i++)
			table += ("\n" + i + "\t|" + registers[i].getVi());
		System.out.println(table);
	}
	
	
}
