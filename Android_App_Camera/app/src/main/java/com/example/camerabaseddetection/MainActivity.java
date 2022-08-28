package com.example.camerabaseddetection;

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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.camerabaseddetection.ml.EfficentNetv2;
import com.example.camerabaseddetection.ml.Efficentnetv2Augumented98;
import com.example.camerabaseddetection.ml.NasnetMobileAugSan98;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {
    TextView result;
    ImageView imageView;
    Button picture;
    int imageSize = 224;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        picture = (Button) findViewById(R.id.button);
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
    }

    public void classifyImage(Bitmap image){
        try {
            EfficentNetv2 model = EfficentNetv2.newInstance(getApplicationContext());
            Efficentnetv2Augumented98 model1 = Efficentnetv2Augumented98.newInstance(getApplicationContext());
            NasnetMobileAugSan98 model2 = NasnetMobileAugSan98.newInstance(getApplicationContext());
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
            EfficentNetv2.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            Efficentnetv2Augumented98.Outputs outputs1 = model1.process(inputFeature0);
            TensorBuffer outputFeature1 = outputs1.getOutputFeature0AsTensorBuffer();
            NasnetMobileAugSan98.Outputs outputs2 = model2.process(inputFeature0);
            TensorBuffer outputFeature2 = outputs2.getOutputFeature0AsTensorBuffer();

            float[] data=outputFeature0.getFloatArray();
            float[] data1=outputFeature1.getFloatArray();
            float[] data2=outputFeature2.getFloatArray();
            double[] finaldata=new double[data.length];
            double[] weights= {0.35,0.33,0.32};
            String [] arr={"Apple","Banana","Bean","Beetroot","Bitter gourd","Bottle gourd","Brinjal","Broccoli","Cabbage","Capsicum","Carrot","Cauliflower","Cucumber","Custard apple","Dragonfruit","Fig","Garlic","Ginger","Grape","Guava","Jackfruit","Kiwi","Lemon","Mango","Mosambi","Muskmelon","Okra","Onion","Orange","Papaya","Pear","Peas","Pineapple","Pomegranate","Potato","Pumpkin","Radish","Sapodilla","Strawberry","Sweet potato","Tomato","Watermelon"};
            String [] Model_names={"EfficientNetV2 Without Augumentation","EfficientNetV2 With Augumentation","NasNet With Augumentaion"};
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
                if(data1[i]>maxval)
                {
                    maxval=data1[i];
                    class_model_2=i;
                }
            }

            maxval=-1000000.0;
            i=0;
            for(i=0;i<data2.length;i++)
            {
                if(data2[i]>maxval)
                {
                    maxval=data2[i];
                    class_model_3=i;
                }
            }


            for(i=0;i<data.length;i++)
            {
                finaldata[i]=(weights[0]*data[i])+(weights[1]*data1[i])+(weights[2]*data2[i]);
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
            model2.close();
            String res=Model_names[0]+" Gives Prediction: "+arr[class_model_1]+"\n"+Model_names[1]+" Gives Prediction: "+arr[class_model_2]+"\n"+Model_names[2]+" Gives Prediction: "+arr[class_model_3]+"\n"+"Combined Model Gives Prediction: "+arr[final_pred]+"\n";
            result.setText(res);
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
            classifyImage(image);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}