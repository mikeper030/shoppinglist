package com.ultimatetoolsil.list;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import Adapters.MainListAdapter;
import Adapters.ViewPagerAdapter;
import models.Category;
import models.Item;

import static com.ultimatetoolsil.list.MainListFragment.LoadDBData;
import static com.ultimatetoolsil.list.MainListFragment.dest;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener {

    public static FirebaseAuth auth;
    public static ListView drawerlist;
    private DrawerLayout drawer;
    public static String userID;
    public static int height1;
    public static int height2;
    FirebaseDatabase database;
    protected BottomNavigationView navigationView;
    public static HashSet<String> data2;
    MenuItem prevMenuItem;
    public static int ll;
    public static DatabaseReference myRef;
    public  static Display display;
    private ViewPager viewPager;
    static ArrayList<String> values = new ArrayList<>();;
    String username=null;
    private InterstitialAd mInterstitialAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {

            startActivity(new Intent(MainActivity.this, signups.class));
            finish();
        } else {
            auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            userID = user.getUid();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference();
            MobileAds.initialize(this,
                    "ca-app-pub-2883974575291426~6454080257");
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId("ca-app-pub-2883974575291426/2773608122");
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (prevMenuItem != null) {
                        prevMenuItem.setChecked(false);
                    } else {
                        navigationView.getMenu().getItem(0).setChecked(false);
                    }
                    Log.d("page", "onPageSelected: " + position);
                    navigationView.getMenu().getItem(position).setChecked(true);
                    prevMenuItem = navigationView.getMenu().getItem(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            setupViewPager(viewPager);
            drawerlist = (ListView) findViewById(R.id.drawer_list);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
             display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics;
            metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
             height1= getDPI(45, metrics);
             height2=getDPI(35,metrics);
            navigationView = (BottomNavigationView) findViewById(R.id.bottom_nav);
            navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_lists:
                            viewPager.setCurrentItem(0);
                            break;
                        case R.id.action_bills:
                            viewPager.setCurrentItem(1);
                            break;

                    }
                    return false;
                }
            });

          data2= new HashSet<>();

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ll = R.id.linearfaq;
            if (Build.VERSION.SDK_INT >= 21) {
                Window window = this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

            }
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            this.overridePendingTransition(R.anim.ltr,
                    R.anim.rtl);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            View parentView = findViewById(R.id.linLayout);
            View par=findViewById(R.id.btns);
            View ss=par.findViewById(R.id.button13);
            ss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey check out my app at: https://play.google.com/store/apps/details?id=org.ultimatetoolsil.list");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }
            });
            View setings=par.findViewById(R.id.button14);
            setings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                                mInterstitialAd.setAdListener(new AdListener(){
                                    @Override
                                    public void onAdClosed(){
                                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                        Intent i = new Intent(MainActivity.this,Settings.class);
                                        i.putExtra("username",username);
                                        Log.d("username",username);
                                        startActivity(i);
                                    }
                                });
                            } else {
                                Intent i = new Intent(MainActivity.this,Settings.class);
                                i.putExtra("username",username);
                                Log.d("username",username);
                                startActivity(i);
                                Log.d("TAG", "The interstitial wasn't loaded yet.");
                            }   //Your code to run in GUI thread here
                        }//public void run() {
                    });

                }
            });
            View viewById = parentView.findViewById(R.id.camera_ibtn);
            viewById.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Fragment nextFrag= new CreateListFragment();
                   getSupportFragmentManager().beginTransaction()
                            .replace(android.R.id.content, nextFrag,"findThisFragment")
                            .addToBackStack(null)
                            .commit();
                    drawer.closeDrawer(GravityCompat.START);


                }
            });
            View layout = findViewById(R.id.lly);
            View view = layout.findViewById(R.id.onee);
            getUsername();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addUserFragment newGamefragment = new addUserFragment();
                    Bundle bundle= new Bundle();
                    bundle.putString("direct",dest);
                    newGamefragment.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(android.R.id.content, newGamefragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    drawer.closeDrawer(GravityCompat.START);
                }
            });

        }

    }

    public static void LoadListNames(final Context context) {
        final LinkedHashMap<String, String> data = new LinkedHashMap<>();
        values = new ArrayList<>();

        myRef.child("users").child(userID).child("lists").child("data").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("dxxx1", child.getKey());
                    data.put("me", child.getKey());
                    values.add(child.getKey());
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
                drawerlist.setAdapter(adapter);

                     drawerlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                         @Override
                         public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                             final CharSequence[] items = { "Delete" };
                             TextView textView = (TextView) view.findViewById(android.R.id.text1);
                             final String name = textView.getText().toString();
                             AlertDialog.Builder builder = new AlertDialog.Builder(context);

                             builder.setTitle("delete list");
                             builder.setItems(items, new DialogInterface.OnClickListener() {

                                 public void onClick(DialogInterface dialog, int item) {

                                     new AlertDialog.Builder(context)
                                             .setTitle((R.string.delete))
                                            // .setMessage(getString(R.string.item_removed))
                                             .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                 public void onClick(DialogInterface dialog, int which) {
                                                 myRef.child("users").child(userID).child("lists").child("data").child(name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {

                                                         LoadListNames(context);
                                                     }
                                                 });

                                                 }
                                             })
                                             .show();

                                 }

                             });

                             AlertDialog alert = builder.create();

                             alert.show();
                             return false;
                         }
                     });
                    drawerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        String name = textView.getText().toString();

                        LoadDBData(context, name,"me");
                        MainListFragment.dest = name;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });



        myRef.child("users").child(userID).child("lists").child("shared").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                models.List list= new models.List();
                list.setName(dataSnapshot.getValue(models.List.class).getName());
                list.setUsers(dataSnapshot.getValue(models.List.class).getUsers());

               // Log.d("dxxx2", dataSnapshot.getValue(String.class));
                values.add(list.getName());
                data.put(list.getUsers().get(0), list.getName());
               // Log.d("dass",data.toString());

                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, android.R.id.text1, values);
                drawerlist.setAdapter(adapter);

                Log.d("dxxx3", values.toString());
                drawerlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        TextView textView = (TextView) view.findViewById(android.R.id.text1);
                        String name = textView.getText().toString();
                        Object[] keys = data.keySet().toArray();
                        String whos=keys[position].toString();
                        LoadDBData(context, name,whos);
                        MainListFragment.dest = name;
                    }
                });
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

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_drawer, menu);
//
//        return true;
//    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.button13) {

            // Handle the camera action
        } else if (id == R.id.button14) {


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment  fragmentHome = new MainListFragment();
       BillsLocatorFragment fragmentWallet = new BillsLocatorFragment();
        String fragmentTag = makeFragmentName(viewPager.getId(), 1);
        Log.d("fragment",fragmentTag);
        viewPagerAdapter.addFragment(fragmentHome);
        viewPagerAdapter.addFragment(fragmentWallet);
        viewPager.setAdapter(viewPagerAdapter);
    }
    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }

    public static int getDPI(int size, DisplayMetrics metrics){
        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;
    }
 public void getUsername() {
     try {


         myRef.child("users").child(userID).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 username = dataSnapshot.getValue(String.class);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }

         });

     } catch (Exception e) {
         e.printStackTrace();
     }
 }
}
