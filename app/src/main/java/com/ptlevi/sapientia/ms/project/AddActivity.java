package com.ptlevi.sapientia.ms.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Button BTok = findViewById(R.id.BTok);
        BTok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ETname = findViewById(R.id.ETname);
                EditText ETdetails = findViewById(R.id.ETdetails);
                if(ETname.getText().toString().equals("") || ETdetails.getText().toString().equals("")){
                    Toast.makeText(AddActivity.this, "Minden mezot ki kell tolteni!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uId;
                mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser() == null){
                    Toast.makeText(AddActivity.this, "You are not logged in!", Toast.LENGTH_SHORT).show();
                    uId = "bad";
                } else {
                    uId = mAuth.getCurrentUser().getUid();
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("advertisments");
                String key = myRef.push().getKey();

                myRef.child(key).child("name").setValue(ETname.getText().toString());
                myRef.child(key).child("details").setValue(ETdetails.getText().toString());
                myRef.child(key).child("user").setValue(uId);

                Toast.makeText(AddActivity.this, "buttonTest", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
