package com.example.chotu.gelfie.grid;

/**
 * Created by CHOTU on 10/31/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.chotu.gelfie.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    public List<Item> items = new ArrayList<Item>();
    private LayoutInflater inflater;
    public Context mContext;

    public MyAdapter(Context context,ArrayList<String> imageArrayList,ArrayList<String> urlArrayList) {

        mContext=context;

        inflater = LayoutInflater.from(context);
        int  size=imageArrayList.size();
        for(int i=0;i<size;i++)
        {
            items.add(new Item(imageArrayList.get(i),urlArrayList.get(i)));
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        //return items.get(i).drawableId;
        return 1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if(v == null) {
            v = inflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView)v.getTag(R.id.picture);
        name = (TextView)v.getTag(R.id.text);

        Item item = (Item)getItem(i);

        Glide.with(mContext).load(item.path).into(picture);
        name.setText(item.name);

        return v;
    }

    private class Item {
        final String name;
        final String path;


        Item(String name, String path){
            this.name = name;
            this.path=path;

        }
    }
}
