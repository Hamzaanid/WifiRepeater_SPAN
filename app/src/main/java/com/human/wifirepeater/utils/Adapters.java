package com.human.wifirepeater.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.human.wifirepeater.models.WifiModel;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Adapters {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private final Context mContext;
    private final WifiManager mWifiManager;
    private final ListView mListView;
    private List<WifiModel> WifiList = new ArrayList<>();

    public Adapters(Context context, ListView listView) {
        this.mContext = context;
        this.mListView = listView;
        this.mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // Enregistrer le BroadcastReceiver pour détecter les changements d'état du Wi-Fi
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mContext.registerReceiver(wifiReceiver, filter);
        // Démarrer le scan WiFi
        startWifiScan();
        connectToWifi(mContext, "desk-H", "00008888");
    }

    public void showToast(Context context, String msg) {
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, msg, duration);
        toast.show();
    }

    private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                            WifiManager.WIFI_STATE_UNKNOWN);
                    if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
                        // Wi-Fi est activé, démarrer le scan
                        startWifiScan();
                    }
                } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    // Nouveaux résultats du scan disponibles, mettre à jour la liste
                    updateWifiList();
                }
            }
        }
    };

    private void startWifiScan() {
        // cette function fait seulement le scan
        if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                mWifiManager.startScan();
            }
        }
    }

    public void unregisterReceiver() {
        mContext.unregisterReceiver(wifiReceiver);
    }

    private void updateWifiList() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<ScanResult> scanResults = mWifiManager.getScanResults();
        Comparator<WifiModel> comparator = (wifi1, wifi2) -> Integer.compare(wifi2.level, wifi1.level);
        if (scanResults != null) {
            WifiModel ObjResult;
            for (ScanResult result : scanResults) {
                if (result.BSSID != null && result.level > -80) {
                    ObjResult = new WifiModel(result.BSSID, result.SSID, result.frequency, result.level);
                    WifiList.add(ObjResult);
                }
            }
            WifiList.sort(comparator);
            // Affichez le SSID dans text1 et le niveau (level) dans text2
            ArrayAdapter<WifiModel> mAdapter = new ArrayAdapter<WifiModel>(mContext, android.R.layout.simple_list_item_2,
                    android.R.id.text1, WifiList) {
                @SuppressLint("SetTextI18n")
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text1 = view.findViewById(android.R.id.text1);
                    TextView text2 = view.findViewById(android.R.id.text2);
                    // Affichez le SSID dans text1 et le niveau (level) dans text2
                    text1.setText(WifiList.get(position).SSID);
                    text2.setText("Level: " + WifiList.get(position).level);

                    return view;
                }
            };
            //this.mListView.removeAllViews();
            this.mListView.setAdapter(mAdapter);

        }
    }

    public void connectToWifi(Context context, String ssid, String password) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            WifiNetworkSpecifier specifier = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build();

            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .setNetworkSpecifier(specifier)
                    .build();

            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            try{
                if (cm != null)
                    cm.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback());
            }catch (Exception e) {
                Log.i("WifiConnector", "Error in requestNetwork(): " + e.getMessage());
            }
        }
    }
}
