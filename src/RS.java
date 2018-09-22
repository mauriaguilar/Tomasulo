//Estacion de Reserva

public class RS {
	
	private boolean busy;
	private String op;
	private int vj,vk;
	private int qj,qk;
	
	public RS() {
		busy = false;
		op = null;
		vj = 0; //ver
		vk = 0; //ver
		qj = 0; //ver
		qk = 0; //ver
	}
	
	public boolean getBusy() {
		return busy;
	}	
	
	public String getOp() {
		return op;
	}
	
	public int getVj() {
		return vj;
	}
	
	public int getVk() {
		return vk;
	}
}
