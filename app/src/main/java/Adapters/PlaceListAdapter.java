package Adapters;

/*
* Copyright (C) 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ultimatetoolsil.list.PlacePickerActivity;
import com.ultimatetoolsil.list.R;

import com.google.android.gms.location.places.PlaceBuffer;

import provider.PlaceContract;
import provider.PlaceDbHelper;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder> {
    PlaceDbHelper mPlaceDbHelper;
    private Context mContext;
    private PlaceBuffer mPlaces;


    public PlaceListAdapter(Context context, PlaceBuffer places) {
        this.mContext = context;
        this.mPlaces = places;
        mPlaceDbHelper = new PlaceDbHelper(context);
    }


    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get the RecyclerView item layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_place_card, parent, false);
        return new PlaceViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final PlaceViewHolder holder, final int position) {

        String placeName = mPlaces.get(position).getName().toString();
        String placeAddress = mPlaces.get(position).getAddress().toString();
        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(placeAddress);
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(mContext);
                }
                builder.setTitle(R.string.delete)
                        .setMessage(R.string.sure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // remove geofence from server and delete item in recyclerveiw
                                PlacePickerActivity.mGeofencing.deleteGeofence(mPlaces.get(position).getId());
                                final SQLiteDatabase db = mPlaceDbHelper.getWritableDatabase();
                                Log.d("fd",mPlaces.get(position).getId());
                                String table = "places";
                                String whereClause = "placeID";
                                String[] whereArgs = new String[] { mPlaces.get(position).getId() +""};
                                db.delete(table, "PlaceID=?",new String[]{mPlaces.get(position).getId()});
                                PlacePickerActivity.refreshPlacesData(mContext);

                                notifyItemRemoved(position);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public void swapPlaces(PlaceBuffer newPlaces){
        mPlaces = newPlaces;
        if (mPlaces != null) {
            // Force the RecyclerView to refresh
            this.notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        if(mPlaces==null) return 0;
        return mPlaces.getCount();
    }

    /**
     * PlaceViewHolder class for the recycler view item
     */
    class PlaceViewHolder extends RecyclerView.ViewHolder {
         ImageButton delete;
        TextView nameTextView;
        TextView addressTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name_text_view);
            addressTextView = (TextView) itemView.findViewById(R.id.address_text_view);
            delete=(ImageButton)itemView.findViewById(R.id.del);

        }

    }
}
