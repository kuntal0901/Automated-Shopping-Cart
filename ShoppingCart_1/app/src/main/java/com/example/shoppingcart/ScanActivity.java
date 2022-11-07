//package com.example.shoppingcart;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//
//public class ScanActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_scan);
//    }
//}

package com.example.shoppingcart;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shoppingcart.ml.Efficentnetv2Augumented;
import com.example.shoppingcart.ml.MobilenetAugumented;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.widget.Toast;

public class ScanActivity extends AppCompatActivity {
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ScanActivity.this,HomeActivity.class));
    }

    ArrayList <Float> arrli1=new ArrayList<>(1);
    ConnecttoCartActivity ct=new ConnecttoCartActivity();
    TextView result;
    ImageView imageView;
    Button picture,back;
    int imageSize = 224;
    ProgressDialog pg;
    int counter=0;

    public static float min(float[] arr)
    {
        int size=arr.length;
        int i=0;
        float minval=10000000;
        for (i=0;i<size;i++)
        {
            if(arr[i]<minval)
            {
                minval=arr[i];
            }
        }
        return minval;
    }

    public static float max(float[] arr)
    {
        int size=arr.length;
        int i=0;
        float maxval=-100000000;
        for (i=0;i<size;i++)
        {
            if(arr[i]>maxval)
            {
                maxval=arr[i];
            }
        }
        return maxval;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        ConnecttoCartActivity ct=new ConnecttoCartActivity();
        Log.i("Action","Scan Activity: On Create Inside here "+ct.connected);
//        if(ct.connected==false){
//            Toast.makeText(ScanActivity.this,"Moving to Connect to cart Page because you are not connected to cart",Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(ScanActivity.this,ConnecttoCartActivity.class));
//        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        arrli1.add(0.0f);
        pg=new ProgressDialog(this);
        result = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        picture = (Button) findViewById(R.id.button);
        back=(Button)findViewById(R.id.back);
        picture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                // Launch camera if we have permission

                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 1);
                } else {
                    //Request camera permission if we don't have it.
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScanActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

    }



    public void classifyImage(Bitmap image){
        try {
            Log.i("Action","Scan Activity: Inside classify Image");
            pg.setTitle("Model Prediction");
            pg.setMessage("Predicting .............");
            Efficentnetv2Augumented model =  Efficentnetv2Augumented.newInstance(getApplicationContext());
            MobilenetAugumented model1 = MobilenetAugumented.newInstance(getApplicationContext());
//            NasnetNonAugumented model2 = NasnetNonAugumented.newInstance(getApplicationContext());
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int [] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            for(int i = 0; i < imageSize; i++){
                for(int j = 0; j < imageSize; j++){
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Efficentnetv2Augumented.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            MobilenetAugumented.Outputs outputs1 = model1.process(inputFeature0);
            TensorBuffer outputFeature1 = outputs1.getOutputFeature0AsTensorBuffer();
//            NasnetNonAugumented.Outputs outputs2 = model2.process(inputFeature0);
//            TensorBuffer outputFeature2 = outputs2.getOutputFeature0AsTensorBuffer();

            float[] data=outputFeature0.getFloatArray();
            float[] data1=outputFeature1.getFloatArray();
            Log.i("Action","Prediction Done");
//            float[] data2=outputFeature2.getFloatArray();
            double[] finaldata=new double[data.length];
            double[] weights= {0.50,0.50};
            String [] arr={"Baby Potato", "Banana", "Beetroot", "Bittergourd", "Black brinjal", "Broad Beans", "Brown Coconut", "Cabbage", "CauliFlower", "Custard apple", "Dragonfruit", "Drumstick", "French Beans", "Garlic","Ginger","Green Apple", "Green Capscium", "Green Chilli", "Green Grapes", "Green Pumpkin", "Green Zuchini", "Guava", "JackFruit", "Kiran Watermelon", "Kiwi", "Lemon", "Mango", "Mango Raw","Muskmelon", "Okra", "Onion", "Ooty Carrot", "Orange", "Orange Carrot", "Papaya", "Pear", "Pineapple", "Pointed gourd", "Potato", "Radish", "Red Apple", "Red Capscium", "Ridge gourd", "Strawberry","Sweet Potato", "Sweet lime", "Tomato", "Yam", "Yellaki Banana", "Yellow Capscium", "Yellow Zuchini"};
//            String [] Model_names={"EfficientNetV2 With Augumentation","MobileNet V2 with Augumentation","NasnetNonAugumented"};
            String [] Model_names={"EfficientNetV2 With Augumentation","MobileNet V2 with Augumentation"};
            int i;
            double maxval;
            maxval=-1000000.0;
            i=0;
            int class_model_1 = 0;
            int class_model_2=0;
            int class_model_3=0;
            int final_pred=0;
            for(i=0;i<data.length;i++)
            {
                data[i]=2*(data[i]-min(data))/(max(data)-min(data))-1;
                if(data[i]>maxval)
                {
                    maxval=data[i];
                    class_model_1=i;
                }
            }

            maxval=-1000000.0;
            i=0;
            for(i=0;i<data1.length;i++)
            {
                data1[i]=2*(data1[i]-min(data1))/(max(data1)-min(data1))-1;
                if(data1[i]>maxval)
                {
                    maxval=data1[i];
                    class_model_2=i;
                }
            }

//            maxval=-1000000.0;
//            i=0;
//            for(i=0;i<data2.length;i++)
//            {
//                data2[i]=2*(data2[i]-min(data2))/(max(data2)-min(data2))-1;
//                if(data2[i]>maxval)
//                {
//                    maxval=data2[i];
//                    class_model_3=i;
//                }
//            }


            for(i=0;i<data.length;i++)
            {
//                finaldata[i]=(weights[0]*data[i])+(weights[1]*data1[i])+(weights[2]*data2[i]);
                finaldata[i]=(weights[0]*data[i])+(weights[1]*data1[i]);
            }


            maxval=-1000000.0;
            i=0;
            for(i=0;i<data.length;i++)
            {
                if(finaldata[i]>maxval)
                {
                    maxval=finaldata[i];
                    final_pred=i;
                }
            }



            // Releases model resources if no longer used.
            model.close();
            model1.close();
            pg.dismiss();
            Log.i("Action","Model closed");
//            model2.close();
//            String res=Model_names[0]+" Gives Prediction: "+arr[class_model_1]+"\n"+Model_names[1]+" Gives Prediction: "+arr[class_model_2]+"\n"+Model_names[2]+" Gives Prediction: "+arr[class_model_3]+"\n"+"Combined Model Gives Prediction: "+arr[final_pred]+"\n";

            String res=Model_names[0]+" Gives Prediction: "+arr[class_model_1]+"\n"+Model_names[1]+" Gives Prediction: "+arr[class_model_2]+"\n";
            Log.i("Action",res);

            result.setText(res);

//            pg.setTitle("Detecting Weight");
//            pg.setMessage("Waiting to detect an change in weight of atleast 150gms");
//
////            if(counter==0 && )
////            {
////
////            }
//            ct.beginListenForData();
//            while((ct.return_last_val()-arrli1.get(arrli1.size()-1))<150)
//            {
//
//            }
//            arrli1.add(ct.return_last_val());
//            pg.dismiss();
//            res=Model_names[0]+" Gives Prediction: "+arr[class_model_1]+"\n"+Model_names[1]+" Gives Prediction: "+arr[class_model_2]+"\n"+"Combined Model Gives Prediction: "+arr[final_pred]+"\n"+"Weight as received is "+(ct.return_last_val()-arrli1.get(arrli1.size()-1))+"\n";
//            result.setText(res);
////            (ct.return_last_val()-arrli1.get(arrli1.size()-1))>=150
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min(image.getWidth(), image.getHeight());
            image = ThumbnailUtils.extractThumbnail(image, dimension, dimension);
            imageView.setImageBitmap(image);

            image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
            Log.i("Action","Image stored");
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}