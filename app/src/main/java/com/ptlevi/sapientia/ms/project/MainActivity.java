package com.ptlevi.sapientia.ms.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends Activity implements RecyclerViewAdapter.ItemClickListener {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.rv_items);

        final ArrayList<Advertisment> advertisments = new ArrayList<Advertisment>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, advertisments);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();


        Button BTadd = findViewById(R.id.BTadd);

        BTadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null){
                    //TODO
                    // go to the add activity
                    Toast.makeText(MainActivity.this, "addActivity", Toast.LENGTH_SHORT).show();
                    Intent addIntent = new Intent(MainActivity.this, AddActivity.class);
                    startActivity(addIntent);
                } else {
                    //TODO
                    // go to the login activity
                    Toast.makeText(MainActivity.this, "LoginActivity", Toast.LENGTH_SHORT).show();
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                }
            }
        });

        ImageView IVprofile = findViewById(R.id.IVprofile);

        IVprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                // go to the login activity
                Toast.makeText(MainActivity.this, "LoginActivity", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("advertisments");

        //myRef.child("message").setValue("Szia sanyi");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getKey().child("name").getValue(String.class);
                advertisments.clear();
                for(DataSnapshot tempSnapshot : dataSnapshot.getChildren()){
                    String name = (String) tempSnapshot.child("name").getValue();
                    String details = (String) tempSnapshot.child("details").getValue();
                    Log.d("DEBUG", "Title: " + name + ", Details: " + details);
                    Advertisment adv = new Advertisment();
                    adv.setTitle(name);
                    adv.setDescription(details);
                    advertisments.add(adv);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DEBUG", "Log In failed", error.toException());
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d("Mukszik", "ennyiedik: " + position);
    }
}
