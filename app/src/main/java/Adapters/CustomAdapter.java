package Adapters;

/**
 * Created by mike on 12 Mar 2018.
 */


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindorks.paracamera.Camera;
import com.ultimatetoolsil.list.MainActivity;
import com.ultimatetoolsil.list.R;
import com.ultimatetoolsil.list.imageViewerFragment;

import java.util.ArrayList;

import models.BillModel;
import models.PicassoClient;

public class CustomAdapter extends ArrayAdapter<BillModel> {
    private final Context context;
    private final ArrayList<BillModel> values;

    public CustomAdapter(Context context, ArrayList<BillModel> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(final int position, final View convertView, @NonNull ViewGroup parent) {
        View rowView = null;
        final ViewHolder holder = new ViewHolder();
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.bills_item, null);

            holder.tv1 = (TextView) rowView.findViewById(R.id.textView3);
            holder.tv2 = (TextView) rowView.findViewById(R.id.textView5);
            holder.img = (ImageView) rowView.findViewById(R.id.imageView2);
            holder.img.setTag("tt");
            holder.tv1.setText(values.get(position).getListname());
            holder.tv2.setText(values.get(position).getStatus());
            PicassoClient.DownloadSingleImage(context,values.get(position).getUrl(),holder.img,0);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageViewerFragment fragmentB = new imageViewerFragment();
                    Bundle args = new Bundle();
                    args.putString("link", values.get(position).getUrl());
                    fragmentB.setArguments(args);
                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(android.R.id.content, fragmentB, "ss").addToBackStack(null).commit();
                }
            });
        }
        return rowView;
    }
        static class ViewHolder {
            public TextView tv1, tv2;
            public ImageView img;
        }
    }
