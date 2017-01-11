package com.eebie.eebie;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eebie.eebie.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private User currentUser;
    private DatabaseReference accounts_root = FirebaseDatabase.getInstance().getReference().child("accounts");
    private DatabaseReference single_account;
    private EditText emailText;
    private EditText fullName;
    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentUserJson = prefs.getString("currentUser", null);
        JSONObject j=null;

        try {
            j = new JSONObject(currentUserJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentUser = gson.fromJson(j.toString(), User.class);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView username_text = (TextView) findViewById(R.id.username_text);
        username_text.setText(currentUser.getUsername());

        emailText = (EditText) findViewById(R.id.emailEdit);
        fullName = (EditText) findViewById(R.id.fullNameEdit);
        emailText.setText(currentUser.getEmail());
        fullName.setText(currentUser.getFullName());



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    public void save(View view) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        dialog.show();
        //retrieving all fields
        String fullName_Text = fullName.getText().toString();
        String email_text = emailText.getText().toString();

        //update user model with all the fields

        currentUser.setFullName(fullName_Text);
        currentUser.setEmail(email_text);

        //updating user model on firebase database
        single_account = accounts_root.child(Integer.toString(currentUser.getId()));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("fullName", currentUser.getFullName());
        map.put("email", currentUser.getEmail());
        single_account.updateChildren(map);

        //updating user model in sharedpreference

        String json = gson.toJson(currentUser);
        //storing the newUser json object in sharedPreferences for later quick use
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        //getting the shared-preference of "isLoggedIn" and setting it to true
        prefs.edit().putString("currentUser", json).commit();
        dialog.dismiss();


    }

}
