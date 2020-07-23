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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ActivitySelectSymptoms extends AppCompatActivity implements SymptomAdapter.Holder.SymptomClickListener {

    //classes instances
    private static FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Query qRef;
    private ArrayList<Symptom> list = new ArrayList<>();
    //for user queries
    private static ArrayList<User> userList = new ArrayList<>();
    private ArrayList<Symptom> symptomsList = new ArrayList<>();
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private SymptomAdapter adapter;
    private ProgressBar pBar;
    private BodyLocation bodyLocation;
    private BodySubLocation bodySubLocation;
    private Button getReport;
    private String currentSymptoms = "Case Symptoms: ";
    private TextView addedSymptoms;


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
        getSymptomsFromFirebase();

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
                Intent i = new Intent(ActivitySelectSymptoms.this, ActivitySymptomsCaseDetails.class);
                i.putParcelableArrayListExtra("symptoms", symptomsList);
                i.putParcelableArrayListExtra("user", userList);
                startActivity(i);
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

    public void getSymptomsFromFirebase(){
        //query firebase and add listener
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Body/Symptoms/"+bodySubLocation.getID());
        ref.addListenerForSingleValueEvent(listener);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pBar.setVisibility(View.INVISIBLE);
                rv.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    //get user info from firebase
    public void getUserInfoFromFirebase() {
        Log.d("user email", mAuth.getCurrentUser().getEmail());
        qRef = FirebaseDatabase.getInstance().getReference("User")
                .orderByChild("email")
                .equalTo(mAuth.getCurrentUser().getEmail());
        qRef.addListenerForSingleValueEvent(userInfoListener);

    }

    //data listener
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            list.clear();
            if (dataSnapshot.exists()) {
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    Symptom symp = dss.getValue(Symptom.class);
                    list.add(symp);
                }
            } else {
                //call API
                APIMedical.getSymptomsForBodySubLocation(String.valueOf(bodySubLocation.getID()), userList.get(0));
                //recursive call for symptoms from Firebase
                getSymptomsFromFirebase();
            }
            //initiate adapter and attached it to recycler view
            adapter = new SymptomAdapter(list, ActivitySelectSymptoms.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    //get userinfo and call API
    static ValueEventListener userInfoListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Log.d("user List initial", String.valueOf(userList.size()));
            userList.clear();
            Log.d("user List cleared", String.valueOf(userList.size()));
            for (DataSnapshot dss:dataSnapshot.getChildren()) {
                User user = dss.getValue(User.class);
                userList.add(user);
                Log.d("user List added", String.valueOf(userList.size()));
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    @Override
    public void onSymptomClick(int position) {
        symptomsList.add(list.get(position));
        currentSymptoms += list.get(position).getName()+ " | ";
        addedSymptoms.setText(currentSymptoms);
    }


}
