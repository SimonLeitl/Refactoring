package com.example.linkn.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {
    private double[] gps;
    private String name;
    private int id;
    public FirebaseAuth auth;
    //public FirebaseAuth.AuthStateListener authListener;


    public TextView vornameTextView, nachnameTextView, gebTextView, emailTextView;
    public Button createNewFarmshopButton;

    private FirebaseFirestore mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_view);
        mDatabase = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        read();
        // this listener will be called when there is change in firebase user session
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Profile.this, LoginActivity.class));
                    finish();
                }
            }
        };

    }

    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            auth.signOut();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(Profile.this, LoginActivity.class));
                finish();
            }
        }
    };

    public Profile() {
    }

    public void read() {
        vornameTextView = (TextView) findViewById(R.id.vornameTextView);
        nachnameTextView = (TextView) findViewById(R.id.nachnameTextView);
        gebTextView = (TextView) findViewById(R.id.gebTextView);
        gebTextView = (TextView) findViewById(R.id.gebTextView);
        createNewFarmshopButton=(Button) findViewById(R.id.createNewFarmshopButton);
        //ruft den aktuellen User ab
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //gibt den String des aktuellen Users
        String uid = currentUser.getUid();

        Task<DocumentSnapshot> customerTask = mDatabase.document("Customer/" + uid).get();
        Task<DocumentSnapshot> farmerTask = mDatabase.document("Farmer/" + uid).get();

        Tasks.whenAllComplete(customerTask, farmerTask).onSuccessTask(result -> {

            boolean customerExists = ((DocumentSnapshot) result.get(0).getResult()).exists();
            boolean farmerExists = ((DocumentSnapshot) result.get(1).getResult()).exists();
            if(customerExists==true){
                createNewFarmshopButton.setVisibility(View.INVISIBLE);
                DocumentSnapshot document = customerTask.getResult();
                String vorname= document.getString("firstname");
                String nachname=document.getString("lastname");
                String geb=document.getString("born");
                vornameTextView.setText(vorname);
                nachnameTextView.setText(nachname);
                gebTextView.setText(geb);
            }else{
                DocumentSnapshot document = farmerTask.getResult();
                String vorname= document.getString("firstname");
                String nachname=document.getString("lastname");
                String geb=document.getString("born");
                vornameTextView.setText(vorname);
                nachnameTextView.setText(nachname);
                gebTextView.setText(geb);
                createNewFarmshopButton.setVisibility(View.VISIBLE);
            }
            return null;
        });

//
//        documentSnapshotTask.onSuccessTask(user -> {
//
//            DocumentSnapshot document = task.getResult();
//            String vorname = document.getString("firstname");
//            String nachname = document.getString("lastname");
//            String geb = document.getString("born");
//            vornameTextView.setText(vorname);
//            nachnameTextView.setText(nachname);
//            gebTextView.setText(geb);
//
//            return null;
//        });
    }
    //Methode um neuen Farmshop zu erstellen
    public void createNewFarmshop(View view){
        startActivity(new Intent(Profile.this,Farmshop.class));
    }


    EditText changePasswort;

    public void changePasswordKlick(View view) {
        setContentView(R.layout.change_password);
        changePasswort = (EditText) findViewById(R.id.newPasswortTextBox);
    }



    public void changePassword(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(changePasswort.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Password is updated!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                            //progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    EditText newMail;

    public void changeEmailKlick(View view) {
        setContentView(R.layout.change_mail);
        newMail = (EditText) findViewById(R.id.newMail);

    }

    public void changeEmail(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.updateEmail(newMail.getText().toString().trim())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Email address is updated.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Profile.this, "Failed to update email!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void deleteAcc(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Profile.this, MainActivity.class));
                            } else {
                                Toast.makeText(Profile.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public Profile(String name, int id, double[] gps) {
        this.name = name;
        this.id = id;


    }

    //Methode für den LogOut
    public void logOut(View view) {

        auth.signOut();
        startActivity(new Intent(Profile.this, MainActivity.class));

    }

    public void mapKlick(View view) {
        startActivity(new Intent(Profile.this, MapsActivity.class));

    }

    public void profilButton(View view) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(Profile.this, MainActivity.class));

        } else {
            startActivity(new Intent(Profile.this, Profile.class));
        }
    }

    // Setzt Layout zurück auf Profilansicht
    public void zurückButton(View view) {
        setContentView(R.layout.profil_view);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void editProfile() {

    }
}
