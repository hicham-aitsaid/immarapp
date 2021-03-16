package com.example.ImmarAppUsage.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ImmarAppUsage.MyService;
import com.example.ImmarAppUsage.R;
import com.example.ImmarAppUsage.Restarter;
import com.example.ImmarAppUsage.adapter.AppAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import bot.box.appusage.contract.UsageContracts;
import bot.box.appusage.datamanager.DataManager;
import bot.box.appusage.handler.Monitor;
import bot.box.appusage.model.AppData;
import bot.box.appusage.utils.Duration;

import static android.app.Service.START_STICKY;
import static bot.box.appusage.utils.DurationRange.TODAY;

public class MainActivity extends AppCompatActivity implements UsageContracts.View
        , AdapterView.OnItemSelectedListener {

    Intent mServiceIntent;
    private MyService mYourService;
    private AppAdapter mAdapter;
    FirebaseAuth firebaseAuth;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start app in background as a service
        startService(new Intent(MainActivity.this, MyService.class));

        // Permission checking
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 100);
        }
    }

    @Override
    protected void onDestroy() {
        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
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


    /**
     * @param usageData   list of application that has been within the duration for which query has been made.
     * @param mTotalUsage a sum total of the usage by each and every app with in the request duration.
     * @param duration    the same duration for which query has been made i.e.fetchFor(Duration...)
     */



    @Override
    public void getUsageData(List<AppData> usageData, long mTotalUsage, int duration) {
        mAdapter.updateData(usageData);
    }
}