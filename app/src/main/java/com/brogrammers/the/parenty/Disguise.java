package com.brogrammers.the.parenty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Disguise extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView tv_note;
    Button btn_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disguise);
        sharedPreferences = getSharedPreferences("userdetails", Context.MODE_PRIVATE);

        tv_note = (TextView) findViewById(R.id.tv_note);
        btn_feedback = (Button) findViewById(R.id.btn_feedback);

        if ((sharedPreferences.getString("usertype", "").equals("child")) && (sharedPreferences.getString("disguise", "").equals("on"))) {
            tv_note.setVisibility(TextView.GONE);
            Toast.makeText(Disguise.this, "on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Disguise.this, "off", Toast.LENGTH_SHORT).show();
            tv_note.setVisibility(TextView.VISIBLE);
            tv_note.setText(R.string.disguise_note);
        }

        btn_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sharedPreferences.getString("usermobile", "").equals(tv_note.getText())) {
                    sharedPreferences.edit().putString("usertype", "").commit();
                    sharedPreferences.edit().putString("username", "").commit();
                    sharedPreferences.edit().putString("usermobile", "").commit();
                    sharedPreferences.edit().putString("disguise", "").commit();
                    Intent login = new Intent(Disguise.this, Login.class);
                    startActivity(login);

                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(Disguise.this).create();
                    alertDialog.setTitle("Submitted");
                    alertDialog.setMessage("Thank you for your valuable feedback");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }

            }
        });

    }


    @Override
    protected void onStop() {
        sharedPreferences.edit().putString("disguise","on").commit();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        sharedPreferences.edit().putString("disguise","on").commit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        sharedPreferences.edit().putString("disguise","on").commit();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((sharedPreferences.getString("usertype", "").equals("child")) && (sharedPreferences.getString("disguise", "").equals("on"))) {
            tv_note.setVisibility(TextView.GONE);
            Toast.makeText(Disguise.this, "on", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Disguise.this, "off", Toast.LENGTH_SHORT).show();
            tv_note.setVisibility(TextView.VISIBLE);
            tv_note.setText(R.string.disguise_note);
        }
    }
}
