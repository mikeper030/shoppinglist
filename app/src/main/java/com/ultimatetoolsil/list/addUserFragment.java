package com.ultimatetoolsil.list;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import models.List;

import static com.ultimatetoolsil.list.MainActivity.myRef;
import static com.ultimatetoolsil.list.MainActivity.userID;
import static com.ultimatetoolsil.list.MainListFragment.dest;
import static com.ultimatetoolsil.list.MainListFragment.temp;

/**
 * Created by mike on 3 Apr 2018.
 */

public class addUserFragment extends Fragment {
    private Button button1,button2;
    private EditText editText;
    private TextView textView;

    boolean toast=true;
    String listname;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button1 = (Button) view.findViewById(R.id.button11);
        button2=(Button)view.findViewById(R.id.button12);
        editText = (EditText) view.findViewById(R.id.username);
        textView = (TextView) view.findViewById(R.id.textView13);
       Bundle bundle=getArguments();
        if(bundle!=null) {
            listname = bundle.getString("direct", null);

        }
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body =  utils.writeListToFile(temp);
                Log.d("hashmap",body);

                Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                intent2.setType("text/plain");
                intent2.putExtra(Intent.EXTRA_TEXT, body );
                startActivity(Intent.createChooser(intent2, "Share via"));
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), R.string.in, Toast.LENGTH_SHORT).show();
                    return;
                }
                final Query query = myRef.child("users").orderByChild("username").equalTo(editText.getText().toString()) ;
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String userid = dataSnapshot.getKey();
                        Log.d("what", userid);
                        if (userid != null) {
                            List list= new List();
                            list.setName(dest);
                            ArrayList<String> users= new ArrayList<>();
                            users.add(userid);
                            users.add(userID);
                            list.setUsers(users);
                            myRef.child("users").child(userID).child("lists").child("shared").child(userid).setValue(list);
                            myRef.child("users").child(userid).child("lists").child("shared").child(userID).setValue(list).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                toast = false;
                                Toast.makeText(getActivity(),R.string.success,Toast.LENGTH_SHORT).show();
                                getActivity().onBackPressed();
                            }
                        });
                            toast = false;

                        }
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
                        Log.d("what", "rr");
                    }
                });

                if(toast) {
                    Toast.makeText(getActivity(), R.string.unm, Toast.LENGTH_SHORT).show();

                }
                }

        });

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_layout,
                container, false);
        return view;
    }
}
