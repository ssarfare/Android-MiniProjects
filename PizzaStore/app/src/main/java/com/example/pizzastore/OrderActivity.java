package com.example.pizzastore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrderActivity extends AppCompatActivity {
    TextView basePrice,toppingsTotal,toppingsList,deliveryCost,totalCost;
    Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_order);
            setTitle("Order Details");
            basePrice=findViewById(R.id.tv_basePriceValue);
            toppingsTotal=findViewById(R.id.tv_OrderDetails_TotalToppingsValue);
            toppingsList=findViewById(R.id.tv_toppingList);
            toppingsList.setGravity(Gravity.CENTER);
            deliveryCost=findViewById(R.id.tv_OrderDetails_DeliveryCosValue);
            totalCost=findViewById(R.id.tv_totalCost);
            finishButton=findViewById(R.id.button_finish);
            if(getIntent()!=null && getIntent().getExtras().getSerializable(MainActivity.key)!=null){
                Order o=(Order) getIntent().getExtras().getSerializable(MainActivity.key);
                basePrice.setText(o.basePrice+"$");
                toppingsTotal.setText((o.toppingList.size()*1.5)+"$");
                toppingsList.setText(o.getToppingList());
                deliveryCost.setText(o.deliveryCharges+"$");
                totalCost.setText(o.getTotalCost()+"$");
            }
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(OrderActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
            });

        }catch(Exception e){
            Toast.makeText(OrderActivity.this,"oops something went wrong!",Toast.LENGTH_SHORT).show();
            Log.e("Exception", "onCreate: ", e);
        }




    }
}
