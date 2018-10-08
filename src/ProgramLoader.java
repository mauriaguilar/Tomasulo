import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class ProgramLoader {
		
		private String[][] instructions_list;
		
		public ProgramLoader() {
			//String[][] instructions_list = getInstrucions(1);
		}

		public String[][] getInstrucions(int i) throws FileNotFoundException {
			
			String fichero = "program"+i+".txt";
			String[] words;
			String line;
			
			// Get number of instruction
			File input = new File(fichero); Scanner iterate = new Scanner(input);
			int numLines=0;
			while(iterate.hasNextLine()) {
				String currLine=iterate.nextLine(); numLines++; 
			}
			
			instructions_list = new String[numLines][4];
			int j = 0, k = 0;
			try {
			      FileReader fr = new FileReader(fichero);
			      BufferedReader br = new BufferedReader(fr);
			 
			      System.out.println("PROGRAM "+i+": ");
			      while((line = br.readLine()) != null) {
			    	  //words = null;
			    	  System.out.println(line);
			    	  words = line.split(" ");
			    	  
			    	  //System.out.println("Words ");
			    	  /*
			    	  for(j=0; j<words.length; j++) {
			    	    System.out.println("-"+words[j]+"-");
			    	  }
			    	  */
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
