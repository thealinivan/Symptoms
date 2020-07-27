package com.example.symptoms;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText searchText;
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

        //intial setup
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.ic_app_icon_2);
        getSupportActionBar().setTitle("  Symptoms");

        //UI elements
        searchText = findViewById(R.id.search_text);
        pBar = findViewById(R.id.home_progress_bar);

        //inflater
        rv = findViewById(R.id.home_recycler);
        manager = new GridLayoutManager(ActivityStart.this, 1);
        rv.setLayoutManager(manager);

        //get data
        getAllDiagnosisCasesFromFirebase();

        //search input event
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //initiate adapter and attached it to recycler view
                pBar.setVisibility(View.VISIBLE);
                adapter = new DiagnosisCaseAdapter(
                        getSearchedSymptoms(
                                getOrderedDiagnosisCaseList(diagnosisCaseList),
                                searchText.getText().toString().toLowerCase().trim()), ActivityStart.this);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                pBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
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
                }
            }
            //initiate adapter and attached it to recycler view
            adapter = new DiagnosisCaseAdapter(getOrderedDiagnosisCaseList(diagnosisCaseList), ActivityStart.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //on click listener from DiagnosisCase interface
    @Override
    public void onDiagnosisCaseClick(int position) {
        Intent i = new Intent(ActivityStart.this, ActivitySymptomsCaseDetails.class);
        i.putExtra("DiagnosisCase", getOrderedDiagnosisCaseList(diagnosisCaseList).get(position));
        startActivity(i);
    }

    //order diagnosis list for UI injection
    public static ArrayList<DiagnosisCase>  getOrderedDiagnosisCaseList(ArrayList<DiagnosisCase> list){
        ArrayList<DiagnosisCase> orderedDiagsCaseList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            orderedDiagsCaseList.add(list.get(i));
        }
        return orderedDiagsCaseList;
    }

    //get searched symptoms cases list
    public ArrayList<DiagnosisCase> getSearchedSymptoms (ArrayList<DiagnosisCase> list, String searchWord){
        ArrayList<DiagnosisCase> searchList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDiagnosisSymptoms().toLowerCase().contains(searchWord)){
                searchList.add(list.get(i));
            }
        }
        return searchList;
    }


}























//This project is part of my dissertation report
//University of Roehampton QA Higher Education
//2020
//Alin Ivan