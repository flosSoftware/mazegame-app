package com.albertof.mazegame;


import android.content.Context;
import android.widget.ImageView;

public class Wall extends MazeElement {

	public Wall(int x, int y, Orientation o) {
		super(x, y, o);
	}

	@Override
	public boolean isWalkable() {
		return false;
	}

	@Override
	public Position enter(Direction in) throws NoseGotHurtException {
		throw new NoseGotHurtException("You hit a wall!");
	}

	@Override
	public String toString() {
		return o == Orientation.VERT ? "|" : "-";
	}

    public int getIcon(Context context){
		int fName = o == Orientation.VERT ? R.drawable.wall_v : R.drawable.wall;
		return fName;
	}
	
	@Override
	public int getAudioFile() {
		return -1;
	}

}
