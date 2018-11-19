package com.ultimatetoolsil.list;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.net.URI;
import java.net.URL;
import java.util.UUID;

import models.Category;
import models.Item;
import models.PicassoClient;

import static com.ultimatetoolsil.list.BillsLocatorFragment.storageReference;
import static com.ultimatetoolsil.list.MainListFragment.SaveInDB;

import static com.ultimatetoolsil.list.MainListFragment.dest;
import static com.ultimatetoolsil.list.MainListFragment.myRef;
import static com.ultimatetoolsil.list.MainListFragment.userID;

/**
 * Created by mike on 28 Feb 2018.
 */

public class EditItemFragment extends Fragment {
    public static Item passeditem;
    Spinner units,categories;
   TextView confirm,cancel;
   ImageView image;
   Button delete,plus,minus;
   EditText quantity,notes,price,title;
   CheckBox checkBox;




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        units=(Spinner)view.findViewById(R.id.spinner2);
        categories=(Spinner)view.findViewById(R.id.spinner3);
        title=(EditText) view.findViewById(R.id.textView10);
        confirm=(TextView)view.findViewById(R.id.button8);
        cancel=(TextView)view.findViewById(R.id.button7);
        image=(ImageView)view.findViewById(R.id.imageButton);
        delete=(Button)view.findViewById(R.id.button9);
        quantity=(EditText)view.findViewById(R.id.quanti);
        notes=(EditText)view.findViewById(R.id.nte);
        price=(EditText)view.findViewById(R.id.editText2);
        checkBox=(CheckBox)view.findViewById(R.id.checkBox);
        plus=(Button)view.findViewById(R.id.button5);
        minus=(Button)view.findViewById(R.id.button6);
        Bundle bundle=getArguments();
        if(bundle!=null){
           title.setText(bundle.get("name").toString());
           quantity.setText(bundle.get("quantity").toString());

           if(bundle.get("checked").equals("true"))
           checkBox.setChecked(true);
           else
               checkBox.setChecked(false);

           if(bundle.get("price")!=null)
               price.setText(bundle.get("price").toString());

           selectValue(categories,bundle.get("category").toString());

           if(bundle.get("note")!=null)
               notes.setText(bundle.get("note").toString());

           if(bundle.get("img")!=null){
               PicassoClient.DownloadSingleImage(getActivity(),bundle.get("img").toString(),image,0);
           }
            if(bundle.get("unit")!=null)
               selectValue(units,bundle.get("unit").toString());


          plus.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                 double quant=Double.parseDouble(quantity.getText().toString());
                  quant+=1;

                  //expandedListitem.setQuantity(quant);
                  quantity.setText(String.valueOf(quant));
                 // SaveInDB(dest);
              }
          });
         minus.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                double quant=Double.parseDouble(quantity.getText().toString());
                 quant-=1;
                 if(quant>=0) {
                   //  expandedListitem.setQuantity(quant);
                     quantity.setText(String.valueOf(quant));
                   //  SaveInDB(dest);
                 }
             }
         });
         }
      confirm.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
          passeditem.setNote(notes.getText().toString());
          passeditem.setItemName(title.getText().toString());
          passeditem.setQuantity(Double.parseDouble(quantity.getText().toString()));
          passeditem.setUnittype(units.getSelectedItem().toString());
          passeditem.setCategoryname(categories.getSelectedItem().toString());
          passeditem.setPrice(Double.parseDouble(price.getText().toString()));
          MainListFragment.updateItem(passeditem);

              getActivity().getFragmentManager().popBackStack();

             }
          });
     cancel.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             getActivity().getFragmentManager().popBackStack();

         }
     });
    delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          MainListFragment.removeItem(passeditem);
            getActivity().getFragmentManager().popBackStack();
        }
    });

    image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Advancedviewer newFragment = new Advancedviewer();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Bundle args= new Bundle();
            args.putString("link",passeditem.getImglink());
            Advancedviewer.setItem(passeditem);
            // Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack if needed
            newFragment.setArguments(args);
            transaction.replace(android.R.id.content, newFragment);
            transaction.addToBackStack(null);

// Commit the transaction
            transaction.commit();

        }
    });
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_item,
                container, false);
        return view;

    }
    private void selectValue(Spinner spinner, String value) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
public static void setItemObject(Item item){
      passeditem=item;
  }

}
