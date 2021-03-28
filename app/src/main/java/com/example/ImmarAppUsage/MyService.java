package com.example.ImmarAppUsage;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.ImmarAppUsage.activity.login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    @Override
    public void onCreate() {



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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(1000*60*60*24);
                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // Send data to firebase

                                    // TODO Ali hna zid beli chaque utilisateur bel ID ta3o teb3et les données


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
                                }
                            });
                        } catch (Exception e) {
                        }
                    }
                }
            }).start();
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
