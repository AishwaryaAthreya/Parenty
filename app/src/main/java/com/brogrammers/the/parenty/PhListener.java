package com.brogrammers.the.parenty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by Sandesh on 11-09-2017.
 */

@SuppressLint("SimpleDateFormat") public class PhListener extends BroadcastReceiver{

    @Override
    public void onReceive(Context c, Intent i) {
        Toast.makeText(c,"PHListener",Toast.LENGTH_SHORT).show();
        // TODO Auto-generated method stub
        Bundle bundle=i.getExtras();

        if(bundle==null)
            return;

        SharedPreferences sp=c.getSharedPreferences("ZnSoftech", Activity.MODE_PRIVATE);

        String s=bundle.getString(TelephonyManager.EXTRA_STATE);

        if(i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
        {
            String number=i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            sp.edit().putString("number", number).commit();
            sp.edit().putString("state", s).commit();
        }

        else if(s.equals(TelephonyManager.EXTRA_STATE_RINGING))
        {
            String number=bundle.getString("incoming_number");
            sp.edit().putString("number", number).commit();
            sp.edit().putString("state", s).commit();
        }

        else if(s.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
        {
            sp.edit().putString("state", s).commit();
        }

        else if(s.equals(TelephonyManager.EXTRA_STATE_IDLE))
        {
            String state=sp.getString("state", null);
            if(state!=null) {
                if (!state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    sp.edit().putString("state", null).commit();
                    History h = new History(new Handler(), c);
                    c.getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, h);
                }
            }
            sp.edit().putString("state", s).commit();
        }

    }

}
