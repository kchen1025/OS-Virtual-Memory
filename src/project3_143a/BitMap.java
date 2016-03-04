package project3_143a;

public class BitMap {
	//BM will be represented as an array of int 32bits each
		private int[] BM;
		private int[] MASK;
		
		//initialize a bitmap with size being the size of number of blocks in disk(64 bits)
		public BitMap(){
			//initialize BM to 0
			this.BM = new int[32];
			this.MASK = new int[32];
			for(int i = 0; i < 32 ; i++){
				this.BM[i] = 0;
			}

			MASK[31] = 1;
			for(int i = 30; i>=0; i--)
			{
				MASK[i] = MASK[i+1] << 1;
			}	
		}
		
		public void resetBitmap(){
			for(int i = 0; i < 32 ; i++){
				this.BM[i] = 0;
			}
		}
	
		
		
		public int[] getBitMap(){
			return BM;
		}
		
		
		
		//set the bit in the bitmap to 1 according to the index 
		//if index is greater than 32, will be set to BM[1] int
		public void setBit1(int index){	
			this.BM[index/32] = this.BM[index/32] | this.MASK[index%32];
			
		}
		
		//switching bitmap back to 0 by inverting the original MASK and using bitwise &
		public void setBit0(int index){

			int[] MASK2 = new int[32];
		
			for(int i = 0; i < this.MASK.length; i++){
				MASK2[i] = ~MASK[i];
			}

			this.BM[index/32] = this.BM[index/32] & MASK2[index%32];
			
		}
		
		
		public int getOpenBit(){
			int test;
			for(int i = 0; i<this.BM.length; i++)
			{
				for(int j=0; j<this.MASK.length; j++)
				{
					test = BM[i] & MASK[j];
					
					if(test ==0)
					{
						return i*32 + j;
					}
				}
			}
			
			return 1;
		}
		
		public int getIndex(int index){
			int test = index;
			for(int i = 0; i<this.BM.length; i++)
			{
				for(int j=0; j<this.MASK.length; j++)
				{
					test = BM[i] & MASK[j];
					if(index == 0){
						return test;
					}
					else{
						index--;
					}
				}
			}
			return -1;
		}
		
		
		public static void main(String[] args){
			
			BitMap b = new BitMap();
			b.setBit1(0);
			b.setBit1(2);
			b.setBit1(1023);
			b.setBit0(1023);
			b.setBit1(665);
			
			for(int i = 0; i<b.BM.length; i++){
				System.out.println(b.BM[i]);
			}
			
			System.out.println("lol");
			
			System.out.println(b.getIndex(665));
		}
}
