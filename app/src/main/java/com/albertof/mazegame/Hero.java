package com.albertof.mazegame;


import android.content.Context;
import android.widget.ImageView;

public class Hero {

	public Hero(int x, int y, int lives) {
		p = new Position(x, y);
		this.lives = lives;
	}

	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
	}

	@Override
	public String toString() {
		return "o";
	}

    public int getIcon(Context context){
		int fName = R.drawable.hero;
		return fName;
	}
	
	public void die() {		
		lives--;
		//System.out.println("die: lives = "+lives);
	}
	
	public boolean isDead() {
		
		return lives <= 0;
	}

	Position p;
	
	int lives;

	public int getLives() {
		return lives;
	}


}
