package com.example.shoppingcart.listeners;

import android.view.View;

public class DeleteListener implements View.OnClickListener {

    private View.OnClickListener mFirstListener ;
    private View.OnClickListener mSecondListener ;
    public DeleteListener(View.OnClickListener mFirstListener, View.OnClickListener mSecondListener){
        this.mFirstListener = mFirstListener;
        this.mSecondListener = mSecondListener;
    }
    public void onClick(View v){
        this.mFirstListener.onClick(v);
        this.mSecondListener.onClick(v);
    }
}
