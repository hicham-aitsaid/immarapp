package com.example.ImmarAppUsage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ImmarAppUsage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {
    EditText email,pswrd;
    Button ajouter;
    String mail,ps;
    FirebaseAuth firebaseAuth;
    TextView intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email=findViewById(R.id.mail);
        pswrd=findViewById(R.id.password);
        ajouter=findViewById(R.id.connect);
        firebaseAuth=FirebaseAuth.getInstance();
        intent=findViewById(R.id.in);

        intent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),Inscreption.class);
                startActivity(intent);
            }
        });



        ajouter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mail = email.getText().toString();
                ps = pswrd.getText().toString();

                if (!isEmailValid(mail)) {
                    email.setError("Saisissez un email valid");
                    email.requestFocus();
                } else if (ps.length()<5) {
                    pswrd.setError("Saisissez un mot de passe valid");
                    pswrd.requestFocus();
                }else{
                    auth(mail,ps);
                }
            }
        });

        }

    private boolean isEmailValid(String mail) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private void auth(String mail, String ps) {
        firebaseAuth.signInWithEmailAndPassword(mail, ps)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(login.this, "Authentication échoué.",
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }


                    }
                });
    }
}