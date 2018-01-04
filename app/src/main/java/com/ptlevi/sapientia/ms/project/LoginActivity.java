package com.ptlevi.sapientia.ms.project;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    private FirebaseAuth mAuth;

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    public ProgressDialog mProgressDialog;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;

    /**
     * A LoginActivity onCreate
     * megnézzük, hogy az adott felhasználó be van-e már jelentkezve,
     * ha igen, akkor lekérjük az adatait,
     * ha nem, akkor a bejelentkezéshez szükséges ablak indul
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {

            setTitle("Profile");
            setContentView(R.layout.activity_profile);

            final String uId;
            uId = mAuth.getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference myRef = database.getReference("user");

            final EditText mFname = (EditText) findViewById(R.id.ETfname);
            final EditText mLname = (EditText) findViewById(R.id.ETlname);
            final EditText mTel = (EditText) findViewById(R.id.ETtel);
            final TextView mEmail = (TextView) findViewById(R.id.TVemail);

            myRef.addValueEventListener(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    //String value = dataSnapshot.getKey().child("name").getValue(String.class);
                    String fname = (String) dataSnapshot.child(uId).child("FirstName").getValue(String.class);
                    Log.d("DEBUG", "Title: " + fname );
                    mFname.setText(fname);

                    String lname = (String) dataSnapshot.child(uId).child("LastName").getValue(String.class);
                    Log.d("DEBUG", "Title: " + lname );
                    mLname.setText(lname);

                    String tel = (String) dataSnapshot.child(uId).child("Telephone").getValue(String.class);
                    Log.d("DEBUG", "Title: " + tel );
                    mTel.setText(tel);

                    String email = (String) dataSnapshot.child(uId).child("email").getValue(String.class);
                    Log.d("DEBUG", "Title: " + email );
                    mEmail.setText(email);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w("DEBUG", "Log In failed", error.toException());
                }
            });

            Button mSaveChanges = (Button) findViewById(R.id.BTsave);
            mSaveChanges.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    myRef.child(uId).child("FirstName").setValue(mFname.getText().toString());
                    myRef.child(uId).child("LastName").setValue(mLname.getText().toString());
                    myRef.child(uId).child("Telephone").setValue(mTel.getText().toString());
                }
            });
            Button mMyAdv = (Button) findViewById(R.id.BTmyadv);
            mMyAdv.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    Toast.makeText(LoginActivity.this, "MyAdv", Toast.LENGTH_SHORT).show();
                    Intent addIntent = new Intent(LoginActivity.this, MyAdvActivity.class);
                    startActivity(addIntent);
                }
            });
        }
        else {
            setTitle("Sign In");
            setContentView(R.layout.activity_login);
            // Set up the login form.
            mEmailView = findViewById(R.id.ETemail);

            mPasswordView = (EditText) findViewById(R.id.ETpassword);

            Button mEmailSignInButton = (Button) findViewById(R.id.BTemailSignIn);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signIn(mEmailView.getText().toString(), mPasswordView.getText().toString());
                }
            });

            Button mEmailSignUpButton = (Button) findViewById(R.id.BTemailSignUp);
            mEmailSignUpButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
                }
            });

            Button mSignOutButton = (Button) findViewById(R.id.BTsignOut);
            mSignOutButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    signOut();
                }
            });

            // [START config_signin]
            // Configure Google Sign In
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // [END config_signin]

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            // connection failed, should be handled

                            Toast.makeText(LoginActivity.this, "Google login failed.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            SignInButton mGoogleSignInButton = findViewById(R.id.BTgoogleSignIn);
            mGoogleSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
            });

            // [START initialize_auth]
            mAuth = FirebaseAuth.getInstance();
            // [END initialize_auth]
        }
    }

    /**
     * Az isEmailValid(String email)
     * ellenörzi, hogy az e-mail cím formálya helyes-e?
     *
     * @param  email  a felhasználó által megadott e-mail cím
     * @return      visszatéríti, hogy helyes-e a paraméter ként kapott e-mail
     */
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@") && email.contains(".");
    }

    /**
     * Az isPasswordValid(String password)
     * ellenőrzi a jelszó méretét,
     * ha kevesebb 6karakternél, akkor érvénytelen
     *
     * @param  password a felhasználó által megadott jelszó
     * @return      visszatéríti, hogy helyes-e a paraméter ként kapott jelszó
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 6;
    }

    /**
     * A validateForm()
     * visszatéríti, hogy a megadott e-mail és jelszó
     * ki van-e töltve, és ha igen az érvényes-e.
     * Az ellenörzéshez használja az isPaswordValid és az isEmailValid függvényeket.
     *
     * @return      visszatéríti, hogy érvényes-e az e-mail és a jelszó
     */
    private boolean validateForm() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean valid = true;

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            valid = false;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            valid = false;
        } else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            valid = false;
        }

        return valid;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * A createAccount(Strin email, String password)
     * Ellenörzi, hogy helyes-e az email és a jelszó,
     * ha igen tovább megy, és ha sikeres a regisztráció
     * frissití az UI-t és eltárolja az adatokat Firebase-n
     *
     * @param  email
     * @param  password
     */
    private void createAccount(String email, String password) {
        Log.d("LoginActivity", "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Successfully signed up.",Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "CreateUser Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }
    /**
     * A signIn(String email, String password)
     * A bejelentkezést oldja meg,
     * a regisztrált email és jelszó segítségével.
     * Ha nem érvényes a bevitt email vagy jelszó,
     * illetve nem létezik, akkor leáll.
     * Ha sikerül a bejelentkezés, frissití az UI-t
     * és betölti a felhasználó adatait,
     * ha nem sikerül, akkor megjelenít egy üzenetet.
     *
     * @param  email
     * @param password
     */
    private void signIn(String email, String password) {
        Log.d("LoginActivity", "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        if(mAuth.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "You are already logged in.",Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Successfully signed in.",Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "SignIn Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    /**
     * A signOut()
     * ellenőrzi, hogy van-e bejelentkezve felhasználó
     * az adott eszközön,
     * ha nincs: csak megjelenít egy üzenetet,
     * ha van: kijelentkezik és megjelenít egy üzenetet
     */
    private void signOut() {
        if(mAuth.getCurrentUser() == null){
            Toast.makeText(LoginActivity.this, "You are not logged in.", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signOut();
            Toast.makeText(LoginActivity.this, "Successfully logged out.",Toast.LENGTH_SHORT).show();
            //updateUI(null);
        }
    }

    /**
     * A firebaseAuthWithGoogle(GoogleSignInAccount acct)
     * Lekéri a felhasználó adatait,
     * ha ez sikeres, akkor frissití az UI-t,
     * bejelentkezik és betölti a felhasználó adatait,
     * ha nem sikerül, akkor megjelenít egy üzenetet.
     * @param acct a felhasználó által kiválasztott google account kerül bele
     */
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            Toast.makeText(LoginActivity.this, "Successfully signed in.",Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginActivity", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase google Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                            //updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    /**
     * Az onActivityResult(int requestCode, int resultCode, Intent data)
     * Ellenőrzi, hogy a bejelentkezés sikeres volt-e,
     * ha igen, elmenti az adatokat a firebasen,
     * majd lekéri az aktuális felhasználó adatait
     *
     * @param  requestCode milyen kódú activityből tért vissza
     * @param resultCode helyes-e a visszatérített érték
     * @param data a visszatérített adat
     */
    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(LoginActivity.this, "Google sign in was successfull.",
                        Toast.LENGTH_SHORT).show();


                //var user = firebase.auth().currentUser;
                FirebaseUser user = mAuth.getCurrentUser();
                String email, uid;

                if (user != null) {
                    email = user.getEmail();
                    uid = user.getUid();  // The user's ID, unique to the Firebase project. Do NOT use
                    Toast.makeText(LoginActivity.this, "email: " + email + ", id: " + uid,
                            Toast.LENGTH_SHORT).show();
                    // this value to authenticate with your backend server, if
                    // you have one. Use User.getToken() instead.
                } else {
                    return;
                }

                // Write a message to the database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("user");

                myRef.child(uid).child("email").setValue(email);

                finish();
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                //updateUI(null);

                Toast.makeText(LoginActivity.this, "Google sign in failed.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]
}

