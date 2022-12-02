package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shoppingcart.models.CartItem;
import com.example.shoppingcart.models.CartListViewItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class itemlist extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView finalPriceTextView;
    public static MyAdapter myAdapter;
    ArrayList<Item> list;
    public static ArrayList<CartItem> cartItemList = new ArrayList<CartItem>();
    public static ArrayList<CartListViewItem> cartListViewItems = new ArrayList<CartListViewItem>();
    HashMap<String, Float> itemPriceMap = new HashMap<String, Float>(); //name, price pairs
    final float[] total = {0.0f};
    View.OnClickListener deleteOnClickListener;
    static boolean deleteable=false;
    private void getCartFromSharedPreferences(){
        Gson gson = new Gson();
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(ScanActivity.PREFS_TAG, Context.MODE_PRIVATE);
        String jsonSaved = sharedPref.getString(ScanActivity.PRODUCT_TAG, "");
//        Log.i("Action",jsonSaved);
//        Log.i("Temp Json","Len is"+String.valueOf(jsonSaved.length()));
//        Log.i("Temp Json","Json saved is "+jsonSaved);
        if (jsonSaved.length() != 0) {
            Type type = new TypeToken<List<CartItem>>() {
            }.getType();
//            Log.d("Temp json", jsonSaved);
            cartItemList = gson.fromJson(jsonSaved, type);
//            Log.d("Temp json", cartItemList.toString());
        }
    }

    private void refreshTotal() {
        total[0] = 0;
        for (CartItem i : cartItemList) {
            if (itemPriceMap.containsKey(i.name)) {

                total[0] += i.weight * itemPriceMap.get(i.name);
            }
        }
        Log.d("cart local", "printed");
        Log.d("total", String.valueOf(total[0]));
        finalPriceTextView.setText("Total: " + total[0]);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("logging", "logged");
        setContentView(R.layout.activity_itemlist);
        Bundle data_passed=getIntent().getExtras();
        if(data_passed!=null)
        {
            deleteable=true;
        }
        getCartFromSharedPreferences();
        recyclerView = findViewById(R.id.itemList);
        finalPriceTextView = findViewById(R.id.cartListTotal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        Log.d("Temp Json","cartListViewItems is "+cartListViewItems.toString());
        Log.d("Temp Json","cartItemList is "+cartItemList.toString());

        myAdapter = new MyAdapter(cartListViewItems, cartItemList, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshTotal();
//                MyAdapter.notifyDataSetChanged();

            }
        });
        myAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(myAdapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");
        Button paymentButton = (Button) findViewById(R.id.payButton);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PaymentActivity.class));

            }


        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
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
                cartListViewItems=new ArrayList<>();
                for (CartItem i : cartItemList) {
                    if (itemPriceMap.containsKey(i.name)) {
                        CartListViewItem newItem = new CartListViewItem(i.name, itemPriceMap.get(i.name), i.weight);
                        Log.i("Bomb","This is getting added");
                        cartListViewItems.add(newItem);
                        total[0] += i.weight * itemPriceMap.get(i.name);


                    }
                }

                Log.d("cart local", "printed");
                Log.d("total", String.valueOf(total[0]));
                finalPriceTextView.setText("Total: " + total[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void delete_which_to()
    {
        float weight_remove=Math.abs(WeightService.removed_weight);

    }

}