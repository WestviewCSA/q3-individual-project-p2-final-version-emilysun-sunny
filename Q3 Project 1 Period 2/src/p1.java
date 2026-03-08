import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.HashMap;

public class p1 {
	
	//info about the map
	private static String[][] map;
	private static int rows;
	private static int cols;
	private static int numMazes;
	
	//ArrayList with the coordinates of the final path
	private static ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
	
	//booleans for each switch
	private static boolean stack = false;
	private static boolean queue = false;
	private static boolean opt = false;
	private static boolean time = false;
	private static boolean inCoordinate = false;
	private static boolean outCoordinate = false;
	private static boolean help = false;
	
	//check if map is solvable
	private static boolean storeClosed = false;
	
	//time
	private static long startTime;
	private static long endTime;
	
	public static void main(String[] args) throws IllegalCommandLineInputsException {
		int methodCount = 0;
		//check for switches in command line
		for (String arg : args) {
			switch (arg) {
				case "--Stack":
					stack = true;
					methodCount += 1;
					break;
				case "--Queue":
					queue = true;
					methodCount += 1;
					break;
				case "--Opt":
					opt = true;
					methodCount += 1;
					break;
				case "--Time":
					time = true;
					break;
				case "--Incoordinate":
					inCoordinate = true;
					break;
				case "--Outcoordinate":
					outCoordinate = true;
					break;
				case "--Help":
					help = true;
					break;
			}
		}
		//if help switch is one...
		if (help) {
			System.out.println("This program is intended to find a path from Wolverine's starting position W to the legendary Diamond Wolverine Coin $ using a specified approach. ");
			System.out.println("Command Line Switches:");
			System.out.println("--Stack: use stack-based approach");
			System.out.println("--Queue: use queue-based approach");
			System.out.println("--Opt: find shortest path");
			System.out.println("--Time: print runtime");
			System.out.println("--Incoordinate: coordinate input format (if this switch is not set, text-map based input format)");
			System.out.println("--Outcoordinate: coordinate output format (if this switch is not set, text-map based output format)");
			System.exit(0);
		}
		//ensure only one stack, queue, or opt is switched on
		if (methodCount != 1) {
			throw new IllegalCommandLineInputsException("Command line does not input exactly one --Stack, --Queue, or --Opt");
		}
		//if the input is in coordinate form...
		if (inCoordinate) {
			try {
				readCoorBasedFile("mediumMap1c");
			} catch(IllegalMapCharacterException e) {
				e.printStackTrace();
			} catch(IncorrectMapFormatException e) {
				e.printStackTrace();
			}
		}
		//if the input is in text-map based form...
		else {
			try {
				readMapBasedFile("hardMap2");
			} catch(IncompleteMapException e) {
				e.printStackTrace();
			} catch(IllegalMapCharacterException e) {
				e.printStackTrace();
			} catch(IncorrectMapFormatException e) {
				e.printStackTrace();
			}
		}
		//execute approaches
		if (stack) {
			startTime = System.nanoTime();
			stackBased();
			endTime = System.nanoTime();
		}
		else if (queue) {
			startTime = System.nanoTime();
			queueBased();
			endTime = System.nanoTime();
		}
		else if (opt) {
			startTime = System.nanoTime();
			optimal();
			endTime = System.nanoTime();
		}
		//if the output is in coordinate form...
		if (outCoordinate) {
			coorOutput();
		}
		//if the output is in text-map based form...
		else {
			mapOutput();
		}
		if (time) {
			double runTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
			System.out.println("Total Runtime: " + runTimeSeconds + " seconds");
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
				throw new IncorrectMapFormatException("First 3 digits are not positive/nonzero");
			}
			//create 2D array to put elements of the map
			map = new String[rows*numMazes][cols];
			
			//add elements of the file to the 2D array
			for (int i = 0; i < map.length; i++) {
				String oneRow = myScanner.next();
				//check for incomplete map (not enough characters)
				if (oneRow.length() < cols) {
					throw new IncompleteMapException("Missing characters/rows exist in map");
				}
				for (int j = 0; j < map[0].length; j++) {
					//check for illegal characters
					String element = oneRow.substring(j, j+1);
					if (!element.equals(".") && !element.equals("W") && !element.equals("$") && !element.equals("|") && !element.equals("@")) {
						throw new IllegalMapCharacterException("Illegal character found in map");
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
				throw new IncorrectMapFormatException("First 3 digits are not positive/nonzero");
			}
			//create 2D array to put elements of the map
			map = new String[rows*numMazes][cols];
			
			//loop through the file to add elements that are not periods
			while (myScanner.hasNext()) {
				//assign element to first element of the row in the file
				String element = myScanner.next();
				//check if element is an illegal character--if it is, throw exception
				if (!element.equals(".") && !element.equals("W") && !element.equals("$") && !element.equals("|") && !element.equals("@")) {
					throw new IllegalMapCharacterException("Illegal character found in map");
				}
				//get the row, col, and maze # for the element
				int row = Integer.parseInt(myScanner.next()); 
				int col = Integer.parseInt(myScanner.next()); 
				int mazeNum = Integer.parseInt(myScanner.next()); 
				//check if coordinates fit inside maze
				if (row > rows-1 || row < 0 || col > cols-1 || col < 0 || mazeNum > numMazes-1 || mazeNum < 0) {
					throw new IncorrectMapFormatException("Coordinates do not fit inside map");
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
		
		//find all start locations and add coordinates to ArrayList
		int k = 0; //maze number
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].equals("W")) {
					ArrayList<Integer> oneStartCoor = new ArrayList<Integer>();
					oneStartCoor.add(i-k*rows); //x coor
					oneStartCoor.add(j); //y coor
					oneStartCoor.add(k); //maze coor
					k++; //update maze number to the next maze
					startCoor.add(oneStartCoor);
				}
			}
		}
		System.out.println(startCoor);
		
		//QUEUEING
		
		//add the starting ArrayList to the queue
		queue.add(startCoor.get(0));
			
		//while the coin coordinates have not been found...
		while(coinCoor.size() == 0 || queue.size() > 0) {
			//1. dequeue
			ArrayList<Integer> dequeued = queue.remove();
			System.out.println("Dequeued: " + dequeued);
			int xCoor = dequeued.get(0);
			int yCoor = dequeued.get(1);
			int zCoor = dequeued.get(2); //maze num
				
			//2. enqueue all walkable tiles "." North, South, East, and West of the location just dequeued
			//ensure that xCoor and yCoor are not outside the map and that the coordinate has not been visited before
				
			//loop through offsets
			for (int i = 0; i < 8; i+=2) {
				int xOffset = xCoor + offsets.get(i);
				int yOffset = yCoor + offsets.get(i+1);
				//check if coordinate is in bounds
				if (xOffset >= 0 && xOffset < rows && yOffset >= 0 && yOffset < cols) {
					//create ArrayList for coordinates
					ArrayList<Integer> currCoor = new ArrayList<Integer>();
					currCoor.add(xOffset);
					currCoor.add(yOffset);
					currCoor.add(zCoor);
					//check for coin
					if (map[xOffset+zCoor*rows][yOffset].equals("$")) {
						coinCoor.add(xOffset);
						coinCoor.add(yOffset);
						coinCoor.add(zCoor);
						System.out.println("Coin Coor: " + coinCoor);
						visited.put(currCoor, dequeued);
						break;
					}
					//check for walkable space and if the space was visited before
					if (!visited.containsKey(currCoor) && (map[xOffset+zCoor*rows][yOffset].equals(".") || map[xOffset+zCoor*rows][yOffset].equals("|"))) {
						//current coor is valid--queue it
						queue.add(currCoor);
						//if currCoor is an open walkway, queue the starting coor of the next maze into the stack 
						if (map[xOffset+zCoor*rows][yOffset].equals("|")) {
							System.out.println("Entered" + currCoor);
							queue.add(startCoor.get(zCoor+1));
							visited.put(startCoor.get(zCoor+1), currCoor);
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
		System.out.println("Visited: " + visited);
		
		//TRACEBACK
		traceback(startCoor, coinCoor, visited);
	}
	public static void stackBased() {
		Deque<ArrayList<Integer>> stack = new ArrayDeque<>();
		//create ArrayList for starting coordinates
		ArrayList<ArrayList<Integer>> startCoor = new ArrayList<ArrayList<Integer>>();
				
		//create HashMap to keep track of child-parent coordinates for the purpose of tracing back
		HashMap<ArrayList<Integer>, ArrayList<Integer>> visited = new HashMap<ArrayList<Integer>, ArrayList<Integer>>();
				
		//create ArrayList for coin location coordinates
		ArrayList<Integer> coinCoor = new ArrayList<Integer>();
		
		//find all start location and add coordinates to ArrayList
		int k = 0; //maze number--a third dimension
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].equals("W")) {
					ArrayList<Integer> oneStartCoor = new ArrayList<Integer>();
					oneStartCoor.add(i-k*rows); //x coor
					oneStartCoor.add(j); //y coor
					oneStartCoor.add(k); //maze coor
					k++; //update maze number to the next maze
					startCoor.add(oneStartCoor);
				}
			}
		}
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
		
		//STACKING
		
		//add the starting ArrayList to the queue
		stack.push(startCoor.get(0));
		
		while (coinCoor.size() != 0 || stack.size() > 0) {
			//1. pop
			ArrayList<Integer> popped = stack.pop();
			System.out.println("Popped: " + popped);
			int xCoor = popped.get(0);
			int yCoor = popped.get(1);
			int zCoor = popped.get(2); //maze num
			
			//2. push all walkable tiles "." North, South, East, and West of the location just popped
			//loop through offsets
			for (int i = 0; i < 8; i+= 2) {
				int xOffset = xCoor + offsets.get(i);
				int yOffset = yCoor + offsets.get(i+1);
				//check if coordinate is in bounds
				if (xOffset >= 0 && xOffset < rows && yOffset >= 0 && yOffset < cols) {
					//create ArrayList for coordinates
					ArrayList<Integer> currCoor = new ArrayList<Integer>();
					currCoor.add(xOffset);
					currCoor.add(yOffset);
					currCoor.add(zCoor);
					//check for coin
					if (map[xOffset+zCoor*rows][yOffset].equals("$")) {
						coinCoor.add(xOffset);
						coinCoor.add(yOffset);
						coinCoor.add(zCoor);
						System.out.println("Coin Coor: " + coinCoor);
						visited.put(currCoor, popped);
						break;
					}
					//check for walkable space and if the space was visited before
					if (!visited.containsKey(currCoor) && (map[xOffset+zCoor*rows][yOffset].equals(".") || map[xOffset+zCoor*rows][yOffset].equals("|"))) {
						//current coor is valid--push it
						stack.push(currCoor);
						//if currCoor is an open walkway, push the starting coor of the next maze into the stack 
						if (map[xOffset+zCoor*rows][yOffset].equals("|")) {
							stack.push(startCoor.get(zCoor+1));
							visited.put(startCoor.get(zCoor+1), currCoor);
						}
						//add the current coordinates into visited as the child of the coordinates that it branched off from (the parent)--this is for tracing back later
						visited.put(currCoor, popped);						
					}
				}
				
			}
			if (coinCoor.size() != 0) {
				break;
			}
			if (stack.size() == 0) {
				break;
			}
			System.out.println("Stack: " + stack);
		}
		System.out.println("Visited: " + visited);
		
		//TRACEBACK
		traceback(startCoor, coinCoor, visited);
		
	}
	
	public static void optimal() {
		Queue<ArrayList<Integer>> optimalQueue = new LinkedList<>();
		
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
		
		//find all start locations and add coordinates to ArrayList
		int k = 0; //maze number
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				if (map[i][j].equals("W")) {
					ArrayList<Integer> oneStartCoor = new ArrayList<Integer>();
					oneStartCoor.add(i-k*rows); //x coor
					oneStartCoor.add(j); //y coor
					oneStartCoor.add(k); //maze coor
					k++; //update maze number to the next maze
					startCoor.add(oneStartCoor);
				}
			}
		}
		
		//QUEUEING
		
		//add the starting ArrayList to the queue
		optimalQueue.add(startCoor.get(0));
			
		//while the coin coordinates have not been found...
		while(coinCoor.size() == 0 || optimalQueue.size() > 0) {
			//1. dequeue
			ArrayList<Integer> dequeued = optimalQueue.remove();
			System.out.println("Dequeued: " + dequeued);
			int xCoor = dequeued.get(0);
			int yCoor = dequeued.get(1);
			int zCoor = dequeued.get(2); //maze num
				
			//2. enqueue all walkable tiles "." North, South, East, and West of the location just dequeued
			//ensure that xCoor and yCoor are not outside the map and that the coordinate has not been visited before
				
			//loop through offsets
			for (int i = 0; i < 8; i+=2) {
				int xOffset = xCoor + offsets.get(i);
				int yOffset = yCoor + offsets.get(i+1);
				//check if coordinate is in bounds
				if (xOffset >= 0 && xOffset < rows && yOffset >= 0 && yOffset < cols) {
					//create ArrayList for coordinates
					ArrayList<Integer> currCoor = new ArrayList<Integer>();
					currCoor.add(xOffset);
					currCoor.add(yOffset);
					currCoor.add(zCoor);
					//check for coin
					if (map[xOffset+zCoor*rows][yOffset].equals("$")) {
						coinCoor.add(xOffset);
						coinCoor.add(yOffset);
						coinCoor.add(zCoor);
						System.out.println("Coin Coor: " + coinCoor);
						visited.put(currCoor, dequeued);
						break;
					}
					//check for walkable space ("." or "||")
					if (map[xOffset+zCoor*rows][yOffset].equals(".") || map[xOffset+zCoor*rows][yOffset].equals("|")) {
						//check if this coor has been stacked before
						if (visited.containsKey(currCoor)) {
							//check the previous path length to get the the current Coor and compare it to the present path length
							//use the same logic as tracing back to the start coor to find the resultant path
							int lengthPrevious = 0;
							System.out.println("Current coordinate: " + currCoor);
							ArrayList<Integer> prevTraceback = visited.get(currCoor);
							System.out.println("Previous parent: " + visited.get(currCoor));
							while (prevTraceback != startCoor.get(0)) {
								lengthPrevious+=1;
								System.out.println(prevTraceback);
								if (visited.containsKey(prevTraceback)) {
									prevTraceback = visited.get(prevTraceback);
								}
							}
							int lengthCurr = 1;
							ArrayList<Integer> currTraceback = visited.get(dequeued); //find the parent of the current in visited and traceback
							System.out.println("Current parent: " + dequeued);
							while (currTraceback != startCoor.get(0)) {
								lengthCurr+=1;
								System.out.println(currTraceback);
								if (visited.containsKey(currTraceback)) {
									currTraceback = visited.get(currTraceback);
								}
							}
							System.out.println("Previous Length: " + lengthPrevious);
							System.out.println("Current Length: " + lengthCurr);
							if (lengthCurr < lengthPrevious) {
								//replace the previous parent of the current Coordinate with the present parent
								System.out.println("Replaced");
								visited.replace(currCoor, dequeued);
							}
						}
						else {
							//current coor is valid--push it
							optimalQueue.add(currCoor);
							//if currCoor is an open walkway, push the starting coor of the next maze into the stack 
							if (map[xOffset+zCoor*rows][yOffset].equals("|")) {
								optimalQueue.add(startCoor.get(zCoor+1));
								visited.put(startCoor.get(zCoor+1), currCoor);
							}
							//add the current coordinates into visited as the child of the coordinates that it branched off from (the parent)--this is for tracing back later
							visited.put(currCoor, dequeued);
						}
					}
				}
			}
			if (optimalQueue.size() == 0) {
				break;
			}
			if (coinCoor.size() != 0) {
				break;
			}
			System.out.println("Queue: " + optimalQueue);
				
		}
		System.out.println("Visited: " + visited);
		
		//TRACEBACK
		traceback(startCoor, coinCoor, visited);
	}
	public static void traceback(ArrayList<ArrayList<Integer>> startCoor, ArrayList<Integer> coinCoor, HashMap<ArrayList<Integer>, ArrayList<Integer>> visited) {
		//find the coor that is the parent to the coor of the coin 
		ArrayList<Integer> currTraceback = visited.get(coinCoor);
		//while the code hasn't traced back to the starting pos...
		while (currTraceback != startCoor.get(0)) {
			//add the coor to the result
			result.add(currTraceback);
			//update the current coor to be the parent coor
			if (visited.containsKey(currTraceback)) {
				currTraceback = visited.get(currTraceback);
			}
			//if the curr coor does not have a parent, coin is unreachable 
			else {
				storeClosed = true;
				System.out.println("The Wolverine Store is closed.");
				break;
			}
		}
		System.out.println("Result: " + result);
		System.out.println(result.size());
	}
	public static void mapOutput() {
		//ensure wolverine store is not closed
		if (!storeClosed) {
			//create array for output
			String[][] output = new String[map.length][map[0].length];
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[0].length; j++) {
					output[i][j] = map[i][j];
				}
			}
			//for all the coordinates in result
			for (ArrayList<Integer> coor : result) {
				int xCoor = coor.get(0);
				int yCoor = coor.get(1);
				int zCoor = coor.get(2);
				//only add "+" for periods
				if (output[xCoor+zCoor*rows][yCoor].equals(".")) {
					output[xCoor+zCoor*rows][yCoor] = "+";
				}
			}
			//print output
			for (int i = 0; i < output.length; i++) {
				for (int j = 0; j < output[0].length; j++) {
					System.out.print(output[i][j]);
				}
				System.out.println();
			}
		}
	}
	public static void coorOutput() {
		//ensure wolverine store is not closed
		if (!storeClosed) {
			//loop through result
			for (int i = result.size()-1; i >= 0; i--) {
				ArrayList<Integer> coor = result.get(i);
				int xCoor = coor.get(0);
				int yCoor = coor.get(1);
				int zCoor = coor.get(2);
				//only add "+" for periods
				if (map[xCoor+zCoor*rows][yCoor].equals(".")) {
					System.out.println("+ " + xCoor + " " + yCoor + " " + zCoor);
				}
			}
		}
	}
	
	
}