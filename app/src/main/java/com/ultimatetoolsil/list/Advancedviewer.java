package com.ultimatetoolsil.list;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import models.AdvancedImageView;
import models.Item;
import models.PicassoClient;

import static com.ultimatetoolsil.list.BillsLocatorFragment.storageReference;
import static com.ultimatetoolsil.list.MainListFragment.updateItem;


/**
 * Created by mike on 1 Apr 2018.
 */

public class Advancedviewer extends Fragment {
    Button upload;
    private Camera camera;
    Uri filePath=null;
    public static Item passeditem;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.adavenced_viewer,
                container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AdvancedImageView imageView= (AdvancedImageView) view.findViewById(R.id.img2);
        Bundle args = getArguments();
        if (args != null&&args.get("link")!=null) {
            Log.d("file",args.getString("link"));
            PicassoClient.DownloadSingleImage(getActivity(),args.get("link").toString(),imageView,0);
        }else{
            imageView.setImageResource(R.drawable.no_image);
        }
        upload=(Button)view.findViewById(R.id.button10);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera = new Camera.Builder()
                        .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                        .setTakePhotoRequestCode(1)
                        .setDirectory("pics")
                        .setName("ali_" + System.currentTimeMillis())
                        .setImageFormat(Camera.IMAGE_JPEG)
                        .setCompression(75)
                        .setImageHeight(700)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                        .build(Advancedviewer.this);
                try {

                    camera.takePicture();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File f = null;
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Log.d("code", "code");
            Bitmap bitmap = camera.getCameraBitmap();
            try {
                f = savebitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null) {

                filePath = Uri.fromFile(new File(f.getAbsolutePath()));

                Log.d("path", String.valueOf(filePath));
                uploadImage();
            } else {
                Toast.makeText(getActivity(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
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
                            passeditem.setImglink(downloadURL);
                            updateItem(passeditem);
                            Log.d("pass",downloadURL);
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
 public static void setItem(Item item){
        passeditem=item;
 }

}
