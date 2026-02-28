import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.HashMap;

public class Runner {
	
	private static String[][] map;
	private static int rows;
	private static int cols;
	private static int numMazes;
	
	public static void main(String[] args) {
		try {
			readMapBasedFile("mediumMap1");
			queueBased();
		} catch(IncompleteMapException e) {
			System.out.println(e.getMessage());
		} catch(IllegalMapCharacterException e) {
			System.out.println(e.getMessage());
		} catch(IncorrectMapFormatException e) {
			System.out.println(e.getMessage());
		}
//		try {
//			readCoorBasedFile("easyMap1c");
//		} catch(IllegalMapCharacterException e) {
//			System.out.println(e.getMessage());
//		} catch(IncorrectMapFormatException e) {
//			System.out.println(e.getMessage());
//		}

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
				if (oneRow.length() < cols) {
					throw new IncompleteMapException("IncompleteMapException - missing characters/rows in map" + oneRow.length());
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
			
			System.out.println("Map: " + Arrays.deepToString(map));
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
			System.out.println("Map: " + Arrays.deepToString(map));
			myScanner.close();
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void queueBased() {
		Queue<ArrayList<Integer>> queue = new LinkedList<>();
		
		//create ArrayList for starting coordinates
		ArrayList<ArrayList<Integer>> startCoor = new ArrayList<ArrayList<Integer>>();
		
		//create HashMap to keep track of child-parent coordinates for the purpose of tracing back
		HashMap<ArrayList<Integer>, ArrayList<Integer>> visited = new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
		
		//create ArrayList for coin location coordinates
		ArrayList<Integer> coinCoor = new ArrayList<Integer>();
		
		//create ArrayList of offsets for North, South, East, West
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		//(0, 1)
		offsets.add(-1);
		offsets.add(0);
		//(0, -1)
		offsets.add(1);
		offsets.add(0);
		//(1, 0)
		offsets.add(0);
		offsets.add(1);
		//(-1, 0)
		offsets.add(0);
		offsets.add(-1);
		
		//create an ArrayList to save coordinates of the open walkways
		ArrayList<ArrayList<Integer>> openWalkway = new ArrayList<ArrayList<Integer>>();
		
		//QUEUEING
		
		//loop through each maze
		for (int num = 0; num < numMazes; num++) {
			System.out.println("Maze Number: " + num);
			//find the start location and add coordinates to ArrayList
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (map[i+num*rows][j].equals("W")) {
						ArrayList<Integer> oneStartCoor = new ArrayList<Integer>();
						oneStartCoor.add(i); //x coor
						oneStartCoor.add(j); //y coor
						oneStartCoor.add(num); //maze coor
						startCoor.add(oneStartCoor);
						break;
					}
				}
				//once the starting pos is found break
				if (startCoor.size() > num) {
					break;
				}
			}
			//add the starting ArrayList to the queue
			queue.add(startCoor.get(num));
			System.out.println("Start Coor: " + startCoor);
			
			//while the coin coordinates have not been found...
			while(coinCoor.size() == 0 || queue.size() > 0) {
				//1. dequeue
				ArrayList<Integer> dequeued = queue.remove();
				System.out.println("Dequeued: " + dequeued);
				int xCoor = dequeued.get(0);
				int yCoor = dequeued.get(1);
				
				//2. enqueue all walkable tiles "." North, South, East, and West of the location just dequeued
				//ensure that xCoor and yCoor are not outside the map and that the coordinate has not been visited before
				
				//loop through offsets
				for (int i = 0; i < 8; i+=2) {
					int xOffset = xCoor + offsets.get(i);
					int yOffset = yCoor + offsets.get(i+1);
					//check if coordinate is in bounds
					if (xOffset >= 0 && xOffset < rows && yOffset >= 0 && yOffset < cols) {
						//check for coin
						ArrayList<Integer> currCoor = new ArrayList<Integer>();
						currCoor.add(xOffset);
						currCoor.add(yOffset);
						currCoor.add(num);
						if (map[xOffset+num*rows][yOffset].equals("$")) {
							System.out.println("Coin found");
							coinCoor.add(xOffset);
							coinCoor.add(yOffset);
							coinCoor.add(num);
							visited.put(currCoor, dequeued);
							break;
						}
						//check for walkable space and if the space was visited before
						if (!visited.containsKey(currCoor) && (map[xOffset+num*rows][yOffset].equals(".") || map[xOffset][yOffset].equals("|"))) {
							//create ArrayList for coordinates
							queue.add(currCoor);
							//save coor of open walkway
							if (map[xOffset+num*rows][yOffset].equals("|")) {
								ArrayList<Integer> oneOpenWalkway = new ArrayList<Integer>();
								oneOpenWalkway.add(xOffset);
								oneOpenWalkway.add(yOffset);
								oneOpenWalkway.add(num);
								openWalkway.add(oneOpenWalkway);
								System.out.println("Open Walkway: " + openWalkway);
							}
							//add the current coordinates into visited as the child of the coordinates that it branched off from (the parent)--this is for tracing back later
							visited.put(currCoor, dequeued);
							
						}
					}
				}
				if (queue.size() == 0) {
					break;
				}
				if (coinCoor.size() != 0) {
					break;
				}
				System.out.println("Queue: " + queue);
				
			}
			if (coinCoor.size() != 0) {
				break;
			}
		}
		
		//add open walkways as the parent of starting coors (key = starting coor, value = open walkway)
		if (openWalkway.size() > 0) {
			for (int i = 0; i < openWalkway.size(); i++) {
				visited.put(startCoor.get(i+1), openWalkway.get(i));
			}
		}
		
		System.out.println("Visited: " + visited);
		
		//TRACEBACK
		
		//create ArrayList to store the final path--this will be the path with "+" in the output
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		
		//find the coor that is the parent to the coor of the coin 
		ArrayList<Integer> currTraceback = visited.get(coinCoor);
		//while the code hasn't traced back to the starting pos...
		while (currTraceback != startCoor.get(0)) {
			//add the coor to the result
			result.add(currTraceback);
			//update the current coor to be the parent coor
			currTraceback = visited.get(currTraceback);
		}
		System.out.println("Result: " + result);
		
		
	}

}
