package com.ptlevi.sapientia.ms.project;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddActivity extends AppCompatActivity {

    private static final String TAG = "AddActivity";

    private FirebaseAuth mAuth;

    private static final int RC_TAKE_PICTURE = 101;

    private static final String KEY_FILE_URI = "key_file_uri";
    private static final String KEY_DOWNLOAD_URL = "key_download_url";

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog mProgressDialog;

    private Uri mDownloadUrl = null;
    private Uri mFileUri = null;

    /**
     * Az onCreate-ben beállítjuk a gombokat, hogy mit csináljanak ha a
     * felhasználó rákattint
     * Az Upload gomb feltölt egy képet
     * Az OK gomb lementi a beírt adatokat a Firebase adatbázisába, hogyha
     * ki van töltve minden mező. ELlenkező esetben tudatjuk a felhasználóval
     * egy Toast segítségével, hogy minden mezőt ki kell tölteni
     * A gombokon kívül még egy BroadcastReceivert is létrehozunk,
     * ami az Upload gomb megnyomásakor feltölti az adott képet
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        mAuth = FirebaseAuth.getInstance();

        // Click listeners
        findViewById(R.id.IBupload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });
        // Restore instance state
        if (savedInstanceState != null) {
            mFileUri = savedInstanceState.getParcelable(KEY_FILE_URI);
            mDownloadUrl = savedInstanceState.getParcelable(KEY_DOWNLOAD_URL);
        }
        onNewIntent(getIntent());

        // Local broadcast receiver
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive:" + intent);
                hideProgressDialog();

                switch (intent.getAction()) {
                    case MyUploadService.UPLOAD_COMPLETED:
                    case MyUploadService.UPLOAD_ERROR:
                        onUploadResultIntent(intent);
                        break;
                }
            }
        };

        Button BTok = findViewById(R.id.BTok);
        BTok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ETname = findViewById(R.id.ETname);
                EditText ETdetails = findViewById(R.id.ETdetails);
                ImageButton IBupload = findViewById(R.id.IBupload);
                if(ETname.getText().toString().equals("") || ETdetails.getText().toString().equals("") || mDownloadUrl == null){
                    Toast.makeText(AddActivity.this, "Minden mezot ki kell tolteni!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uId;
                mAuth = FirebaseAuth.getInstance();
                if(mAuth.getCurrentUser() == null){
                    Toast.makeText(AddActivity.this, "You are not logged in!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    uId = mAuth.getCurrentUser().getUid();
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("advertisments");
                String key = myRef.push().getKey();

                myRef.child(key).child("name").setValue(ETname.getText().toString());
                myRef.child(key).child("details").setValue(ETdetails.getText().toString());
                myRef.child(key).child("photo").setValue(mDownloadUrl.toString());
                myRef.child(key).child("user").setValue(uId);
                myRef.child(key).child("isDeleted").setValue(false);

                finish();
            }
        });
    }

    /**
     * @param  intent  Ha az intent-nek van extra adata, akkor frissíti az UI-t az adatokkal
     */
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if this Activity was launched by clicking on an upload notification
        if (intent.hasExtra(MyUploadService.EXTRA_DOWNLOAD_URL)) {
            onUploadResultIntent(intent);
        }

    }

    /**
     * Az onStart(), amikor a felhasználó elindítja az alkalmazást,
     * akkor frissíti az UI-t és létrehoz egy lokális BroadcastReceiver-t
     */
    @Override
    public void onStart() {
        super.onStart();
        updateUI(mAuth.getCurrentUser());

        // Register receiver for uploads and downloads
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mBroadcastReceiver, MyUploadService.getIntentFilter());
    }

    /**
     * Az onStop(), amikor a felhasználó bezárja az alkalmazást,
     * akkor leállítódik a BroadcastReceiver
     */
    @Override
    public void onStop() {
        super.onStop();

        // Unregister download receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Az out változót feltölti a menteni kívánt adatokkal
     *
     * @param  out  az extrákat tartalmazó Boundle típusú változó
     */
    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putParcelable(KEY_FILE_URI, mFileUri);
        out.putParcelable(KEY_DOWNLOAD_URL, mDownloadUrl);
    }

    /**
     * Az onActivityResoult-ban megadjuk, hogy amikor visszatérített
     * egy helyes adatot, akkor feltöltjük a képet a Firebase Storage-ba
     * Ellenkező esetben, ha nem helyes az adat akkor kiírjuk a hibát.
     *
     * @param  requestCode  milyen kódú activity-ből tért vissza
     * @param  resultCode helyes e a visszatérítési érték
     * @param  data a visszatérített adatok
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                mFileUri = data.getData();

                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Ebben a függvényben megtudjuk, hogy hibás volt e a feltöltés vagy sikeres és
     * utána frissítjük az UI-t
     *
     * @param  intent  ide kapja meg, hogy hibás volt a feltöltés vagy sikeres
     */
    private void onUploadResultIntent(Intent intent) {
        // Got a new intent from MyUploadService with a success or failure
        mDownloadUrl = intent.getParcelableExtra(MyUploadService.EXTRA_DOWNLOAD_URL);
        mFileUri = intent.getParcelableExtra(MyUploadService.EXTRA_FILE_URI);

        updateUI(mAuth.getCurrentUser());
    }

    /**
     * Az uploadFromUri függvényben a paraméterként kapott képet
     * egy Service segítségével feltöltjük a Firebase Storage-ba
     * és megjelenítünk egy töltés dialogot, amíg a feltöltés tart.
     *
     * @param  fileUri  A kép Uri típusú fájlja
     */
    private void uploadFromUri(Uri fileUri) {
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // Save the File URI
        mFileUri = fileUri;

        // Clear the last download, if any
        updateUI(mAuth.getCurrentUser());
        mDownloadUrl = null;

        // Start MyUploadService to upload the file, so that the file is uploaded
        // even if this Activity is killed or put in the background
        startService(new Intent(this, MyUploadService.class)
                .putExtra(MyUploadService.EXTRA_FILE_URI, fileUri)
                .setAction(MyUploadService.ACTION_UPLOAD));

        // Show loading spinner
        showProgressDialog(getString(R.string.progress_uploading));
    }

    /**
     * A showProgressDialog függvény megjelenít egy töltési ablakot
     * a paraméterben megadott szöveggel
     *
     * @param  caption  a szöveg, amit mutasson a töltés közben
     */
    private void showProgressDialog(String caption) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.setMessage(caption);
        mProgressDialog.show();
    }

    /**
     * A hideProgressDialog függvény eltűnteti a töltési ablakot,
     * ha az meg volt jelenítve
     */
    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Az updateUI függvény frissíti a képernyőt (UI-t), jelen esetben
     * beilleszti azt a képet, amit feltöltöttünk
     *
     * @param  user  a bejelentkezett Firebase felhasználót kell tartalmazza
     */
    private void updateUI(FirebaseUser user) {
        // Signed in or Signed out
        if (user == null) {
            finish();
            Toast.makeText(AddActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
        }

        // Download URL and Download button
        if (mDownloadUrl != null) {
            ImageButton IBupload = findViewById(R.id.IBupload);
            Glide.with(this)
                    .load(mDownloadUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.imagenotfound)
                    .into(IBupload);
            /*findViewById(R.id.BTupload).setVisibility(View.GONE);
        } else {
            findViewById(R.id.BTupload).setVisibility(View.VISIBLE);*/
        }
    }

    /**
     * A launchCamera függvény megnyitja a felhasználó galériáját és
     * ami képet ott kiválaszt, az fog visszatérülni az onActivityResult
     */
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Pick an image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, RC_TAKE_PICTURE);
    }
}
