import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Runner {
	
	private static String[][] map;
	private static int rows;
	private static int cols;
	private static int numMazes;
	
	public static void main(String[] args) {
		readMapBasedFile("hardMap1");
		readCoorBasedFile("easyMap1c");

	}
	
	public static void readMapBasedFile(String fileName) {
		//create a File object referencing the map-based file
		File mapBased = new File(fileName);
		
		//create a scanner from the File object
		try {
			Scanner myScanner = new Scanner(mapBased);
			
			rows = Integer.parseInt(myScanner.next());
			cols = Integer.parseInt(myScanner.next());
			numMazes = Integer.parseInt(myScanner.next());
			//create 2D array to put elements of the map
			map = new String[rows*numMazes][cols];
			
			//add elements of the file to the 2D array
			for (int i = 0; i < map.length; i++) {
				String oneRow = myScanner.next();
				for (int j = 0; j < map[0].length; j++) {
					map[i][j] = oneRow.substring(j, j+1);	
				}
			}
			System.out.println(Arrays.deepToString(map));
			myScanner.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void readCoorBasedFile(String fileName) {
		//create a File object referencing the map-based file
		File coorBased = new File(fileName);
		
		//create a scanner from the File object
		try {
			Scanner myScanner = new Scanner(coorBased);
			
			rows = Integer.parseInt(myScanner.next());
			cols = Integer.parseInt(myScanner.next());
			numMazes = Integer.parseInt(myScanner.next());
			//create 2D array to put elements of the map
			map = new String[rows*numMazes][cols];
			
			//add periods for the first digits
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; i++) {
					map[i][j] = ".";
				}
			}
			
			while (myScanner.hasNext()) {
				String element = myScanner.next();
				int row = Integer.parseInt(myScanner.next());
				int col = Integer.parseInt(myScanner.next());
				int mazeNum = Integer.parseInt(myScanner.next());
				
				
			}
			
			System.out.println(Arrays.deepToString(map));
			myScanner.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

}
