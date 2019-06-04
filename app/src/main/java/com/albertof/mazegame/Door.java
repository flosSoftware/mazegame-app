package com.albertof.mazegame;


import android.content.Context;
import android.widget.ImageView;

public class Door extends MazeElement {

	private boolean open;

	public Door(int x, int y, boolean open, Orientation o) {
		super(x, y, o);
		setOpen(open);
	}

	@Override
	public boolean isWalkable() {
		return open;
	}

	@Override
	public Position enter(Direction inDir) throws NoseGotHurtException {
		if(!open) {
			throw new NoseGotHurtException("You hit a closed door!");			
		} else if(!Direction.DIRECTIONS_STANDARD.contains(inDir)) {
			throw new IllegalArgumentException("You can't enter a door from a diagonal direction");			
		}
		//System.out.println(inDir);
		return p.getNext(inDir);
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
	
	@Override
	public String toString() {
		if(isOpen())
			return o == Orientation.VERT ? "/" : "\\";
		else
			return o == Orientation.VERT ? "?" : "_";
	}
	@Override
	public int getIcon(Context context) {
		int fName;
		if(isOpen())
			fName = o == Orientation.VERT ? R.drawable.door_open : R.drawable.garage_open;
		else
			fName = o == Orientation.VERT ? R.drawable.door_closed : R.drawable.garage_closed;
		return fName;
	}
	
	@Override
	public int getAudioFile() {
		return R.raw.door;
	}


}
