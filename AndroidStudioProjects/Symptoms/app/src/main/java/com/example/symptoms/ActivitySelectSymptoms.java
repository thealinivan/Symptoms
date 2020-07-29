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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.List;

public class ActivitySelectSymptoms extends AppCompatActivity implements SymptomAdapter.Holder.SymptomClickListener {

    //classes instances
    private static FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Query qRef;
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private SymptomAdapter adapter;
    private ProgressBar pBar;
    private BodyLocation bodyLocation;
    private BodySubLocation bodySubLocation;
    private Button getReport;
    private String currentSymptoms = "Case Symptoms: ";
    private TextView addedSymptoms;
    //for user queries
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Symptom> symptomsList = new ArrayList<>();
    private ArrayList<Diagnosis> diagsList = new ArrayList<>();
    private ArrayList<Symptom> selectedSymptomsList = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_symptoms3);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Symptoms");
        toolbar.setNavigationIcon(R.drawable.ic_back);

        //instances & elements linking
        pBar = findViewById(R.id.s3_progress_bar);
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        getReport = findViewById(R.id.symptoms_next);
        addedSymptoms = findViewById(R.id.added_symptoms);

        //recyclerview
        rv = findViewById(R.id.symptoms_recycler);
        manager = new GridLayoutManager(ActivitySelectSymptoms.this, 1);
        rv.setLayoutManager(manager);
        addedSymptoms.setText(currentSymptoms);

        //Http strict mode permission
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //load progress bar
        pBar.setVisibility(View.VISIBLE);
        rv.setVisibility((View.INVISIBLE));

        //get objects from previous activity
        bodyLocation = getIntent().getParcelableExtra("BodyLocation");
        bodySubLocation = getIntent().getParcelableExtra("BodySubLocation");

        getUserInfoFromFirebase();

        //handle back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivitySelectSymptoms.this, ActivitySelectBodySubLocation.class);
                i.putExtra("BodyLocation", bodyLocation);
                startActivity(i);
            }
        });

        getReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedSymptomsList.size() > 0) {
                    pBar.setVisibility(View.VISIBLE);
                    getDiagnosisFromFirebase(selectedSymptomsList);
                    pBar.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(ActivitySelectSymptoms.this, "Select at least 1 symptom", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //user authentication validation
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(ActivitySelectSymptoms.this, ActivityLogin.class));
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
    public String getSymptomsIDsStringFromList(List<Symptom> symptoms) {
        String sympStringIDs = "";
        for (int i = 0; i <symptoms.size(); i++){
            sympStringIDs += symptoms.get(i).getID();
        }
        return sympStringIDs;
    }

    public String getSymptomsStringFromList(List<Symptom> symptoms) {
        String sympString = "";
        for (int i = 0; i <symptoms.size(); i++){
            sympString += selectedSymptomsList.get(i).getName()+ " | ";
        }
        return sympString;
    }

    //get user info from firebase
    public void getUserInfoFromFirebase() {
        qRef = FirebaseDatabase.getInstance().getReference("User")
                .orderByChild("email")
                .equalTo(mAuth.getCurrentUser().getEmail());
        qRef.addListenerForSingleValueEvent(userInfoListener);
    }

    public void getSymptomsFromFirebaseOrMedicalAPI(BodySubLocation bodySubLocation){
        //query firebase and add listener
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Body/Symptoms/"+bodySubLocation.getID());
        ref.addListenerForSingleValueEvent(symptomsListener);
    }


    //get diagnosis from Firebase based on symptoms list
    public void getDiagnosisFromFirebase(List<Symptom> symptomsList){
        Query qRef;
        qRef = FirebaseDatabase.getInstance().getReference().child("Diagnosis")
                .orderByKey()
                .equalTo(getSymptomsIDsStringFromList(symptomsList));
        qRef.addListenerForSingleValueEvent(diagnosisListener);
    }

    //store diagnosis case with diagnosis and user references
    public void storeDiagnosisCaseInFirebase (List<Symptom> symptomsList, User user){

        //create new Diagnosis case object
        final DiagnosisCase diagnosisCase = new DiagnosisCase(getSymptomsIDsStringFromList(selectedSymptomsList), getSymptomsStringFromList(selectedSymptomsList), user.getEmail());

        //storage to firbase
        DatabaseReference dbRef;
        dbRef = FirebaseDatabase.getInstance().getReference("DiagnosisCase");
        dbRef.child(dbRef.push().getKey()).setValue(diagnosisCase).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                //move to next activity
                Intent i = new Intent(ActivitySelectSymptoms.this, ActivitySymptomsCaseDetails.class);
                i.putExtra("DiagnosisCase", diagnosisCase);
                i.putParcelableArrayListExtra("symptoms", selectedSymptomsList);
                i.putParcelableArrayListExtra("user", userList);
                startActivity(i);
            }
        });
    }

    //user info listener
    ValueEventListener userInfoListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userList.clear();
            for (DataSnapshot dss:dataSnapshot.getChildren()) {
                User user = dss.getValue(User.class);
                userList.add(user);
            }
            getSymptomsFromFirebaseOrMedicalAPI(bodySubLocation);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //symptoms listener
    ValueEventListener symptomsListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            symptomsList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    Symptom symp = dss.getValue(Symptom.class);
                    symptomsList.add(symp);
                }
            } else {
                //call API
                APIMedical.getSymptomsForBodySubLocation(String.valueOf(bodySubLocation.getID()), userList.get(0));
                //recursive call for symptoms from Firebase
                getSymptomsFromFirebaseOrMedicalAPI(bodySubLocation);
            }
            //initiate adapter and attached it to recycler view
            adapter = new SymptomAdapter(symptomsList, ActivitySelectSymptoms.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.VISIBLE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //diagnosis listener
    ValueEventListener diagnosisListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            diagsList.clear();
            if (!dataSnapshot.exists()) {
                //call API //memo: convert symptoms ID's to JSON serialized int array before call
                APIMedical.getAllDiagnosisForSymptompsCase(selectedSymptomsList, userList.get(0));
            }
            //store diags case in firebase
            storeDiagnosisCaseInFirebase(selectedSymptomsList, userList.get(0));
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    @Override
    public void onSymptomClick(int position) {
        if (currentSymptoms.contains(symptomsList.get(position).getName())){
            Toast.makeText(this, "Already added!", Toast.LENGTH_SHORT).show();
        } else {
            selectedSymptomsList.add(symptomsList.get(position));
            currentSymptoms += symptomsList.get(position).getName()+ " | ";
            addedSymptoms.setText(currentSymptoms);
        }
        addedSymptoms.setBackgroundColor(getResources().getColor(R.color.colorLight2));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                addedSymptoms.setBackgroundColor(getResources().getColor(android.R.color.white));
            }
        }, 1000);
    }


}
