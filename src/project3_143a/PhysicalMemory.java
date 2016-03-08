package project3_143a;

import java.util.Arrays;

public class PhysicalMemory {
	//constants kept for code readability
	private static final int frameSize = 512;
	private static final int memorySize = 1024;
	private static final int emptyAddress = -1;
	private static final int initializedAddress = 0;
	
	private int[] PM;
	private BitMap BM;
	private TLB tlb; 
	
	public PhysicalMemory(){
		PM = new int[memorySize*frameSize];
		BM = new BitMap();
		tlb = new TLB();
	}
	
	
	//prints all frames that are occupied (debugging purposes)
	public void printMem(){
		for(int i = 0; i < frameSize*memorySize; i++){
			if(BM.getIndex(i/frameSize) != 0){
				
				if(i%frameSize == 0){
					System.out.println();
					System.out.print(i/frameSize+" |");
					System.out.print(this.PM[i]+ " ");
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
		
		//initialize all values to 0 in segment table
		for(int i = 0 ; i < frameSize; i++){
			this.PM[i] = initializedAddress;
		}
		
		//set bitmap to indicate first frame is for segment table
		this.BM.setBit1(0);
		
	}	
	
	public void setST(int st_index, int address){
		
		this.PM[st_index] = address;
		
		if(address != -1){
			//init the page table for 1024 cells after the address
			for(int i = address; i < address+(2 * frameSize); i++){
				this.PM[i] = initializedAddress;
			}
			this.BM.setBit1(address/frameSize);
			this.BM.setBit1((address/frameSize)+1);
		}
		
	}
	
	private int getPTAddress(int segmentIndex){
		return this.PM[segmentIndex];
	}
	
	
	public void setPT(int pageIndex, int segmentIndex, int pageAddress){
	
		int pageTableStart = getPTAddress(segmentIndex);

		if(pageTableStart != -1 && pageTableStart != 0){
			
			//set index in page table to page address
			this.PM[pageTableStart + pageIndex] = pageAddress;
			
			if(pageAddress != -1){
				//initialize the page
				for(int i = pageAddress; i < pageAddress + frameSize; i++){
					this.PM[i] = initializedAddress;
				}
				
				//set bit map to reflect this
				this.BM.setBit1(pageAddress/frameSize);
			}	
		}
		else{
			System.out.println("Page table does not exist");
		}	
	}
	
	public String addressTranslation(int rwBit, int virtualAddress, boolean tlb_flag){
		if(rwBit == 0 && tlb_flag == true){
			//read
			return readAccess_TLB(virtualAddress);
		}
		if(rwBit == 1 && tlb_flag == true){
			//write
			return writeAccess_TLB(virtualAddress);
		}
		
		if(rwBit == 0 && tlb_flag == false){
			//read
			return readAccess(virtualAddress);
		}
		if(rwBit == 1 && tlb_flag == false){
			//write
			return writeAccess(virtualAddress);
		}
		return "error in rwBit";
	}
	
	
	private int getEntryOfPT(int pageTableAddress, int pageIndex){
		return this.PM[pageTableAddress+pageIndex];
	}
	
	private int getEntryOfST(int segmentIndex){
		return this.PM[segmentIndex];
	}
	
	private void setEntryOfST(int segmentIndex, int pageTableIndex){
		this.PM[segmentIndex] = pageTableIndex;
	}
	
	private void setEntryOfPT(int pageTableAddress, int pageTableIndex, int newPageAddress){
		this.PM[pageTableAddress+pageTableIndex] = newPageAddress;
	}
	
	private void createNewPage(VirtualMemory va){
		int pageIndex = BM.getOpenBit();
		BM.setBit1(pageIndex);
		setEntryOfPT(PM[va.getS()],va.getP(),pageIndex*frameSize);
		
		for(int i = pageIndex*frameSize; i < pageIndex*frameSize + frameSize; i++){
			this.PM[i] = initializedAddress;
		}
	}
	
	private String writeAccess(int virtualAddress){
		VirtualMemory va = new VirtualMemory(virtualAddress);
		
		int pageTableAddress = getEntryOfST(va.getS());
		
		
		if(pageTableAddress == -1){
			return "pf";
		}
		
		int pageAddress = getEntryOfPT(pageTableAddress, va.getP());
		
		if(pageAddress == -1){
			return "pf";
		}
		
		
		
		if(pageTableAddress == 0){
			//allocate new PT
			int openPair = BM.getOpenPair();
			BM.setBit1(openPair);
			BM.setBit1(openPair+1);
			setEntryOfST(va.getS(),openPair*frameSize);
			
			for(int j = openPair*frameSize; j < openPair*frameSize + 2 * frameSize; j++){
				this.PM[j] = initializedAddress;
			}
			
			//allocate new page
			createNewPage(va);
			int ret = getEntryOfPT(PM[va.getS()],va.getP()) + va.getW();
			return Integer.toString(ret);
		}
		
		else if(pageAddress == 0){
			createNewPage(va);
			int ret = getEntryOfPT(PM[va.getS()],va.getP()) + va.getW();
			return Integer.toString(ret);
		}
		
		else{
			int ret = getEntryOfPT(PM[va.getS()],va.getP()) + va.getW();
			return Integer.toString(ret);
		}
		
		
	}
	
	private String readAccess(int virtualAddress){
		VirtualMemory va = new VirtualMemory(virtualAddress);
	
		int pageTableAddress = getEntryOfST(va.getS());

		
		
		if(pageTableAddress == -1){
			return "pf";
		}
		if(pageTableAddress == 0){
			return "err";
		}
		
		int pageAddress = getEntryOfPT(pageTableAddress, va.getP());
		
		if(pageAddress == -1){
			return "pf";
		}
		if(pageAddress == 0){
			return "err";
		}
		
		else{
			return Integer.toString(pageAddress+va.getW());
		}			
	}
	
	
	//returns the PA of the page
	private String readAccess_TLB(int virtualAddress){
		//check tlb for f hit or miss
		VirtualMemory va = new VirtualMemory(virtualAddress);
		
		//tlb hit
		if(tlb.hitOrMiss(va)){
			int hitIndex = tlb.searchTLB(va);
			tlb.updateLRU_hit(hitIndex);
			return "h "+Integer.toString(va.getW()+tlb.getFramefromIndex(hitIndex));
		}
		//tlb miss
		else{
			String s = readAccess(virtualAddress);
			if(s == "pf" || s == "err"){
				return "m "+s;
			}
			//need to update the tlb with the new values found
			//from read access
			int missIndex = tlb.getLowestLRU();
			tlb.setTLB_cell(missIndex, va, this.PM);
			
			return "m "+s;
		}
	}
	
	private String writeAccess_TLB(int virtualAddress){
		//check tlb for f hit or miss
		VirtualMemory va = new VirtualMemory(virtualAddress);
		
		//tlb hit
		if(tlb.hitOrMiss(va)){
			int hitIndex = tlb.searchTLB(va);
			tlb.updateLRU_hit(hitIndex);
			return "h "+Integer.toString(va.getW()+tlb.getFramefromIndex(hitIndex));
		}
		//tlb miss
		else{
			String s = writeAccess(virtualAddress);
			if(s == "pf"){
				return "m "+s;
			}
			//need to update the tlb with the new values found
			//from read access
			int missIndex = tlb.getLowestLRU();
			tlb.setTLB_cell(missIndex, va, this.PM);
			
			return "m "+s;
		}
	}
	

	
	
	public static void main(String[] args){
		boolean TLB_FLAG = false;
		
		PhysicalMemory p = new PhysicalMemory();
		p.init();
		p.setST(0,4608);
		p.setST(1, -1);
		p.setST(2,9216);
		p.setST(5, 1536);
		
		p.setPT(0, 0, 4096);
		p.setPT(0,5,7168);
		p.setPT(1,5,8704);
		p.setPT(2,5,-1);
		p.setPT(4,5,32768);
		p.setPT(5,5,65536);
		System.out.println(p.addressTranslation(0, 524288,TLB_FLAG));
//		System.out.println(p.addressTranslation(1,1048586,TLB_FLAG));
//		System.out.println(p.addressTranslation(1, 1049088,TLB_FLAG));
//		System.out.println(p.addressTranslation(1, 2098698,TLB_FLAG));
//		
		p.printMem();
	}
	
}
