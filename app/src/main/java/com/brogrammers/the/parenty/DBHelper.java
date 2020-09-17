package com.brogrammers.the.parenty;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Sandesh on 11-09-2017.
 */

public class DBHelper extends SQLiteOpenHelper{

    SharedPreferences sharedPreferences;
    Context context;
    public DBHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        this.context=context;
        sharedPreferences=context.getSharedPreferences("userdetails",Context.MODE_PRIVATE);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table if not exists call_history(number varchar, date varchar, time varchar, duration varchar, type varchar)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS call_history");
        onCreate(db);
    }

    public boolean insertdata(String number, String date, String time,String duration, String type)
    {
        SQLiteDatabase sdb=this.getWritableDatabase();
        sdb.execSQL("insert into call_history values('"+number+"','"+date+"','"+time+"','"+duration+"','"+type+"')");

        HashMap<String,String> params=new HashMap<String,String>();
        params.put("calltype",type);
        params.put("callnumber",number);
        params.put("calldate",date);
        params.put("calltime",time);
        params.put("callduration",duration);
        params.put("childid",sharedPreferences.getString("childid",""));


        Response.Listener<String> responseListener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    String result=jsonObject.getString("success");
                    if(result.equals("true"))
                        Toast.makeText(context,"Data sent",Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(context,"Sending failed",Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    Toast.makeText(context,"jsonexcption"+response,Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };

        Response.ErrorListener errorListener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        };

        DbAPICall sendLocation =new DbAPICall(params,"updateCall.php",responseListener,errorListener);
        RequestQueue Queue= Volley.newRequestQueue(context);
        Queue.add(sendLocation);
        return true;
    }

    public Cursor getData()
    {
        SQLiteDatabase sdb=this.getReadableDatabase();
        Cursor c=sdb.rawQuery("select * from call_history", null);
        return c;
    }
    public void deleteTable()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS call_history");
        onCreate(db);
    }

}
