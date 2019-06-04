package com.albertof.mazegame;


import android.content.Context;
import android.widget.ImageView;

public class Monster {
	public Monster(int x, int y, Direction d) {
		p = new Position(x, y);
		this.d = d;
	}

	public Direction getD() {
		return d;
	}

	public void setD(Direction d) {
		this.d = d;
	}

	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
	}

	public Position getNextP() {
		return p.getNext(d);
	} 

	@Override
	public String toString() {
		return d == Direction.LEFT ? "<" : d == Direction.RIGHT ? ">" : d == Direction.UP ? "^" : "v";
	}

    public int getIcon(Context context){
        int fName = R.drawable.monster;
        return fName;
	}

	public void invertDir() {
		d = Direction.invertDir(d);
	}

	private Position p;

	private Direction d;

}
