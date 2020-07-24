package com.example.symptoms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Contacts;
import android.util.Log;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Consumer;


public class ActivitySymptomsCaseDetails extends AppCompatActivity implements DiagnosisAdapter.Holder.DiagnosisClickListener {

    //classes instances
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Query qRef;
    private List<User> userList = new ArrayList<>();
    private List<Symptom> symptomsList = new ArrayList<>();
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private DiagnosisAdapter adapter;
    private ProgressBar pBar;
    private BodySubLocation bodySubLocation;
    private BodyLocation bodyLocation;
    private ArrayList<Diagnosis> diagsList = new ArrayList<>();

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
        pBar = findViewById(R.id.diagnosis_progress_bar);
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //header
        UIemail = findViewById(R.id.patient_email);
        UIgender = findViewById(R.id.patient_gender);
        UIage = findViewById(R.id.patient_age);
        UIsymptoms = findViewById(R.id.case_symptoms_recycler);

        //link recycler view with UI
        rv = findViewById(R.id.diagnosis_recycler);
        manager = new GridLayoutManager(ActivitySymptomsCaseDetails.this, 1);
        rv.setLayoutManager(manager);

        pBar.setVisibility(View.VISIBLE);

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

        //get diagnosis from firebase related to current diagnosis case's symptoms previously selected by the user
        getDiagnosisFromFirebaseOrMedicalAPI(symptomsList);
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


    //get symptoms ID's in a String to use it as diagnosis case reference when storing in firebase
    public String getSymptomsStringFromList(List<Symptom> symptoms) {
        String sympString = "";
        for (int i = 0; i <symptoms.size(); i++){
            sympString += symptoms.get(i).getID();
        }
        return sympString;
    }

    //get diagnosis case from Firebase based on symptoms list
    public void getDiagnosisFromFirebaseOrMedicalAPI(List<Symptom> symptoms){
        qRef = FirebaseDatabase.getInstance().getReference().child("Diagnosis")
                .orderByKey()
                .equalTo(getSymptomsStringFromList(symptoms));
        qRef.addListenerForSingleValueEvent(diagnosisListener);

    }

    //store diagnosis case with diagnosis and user references
    public void storeDiagnosisCaseInFirebase (List<Symptom> symptoms, User user){
        DiagnosisCase diagnosisCase = new DiagnosisCase(getSymptomsStringFromList(symptoms), user.getEmail());
        DatabaseReference dbRef;
        dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("DiagnosisCase").child(getSymptomsStringFromList(symptoms)).setValue(diagnosisCase);
    }

    //data listener
    ValueEventListener diagnosisListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            diagsList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<ArrayList<Diagnosis>> t = new GenericTypeIndicator <ArrayList<Diagnosis>>() {};
                    diagsList = dss.getValue(t);
                    int i;
                    for (i = 0; i < diagsList.size(); i++){
                        Log.d("diags: ", diagsList.get(0).getIssue().getName());
                    }

                }
            } else {
                Log.d("WARNING: ", "API LOOOOP !");
                //call API //memo: convert symptoms ID's to JSON serialized int array before call
                APIMedical.getAllDiagnosisForSymptompsCase(symptomsList, userList.get(0));
                //store diagnosisCase in Firebase
                storeDiagnosisCaseInFirebase(symptomsList, userList.get(0));
                getDiagnosisFromFirebaseOrMedicalAPI(symptomsList);
            }
            //initiate adapter and attached it to recycler view
            adapter = new DiagnosisAdapter(diagsList, ActivitySymptomsCaseDetails.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    @Override
    public void onDiagnosisClick(int position) {
        Log.d("Diagnosis clicked: -alin-> ", String.valueOf(position));
    }


}
