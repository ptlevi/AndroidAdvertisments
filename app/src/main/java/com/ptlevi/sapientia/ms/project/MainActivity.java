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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends Activity implements RecyclerViewAdapter.ItemClickListener {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.rv_items);

        ArrayList<Advertisment> advertisments = new ArrayList<Advertisment>();
        Advertisment advertisment = new Advertisment();
        advertisment.setTitle("Itt a cim");
        advertisment.setDescription("Itt a leiras");
        advertisments.add(advertisment);

        Advertisment advertisment2 = new Advertisment();
        advertisment2.setTitle("Itt a cim2");
        advertisment2.setDescription("Itt a leiras2");
        advertisments.add(advertisment2);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, advertisments);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);

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
