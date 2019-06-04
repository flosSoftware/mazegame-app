package com.albertof.mazegame;

import java.util.ArrayList;
import java.util.Arrays;

public enum Direction {
	UP,
	UP_R,
	RIGHT,
	DOWN_R,
	DOWN,
	DOWN_L,
	LEFT,
	UP_L; 
	
	public static Direction invertDir(Direction dir) {
		
		if (dir == Direction.RIGHT)
			return Direction.LEFT;
		else if (dir == Direction.LEFT)
			return Direction.RIGHT;
		else if (dir == Direction.DOWN)
			return Direction.UP;
		else if (dir == Direction.UP)
			return Direction.DOWN;
		else if (dir == Direction.DOWN_L)
			return Direction.UP_R;
		else if (dir == Direction.DOWN_R)
			return Direction.UP_L;
		else if (dir == Direction.UP_L)
			return Direction.DOWN_R;
		else if (dir == Direction.UP_R)
			return Direction.DOWN_L;
		else
			return null;
	}
	
	public static final ArrayList<Direction> DIRECTIONS_FULL_INV = new ArrayList<Direction>(
		Arrays.asList(
			Direction.DOWN, 
			Direction.DOWN_L, 
			Direction.LEFT, 
			Direction.UP_L,
			Direction.UP, 
			Direction.UP_R, 
			Direction.RIGHT, 
			Direction.DOWN_R
	));
	
	public static final ArrayList<Direction> DIRECTIONS_STANDARD = new ArrayList<Direction>(
		Arrays.asList(		
			Direction.DOWN, 
			Direction.LEFT, 
			Direction.UP,  
			Direction.RIGHT			
	));
}
