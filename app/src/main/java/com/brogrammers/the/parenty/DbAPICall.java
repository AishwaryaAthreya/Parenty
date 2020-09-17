package com.brogrammers.the.parenty;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Sandesh on 22-08-2017.
 */

public class DbAPICall extends StringRequest {

    private static String REGISTER_REQUEST_URL="https://3musketers2017.000webhostapp.com/parenty/";
    private Map params;

    public DbAPICall(Map params, String phpapi, Response.Listener<String> listener,Response.ErrorListener errorListener)
    {
        super(Method.POST,REGISTER_REQUEST_URL+phpapi,listener,errorListener);
        this.params=params;
    }

    @Override
    public Map getParams() {
        return params;
    }

}
