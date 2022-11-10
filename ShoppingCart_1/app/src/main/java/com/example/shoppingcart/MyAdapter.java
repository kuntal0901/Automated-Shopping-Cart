package com.example.shoppingcart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingcart.models.CartListViewItem;

import java.util.ArrayList;
import java.util.List;

//public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
//
//    Context context;
//
//    ArrayList<Item> list;
//
//    public MyAdapter(Context context, ArrayList<Item> list) {
//        this.context = context;
//        this.list = list;
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
//        return new MyViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
//        Item item = list.get(position);
//        holder.name.setText(item.getName());
//        holder.price.setText((int) item.getPrice());
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//        TextView name, price;
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            name = itemView.findViewById(R.id.name);
//            price= itemView.findViewById(R.id.price);
//
//        }
//    }
//}


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<CartListViewItem> itemList;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemTitleTextView;
        private final TextView itemPriceTextView;
        private final TextView itemWeightTextView;
        private final TextView itemSubTotalTextView;
//        private final ImageView bookCoverImageView;

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


        public ViewHolder(View view) {
            super(view);
            itemWeightTextView = view.findViewById(R.id.mdQuantity);
            itemTitleTextView = view.findViewById(R.id.mdName);
            itemPriceTextView = view.findViewById(R.id.mdPrice);
            itemSubTotalTextView = view.findViewById(R.id.mdSubTotal);
        }


    }

    public MyAdapter(@NonNull List<CartListViewItem> mPItemList) {
        this.itemList = mPItemList;
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
        CartListViewItem item = itemList.get(position);

        holder.getItemTitleTextView().setText(item.name);
        holder.getItemPriceTextView().setText(item.getPrice());
        holder.getItemWeightTextView().setText(item.getQuantity());
        holder.getItemWeightTextView().setText(item.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}