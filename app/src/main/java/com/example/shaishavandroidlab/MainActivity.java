  package com.example.shaishavandroidlab;

 import androidx.appcompat.app.AppCompatActivity;

  import android.content.Context;
  import android.content.Intent;
  import android.content.SharedPreferences;
  import android.os.Bundle;
  import android.util.Log;
  import android.widget.Button;
  import android.widget.EditText;

  public class MainActivity extends AppCompatActivity {

      private static String TAG = "MainActivity";

      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          Button loginButton = findViewById(R.id.loginButton);
          EditText emailEditText = findViewById(R.id.edittextEmail);

          SharedPreferences prefs = getSharedPreferences("MyData", Context.MODE_PRIVATE);
          SharedPreferences.Editor editor = prefs.edit();

          String emailAddress = prefs.getString("LoginName", "");

          emailEditText.setText(emailAddress);
          loginButton.setOnClickListener( clk-> {
              editor.putString("LoginName", emailEditText.getText().toString());
              editor.apply();
              Intent nextPage = new Intent( MainActivity.this, SecondActivity.class);
              nextPage.putExtra( "EmailAddress", emailEditText.getText().toString() );
              startActivity( nextPage);
          } );

          Log.w( TAG, "is the first function that gets created when an application is launched. There are several other functions that get called when an application is launching:" );
      }

      @Override
      protected void onPause() {
          super.onPause();
          Log.w(TAG,"The application no longer responds to user input");
      }

      @Override
      protected void onResume() {
          super.onResume();
          Log.w(TAG,"The application is now responding to user input");
      }

      @Override
      protected void onStart() {
          super.onStart();
          Log.w(TAG,"The application is now visible on screen");
      }

      @Override
      protected void onStop() {
          super.onStop();
          Log.w(TAG,"The application is no longer visible");
      }

      @Override
      protected void onDestroy() {
          super.onDestroy();
          Log.w(TAG,"Any memory used by the application is freed");
      }
  }