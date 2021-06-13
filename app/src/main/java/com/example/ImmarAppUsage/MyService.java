package com.example.ImmarAppUsage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import bot.box.appusage.datamanager.DataManager;
import bot.box.appusage.model.AppData;

import static bot.box.appusage.utils.DurationRange.MONTH;
import static bot.box.appusage.utils.DurationRange.TODAY;
import static bot.box.appusage.utils.DurationRange.WEEK;
import static bot.box.appusage.utils.DurationRange.YESTERDAY;

public class MyService extends Service {
    FirebaseAuth firebaseAuth;
    String id;


    public MyService() { }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground()
    {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public void onCreate() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
        if(mFirebaseUser != null) {
            id = mFirebaseUser.getUid(); //Do what you need to do with the id
        }


        DataManager d = new DataManager();
        List<AppData> appsUsedToday = d.getUsedApps(this,TODAY);
        List<AppData> appsUsedYesterday = d.getUsedApps(this,YESTERDAY);
        List<AppData> appsUsedLastWeek = d.getUsedApps(this,WEEK);
        List<AppData> appsUsedLastMonth = d.getUsedApps(this,MONTH);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("BDD");
        Handler mHandler = new Handler();


        // Send data to firebase

        // Send Today's Data
        for (int index = 0; index < appsUsedToday.size(); index++) {
            String nom = appsUsedToday.get(index).mName;
            Long temps = appsUsedToday.get(index).mUsageTime;
            int fois = appsUsedToday.get(index).mCount;
            Long wifi = appsUsedToday.get(index).mWifi;
            Long data = appsUsedToday.get(index).mMobile;

            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Today").child(nom).child("Usage").setValue(temps);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Today").child(nom).child("foislance").setValue(fois);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Today").child(nom).child("usagewifi").setValue(wifi);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Today").child(nom).child("usagedata").setValue(data);
        }

        // Send Yesterday's Data
        for (int index = 0; index < appsUsedYesterday.size(); index++) {
            String nom = appsUsedYesterday.get(index).mName;
            Long temps = appsUsedYesterday.get(index).mUsageTime;
            int fois = appsUsedYesterday.get(index).mCount;
            Long wifi = appsUsedYesterday.get(index).mWifi;
            Long data = appsUsedYesterday.get(index).mMobile;

            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Yesterday").child(nom).child("Usage").setValue(temps);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Yesterday").child(nom).child("foislance").setValue(fois);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Yesterday").child(nom).child("usagewifi").setValue(wifi);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Yesterday").child(nom).child("usagedata").setValue(data);
        }

        // Send Last Week's Data
        for (int index = 0; index < appsUsedLastWeek.size(); index++) {
            String nom = appsUsedLastWeek.get(index).mName;
            Long temps = appsUsedLastWeek.get(index).mUsageTime;
            int fois = appsUsedLastWeek.get(index).mCount;
            Long wifi = appsUsedLastWeek.get(index).mWifi;
            Long data = appsUsedLastWeek.get(index).mMobile;

            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastWeek").child(nom).child("Usage").setValue(temps);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastWeek").child(nom).child("foislance").setValue(fois);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastWeek").child(nom).child("usagewifi").setValue(wifi);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastWeek").child(nom).child("usagedata").setValue(data);
        }

        // Send Last Month's Data
        for (int index = 0; index < appsUsedLastMonth.size(); index++) {
            String nom = appsUsedLastMonth.get(index).mName;
            Long temps = appsUsedLastMonth.get(index).mUsageTime;
            int fois = appsUsedLastMonth.get(index).mCount;
            Long wifi = appsUsedLastMonth.get(index).mWifi;
            Long data = appsUsedLastMonth.get(index).mMobile;

            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastMonth").child(nom).child("Usage").setValue(temps);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastMonth").child(nom).child("foislance").setValue(fois);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastMonth").child(nom).child("usagewifi").setValue(wifi);
            myRef.child(firebaseAuth.getCurrentUser().getUid()).child("LastMonth").child(nom).child("usagedata").setValue(data);

        }


        // get time when sent

        /*Date currentTime = Calendar.getInstance().getTime();
        String time = currentTime.toString();*/
        Date cDate = new Date();
        String sync = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(cDate);
        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("Sync").child(sync).setValue(sync);

        //get operator name

        TelephonyManager tel;
        tel = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operateur = tel.getNetworkOperatorName();
        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("operateur").setValue(operateur);


        // get device name

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String devicename = manufacturer + " " + model;
        myRef.child(firebaseAuth.getCurrentUser().getUid()).child("appareil").setValue(devicename);



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
