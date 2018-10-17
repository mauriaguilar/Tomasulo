import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class ProgramLoader {

		private int programNumber = 1;
		private String[][] instructions_list;
		
		public ProgramLoader(int i) {
			//String[][] instructions_list = getInstrucions(1);
			programNumber = i;
		}

		public String[][] getInstrucions() {
			
			String fichero = "program"+programNumber+".txt";
			String[] words;
			String line;
			int numLines=0;
			
			// Get number of instruction
			File input = new File(fichero);
			Scanner iterate;
			try {
				iterate = new Scanner(input);
				while(iterate.hasNextLine()) {
					//String currLine=iterate.nextLine();
					numLines++; 
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
			
			instructions_list = new String[numLines][4];
			int j = 0, k = 0;
			try {
				      FileReader fr = new FileReader(fichero);
				      BufferedReader br = new BufferedReader(fr);
				 
				      System.out.println("PROGRAM "+programNumber+": ");
				      while((line = br.readLine()) != null) {
				    	  System.out.println(line);
				    	  words = line.split(" ");
				    	  instructions_list[k++] = words;
				      }

				      fr.close();
				}
			    catch(Exception e) {
			      System.out.println("Excepcion reading file "+ fichero + ": " + e);
			    }
			
			return instructions_list;
		}
		
		
}
