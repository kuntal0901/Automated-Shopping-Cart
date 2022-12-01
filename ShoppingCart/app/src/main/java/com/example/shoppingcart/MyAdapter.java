package com.example.shoppingcart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingcart.listeners.DeleteListener;
import com.example.shoppingcart.models.CartItem;
import com.example.shoppingcart.models.CartListViewItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<CartListViewItem> itemList;
    Context context;
    List<CartItem> mPCartItems;
    View.OnClickListener deleteListener;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemTitleTextView;
        private final TextView itemPriceTextView;
        private final TextView itemWeightTextView;
        private final TextView itemSubTotalTextView;
//        private final ImageView bookCoverImageView;
        private final Button itemDelete;

        public TextView getItemTitleTextView() {
            return itemTitleTextView;
        }
        public TextView getItemPriceTextView() {
            return itemPriceTextView;
        }
        public TextView getItemWeightTextView() {
            return itemWeightTextView;
        }
        public TextView getItemSubTotalTextView() {
            return itemSubTotalTextView;
        }
        public Button getItemDelete(){
            return itemDelete;
        }

        public ViewHolder(View view) {
            super(view);
            itemWeightTextView = view.findViewById(R.id.mdQuantity);
            itemTitleTextView = view.findViewById(R.id.mdName);
            itemPriceTextView = view.findViewById(R.id.mdPrice);
            itemSubTotalTextView = view.findViewById(R.id.mdSubTotal);
            itemDelete = view.findViewById(R.id.mdDeleteButton);
        }
    }

    public MyAdapter(@NonNull List<CartListViewItem> mPItemList, List<CartItem> mpCartListItems, View.OnClickListener deleteListener) {
        this.itemList = mPItemList;
        this.mPCartItems = mpCartListItems;
        this.deleteListener = deleteListener;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartListViewItem i = itemList.get(position);
        DeleteListener dl = new DeleteListener(new View.OnClickListener(){

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                itemList.remove(i);
                mPCartItems.removeIf(item -> Objects.equals(item.name, i.name));
                Gson gson = new Gson();
                SharedPreferences sharedPref = context.getApplicationContext().getSharedPreferences(ScanActivity.PREFS_TAG, Context.MODE_PRIVATE);
                Log.d("json", mPCartItems.toString());
                Log.d("json", gson.toJson(mPCartItems));
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(ScanActivity.PRODUCT_TAG, gson.toJson(mPCartItems));
                editor.apply();
                notifyDataSetChanged();
            }
        }, this.deleteListener);
        holder.getItemTitleTextView().setText(i.name);
        holder.getItemPriceTextView().setText(i.getPrice());
        holder.getItemWeightTextView().setText(i.getQuantity());
        holder.getItemSubTotalTextView().setText(i.getTotalPrice());
        holder.getItemDelete().setOnClickListener(dl);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}