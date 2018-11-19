package com.ultimatetoolsil.list;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import Adapters.PickerAdapter;
import models.CustomList;
import models.Item;

/**
 * Created by mike on 8 Mar 2018.
 */

public class ListPickerFragment extends Fragment {
    ArrayList<CustomList> pickedlists;
    private String placeID;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ListView listView = (ListView) view.findViewById(R.id.picker);
        pickedlists = new ArrayList<CustomList>();
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.background_border);
        GradientDrawable gradientDrawable = (GradientDrawable) drawable;
        gradientDrawable.setStroke(5, getResources().getColor(R.color.gray));
        view.setBackgroundDrawable(gradientDrawable);

        Button save = (Button) view.findViewById(R.id.button4);

        ArrayList<String> temp = new ArrayList<String>(MainActivity.values);
        final ArrayList<CustomList> data = new ArrayList<>();
        for (int i = 0; i < temp.size(); i++) {
            CustomList customList = new CustomList();
            customList.setListname(temp.get(i));
            data.add(customList);
        }

        final PickerAdapter pickerAdapter = new PickerAdapter(getActivity(), data);
        listView.setAdapter(pickerAdapter);
        Bundle b = new Bundle();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get selected items from picker
                Log.d("list", data.toString());
                OnFragmentInteractionListener listener = (OnFragmentInteractionListener) getActivity();
                listener.onFragmentSetLists(data,placeID);
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_picker,
                container, false);
        return view;


    }

    public interface OnFragmentInteractionListener {
        public void onFragmentSetLists(ArrayList<CustomList> lists,String placeID);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
          if(bundle!=null)
          placeID=bundle.getString("id");
        }
    }

}
