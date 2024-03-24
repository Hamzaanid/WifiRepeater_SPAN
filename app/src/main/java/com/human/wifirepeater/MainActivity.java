package com.human.wifirepeater;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import com.human.wifirepeater.utils.Adapters;

public class MainActivity extends AppCompatActivity {
    private Adapters adapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listViewWifi = findViewById(R.id.list);
        adapters = new Adapters(this, listViewWifi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapters.unregisterReceiver();
    }
}
