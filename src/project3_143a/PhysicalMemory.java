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
	
	public String addressTranslation(int rwBit, int virtualAddress){
		if(rwBit == 0){
			//read
			return readAccess(virtualAddress);
		}
		if(rwBit == 1){
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
		int pageAddress = getEntryOfPT(pageTableAddress, va.getP());
		
		if(pageTableAddress == -1 || pageAddress == -1){
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
		int pageAddress = getEntryOfPT(pageTableAddress, va.getP());
		
		if(pageTableAddress == -1 || pageAddress == -1){
			return "pf";
		}
		if(pageTableAddress == 0 || pageAddress == 0){
			return "error";
		}
		else{
			return Integer.toString(pageAddress+va.getW());
		}			
	}
	
	public static void main(String[] args){
		PhysicalMemory p = new PhysicalMemory();
		p.init();
		p.setST(2,2048);
		
		p.setPT(0, 2, 512);
		p.setPT(1, 2, -1);
		System.out.println(p.addressTranslation(0,1048576));
		System.out.println(p.addressTranslation(1,1048586));
		System.out.println(p.addressTranslation(1, 1049088));
		System.out.println(p.addressTranslation(1, 2098698));
		
		p.printMem();
	}
	
}
