package com.randomringtonemanager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

public class RingtoneService extends Service {

    BroadcastReceiver myreciReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ReceiverClass phoneStateReceiver = new ReceiverClass();
        System.out.println(" Check service is working start" );
        myreciReceiver = new PhoneStateReceiver();
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.intent.action.TelephonyManager.ACTION_PHONE_STATE_CHANGED");
        registerReceiver(myreciReceiver, intentFilter);
//        registerReceiver(phoneStateReceiver,getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        System.out.println(" Check service is working bind" );
        return null;
    }
}
class ReceiverClass extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String callState = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (callState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                System.out.println("Ringing......");
                RingtoneHelper.changeRingtone(context);
            }
        }
    }
}
