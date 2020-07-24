package com.example.symptoms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public class ActivitySelectBodySubLocation extends AppCompatActivity implements BodySubLocationAdapter.Holder.BodySubLocationClickListener {

    //classes instances
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private Query qRef;
    private ArrayList<BodySubLocation> list = new ArrayList<>();
    private RecyclerView rv;
    private RecyclerView.LayoutManager manager;
    private BodySubLocationAdapter adapter;
    private ProgressBar pBar;
    private BodyLocation bodyLocation;
    //private static BodyLocation currentBodyLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_symptoms2);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Symptoms");
        toolbar.setNavigationIcon(R.drawable.ic_back);

        //instances & elements linking
        pBar = findViewById(R.id.s2_progress_bar);
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //recyclerview
        rv = findViewById(R.id.body_sublocation_recycler);
        manager = new GridLayoutManager(ActivitySelectBodySubLocation.this, 1);
        rv.setLayoutManager(manager);

        //handle back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySelectBodySubLocation.this, ActivitySelectBodyLocation.class));
            }
        });

        //load progress bar
        pBar.setVisibility(View.VISIBLE);
        rv.setVisibility((View.INVISIBLE));

        ///get current attr and pass it to fragment
        bodyLocation = getIntent().getParcelableExtra("BodyLocation");


        //query firebase and add listener
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Body/Sublocations/"+bodyLocation.getID());
        ref.addListenerForSingleValueEvent(listener);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //user authentication validation
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(ActivitySelectBodySubLocation.this, ActivityLogin.class));
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

    //data listener
    ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            list.clear();
            for (DataSnapshot dss:dataSnapshot.getChildren()){
                BodySubLocation bodySubLoc = dss.getValue(BodySubLocation.class);
                list.add(bodySubLoc);
            }

            //initiate adapter and attached it to recycler view
            adapter = new BodySubLocationAdapter(list, ActivitySelectBodySubLocation.this);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            pBar.setVisibility(View.INVISIBLE);
            rv.setVisibility(View.VISIBLE);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    @Override
    public void onBodySubLocationClick(int position) {
        Intent i = new Intent(ActivitySelectBodySubLocation.this, ActivitySelectSymptoms.class);
        i.putExtra("BodyLocation", bodyLocation);
        i.putExtra("BodySubLocation", list.get(position));
        startActivity(i);
    }
}
