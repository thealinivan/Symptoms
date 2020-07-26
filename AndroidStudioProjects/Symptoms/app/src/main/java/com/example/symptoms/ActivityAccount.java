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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ActivityAccount extends AppCompatActivity implements DiagnosisCaseAdapter.Holder.DiagnosisCaseClickListener{

    //classes objects
    private FirebaseAuth mAuth;
    private Query qRef;
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private DiagnosisCaseAdapter adapter;
    private Button signOutBtn, accAdd;
    private ProgressBar pBar;
    private TextView accountName, noCases;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<DiagnosisCase> diagnosisCaseList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //set Activity title
        getSupportActionBar().setTitle(" My Account");

        //handle back button
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAccount.this, ActivityStart.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();
        signOutBtn = findViewById(R.id.signOutBtn);
        pBar = findViewById(R.id.account_progress_bar);
        accountName = findViewById(R.id.account_subtitle);
        accAdd = findViewById(R.id.account_add);
        noCases = findViewById(R.id.no_cases_message);

        rv = findViewById(R.id.account_recycler);
        manager = new GridLayoutManager(ActivityAccount.this, 1);
        rv.setLayoutManager(manager);

        accAdd.setVisibility(View.INVISIBLE);
        noCases.setVisibility(View.INVISIBLE);

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ActivityAccount.this, "You have been signed out!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ActivityAccount.this, ActivityStart.class));
            }
        });

        accAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityAccount.this, ActivitySelectBodyLocation.class));
            }
        });
    }

    //check if user is authenticated
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(ActivityAccount.this, ActivityLogin.class));
            Toast.makeText(this, "Login please!", Toast.LENGTH_SHORT).show();
        } else {
            String name = (mAuth.getCurrentUser().getEmail()).split("@")[0];
            String s1 = name.substring(0, 1).toUpperCase();
            accountName.setText((s1 + name.substring(1) + "'s symptoms"));
            getUserDiagnosisCasesListFromFirebase();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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


    public void getUserDiagnosisCasesListFromFirebase () {
        qRef = FirebaseDatabase.getInstance().getReference("DiagnosisCase")
                .orderByChild("userEmail")
                .equalTo(mAuth.getCurrentUser().getEmail());
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
                //initiate adapter and attached it to recycler view
                adapter = new DiagnosisCaseAdapter(diagnosisCaseList, ActivityAccount.this);
                rv.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } else {
                updateUI();
            }
            pBar.setVisibility(View.INVISIBLE);

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //update Ui if no user symptoms cases
    public void updateUI(){
        accAdd.setVisibility(View.VISIBLE);
        noCases.setVisibility(View.VISIBLE);
    }

    //on diagnosis item click
    @Override
    public void onDiagnosisCaseClick(int position) {
        Intent i = new Intent(ActivityAccount.this, ActivitySymptomsCaseDetails.class);
        i.putExtra("DiagnosisCase", diagnosisCaseList.get(position));
        startActivity(i);
    }
}


