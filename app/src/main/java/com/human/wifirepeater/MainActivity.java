package com.human.wifirepeater;
import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.widget.ListView;

import com.human.wifirepeater.utils.Adapters;

import java.net.InetAddress;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Adapters adapters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewWifi = findViewById(R.id.list);
        adapters = new Adapters(this, listViewWifi);
    }

    WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo info) {
            final InetAddress groupOwnerAddress = info.groupOwnerAddress;
            if (info.groupFormed && info.isGroupOwner) {
                Toast.makeText(MainActivity.this, "host", Toast.LENGTH_SHORT).show();
            } else if (info.groupFormed) {
                Toast.makeText(MainActivity.this, "Client", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapters.unregisterReceiver();
    }
}
