package com.example.shoppingcart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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

        list = new ArrayList<>();
        list.add(new Item("apple", 123.0f));
        list.add(new Item("orange", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        list.add(new Item("apple", 123.0f));
        myAdapter = new MyAdapter(list);
        recyclerView.setAdapter(myAdapter);

//        database.addValueEventListener(new ValueEventListener(){
//
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
//
//                    Item item = dataSnapshot.getValue(Item.class);
//                    list.add(item);
//
//                }
//                myAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


    }
}