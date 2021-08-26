package com.example.ImmarAppUsage.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ImmarAppUsage.R;
import com.google.firebase.auth.FirebaseAuth;

public class Acceuil extends AppCompatActivity {
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceuil);
        firebaseAuth=FirebaseAuth.getInstance();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                connexion();
            }
        }, 2000);
    }

    private void connexion() {
        if (firebaseAuth.getCurrentUser()!=null){
            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent=new Intent(getApplicationContext(),login.class);
            startActivity(intent);
        }
    }
}