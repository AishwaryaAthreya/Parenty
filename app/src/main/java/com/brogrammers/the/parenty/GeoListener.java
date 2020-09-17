package com.brogrammers.the.parenty;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Sandesh on 07-09-2017.
 */

public class GeoListener extends BroadcastReceiver implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    Context context;
    private FusedLocationProviderApi locationProvider = LocationServices.FusedLocationApi;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Double myLatitude;
    private Double myLongitude;
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context,"GeolistnerCalled",Toast.LENGTH_SHORT).show();
        this.context=context;
        sharedPreferences=context.getSharedPreferences("userdetails",Context.MODE_PRIVATE);

        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000 * 1000);
        locationRequest.setFastestInterval(150 * 1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            requestLocationUpdates();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();

        Toast.makeText(context, String.valueOf(myLatitude), Toast.LENGTH_SHORT).show();
        Toast.makeText(context, String.valueOf(myLongitude), Toast.LENGTH_SHORT).show();

        HashMap<String,String> params=new HashMap<String,String>();
        params.put("latitude",location.getLatitude()+"");
        params.put("longitude",location.getLongitude()+"");
        params.put("childid",sharedPreferences.getString("childid",""));
        Calendar cal= Calendar.getInstance();
        Toast.makeText(context, "Ticked", Toast.LENGTH_SHORT).show();
        try {
            String timestamp=URLEncoder.encode(
                    cal.get(Calendar.YEAR)+"-"+
                    cal.get(Calendar.MONTH)+"-"+
                    cal.get(Calendar.DAY_OF_MONTH)+" "+
                    cal.get(Calendar.HOUR)+":"+
                    cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND),"UTF-8");
            params.put("timestamp",timestamp);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String result=jsonObject.getString("success");
                    if(result.equals("true"))
                        Toast.makeText(context,"Data sent", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context,"Sending failed", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(context,"jsonexcption"+response, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context,error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        DbAPICall sendLocation =new DbAPICall(params,"sendLocation.php",responseListener,errorListener);
        RequestQueue Queue= Volley.newRequestQueue(context);
        Queue.add(sendLocation);


    }
}
