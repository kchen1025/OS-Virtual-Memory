package project3_143a;

public class TLB {
	private TLB_Cell[] table;
	
	public TLB(){
		table = new TLB_Cell[4];
		table[0] = new TLB_Cell();
		table[1] = new TLB_Cell();
		table[2] = new TLB_Cell();
		table[3] = new TLB_Cell();
		
		for(int i = 0; i < table.length; i++){
			table[i].setLRU(i);
		}
	}
	
	public int getFramefromIndex(int index){
		return this.table[index].getFrame();
	}
	
	
	public boolean hitOrMiss(VirtualMemory va){
		//returns hit or miss (true or false)
		
		//get the s and p values and add them for the tlb (bootleg af )
		int sp = va.getS()+va.getP();
		
		for(int i = 0; i < this.table.length; i++){
			if(this.table[i].getSP() == sp){
				return true; //hit
			}
		}
		
		//else will miss
		return false; //miss
	}
	
	public int searchTLB(VirtualMemory va){
		//return the index of a hit
		
		int sp = va.getS()+va.getP();
		
		for(int i = 0; i < this.table.length; i++){
			if(this.table[i].getSP() == sp){
				return i;
			}
		}
		
		return -1;
	}
	
	public int getLowestLRU(){
		//returns the index of the cell with lowest lru
		for(int i = 0 ; i< this.table.length; i++){
			if(this.table[i].getLRU() == 0){
				return i;
			}
		}
		//shouldnt happen
		return -1;
	}
	
	public void setTLB_cell(int index, VirtualMemory va, int[] pm){
		updateLRU_miss();
		this.table[index].setSP(va.getS()+va.getP());
		this.table[index].setFrame(pm[pm[va.getS()]+va.getP()]);
	}
	
	public void updateLRU_hit(int hitIndex){
		int matchingLRU = this.table[hitIndex].getLRU();
		
		for(int i = 0; i < this.table.length; i++){
			if(this.table[i].getLRU() > matchingLRU){
				int value = this.table[i].getLRU();
				this.table[i].setLRU(value-1);
			}
		}
		
		this.table[hitIndex].setLRU(3);
	}
	
	
	public void updateLRU_miss(){
		int lowestLRU = getLowestLRU();
		
		for(int i = 0 ; i< this.table.length; i++){
			int lru = this.table[i].getLRU();
			this.table[i].setLRU(lru-1);
		}
		
		this.table[lowestLRU].setLRU(3);
	}
	
	public void printTLB(){
		for(int i = 0; i < this.table.length; i++){
			System.out.print(this.table[i].getLRU()+" ");
			System.out.print(this.table[i].getSP()+" ");
			System.out.print(this.table[i].getFrame()+" ");
			System.out.println();
		}
	}
	
	public static void main(String[] args){
		TLB t = new TLB();
		t.updateLRU_hit(2);
		//t.updateLRU_miss();
		t.printTLB();
	}
	
}
