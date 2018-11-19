package com.ultimatetoolsil.list;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import models.BillModel;
import models.List;


import static com.ultimatetoolsil.list.MainActivity.LoadListNames;
import static com.ultimatetoolsil.list.MainActivity.userID;
import static com.ultimatetoolsil.list.MainListFragment.LoadDBData;
import static com.ultimatetoolsil.list.MainListFragment.dest;


/**
 * Created by mike on 2 Mar 2018.
 */

public class CreateListFragment extends Fragment {
    private EditText input;
    private Button save;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private RelativeLayout relativeLayout;
    private ListView listView;
    String ss;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        input = (EditText) view.findViewById(R.id.edit1);
        save = (Button) view.findViewById(R.id.button3);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        listView = (ListView) view.findViewById(R.id.users);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rlv4);
        try {
            Bundle bundle = getArguments();
            if ((bundle.getString("f")!=null)) {
                if (bundle.getString("f").equals("f")) {
                    input.setText(bundle.getString("name"));
                    input.setEnabled(false);
                    save.setEnabled(false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }


        //add user mode

        final ArrayList<String> values = new ArrayList<>();


        MainListFragment.myRef.child("users").child(userID).child("lists").child("shared").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                List list = new List();
                list.setUsers(dataSnapshot.getValue(List.class).getUsers());

                ArrayList<String> users = new ArrayList(list.getUsers());
                Log.d("srrr",list.getUsers().toString());
                for (int i = 0; i < users.size(); i++) {
                    if (userID.equals(users.get(i))) {
                        for (int j = 0; j < users.size(); j++) {
                            getusername(users.get(j),values);
                        }
                    }
                }
               Log.d("srrr",values.toString());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),R.string.in,Toast.LENGTH_SHORT).show();
                    return;
                }
                addUserFragment newGamefragment = new addUserFragment();
                Bundle bundle= new Bundle();
                bundle.putString("direct",input.getText().toString());
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, newGamefragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //save list in firebase change main list than exit
                if(input.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),R.string.in,Toast.LENGTH_SHORT).show();
                    return;
                }
                myRef.child("users").child(userID).child("lists").child("data").child(input.getText().toString()).setValue("");
                LoadListNames(getActivity());
                LoadDBData(getActivity(),input.getText().toString(),"me");
                BillModel billModel= new BillModel();
                billModel.setListname(input.getText().toString());
                billModel.setStatus(getActivity().getResources().getString(R.string.n_uploaded));
                billModel.setUrl("null");
                myRef.child("users").child(userID).child("lists").child("invoices").child(input.getText().toString()).setValue(billModel);

                closefragment();


            }
        });


    }
    public String getusername(String id, final ArrayList<String> va) {
        myRef.child("users").child(id).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ss=dataSnapshot.getValue(String.class);
                Log.d("srrr",ss);
                va.add(ss);
                Log.d("srrr2",va.toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, va);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return ss;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_list,
                container, false);
        return view;

    }
    private void closefragment() {
        getActivity().onBackPressed();
    }
}
