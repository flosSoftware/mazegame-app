package com.albertof.mazegame;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

// IMPLEMENTA IL CONCETTO DI DATASOURCE IN ANDROID
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private ImageView[][] icons;

    public ImageAdapter(Context c, ImageView[][] icons) {
        // LE ICONE INIZIALI
        mContext = c;
        this.icons = icons;
    }

    public int getCount() {
        return icons[0].length * icons[0].length;
    }

    // NON USATO
    public Object getItem(int position) {
        return null;
    }
    // NON USATO
    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        // DA POSIZIONE IN GRIDVIEW PASSO ALLE COORDINATE X/Y
        int x = position/icons[0].length;
        int y = position-(x*icons[0].length);

        //Log.wtf("WTF","pos: "+position+" x: "+x+" y: "+y);
        //Log.wtf("WTF",".... "+String.valueOf(icons[x][y].getTag()));

        ImageView imageView;

        if (convertView == null) {

            imageView = icons[x][y];
            // if it's not recycled, initialize some attributes
            //imageView = new ImageView(mContext);

            int imgW = ( (GridView)parent ).getColumnWidth();

           // Log.wtf("WTF",".... "+String.valueOf(imageView.getTag()));

            imageView.setLayoutParams(new ViewGroup.LayoutParams( imgW, imgW ) );

            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE); // !!!don't use this!!!

            imageView.setPadding(0,0,0,0);

        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;
    }

}