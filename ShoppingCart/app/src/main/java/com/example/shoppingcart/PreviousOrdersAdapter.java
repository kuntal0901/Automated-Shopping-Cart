package com.example.shoppingcart;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingcart.models.CartItem;
import com.example.shoppingcart.models.CartListViewItem;
import com.example.shoppingcart.models.OrdersItem;

import java.util.List;

public class PreviousOrdersAdapter extends RecyclerView.Adapter<PreviousOrdersAdapter.ViewHolder> {

    List<OrdersItem> itemList;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView orderIDTextView;
        private final TextView contentTextView;
        private final TextView prevPriceTextView;

        public TextView getOrderIDTextView() {
            return orderIDTextView;
        }

        public TextView getContentTextView() {
            return contentTextView;
        }

        public TextView getPrevPriceTextView() {
            return prevPriceTextView;
        }


        public ViewHolder(@NonNull View view) {
            super(view);
            orderIDTextView = view.findViewById(R.id.xeOrderID);
            contentTextView = view.findViewById(R.id.xeContent);
            prevPriceTextView = view.findViewById(R.id.xePrice);
        }



    }

    public PreviousOrdersAdapter(@NonNull List<OrdersItem> mPItemList) {
        this.itemList = mPItemList;

    }

    @NonNull
    @Override
    public PreviousOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_previous_order_view, parent, false);
        return new PreviousOrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviousOrdersAdapter.ViewHolder holder, int position) {
        OrdersItem i = itemList.get(position);
        holder.getOrderIDTextView().setText(i.id);
        holder.getContentTextView().setText(i.content);
        holder.getPrevPriceTextView().setText(i.total);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
