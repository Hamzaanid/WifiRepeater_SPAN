package com.human.wifirepeater;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class ScanWifi extends AppCompatActivity {
    private ListView listViewWifi;
    private List<ScanResult> wifiScanResults;
    private List<String> wifiList;
    private ArrayAdapter<String> wifiAdapter;
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_wifi);

        // Récupérer la ListView depuis le layout XML
        listViewWifi = findViewById(R.id.list_view_wifi);

        // Initialiser les listes pour les résultats du scan WiFi et les noms des réseaux WiFi
        wifiScanResults = new ArrayList<>();
        wifiList = new ArrayList<>();

        // Initialiser l'adaptateur pour la ListView
        wifiAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifiList);
        listViewWifi.setAdapter(wifiAdapter);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.startScan();

        // Mettre à jour la liste des réseaux WiFi après le scan
        updateWifiList();
    }

    private void updateWifiList() {
        // Obtenir les résultats du scan WiFi
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        wifiScanResults = wifiManager.getScanResults();
        wifiList.clear();
        // Ajouter les noms des réseaux WiFi à la liste
        for (ScanResult result : wifiScanResults) {
            wifiList.add(result.SSID);
        }
        // Mettre à jour l'affichage de la ListView
        wifiAdapter.notifyDataSetChanged();
        connectToYourWifi("desk-H","00008888");
    }

    private void connectToYourWifi(String ssid, String password) {
        WifiNetworkSpecifier wifiNetworkSpecifier = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder()
                    .setSsid(ssid)
                    .setWpa2Passphrase(password)
                    .build();
        }

        NetworkRequest networkRequest = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                networkRequest = new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .setNetworkSpecifier(wifiNetworkSpecifier)
                        .build();
            }
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        Handler handler = new Handler(Looper.getMainLooper()); // Utilisez le looper principal pour le traitement sur le thread principal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback(), handler);
        }
    }
}
