package com.human.wifirepeater.utils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import com.human.wifirepeater.ScanWifi;

public class WifiDetection {
    private Context mContext;
    private BroadcastReceiver mReceiver;

    public WifiDetection(Context context) {
        this.mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    startWifiScan();
                }
            }
        };
        // Enregistrer le BroadcastReceiver
        mContext.registerReceiver(mReceiver, filter);
        startWifiScan();
    }
    public void unregisterReceiver() {
        mContext.unregisterReceiver(mReceiver);
    }
    private void startWifiScan() {
        Intent intent = new Intent(mContext.getApplicationContext(), ScanWifi.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}
