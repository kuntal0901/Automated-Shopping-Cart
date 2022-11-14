package com.example.shoppingcart;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WeightService extends Service {
    public static ArrayList <Float> li=new ArrayList<>();
    public static ArrayList <Float> shopping_cart_weights=new ArrayList<>();
    public static LinkedHashMap<LocalTime,Float> item_added=new LinkedHashMap<>();
    public static LinkedHashMap<LocalTime,Float> item_removed=new LinkedHashMap<>();
    public static LinkedHashMap<LocalTime,String> item_pred=new LinkedHashMap<>();
    public static LinkedHashMap<LocalTime,Float> diff_weight=new LinkedHashMap<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        new Thread(new Runnable(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    if(li.size()>3)
                    {
                        int size=li.size();
                        float first=shopping_cart_weights.get(0);
                        float prev=li.get(size-3)-first;
                        float mid=li.get(size-2)-first;
                        float last=li.get(size-1)-first;
//                        Log.i("Action","prev is "+String.valueOf(prev)+" mid is "+String.valueOf(mid)+" last is "+String.valueOf(last));
                        if(Math.abs(prev-mid)<50 && Math.abs(last-mid)<50)
                        {
//                            Log.i("Action","Inside when both satisidied");
//                            Log.i("Action","Last time is "+last_time.toString()+" Current is" +current.toString()+" Diff is"+String.valueOf(diff));
                            float cart_last_weights=shopping_cart_weights.get(shopping_cart_weights.size()-1)-first;
                            if(Math.abs(mid- cart_last_weights)>100)
                            {
                                if(mid-cart_last_weights>0)
                                {
                                    //Item Added
                                    Log.i("Action","Increase Detected in weights");
                                    LocalTime[]last_time_arr =item_pred.keySet().toArray(new LocalTime[item_pred.size()]);
                                    LocalTime last_time=last_time_arr[last_time_arr.length-1];
                                    LocalTime current=LocalTime.now();
                                    long diff=Duration.between(last_time,current).getSeconds();
                                    if(diff>=20)
                                    {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(WeightService.this,"Increase in weight detected Taking you to Scan to add new item",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(WeightService.this,ScanActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                            }
                                        });
                                    }
                                    diff_weight.put(LocalTime.now(),mid-cart_last_weights);
                                    shopping_cart_weights.add(mid);
                                    try {
                                        Thread.sleep(20000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else
                                {
                                    //Item Removed
                                    diff_weight.put(LocalTime.now(),mid-cart_last_weights);
                                    shopping_cart_weights.add(mid);
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(WeightService.this,"Decrease in weight detected Taking you to Cart Page to delete needed item",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(WeightService.this,CartActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }
                                    });
                                }
                            }
                            else
                            {
                                Log.i("Action","Stable weights nothing added");
                            }
                        }
                        else
                        {
                            Log.i("Action","Increase or decrease in progress");
                        }
                    }
                    else
                    {
                        Log.i("Action","Callibration In Progress");
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
