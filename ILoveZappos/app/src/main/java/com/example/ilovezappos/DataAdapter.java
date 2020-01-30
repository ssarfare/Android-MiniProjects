package com.example.ilovezappos;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private List<OrderData> dataList;
    private String type;

    DataAdapter(List<OrderData> dataList,String type){
        this.dataList=dataList;
        this.type=type;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_data_row,parent,false);
        MyViewHolder viewHolder= new MyViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderData data=dataList.get(position);
        holder.amount.setText(String.valueOf(data.amount));
        DecimalFormat df = new DecimalFormat("#.########");
        holder.value.setText(String.format("%.8f",data.value));
        if(type.equals("Bids")){
            holder.value.setTextColor(Color.GREEN);
        }
        else
            holder.value.setTextColor(Color.RED);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView amount,value;
        public MyViewHolder(View v) {
            super(v);
            amount=v.findViewById(R.id.textView_amount);
            value=v.findViewById(R.id.textView_value);
        }
    }


}
