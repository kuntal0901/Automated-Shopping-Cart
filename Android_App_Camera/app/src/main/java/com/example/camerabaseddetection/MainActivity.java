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
import com.example.camerabaseddetection.ml.MobilenetV3;
import com.example.camerabaseddetection.ml.NasnetMobile;

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
            result.setText(str+"Class belonging to is "+arr[classes]);



            // Releases model resources if no longer used.
            model.close();
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