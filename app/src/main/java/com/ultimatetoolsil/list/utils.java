package com.ultimatetoolsil.list;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import models.Category;
import models.Item;


/**
 * Created by mike on 11 Dec 2017.
 */

public class utils {


    private Context mContext = null;
    public utils(Context con){
        mContext = con;
    }

    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager

                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
    public static String writeListToFile(HashMap<Category,List<Item>> map){
        FileWriter writer = null;
        String text=null;
        try {
            writer = new FileWriter(Environment.getExternalStorageDirectory().getAbsolutePath()+"/output.txt");
            for (Map.Entry<Category, List<Item>> entry : map.entrySet()) {
                String category=entry.getKey().getCategoryName();
                writer.write(category);
                writer.write(System.getProperty("line.separator"));
                writer.write("=================");
                writer.write(System.getProperty("line.separator"));
                List<Item> items=entry.getValue();
                for (int i=0;i<items.size();i++){
                    writer.write(items.get(i).getItemName()+" "+items.get(i).getQuantity()+" "+items.get(i).getUnittype());
                    writer.write(System.getProperty("line.separator"));
                }
                writer.write(System.getProperty("line.separator"));
                writer.write(System.getProperty("line.separator"));
                // ...
            }

        writer.close();
            text=readFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/output.txt");
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"oputput.txt");
            if(f.exists())
                f.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }
    public static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }

            return stringBuilder.toString();
        } finally {
            reader.close();
        }
    }
}