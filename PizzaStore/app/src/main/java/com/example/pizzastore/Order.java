package com.example.pizzastore;

import java.io.Serializable;
import java.util.ArrayList;

public class Order implements Serializable {
    public double basePrice=6.50;
    public double deliveryCharges=0;
    public double costPerTopping=1.50;
    public ArrayList<String> toppingList;
    public double getTotalCost(){
        double totalPrice=basePrice;
        int countOfTopping=toppingList.size();
        totalPrice = totalPrice+countOfTopping*costPerTopping+deliveryCharges;
        return totalPrice;
    }

    public String getToppingList(){
        String commaSeperatedToppings="";
        for(String s:toppingList){
            commaSeperatedToppings=commaSeperatedToppings+s;
            commaSeperatedToppings=commaSeperatedToppings+",";
        }
        return commaSeperatedToppings;
    }
}
