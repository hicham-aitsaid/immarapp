package com.example.ImmarAppUsage.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ImmarAppUsage.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inscreption extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    EditText nom,num,email,motpasse;
    TextView anne,sexe,niveau,categorie,wilaya;
    Button signup;
    private String[] listeitems;
    String lenom,lenum,mail,pswrd,date,genre,leniveau,lacategorie,lawilaya,id;
    String datetwo,selectedDate1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inscreption);

        firebaseAuth= FirebaseAuth.getInstance();



        nom=findViewById(R.id.nom);
        num=findViewById(R.id.num);
        email=findViewById(R.id.email);
        motpasse=findViewById(R.id.motpasse);
        anne=findViewById(R.id.date);
        sexe=findViewById(R.id.sexe);
        niveau=findViewById(R.id.niveau);
        categorie=findViewById(R.id.categorie);
        wilaya=findViewById(R.id.wilaya);
        signup=findViewById(R.id.inscreption);

        Calendar calendar = Calendar.getInstance();
        final int year1 = calendar.get(Calendar.YEAR);
        final int month1 = calendar.get(Calendar.MONTH);
        final int day1 = calendar.get(Calendar.DAY_OF_MONTH);

        sexe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeitems=new String[]{"Homme","Femme"};
                AlertDialog.Builder mbuilder=new AlertDialog.Builder(Inscreption.this);
                mbuilder.setTitle("Selectionner Genre");
                mbuilder.setSingleChoiceItems(listeitems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexe.setText(listeitems[which]);
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog =mbuilder.create();
                alertDialog.show();
            }
        });

        anne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Inscreption.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year1, int month1, int day1) {
                        datetwo = day1 + "/" + month1 + "/" + year1;
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year1, month1, day1);
                        selectedDate1 = sdf.format(calendar.getTime());
                        anne.setText(selectedDate1);

                    }
                }, year1, month1, day1);
                datePickerDialog.show();
            }
        });

        wilaya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeitems=new String[]{"Adrar","Chlef","Laghouat","Oum El Bouaghi","Batna","Béjaïa","Biskra","Béchar","Blida","Bouira","Tamanghasset","Tébessa","Tlemcen","Tiaret","Tizi Ouzou","Jijel","Sétif","Saïda","Skikda","Sidi Bel Abbes","Annaba","Guelma","Constantine","Médéa","Mostaganem","M'Sila","Mascara","Ouargla"," El Bayadh","Illizi","Boumerdès","El Taref","Tindouf","Tissemsilt","El Oued","Khenchela","Souk Ahras","Tipasa","Mila","Aïn Defla","Naama","Aïn Témouchent"};
                AlertDialog.Builder mbuilder=new AlertDialog.Builder(Inscreption.this);
                mbuilder.setTitle("Selectionner la wilaya");
                mbuilder.setSingleChoiceItems(listeitems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wilaya.setText(listeitems[which]);
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog =mbuilder.create();
                alertDialog.show();
            }

        });

        niveau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listeitems=new String[]{"non scolarisé","niveau primaire","niveau moyen","niveau secondaire","niveau superieur"};
                AlertDialog.Builder mbuilder=new AlertDialog.Builder(Inscreption.this);
                mbuilder.setTitle("Selectionner le Niveau");
                mbuilder.setSingleChoiceItems(listeitems, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        niveau.setText(listeitems[which]);
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog =mbuilder.create();
                alertDialog.show();
            }

        });



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lenom=nom.getText().toString();
                lenum=num.getText().toString();
                mail=email.getText().toString();
                pswrd=motpasse.getText().toString();
                date=anne.getText().toString();
                genre=sexe.getText().toString();
                leniveau=niveau.getText().toString();
                lacategorie=categorie.getText().toString();
                lawilaya=wilaya.getText().toString();



                if (lenom.length()<3){
                    nom.setError("Saisissez votre pseudo");
                    nom.requestFocus();
                }
                else if (lenum.length()<10){
                    num.setError("Saisissez votre numéro de téléphone");
                    num.requestFocus();
                } if (!isEmailValid(mail)) {
                    email.setError("Saisissez un email valid");
                    email.requestFocus();
                } else if (pswrd.length()<5){
                    motpasse.setError("Saisissez un mot de passe valid");
                    motpasse.requestFocus();
                }else if (date.equalsIgnoreCase("Année de naissance")){
                    Toast.makeText(getApplicationContext(),"Entre votre Année de naissance",Toast.LENGTH_SHORT).show();
                }
                else if (genre.equalsIgnoreCase("Genre")){
                    Toast.makeText(getApplicationContext(),"Entre votre Genre",Toast.LENGTH_SHORT).show();
                }else if (leniveau.equalsIgnoreCase("Niveau s’instruction")){
                    Toast.makeText(getApplicationContext(),"Entre votre Niveau",Toast.LENGTH_SHORT).show();
                }
           //     else if (lacategorie.equalsIgnoreCase("Catégorie socio- professionnelle")){
             //       Toast.makeText(getApplicationContext(),"Entrer votre Catégorie socio- professionnelle",Toast.LENGTH_SHORT).show();
               // }
                else if (lawilaya.equalsIgnoreCase("Wilaya")){
                    Toast.makeText(getApplicationContext(),"Entrer votre wilaya",Toast.LENGTH_SHORT).show();
                }else {
                    userAuthentification(mail,pswrd);

                }

            }
        });

    }

    private boolean isEmailValid(String mail) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();

    }

    private void userAuthentification(String mail, String pswrd) {
        firebaseAuth.createUserWithEmailAndPassword(mail,pswrd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    id=firebaseAuth.getCurrentUser().getUid();
                    adduser(mail,lenom,leniveau,lawilaya,date,genre,lenum,id);
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(Inscreption.this, "Authentication échoué.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void adduser(final String mail, final String lenom, final String leniveau,final String lawilaya, final String date,final String genre, final String lenum,final String id) {
        final DatabaseReference rootref;
        rootref= FirebaseDatabase.getInstance().getReference();
        rootref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("num",lenum);
                    userDataMap.put("nom",lenom);
                    userDataMap.put("id",id);
                    userDataMap.put("email",mail);
                    userDataMap.put("niveau",leniveau);
                    userDataMap.put("wilaya",lawilaya);
                    userDataMap.put("date",date);
                    userDataMap.put("genre",genre);

                    rootref.child("nouveau_utilisateur").child(firebaseAuth.getCurrentUser().getUid()).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Inscreption.this,"Votre inscription a été terminée avec succés",Toast.LENGTH_SHORT).show();
                            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                        }
                    });

                }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}