package com.albertof.mazegame;

import android.content.Context;
import android.widget.ImageView;




public class Space extends MazeElement {

    private boolean isFinal;

    public Space(int x, int y, boolean isFinal) {
        super(x, y, Orientation.HORIZ);
        this.setFinal(isFinal);
    }

    @Override
    public boolean isWalkable() {
        return true;
    }

    @Override
    public Position enter(Direction in) throws NoseGotHurtException {
        return this.p;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public String toString() {
        return isFinal ? "x" : " ";
    }

    public int getIcon(Context context){
        int icon = R.drawable.dummy;
        if(isFinal())
            icon = R.drawable.destination;
        return icon;
    }

    @Override
    public int getAudioFile() {
        return -1;
    }

}
