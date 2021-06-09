package com.example.ImmarAppUsage.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Browser;
import android.telephony.ServiceState;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ImmarAppUsage.DeviceInfo;
import com.example.ImmarAppUsage.MyService;
import com.example.ImmarAppUsage.R;
import com.example.ImmarAppUsage.Restarter;
import com.example.ImmarAppUsage.adapter.AppAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bot.box.appusage.contract.UsageContracts;
import bot.box.appusage.datamanager.DataManager;
import bot.box.appusage.handler.Monitor;
import bot.box.appusage.model.AppData;
import bot.box.appusage.utils.Duration;

import static android.app.Service.START_STICKY;
import static bot.box.appusage.utils.DurationRange.MONTH;
import static bot.box.appusage.utils.DurationRange.TODAY;
import static bot.box.appusage.utils.DurationRange.WEEK;
import static bot.box.appusage.utils.DurationRange.YESTERDAY;

public class MainActivity extends AppCompatActivity implements UsageContracts.View
        , AdapterView.OnItemSelectedListener {


    FirebaseAuth.AuthStateListener mAuthListener;

    Intent mServiceIntent;
    private MyService mYourService;
    private AppAdapter mAdapter;
    FirebaseAuth firebaseAuth;
    String id;
    DeviceInfo deviceInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        mYourService = new MyService();
        mServiceIntent = new Intent(this, mYourService.getClass());
        if (!isMyServiceRunning(mYourService.getClass())) {
            startService(mServiceIntent);
        }

        // check if user is already logged in
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
        } else {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        }

        // Start service
        startService(new Intent(MainActivity.this, MyService.class));



        // Permission checking
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        }




    }

    @Override
    protected void onDestroy() {
       /* Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);*/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Monitor.hasUsagePermission()) {
            Monitor.scan().getAppLists(this).fetchFor(Duration.TODAY);
            init();
        } else {
            Monitor.requestUsagePermission();
        }
    }

    private void init() {
        RecyclerView mRecycler = findViewById(R.id.recycler);
        mAdapter = new AppAdapter(this);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.duration));
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(mAdapter);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Monitor.scan().getAppLists(this).fetchFor(i);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }


    @Override
    public void showProgress() {
    }

    @Override
    public void hideProgress() {
    }


    @Override
    public void getUsageData(List<AppData> usageData, long mTotalUsage, int duration) {
        mAdapter.updateData(usageData);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }




}