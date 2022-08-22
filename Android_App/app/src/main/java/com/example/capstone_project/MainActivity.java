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

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button select,predict;
    private TextView tv;
    private Bitmap img;
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
                img=Bitmap.createScaledBitmap(img,224,224,true);

                try {
                    EfficentNetv2 model = EfficentNetv2.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorImage tensorImage=new TensorImage(DataType.FLOAT32);
                    tensorImage.load(img);
                    ByteBuffer byteBuffer=tensorImage.getBuffer();

                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    EfficentNetv2.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();


                    // Releases model resources if no longer used.
                    model.close();
                    float[] data=outputFeature0.getFloatArray();
                    int i=0;
                    double maxval=-1000000.0;
                    int classes=-1;
                    for(i=0;i<data.length;i++){
                        if(data[i]>maxval)
                        {
                            maxval=data[i];
                            classes=i;
                        }
                    }
                    tv.setText("Length of Data is"+data.length+"\nMax Val Obtained is "+maxval+"\nClass belonging to is "+classes);
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