package com.brogrammers.the.parenty;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

public class Registration extends AppCompatActivity {

    EditText et_retypeppwd,et_ppwd,et_pmobno,et_pemail,et_pname;
    Button btn_signup;
    AVLoadingIndicatorView loading_animation;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        et_pname=(EditText)findViewById(R.id.et_pname);
        et_pemail=(EditText)findViewById(R.id.et_pemail);
        et_pmobno=(EditText)findViewById(R.id.et_pmobno);
        et_ppwd=(EditText)findViewById(R.id.et_ppwd);
        et_retypeppwd=(EditText)findViewById(R.id.et_retypeppwd);
        loading_animation=(AVLoadingIndicatorView)findViewById(R.id.loading_animation);

        btn_signup=(Button)findViewById(R.id.btn_signup);

        awesomeValidation=new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(Registration.this, R.id.et_pemail , android.util.Patterns.EMAIL_ADDRESS, R.string.email_error);
        awesomeValidation.addValidation(Registration.this, R.id.et_pname, "[a-zA-Z\\s]+", R.string.name_error);
        awesomeValidation.addValidation(Registration.this,R.id.et_mobileno,"^[2-9]{2}[0-9]{8}$", R.string.mobile_error);
        awesomeValidation.addValidation(Registration.this, R.id.et_retypeppwd, R.id.et_ppwd, R.string.err_password_confirmation);

// Step 3: set a trigger

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(awesomeValidation.validate()) {

                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);

                    loading_animation.show();
                    blockUI();
                    //Toast.makeText(Registration.this, "onclick", Toast.LENGTH_SHORT).show();
                    String phpapi = ((Parenty) Registration.this.getApplication()).getDb_url() + "registerUser.php";
                    StringRequest signup = new StringRequest(Request.Method.POST, phpapi, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            Toast.makeText(Registration.this, response, Toast.LENGTH_SHORT).show();
                            try {

                                JSONObject jsonObject = new JSONObject(response);
                                Boolean success = new Boolean(jsonObject.getBoolean("success"));
                                //Toast.makeText(Registration.this, "jsonconverted" + success, Toast.LENGTH_SHORT).show();
                                if (success) {
                                    //Toast.makeText(Registration.this, "success", Toast.LENGTH_SHORT).show();
                                    if (jsonObject.getBoolean("userexist")) {
                                        //Toast.makeText(Registration.this, "userexist", Toast.LENGTH_SHORT).show();
                                        AlertDialog alertDialog = new AlertDialog.Builder(Registration.this).create();
                                        alertDialog.setTitle("Oops");
                                        alertDialog.setMessage("Phone number already exist.Please Login.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent login = new Intent(Registration.this, Login.class);
                                                        login.putExtra("alreadyregisteredmobileno", et_pemail.getText());
                                                        Registration.this.startActivity(login);
                                                    }
                                                });
                                        loading_animation.hide();
                                        unblockUI();
                                        alertDialog.show();


                                    } else {
                                       // Toast.makeText(Registration.this, "usernew", Toast.LENGTH_SHORT).show();
                                        AlertDialog alertDialog = new AlertDialog.Builder(Registration.this).create();
                                        alertDialog.setTitle("Thankyou");
                                        alertDialog.setMessage("Welcome to Parenty.");
                                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        Intent login=new Intent(Registration.this,Login.class);
                                                        startActivity(login);

                                                    }
                                                });
                                        loading_animation.hide();
                                        unblockUI();
                                        alertDialog.show();
                                    }
                                } else
                                    Toast.makeText(Registration.this, "nosuccess", Toast.LENGTH_SHORT).show();


                            } catch (JSONException e) {
                                AlertDialog alertDialog = new AlertDialog.Builder(Registration.this).create();
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
                            loading_animation.hide();
                            unblockUI();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            if (error instanceof TimeoutError) {
                                Toast.makeText(getApplicationContext(), "Communication Error!", Toast.LENGTH_SHORT).show();

                            } else if (error instanceof NoConnectionError) {
                                AlertDialog alertDialog = new AlertDialog.Builder(Registration.this).create();
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
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("username", et_pname.getText().toString());
                            params.put("useremail", et_pemail.getText().toString());
                            params.put("userpassword", et_ppwd.getText().toString());
                            params.put("usermobileno", et_pmobno.getText().toString());
                            return params;
                        }

                    };
                    signup.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestQueue queue = Volley.newRequestQueue(Registration.this);
                    queue.add(signup);

                }
            }
        });

    }

    void blockUI()
    {

        et_pname.setClickable(false);
        et_pname.setFocusable(false);
        et_pname.setFocusableInTouchMode(false);

        et_pmobno.setClickable(false);
        et_pmobno.setFocusable(false);
        et_pmobno.setFocusableInTouchMode(false);

        et_ppwd.setClickable(false);
        et_ppwd.setFocusable(false);
        et_ppwd.setFocusableInTouchMode(false);

        et_pemail.setClickable(false);
        et_pemail.setFocusable(false);
        et_pemail.setFocusableInTouchMode(false);
        
        et_retypeppwd.setClickable(false);
        et_retypeppwd.setFocusable(false);
        et_retypeppwd.setFocusableInTouchMode(false);

        et_retypeppwd.setClickable(false);
        et_retypeppwd.setFocusable(false);
        et_retypeppwd.setFocusableInTouchMode(false);
        

    }

    void unblockUI()
    {
        et_pname.setClickable(true);
        et_pname.setFocusable(true);
        et_pname.setFocusableInTouchMode(true);

        et_pmobno.setClickable(true);
        et_pmobno.setFocusable(true);
        et_pmobno.setFocusableInTouchMode(true);

        et_ppwd.setClickable(true);
        et_ppwd.setFocusable(true);
        et_ppwd.setFocusableInTouchMode(true);

        et_pemail.setClickable(true);
        et_pemail.setFocusable(true);
        et_pemail.setFocusableInTouchMode(true);

        et_retypeppwd.setClickable(true);
        et_retypeppwd.setFocusable(true);
        et_retypeppwd.setFocusableInTouchMode(true);

        et_retypeppwd.setClickable(true);
        et_retypeppwd.setFocusable(true);
        et_retypeppwd.setFocusableInTouchMode(true);
    }
}
