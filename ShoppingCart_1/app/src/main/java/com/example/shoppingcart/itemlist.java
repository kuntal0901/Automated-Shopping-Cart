package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class itemlist extends AppCompatActivity {

    RecyclerView recyclerView;
    //    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<Item> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemlist);
        recyclerView = findViewById(R.id.itemList);
//        database = FirebaseDatabase.getInstance().getReference("Items");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> items = new ArrayList<>();
        list = new ArrayList<>();;

        myAdapter = new MyAdapter(list);
        recyclerView.setAdapter(myAdapter);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("items");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                list.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    Log.d("s4",snapshot.toString() );
                    double price_kg = 0.0;
                    try{
                        // stored data is exactly a double
                        price_kg = (double)snapshot.getValue();
                    } catch (Exception ex) {
                        // stored data is exactly an integer
                        price_kg = (double)((long)snapshot.getValue());
                    }

//                    float price = ((Double) Objects.requireNonNull(snapshot.getValue())).floatValue();
                    list.add(new Item(snapshot.getKey(), (float) price_kg));
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}