package Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ultimatetoolsil.list.R;

import java.util.ArrayList;

import models.CustomList;

/**
 * Created by mike on 8 Mar 2018.
 */

public class PickerAdapter extends ArrayAdapter<CustomList> {
    private final Context context;
    private final ArrayList<CustomList> values;

    public PickerAdapter(Context context, ArrayList<CustomList> values) {
        super(context, R.layout.picker_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.picker_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);
        CheckBox ch =(CheckBox)rowView.findViewById(R.id.picker_check);
        ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
               CustomList customList= (CustomList)values.get(position);
               if(isChecked)
                   customList.setChecked(true);
               else
                   customList.setChecked(false);

            values.set(position,customList);
                Log.d("values",String.valueOf(values.get(0).isChecked()));
            }

        });
        textView.setText(values.get(position).getListname());
        // Change the icon for Windows and iPhone


        return rowView;
    }
}