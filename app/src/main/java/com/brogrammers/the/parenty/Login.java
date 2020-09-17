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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btn_login;
    TextView tv_createAccount;
    EditText et_mobileno,et_password;
    AVLoadingIndicatorView loading_animation;
    SharedPreferences sharedPreferences;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences=getSharedPreferences("userdetails", Context.MODE_PRIVATE);

        if(!sharedPreferences.getString("usertype","").isEmpty())
        {
            String usertype=sharedPreferences.getString("usertype","");
            if(usertype.equals("parent"))
            {
                Intent parent=new Intent(Login.this,DashBoard.class);
                startActivity(parent);
            }
            else if(usertype.equals("child"))
            {
                Intent child=new Intent(Login.this,Disguise.class);
                startActivity(child);
            }
            else
            {
                Intent childset=new Intent(Login.this,SetChildPhone.class);
                startActivity(childset);
            }
        }

        btn_login=(Button)findViewById(R.id.btn_login);
        tv_createAccount=(TextView)findViewById(R.id.tv_createAccount);
        et_password=(EditText)findViewById(R.id.et_password);
        et_mobileno=(EditText)findViewById(R.id.et_mobileno);
        loading_animation=(AVLoadingIndicatorView)findViewById(R.id.loading_animation);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        Intent intentmobileno=getIntent();
        String mobileno;
        if((mobileno=intentmobileno.getStringExtra("alreadyregisteredmobileno"))!=null)
        {
            et_mobileno.setText(mobileno);
        }

        awesomeValidation.addValidation(this,R.id.et_mobileno,"^[2-9]{2}[0-9]{8}$", R.string.mobile_error);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (awesomeValidation.validate()) {

                    /*InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);*/

                    loading_animation.show();
                    blockUI();
                    String phpapi = ((Parenty) Login.this.getApplication()).getDb_url() + "verifyLogin.php";


                    StringRequest verifyLogin = new StringRequest(Request.Method.POST, phpapi,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(Login.this,response,Toast.LENGTH_SHORT).show();
                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        Boolean success = new Boolean(jsonObject.getString("success"));

                                        if (success) {
                                            Boolean validmobile = new Boolean(jsonObject.getString("validmobile"));
                                            if (validmobile) {
                                                Boolean validpassword = new Boolean(jsonObject.getString("validpassword"));
                                                if (validpassword) {
                                                    String username = jsonObject.getString("user_name");
                                                    String usermobile = jsonObject.getString("user_mobileno");
                                                    String userid=jsonObject.getString("user_id");
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("usertype", "loggedin");
                                                    editor.putString("username",username);
                                                    editor.putString("usermobile",usermobile);
                                                    editor.putString("userid",userid);
                                                    editor.commit();
                                                    Intent intent_setchild = new Intent(Login.this, SetChildPhone.class);
                                                    startActivity(intent_setchild);
                                                } else {
                                                    AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                                    alertDialog.setTitle("Sorry");
                                                    alertDialog.setMessage("Invalid Password");
                                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    dialog.dismiss();
                                                                }
                                                            });
                                                    loading_animation.hide();
                                                    unblockUI();
                                                    alertDialog.show();
                                                }
                                            } else {
                                                AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                                alertDialog.setTitle("Sorry");
                                                alertDialog.setMessage("Invalid MobileNo");
                                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        });
                                                loading_animation.hide();
                                                unblockUI();
                                                alertDialog.show();
                                            }

                                        }

                                    } catch (JSONException e) {


                                        AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                        alertDialog.setTitle("Oops");
                                        alertDialog.setMessage("Something occured.Try again.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                        loading_animation.hide();
                                        unblockUI();
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
                                AlertDialog alertDialog = new AlertDialog.Builder(Login.this).create();
                                alertDialog.setTitle("Oops");
                                alertDialog.setMessage("No Internet Connection");
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                loading_animation.hide();
                                unblockUI();
                                alertDialog.show();
                            } else if (error instanceof AuthFailureError) {
                                loading_animation.hide();
                                unblockUI();
                                Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ServerError) {
                                loading_animation.hide();
                                unblockUI();
                                Toast.makeText(getApplicationContext(), "Server Side Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof NetworkError) {
                                loading_animation.hide();
                                unblockUI();
                                Toast.makeText(getApplicationContext(), "Network Error!", Toast.LENGTH_SHORT).show();
                            } else if (error instanceof ParseError) {
                                loading_animation.hide();
                                unblockUI();
                                Toast.makeText(getApplicationContext(), "Parse Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put("mobileno", et_mobileno.getText().toString());
                            params.put("password", et_password.getText().toString());
                            return params;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                    queue.add(verifyLogin);
                }

            }
        });

        tv_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent_signup=new Intent(Login.this,Registration.class);
                Login.this.startActivity(intent_signup);
            }
        });


    }

    void blockUI()
    {

        et_mobileno.setClickable(false);
        et_mobileno.setFocusable(false);
        et_mobileno.setFocusableInTouchMode(false);

        et_password.setClickable(false);
        et_password.setFocusable(false);
        et_password.setFocusableInTouchMode(false);

    }

    void unblockUI()
    {
        et_mobileno.setClickable(true);
        et_mobileno.setFocusable(true);
        et_mobileno.setFocusableInTouchMode(true);

        et_password.setClickable(true);
        et_password.setFocusable(true);
        et_password.setFocusableInTouchMode(true);

    }

}
