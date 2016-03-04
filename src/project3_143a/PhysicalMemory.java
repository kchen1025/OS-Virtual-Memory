package project3_143a;

import java.util.Arrays;

public class PhysicalMemory {
	private static final int frameSize = 512;
	private static final int memorySize = 1024;
	private static final int emptyAddress = -1;
	private static final int initializedAddress = 0;
	
	private int[] PM;
	private BitMap BM;
	
	public PhysicalMemory(){
		PM = new int[memorySize*frameSize];
		BM = new BitMap();		
	}
	
	
	//prints all frames that are occupied (debugging purposes)
	public void printMem(){
		for(int i = 0; i < frameSize*memorySize; i++){
			if(BM.getIndex(i/frameSize) != 0){
				
				if(i%frameSize == 0){
					System.out.println();
					System.out.print(i/frameSize+" ");
				}
				else{
					
					System.out.print(this.PM[i]+" ");
				}
			}
		}
	}
	
	public void init(){
		//set all values to -1 to indicate nothing is there 
		Arrays.fill(this.PM, emptyAddress);
		
		//set bitmap to indicate first frame is for segment table
		this.BM.setBit1(0);
		
	}
	
	
	public void setST(int st_index, int address){
		this.PM[st_index] = address;
		
		//init the page table for 1024 cells after the address
		for(int i = address; i < address+(2 * frameSize); i++){
			this.PM[i] = initializedAddress;
		}
		this.BM.setBit1(address/frameSize);
		this.BM.setBit1((address/frameSize)+1);
	}
	
	public static void main(String[] args){
		PhysicalMemory p = new PhysicalMemory();
		p.init();
		p.setST(15, 512);
		p.setST(2, 2048);
		p.printMem();
	}
	
}
