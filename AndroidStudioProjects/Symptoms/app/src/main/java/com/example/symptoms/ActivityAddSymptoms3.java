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
import android.widget.ProgressBar;
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

public class ActivityAddSymptoms3 extends AppCompatActivity implements SymptomAdapter.Holder.SymptomClickListener {

    //classes instances
    private static FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Query qRef;
    private ArrayList<Symptom> list = new ArrayList<>();
    //for user queries
    private ArrayList<User> userList = new ArrayList<>();
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private SymptomAdapter adapter;
    private ProgressBar pBar;
    private BodyLocation bodyLocation;
    private BodySubLocation bodySubLocation;;

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

        //recyclerview
        rv = findViewById(R.id.symptoms_recycler);
        manager = new GridLayoutManager(ActivityAddSymptoms3.this, 1);
        rv.setLayoutManager(manager);

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

        getSymptomsFromFirebase();

        //handle back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityAddSymptoms3.this, ActivityAddSymptoms2.class);
                i.putExtra("BodyLocation", bodyLocation);
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
            startActivity(new Intent(ActivityAddSymptoms3.this, ActivityLogin.class));
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
                startActivity(new Intent(this, ActivityAddSymptoms.class));
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

    //get userinfo and call API
    ValueEventListener userInfoListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            userList.clear();
            for (DataSnapshot dss:dataSnapshot.getChildren()) {
                User user = dss.getValue(User.class);
                userList.add(user);
                //api need to be called here to ensure user is available before API call
                APIMedical.getSymptomsForBodySubLocation(String.valueOf(bodySubLocation.getID()), user);
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

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
                //get user info from firebase and call api data
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("User");
                ref.orderByKey()
                        .equalTo(mAuth.getCurrentUser().getUid())
                        .limitToFirst(1);
                ref.addListenerForSingleValueEvent(userInfoListener);
                //recursive call for symptoms from Firebase
                getSymptomsFromFirebase();
            }
            //initiate adapter and attached it to recycler view
            adapter = new SymptomAdapter(list, ActivityAddSymptoms3.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) { }
    };

    @Override
    public void onSymptomClick(int position) {
        Intent i = new Intent(ActivityAddSymptoms3.this, ActivitySymptomsCaseDetails.class);
        i.putExtra("Symptom", list.get(position));
        Log.d("List obj: ", String.valueOf(list.get(position)));
        //startActivity(i);
    }

}
