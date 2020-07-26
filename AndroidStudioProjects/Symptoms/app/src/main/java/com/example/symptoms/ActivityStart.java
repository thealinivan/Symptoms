package com.example.symptoms;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivityStart extends AppCompatActivity implements DiagnosisCaseAdapter.Holder.DiagnosisCaseClickListener{

    private FirebaseAuth mAuth;
    private Query qRef;
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private DiagnosisCaseAdapter adapter;
    private ProgressBar pBar;
    private ArrayList<DiagnosisCase> diagnosisCaseList = new ArrayList<>();

    //open the same activity on back pressed to avoid..
    //..going on the main activity wich should be displayed..
    //..only once when the app is initialized
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ActivityStart.this, ActivityStart.class));
    }

    //set layout and toolbar
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon_2);
        //set Activity title
        getSupportActionBar().setTitle("  Symptoms");

        pBar = findViewById(R.id.home_progress_bar);
        rv = findViewById(R.id.home_recycler);
        manager = new GridLayoutManager(ActivityStart.this, 1);
        rv.setLayoutManager(manager);

        getAllDiagnosisCasesFromFirebase();
    }

    //set toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_right,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //handle menu selection
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

    public void getAllDiagnosisCasesFromFirebase () {
        qRef = FirebaseDatabase.getInstance().getReference("DiagnosisCase");
                //.orderByChild("timestamp");
        qRef.addListenerForSingleValueEvent(userDiagnosisCasesListener);
    }

    //symptoms listener
    ValueEventListener userDiagnosisCasesListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            diagnosisCaseList.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    DiagnosisCase diagsCase = dss.getValue(DiagnosisCase.class);
                    diagnosisCaseList.add(diagsCase);
                    Log.d("Home: diags: ", String.valueOf(diagnosisCaseList.size()));
                }
            }
            //initiate adapter and attached it to recycler view
            adapter = new DiagnosisCaseAdapter(diagnosisCaseList, ActivityStart.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    @Override
    public void onDiagnosisCaseClick(int position) {
        Intent i = new Intent(ActivityStart.this, ActivitySymptomsCaseDetails.class);
        i.putExtra("DiagnosisCase", diagnosisCaseList.get(position));
        startActivity(i);
    }

}























//This project is part of my dissertation report
//University of Roehampton QA Higher Education
//2020
//Alin Ivan