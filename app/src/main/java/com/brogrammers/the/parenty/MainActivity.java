package com.brogrammers.the.parenty;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText et_string,et_int,et_longint,et_date,et_time;
    Button btn_send;
    Calendar cal=Calendar.getInstance();
    private PendingIntent pendingIntent;
    private AlarmManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


        et_string=(EditText)findViewById(R.id.et_string);
        et_int=(EditText)findViewById(R.id.et_int);
        et_longint=(EditText)findViewById(R.id.et_longint);
        et_date=(EditText)findViewById(R.id.et_date);
        et_time=(EditText)findViewById(R.id.et_time);
        Button btn_send=(Button)findViewById(R.id.btn_send);

        //Intent intent = new Intent(this, GeoLocationService.class);
        //startService(intent);

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if(permissionCheck== PackageManager.PERMISSION_GRANTED)
        {
            //Toast.makeText(this, "has permission", Toast.LENGTH_SHORT).show();
        }

        Intent locationintent=new Intent(this,GeoListener.class);
        pendingIntent=PendingIntent.getBroadcast(this,0,locationintent,0);
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 10000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        //Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();


        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               Response.Listener<String> responseListener=new Response.Listener<String>() {
                   @Override
                   public void onResponse(String response) {

                       try{

                           JSONObject jsonObject =new JSONObject(response);
                           String result=jsonObject.getString("success");

                           if(result.equals("true"))
                               Toast.makeText(MainActivity.this,"Successful",Toast.LENGTH_SHORT).show();
                           else
                               Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT).show();


                       }
                       catch(JSONException je)
                       {
                            je.printStackTrace();
                       }


                   }
               };

               Response.ErrorListener errorListener=new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {

                       Toast.makeText(MainActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
                   }
               };
             String phpapi="demo.php";
                Map<String,String> params=new HashMap<String, String>();
                params.put("name",et_string.getText().toString());
                params.put("age",et_int.getText().toString());
                params.put("mobile",et_longint.getText().toString());
                try {
                    params.put("dob", URLEncoder.encode(et_date.getText().toString(),"UTF-8"));
                    params.put("tob", URLEncoder.encode(et_time.getText().toString(),"UTF-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


                DbAPICall trail =new DbAPICall(params,phpapi,responseListener,errorListener);

                RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
                queue.add(trail);

            }
        });


        final DatePickerDialog.OnDateSetListener d= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month+1);
                cal.set(Calendar.DAY_OF_MONTH,day);
                et_date.setText(cal.get(Calendar.DAY_OF_MONTH)+"-"+cal.get(Calendar.MONTH)+"-"+cal.get(Calendar.YEAR));


            }
        };


        final TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {


                cal.set(Calendar.HOUR_OF_DAY,hour);
                cal.set(Calendar.MINUTE,minute);

                //calculation of am pm
                String ampm=cal.get(Calendar.HOUR_OF_DAY)>11?"PM":"AM";
                int timein12hour=cal.get(Calendar.HOUR_OF_DAY);

                //midnight shows 0:45 so to make it 12 :45 this logic is used
                if(timein12hour==0)
                    timein12hour=12;
                else if(timein12hour>12)
                    timein12hour-=12;

                //seconds and minutes should occupy two digits always
                et_time.setText(timein12hour+":"+String.format("%02d",cal.get(Calendar.MINUTE))+" "+ampm);


            }
        };

        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DatePickerDialog(MainActivity.this,d,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        et_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TimePickerDialog(MainActivity.this,t,cal.get(Calendar.HOUR),cal.get(Calendar.MINUTE),false).show();

            }
        });

    }
}
