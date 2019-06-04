package com.albertof.mazegame;

import android.content.Context;
import android.widget.ImageView;

public abstract class MazeElement {

	protected Position p;
	protected Orientation o;

	public abstract boolean isWalkable();

	public abstract Position enter(Direction in) throws NoseGotHurtException;
    // !!!!MODIFICATO!!!!
    public abstract int getIcon(Context context);
    // !!!!MODIFICATO!!!!
	public abstract int getAudioFile();

	protected MazeElement(int x, int y, Orientation o) {
		p = new Position(x, y);
		this.o = o;
	}

	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
	}


}
