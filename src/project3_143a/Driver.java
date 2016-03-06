package project3_143a;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Driver {	
	
	public static void main(String args[]){
		BufferedReader br = null;

		PhysicalMemory pm = new PhysicalMemory();
		pm.init();
		
		String inputFile1 = "file1.txt";
		String inputFile2 = "file2.txt";
		String outputFile = "498592231.txt";
		String outputFile2 = "498592232.txt";
		
		try {
			//creating ouptut file 
			File file2 = new File(outputFile);
	        if (!file2.exists()) {
				file2.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file2.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			br = new BufferedReader(new FileReader(inputFile1));

			String[] firstLine;
			String[] secondLine;

			firstLine = br.readLine().split(" ");
			secondLine = br.readLine().split(" ");
			
			//initiatlize the segment tables for line 1 
			for(int i = 0; i < firstLine.length; i+=2){
				pm.setST(Integer.parseInt(firstLine[i]), Integer.parseInt(firstLine[i+1]));
			}
			
			//initalize the page tables for all of line 2
			for(int i = 0; i < secondLine.length; i+=3){
				pm.setPT(Integer.parseInt(secondLine[i]), Integer.parseInt(secondLine[i+1]), Integer.parseInt(secondLine[i+2]));
			}
			
			
			//read the second input file for the virtual addresses
			br = new BufferedReader(new FileReader(inputFile2));
			firstLine = br.readLine().split(" ");
			
			for(int i = 0; i < firstLine.length; i+=2){
				bw.write(pm.addressTranslation(Integer.parseInt(firstLine[i]),Integer.parseInt(firstLine[i+1]))+" ");
			}
			
			
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

	}
}
