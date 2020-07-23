package com.example.symptoms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ActivitySymptomsCaseDetails extends AppCompatActivity {

    //classes instances
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Query qRef;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Symptom> symptomsList = new ArrayList<>();
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private BodySubLocationAdapter adapter;
    private ProgressBar pBar;
    private BodySubLocation bodySubLocation;
    private BodyLocation bodyLocation;
    private ArrayList<Diagnosis> diagnosisList;
    private List<Integer> symptomsIDs;

    //report header
    private TextView UIemail, UIgender, UIage, UIsymptoms;

    //recycler
    //

    //set layout and toolbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symptoms_case_details);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Symptoms");
        toolbar.setNavigationIcon(R.drawable.ic_back);

        //instances & elements linking
        pBar = findViewById(R.id.s3_progress_bar);
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //header
        UIemail = findViewById(R.id.patient_email);
        UIgender = findViewById(R.id.patient_gender);
        UIage = findViewById(R.id.patient_age);
        UIsymptoms = findViewById(R.id.case_symptoms_recycler);

        //recycler
        //


        //bodySymptomsarray = ...

        //handle back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySymptomsCaseDetails.this, ActivityStart.class);
                i.putExtra("BodySubLocation", bodySubLocation);
                startActivity(i);
            }
        });

        //Http strict mode permission
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //symptomsList = getIntent().getParcelableExtra("symptoms");
        userList = getIntent().getParcelableArrayListExtra("user");
        symptomsList = getIntent().getParcelableArrayListExtra("symptoms");

        //update UI header
        UIemail.setText(((userList.get(0).getEmail()).split("@")[0]).toUpperCase());
        UIgender.setText(userList.get(0).getGender().toUpperCase());
        UIage.setText(String.valueOf((Calendar.getInstance().get(Calendar.YEAR))-(userList.get(0).getYOB())));
        for (int i = 0; i < symptomsList.size(); i++){
            UIsymptoms.setText(UIsymptoms.getText().toString() + (symptomsList.get(i).getName() + " | "));
        }

        //get data from firebase or api to firebase and then from firebase

    }

    @Override
    protected void onStart() {
        super.onStart();
        //user authentication validation
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(ActivitySymptomsCaseDetails.this, ActivityLogin.class));
            Toast.makeText(this, "You need to login first!", Toast.LENGTH_SHORT).show();
        }

    }
    //toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_right,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //menu selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            //handle create symptoms case
            case R.id.action_create_attraction:
                startActivity(new Intent(this, ActivitySelectBodyLocation.class));
                break;
            //handle account
            case R.id.action_account:
                startActivity(new Intent(this, ActivityAccount.class));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }











    public void getDiagnosisFromFirebase(){
        //query firebase and add listener
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Body/Symptoms/"+bodySubLocation.getID());
        ref.addListenerForSingleValueEvent(diagnosisListener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    public void getSymptomsList(ArrayList<Symptom> sympObjArr) {
        for (int i = 0; i <sympObjArr.size(); i++){

        }
    }

    //data listener
    ValueEventListener diagnosisListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            diagnosisList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    Diagnosis diagnosis= dss.getValue(Diagnosis.class);
                    diagnosisList.add(diagnosis);
                }
            } else {
                //call API //memo: convert symptoms ID's to JSON serialized int array before call
                APIMedical.getAllDiagnosisForSymptompsCase(symptomsIDs, userList.get(0));
                //recursive call for symptoms from Firebase
                getDiagnosisFromFirebase();

            }
            //initiate adapter and attached it to recycler view
            //adapter = new DiagnosisAdapter(diagnosisList, ActivitySymptomsCaseDetails.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };



}
