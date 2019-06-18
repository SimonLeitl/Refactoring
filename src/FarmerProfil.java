package com.example.linkn.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FarmerProfile extends AppCompatActivity {

    private static String farmerIDReal;
    private FirebaseFirestore mDatabase;
    private List<String> group = new ArrayList<String>();
    private List<String> shops = new ArrayList<String>();
    private static final String FARMERID = "com.example.linkn.myapplication";
    private TextView farmerVorname, farmerNachname;
    private ListView farmerShops;
    private ArrayAdapter<String> adapter;
    private Farmshop farmShop = new Farmshop();
    private String farmshopID;
    private AdapterView.OnItemClickListener listClickItemNameListener;
    private float bewertung = 0;
    private float anzahl = 0;
    
    CheckBox montagcheckBox,dienstagcheckBox,mittwochcheckBox,donnerstagcheckBox,freitagcheckBox, samstagcheckBox, sonntagcheckBox, geöffnetCeckBox;
    private TextView  shopnameTextView, FarmerTextView, opentimeTextView, ratingTextView, shopArtTextView;
    TextView adressTextView,phoneTextView;


    public static void start(Context context, String farmerIDID) {
        Intent intent = new Intent(context, FarmerProfile.class);
        intent.putExtra(FARMERID, farmerIDID);
        context.startActivity(intent);
        farmerIDReal = farmerIDID;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.farmer_profile);
        getShopsAndMachines(farmerIDReal);
    }

    protected void getShopsAndMachines(String farmerID){
        farmerShops = findViewById(R.id.shopListItem);
        farmerVorname = findViewById(R.id.textViewFarmerVorname);
        farmerNachname = findViewById(R.id.textViewFarmerNachname);
        mDatabase = FirebaseFirestore.getInstance();

        Task<DocumentSnapshot> farmerTask = mDatabase.document("Farmer/" + farmerID).get();


        Tasks.whenAllComplete(farmerTask).onSuccessTask(result -> {

            boolean farmerExists = ((DocumentSnapshot) result.get(0).getResult()).exists();

            if(farmerExists==true) {

                DocumentSnapshot document = farmerTask.getResult();
                document.get(farmerID);
                String farmerVornameDB = document.getString("firstname");
                farmerVorname.setText(farmerVornameDB);
                String farmerNachnameDB = document.getString("lastname");
                farmerNachname.setText(farmerNachnameDB);

                group = (List<String>) document.get("farmshopid");
                getNameAndArt();
               // createListWithNames();
            }
            return null;
        });


        Button okButton = (Button) findViewById(R.id.buttonOK);
        View.OnClickListener onClickListener = OnClickListener -> {
            startActivity(new Intent(FarmerProfile.this, MapsActivity.class));
        };
        okButton.setOnClickListener(onClickListener);
    }

    private void getNameAndArt() {
        List<String> shopsAndNames= new ArrayList<>();
        for(int i = 0;i<group.size();i++){
            farmshopID = group.get(i);

            DocumentReference farmshop = mDatabase.collection("Farmshop").document(group.get(i));
            farmshop.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot document = task.getResult();
                    String shopName = document.getString("shopname");
                    String shopArt = document.getString("Shopart");
                    shopsAndNames.add(shopName + ", " + shopArt);
                    createListWithNames(shopsAndNames);
                    getFarmshopViaID(listClickItemNameListener, farmshopID);
                }
            });
        }
    }

    public void getFarmshopViaID(AdapterView.OnItemClickListener views, String string){
        farmerShops.setOnItemClickListener(views);
        listClickItemNameListener = new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //startActivity(new Intent(FarmerProfile.this, FarmshopView.class));
                showFarmshopbyId(string);
                return;
            }
        };
    }

    private void createListWithNames(List<String> asap) {
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, asap);
        farmerShops.setAdapter(adapter);
    }

    public void showFarmshopbyId( String id){
        setContentView(R.layout.farm_shop_profile);
        adressTextView = (TextView) findViewById(R.id.adressTextView);
        phoneTextView = (TextView) findViewById(R.id.phoneTextView);
        shopnameTextView = (TextView) findViewById(R.id.shopnameTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        FarmerTextView=(TextView) findViewById(R.id.FarmerTextView);
        opentimeTextView=(TextView) findViewById(R.id.opentimeTextView);
        shopArtTextView=(TextView) findViewById(R.id.shopartTextView);
        DocumentReference Farmshop = mDatabase.collection("Farmshop").document(id);

        Farmshop.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot document = task.getResult();
                String adresse = document.getString("straße") + " " + document.getString("hausnummer") + " " + document.getString("plz") + " " + document.getString("ort");
                String phone = document.getString("phone");
                String shopname= document.getString("shopname");
                String inhabername=document.getString("inhabername");
                String shopart=document.getString("Shopart");
                String montag="",dienstag="",mittwoch="",donnerstag="",freitag="",samstag="",sonntag="",dauerhaft, öffnungszeiten;
                if(document.getString("24Stunden")==null) {

                    if (document.getString("Montag") != null) {
                        montag = document.getString("Montag");
                    }
                    if (document.getString("Dienstag") != null) {
                        dienstag = document.getString("Dienstag");
                    }
                    if (document.getString("Mittwoch") != null) {
                        mittwoch = document.getString("Mittwoch");
                    }
                    if (document.getString("Donnerstag") != null) {
                        donnerstag = document.getString("Donnerstag");
                    }
                    if (document.getString("Freitag") != null) {
                        freitag = document.getString("Freitag");
                    }
                    if (document.getString("Samstag") != null) {
                        samstag = document.getString("Samstag");
                    }
                    if (document.getString("Sonntag") != null) {
                        sonntag = document.getString("Sonntag");
                    }

                    öffnungszeiten="Montag: "+montag+"\n"+"Dienstag: "+dienstag+"\n"+"Mittwoch: "+mittwoch+"\n"+"Donnerstag: "+donnerstag+"\n"+"Freitag: "+freitag+"\n"+"Samstag: "+samstag+"\n"+"Sonntag: "+sonntag +"\n";
                }else {

                    öffnungszeiten="24 Stunden geöffnet";
                }

                //                //                // String geb=document.getString("born");

                adressTextView.setText(adresse);
                phoneTextView.setText(phone);
                shopnameTextView.setText(shopname);
                FarmerTextView.setText(inhabername);
                opentimeTextView.setText(öffnungszeiten);
                shopArtTextView.setText(shopart);

            }
        });

        DocumentReference evaluation = mDatabase.collection("Evaluation").document(id);

        evaluation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                //check if the farmshop has an evaluation in the database
                if (document.exists()) {
                    // get the information from the database
                    bewertung = document.getLong("bewertung");
                    anzahl = document.getLong("anzahl");
                    float average;
                    // check if the information from the database do not equals zero then it will calculate the average evaluation
                    if (anzahl != 0) {
                        average = bewertung / anzahl;
                        ratingTextView.setText(average + " / 5");
                    } else {
                        ratingTextView.setText(bewertung + " / 5");
                    }
                }
            }
        });

       // MapsActivity mapsActivity=new MapsActivity();

        //mapsActivity.evaluateShop(id);
        //mapsActivity.goToFarmerEvent();
    }
