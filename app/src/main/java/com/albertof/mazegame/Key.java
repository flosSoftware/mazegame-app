package com.albertof.mazegame;


import android.content.Context;
import android.widget.ImageView;

public class Key extends MazeElement {
	
	private Door door;

	protected Key(int x, int y) {
		super(x, y, Orientation.VERT);
	}

	@Override
	public boolean isWalkable() {
		return true;
	}

	@Override
	public Position enter(Direction in) throws NoseGotHurtException {
		if(door != null) door.setOpen(true);
		return p;
	}

	@Override
	public String toString() {
		return "@";
	}

    public int getIcon(Context context){
        int fName = R.drawable.key;
        return fName;
    }
	
	@Override
	public int getAudioFile() {
		return R.raw.key;
	}

	public Door getDoor() {
		return door;
	}

	public void setDoor(Door d) {
		this.door = d;
	}

}
