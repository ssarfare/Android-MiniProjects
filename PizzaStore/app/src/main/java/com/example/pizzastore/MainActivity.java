package com.example.pizzastore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static HashMap<String,Integer> TOPPINGS;
    static  String key="Order_Details";
    LinearLayout ll_toppings,layout1,layout2;
    Button btn_addTopping,btn_clearPizza,btn_checkout;
    static int layout1Id=View.generateViewId(),layout2Id=View.generateViewId();
    CheckBox ck_delivery;
    ProgressBar pgbar;
    HashMap<Integer,ArrayList<String>> toppingList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);

            TOPPINGS= new HashMap<String,Integer>();
            TOPPINGS.putIfAbsent("Bacon",R.drawable.bacon);
            TOPPINGS.putIfAbsent("Cheese",R.drawable.cheese);
            TOPPINGS.putIfAbsent("Garlic",R.drawable.garlic);
            TOPPINGS.putIfAbsent("Green Pepper",R.drawable.green_pepper);
            TOPPINGS.putIfAbsent("Mushroom",R.drawable.mashroom);
            TOPPINGS.putIfAbsent("Olive",R.drawable.olive);
            TOPPINGS.putIfAbsent("Onion",R.drawable.onion);
            TOPPINGS.putIfAbsent("Red Pepper",R.drawable.red_pepper);
            setContentView(R.layout.activity_main);
            btn_addTopping=findViewById(R.id.button_addToppings);
            btn_clearPizza=findViewById(R.id.button_clearPizza);
            btn_checkout=findViewById(R.id.button_checkout);
            ll_toppings= findViewById(R.id.linearLayout_Toppings);
            layout1=new LinearLayout(MainActivity.this);
            layout1.setId(layout1Id);
            layout2=new LinearLayout(MainActivity.this);
            layout2.setId(layout2Id);
            layout1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            layout1.setGravity(Gravity.CENTER);
            layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
            layout2.setGravity(Gravity.CENTER);
            ll_toppings.addView(layout1);
            ll_toppings.addView(layout2);
            pgbar=findViewById(R.id.progressBar_toppings);
            pgbar.setProgress(0);
            final String []keys=TOPPINGS.keySet().toArray(new String[0]);
            toppingList=new HashMap<Integer, ArrayList<String>>() ;
            final AlertDialog.Builder  builder= new AlertDialog.Builder(this);

            builder.setItems(keys, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ImageView iv= new ImageView(MainActivity.this);

                    int itemId=TOPPINGS.get(keys[i]);

                    int count=0;
                    for(Integer a :toppingList.keySet()){
                        ArrayList<String> o = toppingList.get(a);
                        count=count+o.size();
                    }
                    if(toppingList!=null && count<10)
                        if(!toppingList.containsKey(itemId)){
                            ArrayList<String> list=new ArrayList<String>();
                            list.add(keys[i]);
                            toppingList.put(itemId,list);
                        }
                        else{
                            ArrayList<String> o = toppingList.get(itemId);
                            o.add(keys[i]);
                        }


                    iv.setImageDrawable(getDrawable(itemId));
                    iv.setLayoutParams(new ViewGroup.LayoutParams(150,150));
                    iv.setOnClickListener(MainActivity.this);
                    iv.setId(View.generateViewId());
                    iv.setTag(itemId);
                    int row1Count=0,row2Count=0,col;
                    row1Count=layout1.getChildCount();
                    row2Count=layout2.getChildCount();

                    if(row1Count<5) {
                        layout1.addView(iv);

                    }
                    else if(row2Count<5){
                        layout2.addView(iv);
                    }
                    else
                        Toast.makeText(MainActivity.this,"Maximum Topping Capacity Reached!",Toast.LENGTH_SHORT).show();

                    UpdateProgressBar();

                }
            });


            final AlertDialog toppingItemAlert= builder.create();
            btn_addTopping.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toppingItemAlert.show();
                }
            });
            btn_clearPizza.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LinearLayout parentLayout1=(LinearLayout)findViewById(layout1Id);
                    LinearLayout parentLayout2 = (LinearLayout) findViewById(layout2Id);

                    parentLayout1.removeAllViews();
                    parentLayout2.removeAllViews();
                    toppingList.clear();
                    UpdateProgressBar();
                }
            });

            btn_checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //pass data and call new activity using intent

                    if(toppingList.size()>0){
                        Order order= new Order();
                        order.toppingList=new ArrayList<String>();
                        ck_delivery= findViewById(R.id.checkBox_delivery);
                        if(ck_delivery.isChecked())
                            order.deliveryCharges=4.0;

                        for(Integer i :toppingList.keySet()){
                            ArrayList<String> o = toppingList.get(i);
                            order.toppingList.addAll(o);
                        }
                        Intent i= new Intent(MainActivity.this,OrderActivity.class);
                        i.putExtra(key,order);
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(MainActivity.this,"Please add at least one topping",Toast.LENGTH_SHORT).show();
                    }


                }
            });


        }catch (Exception e){
            Toast.makeText(MainActivity.this,"oops something went wrong!",Toast.LENGTH_SHORT).show();
            Log.e("Exception", "onCreate: ", e);
        }




    }


    @Override
    public void onClick(View view) {
        try{
            ViewParent parent= view.getParent();
            View parentView=(View)parent;
            int parentId=parentView.getId();

            LinearLayout parentLayout1=(LinearLayout)findViewById(layout1Id);
            LinearLayout parentLayout2 = (LinearLayout) findViewById(layout2Id);
            if(parentId==layout1Id) {
                parentLayout1.removeView(view);
                View child = parentLayout2.getChildAt(0);
                if ( child!= null) { parentLayout2.removeView(child);
                    parentLayout1.addView(child);
                }
            }
            if(parentId==layout2Id){
                parentLayout2.removeView(view);
            }
            String []keys=TOPPINGS.keySet().toArray(new String[0]);
            if(toppingList.containsKey(view.getTag())){
                ArrayList<String> o=toppingList.get(view.getTag());
                if(o.size()>0)
                    o.remove(0);
            }

            //toppingList.remove(view.getId());
            UpdateProgressBar();
        }catch(Exception e){
            Toast.makeText(MainActivity.this,"oops something went wrong",Toast.LENGTH_SHORT).show();
            Log.e("Exception", "onClick: ",e );
        }

    }

    public void UpdateProgressBar(){
        try{
            int totalToppingsCount=0;
            LinearLayout l1=findViewById(layout1Id);
            LinearLayout l2=findViewById(layout2Id);
            totalToppingsCount=l1.getChildCount()+l2.getChildCount();
            ProgressBar pg=findViewById(R.id.progressBar_toppings);
            pg.setProgress(totalToppingsCount*10);
        }
        catch(Exception e){
            Toast.makeText(MainActivity.this,"oops something went wrong!",Toast.LENGTH_SHORT).show();
            Log.e("Exception", "UpdateProgressBar: ",e );
        }

    }


}
