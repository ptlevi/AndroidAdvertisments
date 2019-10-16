package com.ptlevi.sapientia.ms.project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdvertismentActivity extends AppCompatActivity {

    private static final String TAG = "AdvertismentActivity";

    /**
     * Az onCreate-ben megkapunk mindig az adott hirdetésnek az ID-ját
     * és azt felhasználva lekérjük a részletesebb adatait a Firebase adatbázisból
     * és megjeleníti azt a felhasználó számára
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisment);

        Bundle b = getIntent().getExtras();
        final String id = b.getString("id");

        Log.d(TAG, "ID: " + id);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("advertisments");


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.child(id).child("name").getValue(String.class);
                Log.d(TAG, "Value is: " + value);

                ImageView IVimage = findViewById(R.id.IVimage);
                TextView TVtitle = findViewById(R.id.TVtitle);
                TextView TVdetails = findViewById(R.id.TVdetail);
                ImageView IVprofile = findViewById(R.id.IVprofile);

                Glide.with(AdvertismentActivity.this)
                        .load(dataSnapshot.child(id).child("photo").getValue())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.imagenotfound)
                        .into(IVimage);

                TVtitle.setText(dataSnapshot.child(id).child("name").getValue(String.class));
                TVdetails.setText(dataSnapshot.child(id).child("details").getValue(String.class));

                final String userID = dataSnapshot.child(id).child("user").getValue(String.class);

                DatabaseReference myRef2 = FirebaseDatabase.getInstance().getReference("user");

                myRef2.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {
                        TextView TVprofilename = findViewById(R.id.TVprofilename);
                        TVprofilename.setText(dataSnapshot2.child(userID).child("email").getValue(String.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
