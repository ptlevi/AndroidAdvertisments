package com.ptlevi.sapientia.ms.project;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends Activity implements RecyclerViewAdapter.ItemClickListener {

    private static final String TAG = "MainActivity";

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;

    private ArrayList<Advertisment> advertisments = new ArrayList<Advertisment>();

    /**
     * Az onCreate-ben (az Activity indulásakor) lekérjük a már meglévő
     * hirdetéseket a Firebase adatbázisból és kilistázzuk azokat a
     * RexyxlerView segítségével.
     * Ezen kívül van egy Hozzáadás (Add) gonbunk, aminek megadjuk, hogy ha
     * a felhasználó be van jelentkezve akkor az AddActivity hívódjon meg,
     * és ha nincs, akkor a LoginActivity hívódjon meg.
     * Van még egy profilkép gombunk is, aminek megadjuk, hogy ha rákattintunk,
     * akkor tudjuk ki- és bejelentkezni a felhasználói fiókunkba
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.rv_items);

        //final ArrayList<Advertisment> advertisments = new ArrayList<Advertisment>();

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
                    Boolean del;
                    del = (Boolean) tempSnapshot.child("isDeleted").getValue();
                    if(del == null) del = false;

                    if(!del){
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
     * Az onItemClick függvény akkor hívódik meg, ha a felhasználó rákattint
     * valamelyik hirdetésre. Amikor rákattintott, akkor lekérjük annak a hirdetésnek
     * az azonosítóját (ID-t) és berakva azt az intentnek az extrájába
     * meghívjuk az AdvertismentActivity-t.
     *
     * @param  view  a jelenlegi nézetet kapjuk meg
     * @param  position megkapjuk, hogy melyik képre kattintottunk
     */
    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "ennyiedik: " + position);
        Log.d(TAG, "ID: " + advertisments.get(position).getId());
        //Log.d("Mukszik", "Title: " + advertisments.get(position).getTitle());
        //Log.d("Mukszik", "Description: " + advertisments.get(position).getDescription());

        Intent i = new Intent(this, AdvertismentActivity.class);
        i.putExtra("id", advertisments.get(position).getId());
        startActivity(i);
    }
}
