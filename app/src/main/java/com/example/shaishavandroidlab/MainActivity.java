package com.example.shaishavandroidlab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

  /** Main Activity first screen of the Application
   * @author Shaishav Goyal
   * @version 0.1
   */
  public class MainActivity extends AppCompatActivity {
      /**
       * this holds text at center of screen
       */
      TextView tv = null;
      /**
       * this holds the editable text or password field at the center of screen
       */
      EditText et = null;
      /**
       * its a button to return result of input of password field
       */
      Button btn = null;
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
          tv = findViewById(R.id.textView);
          et = findViewById(R.id.editText);
          btn = findViewById(R.id.button);
          btn.setOnClickListener( clk-> {
              String password = et.getText().toString();
              if (checkPasswordComplexity(password) == true){
                  tv.setText("Your password meets the requirements");
              } else {
                  tv.setText("You shall not pass!");
              }
          });
      }

      /** This function checks for the complexity
       * of the password entered by user
       * @param pw The String Object that we are checking
       * @return returns true if password is complex
       */
      boolean checkPasswordComplexity( String pw ) {
          boolean foundUpperCase, foundLowerCase, foundNumber, foundSpecial;
          foundUpperCase = foundLowerCase = foundNumber = foundSpecial = false;
          for (int i = 0; i < pw.length(); i++) {
              char c = pw.charAt(i);
              if (Character.isDigit(c)){
                  foundNumber = true;
              } else if (Character.isUpperCase(c)) {
                  foundUpperCase = true;
              } else if (Character.isLowerCase(c)) {
                  foundLowerCase = true;
              } else {
                  foundSpecial = true;
              }
          }
          if(!foundUpperCase) {
              Toast.makeText(MainActivity.this,"missing upper case letter",Toast.LENGTH_SHORT).show();
              return false;
          } else if( ! foundLowerCase) {
              Toast.makeText(MainActivity.this,"missing lower case letter",Toast.LENGTH_SHORT).show();
              return false;
          } else if( ! foundNumber) {
              Toast.makeText(MainActivity.this,"missing Number",Toast.LENGTH_SHORT).show();
              return false;
          } else if(! foundSpecial) {
              Toast.makeText(MainActivity.this,"missing Special Character ",Toast.LENGTH_SHORT).show();
              return false;
          } else {
              return true;
          }
      }

      /** This function checks for if the provided character
       * is the Special Character or not
       * @param c parameter object to be checked
       * @return Returns true if the provided character is a special character
       */
      boolean isSpecialCharacter(char c) {
          switch (c) {
              case '#':
              case '$':
              case '%':
              case '^':
              case '&':
              case '*':
              case '!':
              case '@':
              case '?':
                  return true;
              default:
                  return false;
          }
      }
  }