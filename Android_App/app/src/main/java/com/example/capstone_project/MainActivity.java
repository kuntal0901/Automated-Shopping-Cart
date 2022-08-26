package com.example.capstone_project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.capstone_project.ml.EfficentNetv2;
import com.example.capstone_project.ml.Efficentnetv21;
import com.example.capstone_project.ml.MobilenetV3;
import com.example.capstone_project.ml.NasnetMobile;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button select,predict;
    private TextView tv;
    private Bitmap img;
    int imageSize = 224;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView=(ImageView) findViewById(R.id.imageView);
        select=(Button) findViewById(R.id.button);
        predict=(Button) findViewById(R.id.button2);
        tv=(TextView) findViewById(R.id.textView);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,100);
            }
        });

        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap image;
                image = Bitmap.createScaledBitmap(img, imageSize, imageSize, false);


                    try {
                        EfficentNetv2 model = EfficentNetv2.newInstance(getApplicationContext());
                        MobilenetV3 model1 = MobilenetV3.newInstance(getApplicationContext());
                        NasnetMobile model2 = NasnetMobile.newInstance(getApplicationContext());
                        // Creates inputs for reference.
                        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//            TensorBuffer inputFeature1 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
//            TensorBuffer inputFeature2 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
                        byteBuffer.order(ByteOrder.nativeOrder());

                        // get 1D array of 224 * 224 pixels in image
                        int [] intValues = new int[imageSize * imageSize];
                        image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());

                        // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
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
//            inputFeature1.loadBuffer(byteBuffer);
//            inputFeature2.loadBuffer(byteBuffer);

                        // Runs model inference and gets result.
                        EfficentNetv2.Outputs outputs = model.process(inputFeature0);
                        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                        MobilenetV3.Outputs outputs1 = model1.process(inputFeature0);
                        TensorBuffer outputFeature1 = outputs1.getOutputFeature0AsTensorBuffer();
                        NasnetMobile.Outputs outputs2 = model2.process(inputFeature0);
                        TensorBuffer outputFeature2 = outputs2.getOutputFeature0AsTensorBuffer();

                        float[] data=outputFeature0.getFloatArray();
                        float[] data1=outputFeature1.getFloatArray();
                        float[] data2=outputFeature2.getFloatArray();
                        double[] finaldata=new double[data.length];
//            float weights[3]={0.5,0.3,0.2};
                        int i=0;
                        String [] arr={"Apple","Bean","Beetroot","Bitter_Gourd","Bottle_Gourd","Brinjal","Broccoli","Cabbage","Capsicum","Carrot","Cauliflower","Cucumber","DragonFruit","Garlic","Ginger","Guava","Kiwi","Mosambi","Muskmelon","Okra","Papaya","Pineapple","Pomegranate","Potato","Pumpkin","Radish","Sapodilla","Sweet potato","Tomato","banana","custard_apple","fig","grape","jackfruit","lemon","mango","onion","orange","pear","peas","strawberry","watermelon"};
                        for(i=0;i<data.length;i++)
                        {
                            finaldata[i]=(0.5*data[i])+(0.3*data1[i])+(0.2*data2[i]);
                        }
                        i=0;
                        double maxval=-1000000.0;
                        int classes=-1;
                        for(i=0;i<finaldata.length;i++){
                            if(finaldata[i]>maxval)
                            {
                                maxval=finaldata[i];
                                classes=i;
                            }
                        }
                        String str="Model1 "+data[0]+" Model2 "+data1[0]+" Model3 "+data2[0];
//            result.setText("Class belonging to is "+arr[classes]);
                        tv.setText(str+"Class belonging to is "+arr[classes]);



                        // Releases model resources if no longer used.
                        model.close();

                    } catch (IOException e) {
                        // TODO Handle the exception
                    }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==100)
        {
            imageView.setImageURI(data.getData());
            Uri uri=data.getData();
            try {
                img= MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}