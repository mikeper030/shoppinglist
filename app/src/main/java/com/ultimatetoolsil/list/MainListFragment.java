package com.ultimatetoolsil.list;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import Adapters.MainListAdapter;
import models.Category;
import models.Item;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.ultimatetoolsil.list.MainActivity.LoadListNames;


/**
 * Created by mike on 12 Mar 2018.
 */

public class MainListFragment extends Fragment implements AdapterView.OnItemSelectedListener,SearchView.OnQueryTextListener {
    public static ExpandableListView mainlist;
    private Spinner categoriesList;
    private FirebaseAuth auth;
    private Button plus, minus;
    public static ListView drawerlist;
    public static LinkedHashMap<Category, List<Item>> temp = new LinkedHashMap<>();
    final List<Category> temp2 = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    public static List<Category> categories = new ArrayList<>();
    HashMap<Category, List<Item>> data = new HashMap<Category, List<Item>>();
    private DrawerLayout drawer;
    public static MainListAdapter adapter;
    ImageButton add;
    public static String userID;
    EditText product;
    public static String dest;
    FirebaseDatabase database;
    protected BottomNavigationView navigationView;
    public static boolean showdel = false;
    public static HashSet<String> data2;
    public static int ll;
    public static ImageView DEF1;
    public static TextView def2;
    public static TextView no;
    public static List<Integer> colors;
    private static ProgressBar mPro;
    public static DatabaseReference myRef;
    public static Display display;
    private Menu menu;
    Toolbar  toolbar;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {

            startActivity(new Intent(getActivity(), signups.class));
            getActivity().finish();
        } else {
            mainlist = (ExpandableListView) view.findViewById(R.id.main_list);
            categoriesList = (Spinner) view.findViewById(R.id.spinner);
            categoriesList.setOnItemSelectedListener(this);
            product = (EditText) view.findViewById(R.id.editText);
            add = (ImageButton) view.findViewById(R.id.imageView);
            plus = (Button) view.findViewById(R.id.plus);
            minus = (Button) view.findViewById(R.id.minus);
            initColorList();
            mPro = (ProgressBar) view.findViewById(R.id.pro);
            DEF1 = (ImageView) view.findViewById(R.id.def);
            no = (TextView) view.findViewById(R.id.no_itm);
            def2 = (TextView) view.findViewById(R.id.txt);

           //================mobile ads init====================

            mAdView = view.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

            mInterstitialAd = new InterstitialAd(getActivity());
            mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/2773608122");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
           //===================================================
            dest = getResources().getString(R.string.defaultlist);
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            String name = getActivity().getIntent().getStringExtra("name");
            getActivity().setTitle(R.string.tool);
            if (name != null) {
                name = name.trim();
                Log.d("name22", name);
                LoadDBData(getActivity(), name,"me");
            } else {
                LoadDBData(getActivity(), dest,"me");
            }


            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            LoadListNames(getActivity());

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (product.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), R.string.empty_pr, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Category exists = null;
                    boolean contain = false;
                    Category category1 = new Category();
                    category1.setCategoryName(categoriesList.getSelectedItem().toString());
                    Item item = new Item();
                    item.setChecked(false);
                    item.setQuantity(1.0);
                    item.setUnittype(getResources().getString(R.string.unt));
                    item.setCategoryname(categoriesList.getSelectedItem().toString());
                    item.setItemName(product.getText().toString());
                    Log.d("product", product.getText().toString());
                    Log.d("item", item.getItemName() + item.getQuantity());

                    for (Category category2 : categories) {
                        if (category2.getCategoryName().equals(category1.getCategoryName())) {
                            exists = category2;
                            contain = true;
                        }
                    }
                    if (!contain) {
                        categories.add(category1);
                        List<Item> items = new ArrayList<>();
                        items.add(item);
                        temp.put(category1, items);
                        SaveInDB(dest);
                    } else {
                        contain = false;
                        temp.get(exists).add(item);
                        SaveInDB(dest);
                    }
                    initColors(colors, temp);
                    adapter = new MainListAdapter(getActivity(), categories, temp);
                    mainlist.setAdapter(adapter);
                    product.setText("");
                    // adapter.notifyDataSetChanged();

                }
            });


        }
    }

    private void initColorList() {
        String[] colorsTxt = getActivity().getResources().getStringArray(R.array.colors);
        colors = new ArrayList<Integer>();
        for (int i = 0; i < colorsTxt.length; i++) {
            int newColor = Color.parseColor(colorsTxt[i]);
            colors.add(newColor);
        }
    }

    public static void LoadDBData(final Context context, String listname, String user) {
        if (user.equals("me")) {
            try {

                temp = new LinkedHashMap<>();
                categories = new ArrayList<>();
                myRef.child("users").child(userID).child("lists").child("data").child(listname).addValueEventListener(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {

                            cancelshow();
                            GenericTypeIndicator<Map<String, List<Item>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, List<Item>>>() {
                            };
                            Map<String, List<Item>> hashMap = dataSnapshot.getValue(genericTypeIndicator);
                            int length = hashMap.size();

                            boolean add = true;
                            for (Map.Entry<String, List<Item>> entry : hashMap.entrySet()) {
                                if (temp.size() <= length) {
                                    Category category = new Category();
                                    category.setCategoryName(entry.getKey());
                                    List<Item> items = entry.getValue();
                                    temp.put(category, items);
                                    for (int i = 0; i < categories.size(); i++) {
                                        if (categories.get(i).getCategoryName().equals(category.getCategoryName())) {
                                            add = false;
                                        }
                                    }
                                    if (add)
                                        categories.add(category);
                                }


                            }
                            HashSet<Category> set = new HashSet<Category>(categories);
                            ArrayList<Category> result = new ArrayList<>(set);
                            Log.d("data", temp.toString());
                            Log.d("data2", result.toString());
                            adapter = new MainListAdapter(context, result, temp);
                            mainlist.setAdapter(adapter);
                            initColors(colors, temp);
                            mPro.setVisibility(View.INVISIBLE);

                            Log.d("example", colors.toString());
                        } catch (Exception r) {
                            r.printStackTrace();
                            adapter = new MainListAdapter(context, new ArrayList<Category>(), new LinkedHashMap<Category, List<Item>>());
                            mainlist.setAdapter(adapter);
                            categories = new ArrayList<>();
                            temp = new LinkedHashMap<>();
                            mPro.setVisibility(View.INVISIBLE);
                            shownoitems();
                            Toast.makeText(context, "no data found please create new", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        } catch(Exception e){
            e.printStackTrace();

            Toast.makeText(context, "no data found please create new", Toast.LENGTH_SHORT).show();
        }
    }else {
            myRef.child("users").child(user).child("lists").child("data").child(listname).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                         temp.clear();
                         categories.clear();

                        cancelshow();
                        GenericTypeIndicator<Map<String, List<Item>>> genericTypeIndicator = new GenericTypeIndicator<Map<String, List<Item>>>() {
                        };
                        Map<String, List<Item>> hashMap = dataSnapshot.getValue(genericTypeIndicator);
                        int length = hashMap.size();

                        boolean add = true;
                        for (Map.Entry<String, List<Item>> entry : hashMap.entrySet()) {
                            if (temp.size() <= length) {
                                Category category = new Category();
                                category.setCategoryName(entry.getKey());
                                List<Item> items = entry.getValue();
                                temp.put(category, items);
                                for (int i = 0; i < categories.size(); i++) {
                                    if (categories.get(i).getCategoryName().equals(category.getCategoryName())) {
                                        add = false;
                                    }
                                }
                                if (add)
                                    categories.add(category);
                            }


                        }
                        HashSet<Category> set = new HashSet<Category>(categories);
                        ArrayList<Category> result = new ArrayList<>(set);
                        Log.d("data", temp.toString());
                        Log.d("data2", result.toString());
                        adapter = new MainListAdapter(context, result, temp);
                        mainlist.setAdapter(adapter);
                        initColors(colors, temp);
                        mPro.setVisibility(View.INVISIBLE);

                        Log.d("example", colors.toString());
                    } catch (Exception r) {
                        r.printStackTrace();
                        adapter = new MainListAdapter(context, new ArrayList<Category>(), new LinkedHashMap<Category, List<Item>>());
                        mainlist.setAdapter(adapter);
                        categories = new ArrayList<>();
                        temp = new LinkedHashMap<>();
                        mPro.setVisibility(View.INVISIBLE);
                        shownoitems();
                        Toast.makeText(context, "no data found please create new", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
}


    public static void SaveInDB(String listName) {
        cancelshow();
        for (Map.Entry<Category, List<Item>> entry : temp.entrySet()) {
            Category category = entry.getKey();


            List<Item> items = new ArrayList<>();

            for (Item item : entry.getValue()) {
                items.add(item);


            }

             myRef.child("users").child(userID).child("lists").child("data").child(listName).child(category.getCategoryName()).setValue(items);
        }
    }


    public static void initColors(List<Integer> colors,HashMap<Category,List<Item>> data) {
        int i=0;
        for (Map.Entry<Category, List<Item>> entry : data.entrySet()) {
            Category category =  (Category)entry.getKey();
            category.setColor(colors.get(i));

            List<Item> items = entry.getValue();
            for(int j=0;j<items.size();j++){
                Item item=items.get(j);
                item.setColor(colors.get(i));
                items.set(j,item);
            }
            i++;
            data.put(category,items);
        }
    }
    public static void shownoitems() {
        def2.setVisibility(View.VISIBLE);
        DEF1.setVisibility(View.VISIBLE);
    }
    public static void cancelshow(){
        def2.setVisibility(View.INVISIBLE);
        DEF1.setVisibility(View.INVISIBLE);
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.list_fragment,
                container, false);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("clicked", "testing");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    Log.d("clicked", "open");
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    Log.d("clicked", "close");
                    drawer.openDrawer(GravityCompat.START);

                }
                break;
            case R.id.action_edit:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener(){
                                @Override
                                public void onAdClosed(){
                                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                }
                            });
                        } else {
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }   //Your code to run in GUI thread here
                    }//public void run() {
                });

                if (showdel) {
                    showdel = false;
                    menu.getItem(1).setIcon(getActivity().getResources().getDrawable(android.R.drawable.ic_menu_edit));
                    //hide
                    adapter.notifyDataSetChanged();
                } else {
                    menu.getItem(1).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_done_white_24dp));

                    showdel = true;
                    //show
                    adapter.notifyDataSetChanged();
                }
                Log.d("clicked", "close");
                break;

            case R.id.action_location_found:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                            mInterstitialAd.setAdListener(new AdListener(){
                                @Override
                                public void onAdClosed(){
                                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                    Intent place = new Intent(getActivity(), PlacePickerActivity.class);
                                    startActivity(place);
                                }
                            });
                        } else {
                            Intent place = new Intent(getActivity(), PlacePickerActivity.class);
                            startActivity(place);
                            Log.d("TAG", "The interstitial wasn't loaded yet.");
                        }   //Your code to run in GUI thread here
                    }//public void run() {
                });

                break;

            case R.id.menu_list:

                PopupMenu popup = new PopupMenu(getActivity(),getActivity().findViewById(R.id.menu_list));
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.list_popup, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.two){
                        String body =  utils.writeListToFile(temp);
                         Log.d("hashmap",body);

                            Intent intent2 = new Intent(); intent2.setAction(Intent.ACTION_SEND);
                            intent2.setType("text/plain");
                            intent2.putExtra(Intent.EXTRA_TEXT, body );
                            startActivity(Intent.createChooser(intent2, "Share via"));
                        }
                         if(item.getItemId()==R.id.one){
                             Fragment nextFrag= new CreateListFragment();
                             Bundle bundle= new Bundle();
                             bundle.putString("f","f");
                             bundle.putString("name",dest);
                             nextFrag.setArguments(bundle);
                             getActivity().getSupportFragmentManager().beginTransaction()
                                     .replace(android.R.id.content, nextFrag,"findThisFragment")
                                     .addToBackStack(null)
                                     .commit();
                         }
                        return true;
                    }
                });

                popup.show();//showing popup menu


                break;

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_drawer, menu);
        super.onCreateOptionsMenu(menu,inflater);
        MenuItem mn=menu.findItem(R.id.action_search);
        SearchView searchView =  new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
       // MenuItemCompat.setShowAsAction(mn, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView.setQueryHint(getResources().getString(R.string.search));
        MenuItemCompat.setActionView(mn, searchView);
      searchView.setOnQueryTextListener(this);
        this.menu = menu;
    }
 public static void updateItem(Item item){
    int i=0;
     for (Map.Entry<Category, List<Item>> entry : temp.entrySet()) {
         Category key = entry.getKey();
         if(key.getCategoryName().equals(item.getCategoryname())){
             List<Item> values = entry.getValue();
             for(Item item1: entry.getValue()){
                 if(item1.equals(item)){
                  values.set(i,item);
                 }
                 i++;
             }
         }
         i=0;
        // List<Item> values = entry.getValue();
         // ...
     }
     SaveInDB(dest);
    adapter.notifyDataSetChanged();
 }
    public static void removeItem(Item item){
        int i=0;
        for (Map.Entry<Category, List<Item>> entry : temp.entrySet()) {
            Category key = entry.getKey();
            if(key.getCategoryName().equals(item.getCategoryname())){
                Log.d("okbro","1");
                List<Item> values = entry.getValue();
                for(Item item1: entry.getValue()){
                    if(item1.equals(item)){
                       Log.d("okbro",values.get(i).getItemName());
                        values.remove(i);
                        temp.put(key,values);


                    }
                    i++;
                }
            }
            i=0;
            // List<Item> values = entry.getValue();
            // ...
        }

        adapter.notifyDataSetChanged();
        SaveInDB(dest);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filterData(query,temp,categories);

        expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filterData(query,temp,categories);

         expandAll();
        return false;
    }
    private void expandAll() {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++){
            mainlist.expandGroup(i);
        }
    }
 public static void noitemsfound(){
        no.setVisibility(View.VISIBLE);
  }
    public static void rmvnoitemsfound(){
        no.setVisibility(View.INVISIBLE);
    }
}
