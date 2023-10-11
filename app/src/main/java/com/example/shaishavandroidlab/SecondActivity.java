package com.example.shaishavandroidlab;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        TextView text = findViewById(R.id.screen1Message);
        EditText Number = findViewById(R.id.edittextnumber);
        Button callNumber = findViewById(R.id.callnumberButton);
        Button changePicture = findViewById(R.id.changePictureButton);
        ImageView profileImage = findViewById(R.id.picture);

        Intent fromPrevious = getIntent();
        Intent call = new Intent(Intent.ACTION_DIAL);

        String emailAddress = fromPrevious.getStringExtra("EmailAddress");

        SharedPreferences prefs = getSharedPreferences("MyData",Context.MODE_PRIVATE);

        text.setText("Welcome back " + emailAddress);

        Number.setText(prefs.getString("PhoneNumber",""));
        callNumber.setOnClickListener( clk-> {
            String phoneNumber = Number.getText().toString();
            call.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(call);
        });

        File file = new File( getFilesDir(), "Picture.png");
        if(file.exists())
        {
            Bitmap theImage = BitmapFactory.decodeFile(file.getAbsolutePath());
            profileImage.setImageBitmap( theImage );
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ActivityResultLauncher<Intent> cameraResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap thumbnail = data.getParcelableExtra("data");
                            profileImage.setImageBitmap(thumbnail);
                            FileOutputStream fOut = null;
                            try { fOut = openFileOutput("Picture.png", Context.MODE_PRIVATE);
                                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                                fOut.flush();
                                fOut.close();
                            }
                            catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

        changePicture.setOnClickListener( clk-> {
            cameraResult.launch(cameraIntent);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        EditText phone = findViewById(R.id.edittextnumber);
        SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        String phoneNumber = phone.getText().toString();
        editor.putString("PhoneNumber",phoneNumber);
        editor.apply();
    }
}