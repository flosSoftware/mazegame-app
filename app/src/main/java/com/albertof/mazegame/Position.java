package com.albertof.mazegame;

public class Position {

	private int x;
	private int y;

	public Position(int i, int j) {
		this.setX(i);
		this.setY(j);
	}

	public Position getNext(Direction dir) {

		if (dir == Direction.RIGHT)
			return new Position(x, y + 1);
		else if (dir == Direction.DOWN)
			return new Position(x + 1, y);
		else if (dir == Direction.UP)
			return new Position(x - 1, y);
		else if (dir == Direction.DOWN_L)
			return new Position(x + 1, y - 1);
		else if (dir == Direction.DOWN_R)
			return new Position(x + 1, y + 1);
		else if (dir == Direction.UP_L)
			return new Position(x - 1, y - 1);
		else if (dir == Direction.UP_R)
			return new Position(x - 1, y + 1);
		else
			return new Position(x, y - 1);

	}

	public Position[] getNeighborhood() {
		Direction[] dirs = Direction.values();
		Position[] p = new Position[dirs.length];
		int i = 0;
		for (Direction d : dirs) {
			p[i++] = getNext(d);
		}

		return p;
	}

	@Override
	public String toString() {
		return "x: " + x + ", y: " + y;
	}

	@Override
	public boolean equals(Object obj) {
		Position p = (Position) obj;
		return p.getX() == getX() && p.getY() == getY();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

}
