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

        ArrayList<Advertisment> advertisments = new ArrayList<Advertisment>();
        Advertisment advertisment = new Advertisment();
        advertisment.setTitle("Itt a cim1");
        advertisment.setDescription("Itt a leiras1");
        advertisments.add(advertisment);

        Advertisment advertisment2 = new Advertisment();
        advertisment2.setTitle("Itt a cim2");
        advertisment2.setDescription("Itt a leiras2");
        advertisments.add(advertisment2);
        advertisments.add(advertisment);

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
                    Intent addIntent = new Intent(MainActivity.this, LoginActivity.class);
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

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("temp");

        myRef.child("message").setValue("Szia sanyi");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child("message").getValue(String.class);
                Log.d("DEBUG", "Value is: " + value);
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
