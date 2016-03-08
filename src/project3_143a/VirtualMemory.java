package project3_143a;

public class VirtualMemory {
	private int VA;
	private int[] s;
	private int[] p;
	private int[] w;
	
	//virtual address is an int divided into S (segment table index) P (Page table index) and W(page)
	public VirtualMemory(int virtualAddress){
		VA = virtualAddress;
		s = new int[9];
		p = new int[10];
		w = new int[9];
		splitVirtualAddress();
		
	}
	
	public void splitVirtualAddress(){
		int test = this.VA; 
		String blah = Integer.toBinaryString(test);
		char[] arr = blah.toCharArray();
		int[] lols = new int[32];
			
		for(int i = 0 ; i < 32; i++){
			lols[i] = 0;
		}
		
		for(int i = (32-arr.length); i < 32; i++){
			lols[i] = (int)arr[i-(32-arr.length)] - 48;
		}
		
		
		for(int i = 4; i < 13; i++){
			this.s[i-4] = lols[i];			
		}
		
		for(int i = 13; i< 23; i++){
			this.p[i-13] = lols[i];			
		}
		
		for(int i = 23; i < 32; i++){
			this.w[i-23] = lols[i];
		}
	}
	
	public int getS(){
		String s= "";
		for(int i = 0; i < this.s.length; i++){
			s += String.valueOf(this.s[i]);
		}
		
		return Integer.parseInt(s,2);
	}
	
	public int getP(){
		String s= "";
		for(int i = 0; i < this.p.length; i++){
			s += String.valueOf(this.p[i]);
		}
		return Integer.parseInt(s,2);
	}
	
	public int getW(){
		String s= "";
		for(int i = 0; i < this.w.length; i++){
			s += String.valueOf(this.w[i]);
		}
		return Integer.parseInt(s,2);
	}
	
	
	public static void main(String[] args){
		VirtualMemory vm = new VirtualMemory(1048575);
		System.out.println(vm.getS());
		System.out.println(vm.getP());
		System.out.println(vm.getW());
	}
}
