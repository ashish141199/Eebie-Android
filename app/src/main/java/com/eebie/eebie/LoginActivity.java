package com.eebie.eebie;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.eebie.eebie.API.UserAPI;
import com.eebie.eebie.models.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private String gender;
    private static final String ENDPOINT = "https://eebie.herokuapp.com";
    private ProgressBar progressBar;
    private User retrofitUser;
    private TextInputEditText username_input;
    private DatabaseReference recent_chats_root;
    private DatabaseReference accounts_root;
    private DatabaseReference root;
    private long totalUserCount;
    private Boolean available = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //connecting Firebase to Eebie-Accounts Database
        root = FirebaseDatabase.getInstance().getReference().getRoot();

        recent_chats_root = FirebaseDatabase.getInstance().getReference().child("recent_chats");
        accounts_root = root.child("accounts");
        accounts_root.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                totalUserCount = mutableData.getChildrenCount();
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });

        setContentView(R.layout.activity_login);
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }

        };
        username_input = (TextInputEditText) findViewById(R.id.username);
        username_input.setFilters(new InputFilter[]{filter});

        //Checking whether the user has already logged in or notnot


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void goToMain() {
        if (username_input.getText().toString().trim().equals("")) {
            username_input.setError("Username is required!");
        } else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    public void login(View v) throws IOException {
        if (username_input.getText().toString().trim().equals("")) {
            username_input.setError("Username is required!");
            return;
        }

        //getting all the fields values
        final TextInputEditText username = (TextInputEditText) findViewById(R.id.username);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.bringToFront();
//        progressBar.setVisibility(View.VISIBLE);
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        dialog.show();


        if (username_input.getText().toString().trim().equals("")) {
            username_input.setError("Username is required!");
            progressBar.setVisibility(View.INVISIBLE);


        } else {
            //retrieving the user from database with the same username as the username which was input!
            final Query firebaseUser = accounts_root.orderByChild("username").equalTo(username.getText().toString());
            firebaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //checking if the firebaseUser is available
                    //if available show error message as "username was already taken"

                    if (dataSnapshot.getChildrenCount() != 0) {

                        username.setError("Username already exists!");
                        available = false;
                    }
                    //if not available updateChildren() and create a new user in firebase database
                    else {
                        long newCount = totalUserCount + 1;
                        DatabaseReference singleAccountRoot = accounts_root.child(Long.toString(newCount));
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("username", username.getText().toString());
                        map.put("gender", gender);
                        map.put("id", Long.toString(newCount));
                        singleAccountRoot.updateChildren(map);
                        //converting the newUser object to json string
                        Gson gson = new Gson();
                        User currentUser = new User(username.getText().toString());
                        currentUser.setId((int) totalUserCount + 1);
                        //setting default points and energy for the user
                        currentUser.setEnergy(200);
                        currentUser.setPoints(0);
                        String json = gson.toJson(currentUser);
                        //storing the newUser json object in sharedPreferences for later quick use
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        //getting the shared-preference of "isLoggedIn" and setting it to true
                        prefs.edit().putBoolean("isLoggedIn", true).commit();
                        prefs.edit().putString("currentUser", json).commit();
                        available = true;
                        //log the user in
                        goToMain();
                    }
                    dialog.dismiss();
                    firebaseUser.removeEventListener(this);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.i("firebaseError", databaseError.toString());
                }
            });
        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        gender = "";
        switch (view.getId()) {
            case R.id.male:
                if (checked) {
                    gender = "Male";
                }
                break;
            case R.id.female:
                if (checked) {
                    gender = "Female";
                }
        }

    }


}
