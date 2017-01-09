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

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LoginActivity extends AppCompatActivity {
    private String gender;
    private static final String ENDPOINT = "https://eebie.herokuapp.com";
    private ProgressBar progressBar;
    private User retrofitUser;
    private TextInputEditText username_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        username_input.setFilters(new InputFilter[] { filter });

        //Checking whether the user has already logged in or notnot




        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    private void goToMain() {
        if (username_input.getText().toString().trim().equals("")) {
            username_input.setError("Username is required!");
        }else{
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }
    public void login(View v) throws IOException {
        if (username_input.getText().toString().trim().equals("")) {
            username_input.setError("Username is required!");
            return;
        }

        //getting all the fields values
        TextInputEditText username = (TextInputEditText) findViewById(R.id.username);

//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.bringToFront();
//        progressBar.setVisibility(View.VISIBLE);
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        dialog.show();


        if (username_input.getText().toString().trim().equals("")) {
            username_input.setError("Username is required!");
            progressBar.setVisibility(View.INVISIBLE);

        }else {


            //creating a new User instance
            User user = new User(username.getText().toString(), "", "");

            //sending to server through retrofit
            Retrofit adapter = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl(ENDPOINT)
                    .build();

            UserAPI api = adapter.create(UserAPI.class);
            Call<User> call = api.createUser(user);
            // assigning the response body to the variable 'u' for further needs
            //asynchronous request

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (!response.isSuccessful()) {
                            if(response.message().contains("Bad Request")) {
                                username_input.setError("Username is already taken!");
//                                progressBar.setVisibility(View.GONE);
                                dialog.dismiss();

                            }
                        else{
                                Toast.makeText(LoginActivity.this, "response is not successful", Toast.LENGTH_SHORT).show();
                            }
                    }else{
                        retrofitUser = new User(response.body());
                        Toast.makeText(LoginActivity.this, retrofitUser.getUsername(), Toast.LENGTH_SHORT).show();
//                        progressBar.setVisibility(View.GONE);
                        dialog.dismiss();
                        // storing retrofitUser in realm for faster retrieving
                        Realm.init(LoginActivity.this);
                        RealmConfiguration config = new RealmConfiguration.Builder().name("users.realm").build();
                        Realm realm = Realm.getInstance(config);
                        realm.beginTransaction();
                        User user = realm.copyToRealm(retrofitUser);
                        realm.commitTransaction();
                        //getting the shared-preference of "isLoggedIn" and setting it to true
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                        prefs.edit().putBoolean("isLoggedIn", true).commit();
                        Log.i("zhinga", Boolean.toString(prefs.getBoolean("isLoggedIn", false)));


                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e("ashish", "throwable:", t);
                }
            });
        }
      }
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

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
