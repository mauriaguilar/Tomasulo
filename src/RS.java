//Estacion de Reserva

public class RS {
	
	private int dest;
	private boolean busy;
	private String op;
	private int vj,vk;
	private String qj,qk;
	
	public RS() {
		dest = -1;
		busy = false;
		op = null;
		vj = 0; //ver
		vk = 0; //ver
		qj = null; //ver
		qk = null; //ver
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
	
	public void setDest(int dest) {
		this.dest = dest;
	}
	
	public void setBusy(boolean busy) {
		this.busy = busy;
	}
	
	public void setOp(String op) {
		this.op = op;
	}
	
	public void setVj(int vj) {
		this.vj = vj;
	}
	
	public void setVk(int vk) {
		this.vk = vk;
	}
	
	public void setQj(String qj) {
		this.qj = qj;
	}
	
	public void setQk(String qk) {
		this.qk = qk;
	}
}
