package project3_143a;

public class TLB_Cell {
	//LRU variable is for the prioity of the tlb frame
	private int LRU;
	
	//S,P is the segment and page table addresses of the virtual address
	private int SP;
	private int frame; 
	
	private static final int emptyCell = -1;
	
	public TLB_Cell(){
		//first initialize all cells to -1 to indicate nothing has been initialized 
		this.LRU = emptyCell;
		this.SP = emptyCell;
		this.frame = emptyCell;
	}
	
	public void setLRU(int value){
		this.LRU = value;
	}
	
	public void setSP(int sp){
		this.SP = sp;
	}
	
	public void setFrame(int frame){
		this.frame = frame; 
	}
	
	public int getLRU(){
		return this.LRU;
	}
	
	public int getSP(){
		return this.SP;
	}

	public int getFrame(){
		return this.frame;
	}
}
