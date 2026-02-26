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
		try {
			readMapBasedFile("easyMap2");
		} catch(IncompleteMapException e) {
			System.out.println(e.getMessage());
		} catch(IllegalMapCharacterException e) {
			System.out.println(e.getMessage());
		} catch(IncorrectMapFormatException e) {
			System.out.println(e.getMessage());
		}
		try {
			readCoorBasedFile("easyMap1c");
		} catch(IllegalMapCharacterException e) {
			System.out.println(e.getMessage());
		} catch(IncorrectMapFormatException e) {
			System.out.println(e.getMessage());
		}

	}
	
	public static void readMapBasedFile(String fileName) throws IncompleteMapException, IllegalMapCharacterException, IncorrectMapFormatException {
		//create a File object referencing the map-based file
		File mapBased = new File(fileName);
		
		//create a scanner from the File object
		try {
			Scanner myScanner = new Scanner(mapBased);
			
			rows = Integer.parseInt(myScanner.next()); //assign # of rows to first number in file
			cols = Integer.parseInt(myScanner.next()); //assign # of cols to second number in file
			numMazes = Integer.parseInt(myScanner.next()); //assign # of mazes to third number in file
			//check if the first 3 digits negative or 0--if they are, throw exception
			if (rows <= 0 || cols <= 0 || numMazes <= 0) {
				throw new IncorrectMapFormatException("IncorrectMapFormatException - first 3 digits not positive/nonzero");
			}
			//create 2D array to put elements of the map
			map = new String[rows*numMazes][cols];
			
			//add elements of the file to the 2D array
			for (int i = 0; i < map.length; i++) {
				String oneRow = myScanner.next();
				//check for incomplete map (not enough characters)
				if (oneRow.length() != cols) {
					throw new IncompleteMapException("IncompleteMapException - missing characters/rows in map");
				}
				
				for (int j = 0; j < map[0].length; j++) {
					//check for illegal characters
					String element = oneRow.substring(j, j+1);
					if (!element.equals(".") && !element.equals("W") && !element.equals("$") && !element.equals("|") && !element.equals("@")) {
						throw new IllegalMapCharacterException("IllegalMapCharacterException - illegal character in map");
					}					
					else {
						map[i][j] = element;
					}
				}
			}
			
			System.out.println(Arrays.deepToString(map));
			myScanner.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void readCoorBasedFile(String fileName) throws IncorrectMapFormatException, IllegalMapCharacterException {
		//create a File object referencing the map-based file
		File coorBased = new File(fileName);
		
		//create a scanner from the File object
		try {
			Scanner myScanner = new Scanner(coorBased);
			
			rows = Integer.parseInt(myScanner.next()); //assign # of rows to first number in file
			cols = Integer.parseInt(myScanner.next()); //assign # of cols to second number in file
			numMazes = Integer.parseInt(myScanner.next()); //assign # of mazes to third number in file
			//check if the first 3 digits negative or 0--if they are, throw exception
			if (rows <= 0 || cols <= 0 || numMazes <= 0) {
				throw new IncorrectMapFormatException("IncorrectMapFormatException - first 3 digits not positive/nonzero");
			}
			//create 2D array to put elements of the map
			map = new String[rows*numMazes][cols];
			
			//loop through the file to add elements that are not periods
			while (myScanner.hasNext()) {
				//assign element to first element of the row in the file
				String element = myScanner.next();
				//check if element is an illegal character--if it is, throw exception
				if (!element.equals(".") && !element.equals("W") && !element.equals("$") && !element.equals("|") && !element.equals("@")) {
					throw new IllegalMapCharacterException("IllegalMapCharacterException - illegal character in map");
				}
				//get the row, col, and maze # for the element
				int row = Integer.parseInt(myScanner.next()); 
				int col = Integer.parseInt(myScanner.next()); 
				int mazeNum = Integer.parseInt(myScanner.next()); 
				//check if coordinates fit inside maze
				if (row > rows-1 || row < 0 || col > cols-1 || col < 0 || mazeNum > numMazes-1 || mazeNum < 0) {
					throw new IncorrectMapFormatException("IncorrectMapFormatException - coordinates do not fit inside map");
				}
				map[row+mazeNum*rows][col] = element; 
			}
			
			//set periods for the rest of the digits
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					if (map[i][j] == null) { //if there is not already something there
						map[i][j] = ".";
					}
				}
			} 
			System.out.println(Arrays.deepToString(map));
			myScanner.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
