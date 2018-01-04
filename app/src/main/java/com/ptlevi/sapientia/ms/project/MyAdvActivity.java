package com.ptlevi.sapientia.ms.project;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdvActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    private FirebaseAuth mAuth;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;

    ArrayList<Advertisment> advertisments = new ArrayList<Advertisment>();

    /**
     * A MyAdvActivity onCreate
     * Lekérjük az aktuális felhasználó ID-jét,
     * lekérjük a felhasználó hírdetéseit,
     * ezt egy listában tároljuk.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_adv);

        recyclerView = (RecyclerView) findViewById(R.id.rv_items);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAdapter = new RecyclerViewAdapter(this, advertisments);
        recyclerViewAdapter.setClickListener(this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        setTitle("My Advertisments");

        mAuth = FirebaseAuth.getInstance();
        final String uId;
        uId = mAuth.getCurrentUser().getUid();
        Log.d("Uid:","UID" + uId);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("advertisments");
        final String myId = (String) uId;
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                //String value = dataSnapshot.getKey().child("name").getValue(String.class);
                advertisments.clear();
                for(DataSnapshot tempSnapshot : dataSnapshot.getChildren()){
                    String tId = (String) tempSnapshot.child("user").getValue();
                    Boolean del;
                    del = (Boolean) tempSnapshot.child("isDeleted").getValue();
                    if(del == null) del = false;
                    if(uId.equals(tId) && !del) {
                        String name = (String) tempSnapshot.child("name").getValue();
                        String details = (String) tempSnapshot.child("details").getValue();
                        String photo = (String) tempSnapshot.child("photo").getValue();
                        Log.d("DEBUG", "Title: " + name + ", Details: " + details + ", Photo: " + photo);
                        Advertisment adv = new Advertisment();
                        adv.setId(tempSnapshot.getKey());
                        adv.setTitle(name);
                        adv.setDescription(details);
                        adv.setImage(photo);
                        advertisments.add(adv);
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("DEBUG", "Log In failed", error.toException());
            }
        });
    }

    /**
     * Az onItemClick(View view, final int position)
     * Ha ráklikkel a felhasználó az egyik saját hírdetésére,
     * megjelenik egy üzenet, melyen ha a Yes-gombra kattint,
     * törli az adott hírdetést
     *
     * @param  view
     * @param position a hírdetés aktuális poziciója
     */

    @Override
    public void onItemClick(View view, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyAdvActivity.this);
        // set title
        alertDialogBuilder.setTitle("Delete");
        // set dialog message
        alertDialogBuilder
                .setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // TODO implement yes
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("advertisments");
                        myRef.child(advertisments.get(position).getId()).child("isDeleted").setValue(true);
                        recyclerViewAdapter.notifyDataSetChanged();
                        Log.d("Click", "Deleted");
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO implement no
                        Log.d("Click", "Not deleted");
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
