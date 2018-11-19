package com.ultimatetoolsil.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;


import Adapters.CustomAdapter;
import models.BillModel;
import models.CustomList;


import static com.ultimatetoolsil.list.MainActivity.myRef;
import static com.ultimatetoolsil.list.MainActivity.userID;


/**
 * Created by mike on 12 Mar 2018.
 */

public class BillsLocatorFragment extends Fragment {
   private HashSet<String> entries=new HashSet<>();
    Camera camera;
    FirebaseStorage storage;
    public static StorageReference storageReference;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    private int pos=-1;
    CustomAdapter customAdapter;
    boolean create=false;
   private ListView list;
    ArrayList<BillModel> bills=new ArrayList<>() ;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
         list=(ListView)view.findViewById(R.id.lsst);
        LoadBills();
        LoadListNames(getActivity());
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.bills_layout,
                container, false);
        return view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File f=null;
        if(requestCode == Camera.REQUEST_TAKE_PHOTO){
           Log.d("code","code");
            Bitmap bitmap = camera.getCameraBitmap();
            try {
               f = savebitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(bitmap != null) {
                if(pos!=-1)
                   if(f!=null)
                       filePath = Uri.fromFile(new File(f.getAbsolutePath()));
                Log.d("path",String.valueOf(filePath));
                uploadImage();
            }else{
                Toast.makeText(getActivity(),"Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }










    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //camera.deleteImage();
    }
    private void LoadBills() {

        myRef.child("users").child(userID).child("lists").child("invoices").addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
              if(dataSnapshot.getValue()==null){
                  create=true;
              }else {
                  myRef.child("users").child(userID).child("lists").child("invoices").addChildEventListener(new ChildEventListener() {
                      @Override
                      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                          //data found fetch data from server
                          getUpdates(dataSnapshot);

                      }



                      @Override
                      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                         getUpdates(dataSnapshot);
                      }

                      @Override
                      public void onChildRemoved(DataSnapshot dataSnapshot) {
                         getUpdates(dataSnapshot);
                      }

                      @Override
                      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                      }

                      @Override
                      public void onCancelled(DatabaseError databaseError) {

                      }
                  });
              }
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });

    }


    private void LoadListNames(final Context context){


        myRef.child("users").child(userID).child("lists").child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> values= new ArrayList<>();
                Log.d("datasnapshot",dataSnapshot.toString());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("name",child.getKey());
                    values.add(child.getKey());
                }
                Log.d("crete",String.valueOf(create));
                if(create) {

                    for(int i=0;i<values.size();i++){
                       BillModel billModel= new BillModel();
                       billModel.setListname(values.get(i));
                       billModel.setStatus(getActivity().getResources().getString(R.string.n_uploaded));
                       billModel.setUrl("null");
                        myRef.child("users").child(userID).child("lists").child("invoices").child(values.get(i)).setValue(billModel);
                    }
                    create=false;
                }
                entries=new HashSet<String>(values);
                Log.d("dddd",entries.toString());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }
private void getUpdates(DataSnapshot dataSnapshot){
    boolean add=true;
    BillModel billModel = (BillModel) dataSnapshot.getValue(BillModel.class);
    Log.d("bd",billModel.getListname()+billModel.getStatus()+billModel.getUrl());
    for(int i=0;i<bills.size();i++){
        if(billModel.getListname().equals(bills.get(i).getListname())){
            add=false;
        }
    }
    if(add) {
        bills.add(billModel);
    }

    if(bills.size()>0){
        //check for duplicates

         customAdapter= new CustomAdapter(getActivity(),bills);

        list.setAdapter(customAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {



                         camera = new Camera.Builder()
                                 .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                                 .setTakePhotoRequestCode(1)
                                 .setDirectory("pics")
                                 .setName("ali_" + System.currentTimeMillis())
                                 .setImageFormat(Camera.IMAGE_JPEG)
                                 .setCompression(75)
                                 .setImageHeight(700)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                                 .build(BillsLocatorFragment.this);
                         try {
                             pos = position;
                             camera.takePicture();

                         } catch (Exception e) {
                             e.printStackTrace();
                         }

            }
        });

    }
    }
    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadURL= taskSnapshot.getDownloadUrl().toString();
                            bills.get(pos).setUrl(downloadURL);
                             myRef.child("users").child(userID).child("lists").child("invoices").child(bills.get(pos).getListname()).setValue(bills.get(pos));
                            customAdapter.notifyDataSetChanged();
                            progressDialog.setMessage("Uploaded "+"100"+"%");
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }
    public static File savebitmap(Bitmap bmp) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        File f = new File(Environment.getExternalStorageDirectory()
                + File.separator + "/image.jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        fo.close();
        return f;
    }
}
