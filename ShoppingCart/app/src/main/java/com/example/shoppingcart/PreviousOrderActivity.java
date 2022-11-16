package com.example.shoppingcart;
import com.example.shoppingcart.models.CartItem;
import com.example.shoppingcart.models.CartListViewItem;
import com.example.shoppingcart.models.OrdersItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PreviousOrderActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    PreviousOrdersAdapter adapter;
    ArrayList<Item> list;
    ArrayList<OrdersItem> ordersItems = new ArrayList<OrdersItem>();
    TextView orderIDTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_order);
        recyclerView = findViewById(R.id.previousOrders);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderIDTextView = findViewById(R.id.xeOrderID);
        adapter = new PreviousOrdersAdapter(ordersItems);
        recyclerView.setAdapter(adapter);
        FirebaseAuth mAuth;
        FirebaseUser mUser;
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        String userId = mUser.getDisplayName();
        HashMap<String, Float> itemPriceMap = new HashMap<String, Float>(); //name, price pairs
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("prevorder");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Log.d("s4",snapshot.toString() );
                    double price_kg = 0.0;
                    try {
                        // stored data is exactly a double
                        price_kg = (double) snapshot.getValue();
                    } catch (Exception ex) {
                        // stored data is exactly an integer
                        price_kg = (double) ((long) snapshot.getValue());
                    }
                    itemPriceMap.put(snapshot.getKey(), (float) price_kg);
//                    float price = ((Double) Objects.requireNonNull(snapshot.getValue())).floatValue();
//                    list.add(new Item(snapshot.getKey(), (float) price_kg));
                }

                database.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                            Map<String, String> ordersMap = (Map<String, String>) task.getResult().getValue();
                            assert ordersMap != null;
                            for (Map.Entry<String,String> entry : ordersMap.entrySet()) {
                                String c = getDescFromJSON(entry.getValue());
                                String t = getTotalFromJSON(entry.getValue(), itemPriceMap);
                                OrdersItem newItem = new OrdersItem(entry.getKey(), c, t);
                                ordersItems.add(newItem);
                                System.out.println("Key = " + entry.getKey() +
                                        ", Value = " + entry.getValue());
                            }
                            Log.d("items", String.valueOf(ordersItems));
                            adapter.notifyDataSetChanged();
                        }
                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }


        });

//        database.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    OrdersItem newItem = new OrdersItem(snapshot.getKey());
//                    ordersItems.add(newItem);
////                    Log.d("s4", snapshot.getKey());
////                    Log.d("s4", (String) snapshot.getValue());
//                    String z = snapshot.getKey();
//                    Log.d("s4", z);
//                    String stringToParse =  (String) snapshot.getValue();
////                    JSONParser parser = new JSONParser();
////                    JSONObject json = (JSONObject) parser.parse(stringToParse);
//                    Log.d("s4", (String) snapshot.getValue());
////                    for (Object value : snapshot.getValue(z)) {
////                        Log.d("s4", (String) value);
////                    }
//
//                    orderIDTextView.setText(z);
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



    }

    private String getDescFromJSON(String parseString) {

        Gson gson = new Gson();

        StringBuilder s = new StringBuilder();

        if (parseString.length() != 0) {
            Type type = new TypeToken<List<CartItem>>() {
            }.getType();
            Log.d("json", parseString);
            List<CartItem> cartItemList = gson.fromJson(parseString, type);
            for (CartItem x:cartItemList){
                s.append(x.name).append(" (").append(x.weight).append(") \n");
            }
        }
        return s.toString();
    }

    private String getTotalFromJSON(String parseString, Map<String, Float> priceMap) {

        Gson gson = new Gson();

        Float price = 0.0f;

        if (parseString.length() != 0) {
            Type type = new TypeToken<List<CartItem>>() {
            }.getType();
            Log.d("json", parseString);
            List<CartItem> cartItemList = gson.fromJson(parseString, type);
            for (CartItem x:cartItemList){
                price += x.weight*priceMap.get(x.name);
            }
        }
        return String.valueOf(price);
    }
}
