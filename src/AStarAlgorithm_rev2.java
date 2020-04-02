import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.lang.Math;

class Node {
	private int[][] boardState;
	private int fVal;
	private int gVal;
	private int hVal;
	private int spaceX;
	private int spaceY;	
	private String heuristic;
	private String moveUsed;
	private Node previous;
	private HashMap<Integer, Integer[]> goalMap;
	
	public Node(int[][] currState, int[][] goalState, String heuristic, int spaceX, int spaceY) {
		this.gVal = 0;
		this.boardState = currState;
		this.spaceX = spaceX;
		this.spaceY = spaceY;
		this.previous = null;
		this.moveUsed = "";
		this.heuristic = heuristic;
		
		int h = 0;
		
		// Need to do this before calculating manhattanDistance
		this.goalMap = new HashMap<>();
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Integer[] coordArr = new Integer[2];
				coordArr[0] = i;
				coordArr[1] = j;
				this.goalMap.put(goalState[i][j], coordArr);
			}
		}
		
		if (heuristic.equals("a")) {
			h = misplacedTiles(currState, goalState);
		} else {
			h = manhattanDistance(currState, goalState);
		}
		
		this.hVal = h;
		this.fVal = hVal;
	}
	
	// Building a child node
	public Node(Node previous, String move, int[][] goal) {
		switch(move) {
		case "U":
			this.boardState = moveUp(previous.boardState, previous.spaceX, previous.spaceY);
			this.spaceX = previous.spaceX - 1;
			this.spaceY = previous.spaceY;
			break;
		
		case "D":
			this.boardState = moveDown(previous.boardState, previous.spaceX, previous.spaceY);
			this.spaceX = previous.spaceX + 1;
			this.spaceY = previous.spaceY;
			break;
			
		case "L":
			this.boardState = moveLeft(previous.boardState, previous.spaceX, previous.spaceY);
			this.spaceX = previous.spaceX;
			this.spaceY = previous.spaceY - 1;
			break;
		default:
			this.boardState = moveRight(previous.boardState, previous.spaceX, previous.spaceY);
			this.spaceX = previous.spaceX;
			this.spaceY = previous.spaceY + 1;
		}
		
		int h = 0;
		
		// Need to do this before calculating manhattanDistance
		this.goalMap = new HashMap<>();
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				Integer[] coordArr = new Integer[2];
				coordArr[0] = i;
				coordArr[1] = j;
				this.goalMap.put(goal[i][j], coordArr);
			}
		}
		
		if (previous.heuristic.equals("a")) {
			h = misplacedTiles(this.boardState, goal);
		} else {
			h = manhattanDistance(this.boardState, goal);
		}
		
		this.moveUsed = move;
		this.heuristic = previous.heuristic;
		this.gVal = previous.gVal + 1;
		this.previous = previous;
		this.hVal = h;
		this.fVal = this.gVal + this.hVal;
	}
	
	public int misplacedTiles(int[][] currState, int[][] goalState) {
		int val = 0;
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (currState[i][j] != goalState[i][j] && currState[i][j] != 0) {
					val++;
				}
			}
		}
		return val;
	}
	
	public int manhattanDistance(int[][] currState, int[][] goalState) {
		int sum = 0;
		
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < 3; j++)
			{
				int key = currState[i][j];
				if (key != 0 && key != goalState[i][j])
				{
					Integer[] coordPair = this.goalMap.get(key);
					sum += Math.abs(coordPair[0] - i) + Math.abs(coordPair[1] - j);
				}
			}
		}
		
		return sum;
	}
	
	public int[][] moveUp(int[][] intArr, int spaceX, int spaceY)
	{
		int[][] copy = Arrays.stream(intArr).map(int[]::clone).toArray(int[][]::new);
		int temp = copy[spaceX - 1][spaceY];
		copy[spaceX - 1][spaceY] = 0;
		copy[spaceX][spaceY] = temp;
		
		return copy;
	}
	
	public int[][] moveDown(int[][] intArr, int spaceX, int spaceY)
	{
		int[][] copy = Arrays.stream(intArr).map(int[]::clone).toArray(int[][]::new);
		int temp = copy[spaceX + 1][spaceY];
		copy[spaceX + 1][spaceY] = 0;
		copy[spaceX][spaceY] = temp;
		
		return copy;
	}
	
	public int[][] moveLeft(int[][] intArr, int spaceX, int spaceY)
	{
		int[][] copy = Arrays.stream(intArr).map(int[]::clone).toArray(int[][]::new);
		int temp = copy[spaceX][spaceY - 1];
		copy[spaceX][spaceY - 1] = 0;
		copy[spaceX][spaceY] = temp;
		
		return copy;
	}
	
	public int[][] moveRight(int[][] intArr, int spaceX, int spaceY)
	{
		int[][] copy = Arrays.stream(intArr).map(int[]::clone).toArray(int[][]::new);
		int temp = copy[spaceX][spaceY + 1];
		copy[spaceX][spaceY + 1] = 0;
		copy[spaceX][spaceY] = temp;
		
		return copy;
	}
	
	public ArrayList<Node> expand(int[][] goal) {
		ArrayList<Node> children = new ArrayList<Node>();
		// move up
		if (this.spaceX > 0) {
			children.add(new Node(this, "U", goal));
		}
		// move down
		if (this.spaceX < 2) {
			children.add(new Node(this, "D", goal));
		}
		// move left
		if (this.spaceY > 0) {
			children.add(new Node(this, "L", goal));
		}
		// move right
		if (this.spaceY < 2) {
			children.add(new Node(this, "R", goal));
		}
		return children;
	}
	
	public int getFVal() {
		return this.fVal;
	}
	
	public int getGVal() {
		return this.gVal;
	}
	
	public int getHVal() {
		return this.hVal;
	};
	
	public void printAll() {
		if (this.previous != null) {
			this.previous.printAll();
		}
		System.out.println();
		System.out.println(this);
	}
	
	public void printSteps() 
	{
		if (this.previous != null) 
		{
			this.previous.printSteps();
			switch(this.moveUsed) 
			{
			case "U":
				System.out.println("Move blank up");
				break;
			case "D":
				System.out.println("Move blank down");
				break;
			case "L":
				System.out.println("Move blank left");
				break;
			default:
				System.out.println("Move blank right");
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Node) {
			Node node2 = (Node)obj;
			return Arrays.deepEquals(this.boardState, node2.boardState);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Arrays.hashCode(this.boardState);
	}
	
	@Override
	public String toString()
	{
		String s = "Board:\n" +Arrays.toString(this.boardState[0]) + "\n" + Arrays.toString(this.boardState[1]) + "\n" + Arrays.toString(this.boardState[2]);
		s += "\nf-val: " + this.fVal + "\n";
		return s;
	}
}

public class AStarAlgorithm_rev2
{	
	public static void search(Node startNode, int[][] goal) 
    {
        PriorityQueue<Node> pQueue = new PriorityQueue<Node>(100, new Comparator<Node>() {
        	@Override
        	public int compare(Node a, Node b) {
        		return a.getFVal() - b.getFVal();
        	}
        });
        
        HashSet<Node> seenNodes = new HashSet<Node>();
        
        // Add start node to queue
        pQueue.add(startNode);
        
		int numNodesExplored = 1;
        while(!pQueue.isEmpty()) 
        {
            // Remove the first path from the queue
            Node currNode = pQueue.poll();
            
            seenNodes.add(currNode);
            if (currNode.getHVal() == 0) // Reach solution
            {
				// Print solution
				System.out.println("Solution: ");
				currNode.printSteps();
				System.out.println();
				
				// Print number of required moves
				System.out.println("Given the selected heuristic, the solution required " + (currNode.getGVal()) + " moves.");
				// Print number of explored nodes
				System.out.println("The A* explored " + numNodesExplored + " nodes to find the solution");
                return;
            }
            
            seenNodes.add(currNode);
            
            // expand will give us all child nodes, without loops
            ArrayList<Node> childNodes = currNode.expand(goal);
			
			// Add number of child nodes from total count
			numNodesExplored += childNodes.size();
			
            for (Node n : childNodes) {
                if (!seenNodes.contains(n) && n != null) {
                    pQueue.add(n);
                }
            }   
        }
        // Assume that no solution could be found if this point is reached
        System.out.println("For the above combination of the intial/goal states, there is no solution");
    }
	
	public static boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
	}
	
	public static int[][] linesTo2dIntArr(String l1, String l2, String l3) {
		String[] l1Tokens = l1.split(" ");
		String[] l2Tokens = l2.split(" ");
		String[] l3Tokens = l3.split(" ");
		
		String[][] strBoard = {
				l1Tokens,
				l2Tokens,
				l3Tokens,
		};
		
		int[][] board = new int[3][3];
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (isInteger(strBoard[i][j])) {
					board[i][j] = Integer.parseInt(strBoard[i][j]);
				}
			}
		}
		return board;
	}
	
	public static int[] getSpaceIndex(int[][] intArr) {
		int[] ans = new int[] {-1,-1};
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (intArr[i][j] == 0) {
					ans[0] = i;
					ans[1] = j;
					return ans;
				}
			}
		}
		return ans;
	}
	
	public static void main(String[] args) {
		int[][] start = new int[3][3];
		int[][] goal = new int[3][3];
		String heuristic;
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Enter the initial state:");
		String firstLine = in.nextLine();
		String secondLine = in.nextLine();
		String thirdLine = in.nextLine();
		
		start = linesTo2dIntArr(firstLine, secondLine, thirdLine);
		
		System.out.println("Enter the goal state:");
		firstLine = in.nextLine();
		secondLine = in.nextLine();
		thirdLine = in.nextLine();
		
		goal = linesTo2dIntArr(firstLine, secondLine, thirdLine);
		
		System.out.println("Select Heuristic:");
		System.out.println("\ta) Number of Misplaced Tiles\n\tb) Manhattan Distance");
		heuristic = in.nextLine().toLowerCase();
	
		int[] spaceIndices = getSpaceIndex(start);
		
		Node startNode = new Node(start, goal, heuristic, spaceIndices[0], spaceIndices[1]);
		
		search(startNode, goal);
	}
}