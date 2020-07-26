package com.example.symptoms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


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
    private static List<DiagnosisCase> diagsCaseList = new ArrayList<>();
    private DiagnosisCase currentDiagnosisCase;

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
        symptomsList = getIntent().getParcelableArrayListExtra("symptoms");
        currentDiagnosisCase = getIntent().getParcelableExtra("DiagnosisCase");
        Log.d("currentDiagnosisCase: ", currentDiagnosisCase.getDiagnosisSymptoms());

        //get diagnosis from firebase related to current diagnosis case's symptoms previously selected by the user
        getUserInfoFromFirebase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //user authentication validation
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser == null) {
//            startActivity(new Intent(ActivitySymptomsCaseDetails.this, ActivityLogin.class));
//            Toast.makeText(this, "You need to login first!", Toast.LENGTH_SHORT).show();
//        }

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

    //get user info from firebase
    public void getUserInfoFromFirebase() {
        qRef = FirebaseDatabase.getInstance().getReference("User")
                .orderByChild("email")
                .equalTo(currentDiagnosisCase.getUserEmail());
        qRef.addListenerForSingleValueEvent(userInfoListener);
    }

    //get diagnosis case from Firebase based on symptoms list
    public void getDiagnosisCaseFromFirebase(List<User> userList){
        Query qRef;
        qRef = FirebaseDatabase.getInstance().getReference().child("DiagnosisCase")
                .orderByChild("userEmail")
                .equalTo(userList.get(0).getEmail());
        qRef.addListenerForSingleValueEvent(diagnosisCaseListener);
    }

    //get diagnosis from Firebase based on symptoms list
    public void getDiagnosisFromFirebase(DiagnosisCase diagsCase){
        Query qRef;
        qRef = FirebaseDatabase.getInstance().getReference().child("Diagnosis")
                .orderByKey()
                .equalTo(diagsCase.getDiagnosisSymptomsID());
        qRef.addListenerForSingleValueEvent(diagnosisListener);
    }


    //user info listener
    ValueEventListener userInfoListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    User user = dss.getValue(User.class);
                    userList.add(user);
                }
            } else {
                Log.d(" WAR listener - case diagsCase listener","NO User from Firebase");
            }
            getDiagnosisCaseFromFirebase(userList);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //diagnosis case listener
    ValueEventListener diagnosisCaseListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            diagsCaseList.clear();
            if (dataSnapshot.exists()) {
                Log.d("dC snapshot: ", String.valueOf(dataSnapshot.getValue()));
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    DiagnosisCase diagsCase = dss.getValue(DiagnosisCase.class);
                    diagsCaseList.add(diagsCase);
                }
            } else {
                Log.d(" WAR listener - case diagsCase listener","NO DIAGNOSIS CASES from Firebase");
            }
            Log.d("dCaseList size : ", String.valueOf(diagsCaseList.size()));
            getDiagnosisFromFirebase(currentDiagnosisCase);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //diagnosis listener
    ValueEventListener diagnosisListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            diagsList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<ArrayList<Diagnosis>> t = new GenericTypeIndicator <ArrayList<Diagnosis>>() {};
                    diagsList = dss.getValue(t);
                }
            } else {
                Log.d(" WAR listener - symp diags listener","NO DIAGNOSIS from Firebase");
            }
           updateUI();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    public void updateUI(){
        //update UI header
        UIemail.setText((userList.get(0).getEmail().toUpperCase()).split("@")[0]);
        UIgender.setText(userList.get(0).getGender().toUpperCase());
        UIage.setText(String.valueOf((Calendar.getInstance().get(Calendar.YEAR))-(userList.get(0).getYOB())));
        UIsymptoms.setText(currentDiagnosisCase.getDiagnosisSymptoms());
        //initiate adapter and attached it to recycler view
        adapter = new DiagnosisAdapter(diagsList, ActivitySymptomsCaseDetails.this);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        pBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDiagnosisClick(int position) {
        Log.d("Diagnosis clicked: -alin-> ", String.valueOf(position));
    }


}
