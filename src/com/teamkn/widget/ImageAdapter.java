package com.teamkn.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import com.teamkn.R;
import com.teamkn.application.TeamknApplication;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private ArrayList<String> image_urls;

    public ImageAdapter(ArrayList<String> image_urls) {
        this.image_urls = image_urls;
    }

    @Override
    public int getCount() {
        return image_urls.size();
    }

    @Override
    public Object getItem(int position) {
        return image_urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = TeamknApplication.context;
        ImageView image_view = new ImageView(context);

        image_view.setBackgroundResource(R.drawable.bg_image_loading);
        image_view.setScaleType(ImageView.ScaleType.FIT_XY);
        image_view.setLayoutParams(new Gallery.LayoutParams(400, 400));
        return image_view;
    }

}
