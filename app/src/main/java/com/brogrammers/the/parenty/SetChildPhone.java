package com.brogrammers.the.parenty;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SetChildPhone extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CALLLOG = 200;
    private static final int MY_PERMISSIONS_REQUEST_GET_LOCATION =300 ;
    Button btn_isachild,btn_notachild,btn_addchild;
    EditText et_childname;
    LinearLayout childnamelayout;
    private PendingIntent pendingIntent;
    private AlarmManager manager;
    AwesomeValidation mAwesomeValidation;
    AVLoadingIndicatorView loading_animation;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setchildphone);

        btn_isachild=(Button)findViewById(R.id.btn_isachild);
        btn_notachild=(Button)findViewById(R.id.btn_notachild);
        childnamelayout=(LinearLayout)findViewById(R.id.childnamelayout);
        btn_addchild =(Button)findViewById(R.id.btn_addchild);
        et_childname=(EditText)findViewById(R.id.et_childname);
        loading_animation=(AVLoadingIndicatorView)findViewById(R.id.loading_animation);

        mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(SetChildPhone.this, R.id.et_childname, "[a-zA-Z\\s]+", R.string.name_error);

        sharedPreferences=getSharedPreferences("userdetails", Context.MODE_PRIVATE);

        getLocationPermission();
        getCallPermission();



        btn_isachild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                  childnamelayout.setVisibility(LinearLayout.VISIBLE);

                /*Toast.makeText(SetChildPhone.this," geo receiver check",Toast.LENGTH_SHORT).show();
                ComponentName geocomponent = new ComponentName(SetChildPhone.this, GeoListener.class);
                int geostatus = SetChildPhone.this.getPackageManager().getComponentEnabledSetting(geocomponent);
                if(geostatus == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                    Toast.makeText(SetChildPhone.this," geo receiver enabled",Toast.LENGTH_SHORT).show();
                    SetChildPhone.this.getPackageManager().setComponentEnabledSetting(geocomponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
                } else if(geostatus == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    Toast.makeText(SetChildPhone.this," geo receiver disabled",Toast.LENGTH_SHORT).show();
                    SetChildPhone.this.getPackageManager().setComponentEnabledSetting(geocomponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
                }

                Toast.makeText(SetChildPhone.this," geo receiver check",Toast.LENGTH_SHORT).show();
                ComponentName callcomponent = new ComponentName(SetChildPhone.this, PhListener.class);
                int callstatus = SetChildPhone.this.getPackageManager().getComponentEnabledSetting(callcomponent);
                if(callstatus == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                    Toast.makeText(SetChildPhone.this," geo receiver enabled",Toast.LENGTH_SHORT).show();
                    SetChildPhone.this.getPackageManager().setComponentEnabledSetting(callcomponent, PackageManager.COMPONENT_ENABLED_STATE_DISABLED , PackageManager.DONT_KILL_APP);
                } else if(callstatus == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                    Toast.makeText(SetChildPhone.this," geo receiver disabled",Toast.LENGTH_SHORT).show();
                    SetChildPhone.this.getPackageManager().setComponentEnabledSetting(callcomponent, PackageManager.COMPONENT_ENABLED_STATE_ENABLED , PackageManager.DONT_KILL_APP);
                }*/



            }
        });

        btn_notachild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(SetChildPhone.this).create();
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Confirm parent?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Toast.makeText(SetChildPhone.this, "Parent selected", Toast.LENGTH_LONG).show();
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("usertype", "parent");
                                editor.commit();
                                childnamelayout.setVisibility(LinearLayout.GONE);
                                Intent dashboard = new Intent(SetChildPhone.this, DashBoard.class);
                                startActivity(dashboard);
                            }
                });
                alertDialog.show();
            }
        });


        btn_addchild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                AlertDialog alertDialog = new AlertDialog.Builder(SetChildPhone.this).create();
                alertDialog.setTitle("Confirmation");
                alertDialog.setMessage("Confirm and start service?");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();


                                if(mAwesomeValidation.validate())
                                {
                                    loading_animation.show();
                                    String phpapi = ((Parenty) SetChildPhone.this.getApplication()).getDb_url() + "addChild.php";
                                    //Toast.makeText(Login.this,phpapi,Toast.LENGTH_SHORT).show();

                                    StringRequest setChild = new StringRequest(Request.Method.POST, phpapi,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    try {
                                                        Toast.makeText(SetChildPhone.this,response,Toast.LENGTH_SHORT).show();
                                                        JSONObject jsonObject = new JSONObject(response);
                                                        Boolean success = new Boolean(jsonObject.getString("success"));

                                                        if (success) {

                                                            Intent locationintent=new Intent(SetChildPhone.this,GeoListener.class);
                                                            pendingIntent= PendingIntent.getBroadcast(SetChildPhone.this,0,locationintent,0);
                                                            manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                                                            int interval = 20000;
                                                            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);

                                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                                            editor.putString("usertype", "child");
                                                            editor.putString("disguise", "off");
                                                            editor.putString("childid",jsonObject.getString("childid"));
                                                            editor.commit();
                                                            Toast.makeText(SetChildPhone.this,"Service started",Toast.LENGTH_LONG).show();

                                                            loading_animation.hide();

                                                            Intent disguise=new Intent(SetChildPhone.this,Disguise.class);
                                                            startActivity(disguise);


                                                        }

                                                    } catch (JSONException e) {


                                                        AlertDialog alertDialog = new AlertDialog.Builder(SetChildPhone.this).create();
                                                        alertDialog.setTitle("Oops");
                                                        alertDialog.setMessage("Something occured.Try again.");
                                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                });
                                                        loading_animation.hide();
                                                        alertDialog.show();
                                                        e.printStackTrace();
                                                    }

                                                }
                                            }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error instanceof TimeoutError) {
                                                loading_animation.hide();
                                                Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_SHORT).show();

                                            } else if (error instanceof NoConnectionError) {
                                                AlertDialog alertDialog = new AlertDialog.Builder(SetChildPhone.this).create();
                                                alertDialog.setTitle("Oops");
                                                alertDialog.setMessage("No Internet Connection");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                loading_animation.hide();
                                                alertDialog.show();
                                            } else if (error instanceof AuthFailureError) {
                                                loading_animation.hide();
                                                Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ServerError) {
                                                loading_animation.hide();
                                                Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof NetworkError) {
                                                loading_animation.hide();
                                                Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                                            } else if (error instanceof ParseError) {
                                                loading_animation.hide();
                                                Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }) {
                                        @Override
                                        protected Map<String, String> getParams() {
                                            HashMap<String, String> params = new HashMap<String, String>();
                                            params.put("childname", et_childname.getText().toString());
                                            params.put("parentid", sharedPreferences.getString("userid",""));
                                            return params;
                                        }
                                    };

                                    setChild.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                    RequestQueue queue = Volley.newRequestQueue(SetChildPhone.this);
                                    queue.add(setChild);



                                }

                            }
                        });
                alertDialog.show();


            }
        });



    }

    private void getCallPermission() {

        if (ContextCompat.checkSelfPermission(SetChildPhone.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SetChildPhone.this,
                    Manifest.permission.READ_PHONE_STATE)) {



            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(SetChildPhone.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_CALLLOG);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    private void getLocationPermission(){

        if (ContextCompat.checkSelfPermission(SetChildPhone.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SetChildPhone.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(SetChildPhone.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_GET_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }



}
