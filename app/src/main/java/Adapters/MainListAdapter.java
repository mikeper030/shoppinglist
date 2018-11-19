package Adapters;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.widget.BaseExpandableListAdapter;


import com.ultimatetoolsil.list.EditItemFragment;
import com.ultimatetoolsil.list.MainActivity;
import com.ultimatetoolsil.list.MainListFragment;
import com.ultimatetoolsil.list.R;
import models.Category;
import models.Item;


import static com.ultimatetoolsil.list.MainActivity.height1;
import static com.ultimatetoolsil.list.MainActivity.height2;
import static com.ultimatetoolsil.list.MainListFragment.dest;
import static com.ultimatetoolsil.list.MainListFragment.showdel;
import static com.ultimatetoolsil.list.MainListFragment.LoadDBData;
import static com.ultimatetoolsil.list.MainListFragment.SaveInDB;
import static com.ultimatetoolsil.list.MainListFragment.temp;

/**
 * Created by mike The best android programmer in the world on 27 Feb 2018.
 */

public class MainListAdapter extends BaseExpandableListAdapter  {
    private Context context;
    private LinkedHashMap<Category,List<Item>> originalList= new LinkedHashMap<>();
    private List<Category> expandableListTitle,filtered,tempcat;
    private LinkedHashMap<Category, List<Item>> expandableListDetail,filteredList,tempList;
    private double quant;
   // private boolean checked;

    public MainListAdapter(Context context, List<Category> expandableListTitle,
                                       LinkedHashMap<Category, List<Item>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
         originalList.putAll(expandableListDetail);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        Log.d("size1","ds"+expandableListDetail.size());
                Log.d("size2",String.valueOf(expandableListTitle.size()));
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Item expandedListitem = (Item) getChild(groupPosition, childPosition);

        Drawable drawable= ContextCompat.getDrawable(context,R.drawable.background_border);
        GradientDrawable gradientDrawable= (GradientDrawable) drawable;
        gradientDrawable.setStroke(5,expandedListitem.getColor());

        if (Build.VERSION.SDK_INT >= 19) {


            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item, null);

            }


            if (!isLastChild) {
                View divider = convertView.findViewById(R.id.linearfaq);
                divider.setVisibility(View.INVISIBLE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                int width = (MainActivity.display.getWidth());
                params.height = height2;
                params.width=width-40;
                convertView.setLayoutParams(params);
                View padder = convertView.findViewById(R.id.padder);
                padder.setVisibility(View.INVISIBLE);

            } else {
                View divider = convertView.findViewById(R.id.linearfaq);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                int width = (MainActivity.display.getWidth());
                params.height = height1;
                params.width=width-40;
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                convertView.setLayoutParams(params);
                divider.setVisibility(View.VISIBLE);
                View padder = convertView.findViewById(R.id.padder);
                padder.setBackgroundColor(expandedListitem.getColor());
                padder.setVisibility(View.VISIBLE);

            }
        }else{
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.list_item_low, null);

            }
        }
        String nam=expandedListitem.getItemName();

        final CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        boolean checked=expandedListitem.isChecked();
        if(checked){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    checkBox.setChecked(true);
                    expandedListitem.setChecked(true);
                    SaveInDB(dest);
                 }else{
                    checkBox.setChecked(false);
                    expandedListitem.setChecked(false);
                    SaveInDB(dest);
                }
            }
        });

        Button plus = (Button)convertView.findViewById(R.id.plus);
        Button minus = (Button)convertView.findViewById(R.id.minus);

        final TextView quantity = (TextView) convertView
                .findViewById(R.id.quantity);
        quant=expandedListitem.getQuantity();
        quantity.setText(String.valueOf(quant));

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quant=expandedListitem.getQuantity();
                quant+=1;

                expandedListitem.setQuantity(quant);
                quantity.setText(String.valueOf(quant));
                SaveInDB(dest);
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quant=expandedListitem.getQuantity();
                quant-=1;
                 if(quant>=0) {
                     expandedListitem.setQuantity(quant);
                     quantity.setText(String.valueOf(quant));
                     SaveInDB(dest);
                 }
               }
        });
        ImageButton del =(ImageButton) convertView.findViewById(R.id.del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               List<Item> items= new ArrayList<>(expandableListDetail.get(getGroup(groupPosition)));
               items.remove(childPosition);
                Category category = (Category) getGroup(groupPosition);
               expandableListDetail.put(category,items);
               if(items.size()==0){
                   //remove group header


                   expandableListDetail.remove(expandableListDetail.get(getGroup(groupPosition)));
                   expandableListTitle.remove((groupPosition));
               }

               if(expandableListTitle.size()==0){
                   LoadDBData(context,"null","me");
               }
               notifyDataSetChanged();
               SaveInDB(dest);
               Log.d("adapter",items.toString());
            }
        });
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity)view.getContext();
             //   android.app.Fragment fragment2= new EditItemFragment();
                EditItemFragment fragment2= new EditItemFragment();
                FragmentManager fragmentManager=activity.getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                Bundle args= new Bundle();
                args.putString("name",expandedListitem.getItemName());
                args.putDouble("quantity",expandedListitem.getQuantity());
                args.putString("price",String.valueOf(expandedListitem.getPrice()));
                args.putString("checked",String.valueOf(expandedListitem.isChecked()));
                args.putString("category",expandableListTitle.get(groupPosition).getCategoryName().toString());
                args.putString("unit",String.valueOf(expandedListitem.getUnittype()));
                args.putString("note",expandedListitem.getNote());
                args.putString("img",expandedListitem.getImglink());
                fragment2.setArguments(args);
                fragment2.setItemObject(expandedListitem);
                fragmentTransaction.replace(android.R.id.content,fragment2,"tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        if(showdel){
            del.setVisibility(View.VISIBLE);
        }else {
            del.setVisibility(View.INVISIBLE);
        }

        TextView name =(TextView) convertView.findViewById(R.id.list_text);
        name.setText(nam);
        convertView.setBackgroundDrawable(drawable);

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Log.d("dxd",String.valueOf(expandableListDetail.size()));


        Category category = (Category) getGroup(listPosition);
        Drawable drawable= ContextCompat.getDrawable(context,R.drawable.background_border);
        GradientDrawable gradientDrawable= (GradientDrawable) drawable;
        gradientDrawable.setStroke(5,category.getColor());
        String name=category.getCategoryName();
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.category, null);

        }
//        if (isExpanded)
//            convertView.setPadding(0, 0, 0, 0);
//        else
//            convertView.setPadding(0, 0, 0, 20);

        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.cat_text);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        Log.d("ds",String.valueOf(isExpanded));
        listTitleTextView.setText(name);
        convertView.setBackgroundDrawable(drawable);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }



    public void filterData(String query,LinkedHashMap<Category,List<Item>> original,List<Category> org){
       boolean add=true;
        query=query.toLowerCase();
        tempList=new LinkedHashMap<>();
        filteredList= new LinkedHashMap<>();
        tempcat= new ArrayList<>();
        filtered= new ArrayList<>();

        tempcat.addAll(expandableListTitle);
        tempList.putAll(expandableListDetail);
        expandableListTitle.clear();
        expandableListDetail.clear();
        if(query.isEmpty()){
            expandableListTitle= new ArrayList<>();

            expandableListTitle.addAll(org);
            expandableListDetail= new LinkedHashMap<>(originalList);
            MainListFragment.rmvnoitemsfound();
      //      Log.d("list",expandableListDetail.toString());
      //      Log.d("size",String.valueOf(expandableListDetail.size()+"a"+expandableListTitle.size()));

        }else{
            for (Map.Entry<Category,List<Item>> entry : tempList.entrySet()) {
               //iterate through vevery single entry in hashmap
                Category key = entry.getKey();
                List<Item> value = entry.getValue();

                //iterate through items of each category to find a match with query
                for (int i=0;i<value.size();i++){
                    if(value.get(i).getItemName().toLowerCase().contains(query)){
                        filteredList.put(key,value);
                        if(filtered.size()==0)
                            filtered.add(key);


                        for(int j=0;j<filtered.size();j++) {
                            if (filtered.get(j).getCategoryName().equals(key.getCategoryName())) {
                                add = false;
                            }
                        }
                        if (add) {
                            filtered.add(key);
                            add=true;
                        }

                    }
                }
                // ...
            }
         if(filteredList.size()>0){
             expandableListTitle.clear();
             expandableListDetail.clear();
           expandableListDetail.putAll(filteredList);
           expandableListTitle.addAll(filtered);
             MainListFragment.rmvnoitemsfound();
         }else {
             MainListFragment.noitemsfound();
         }
        }
      //  Log.d("size",String.valueOf(expandableListDetail.size()+"a"+expandableListTitle.size()));
         notifyDataSetChanged();

    }
}
