package com.eebie.eebie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eebie.eebie.models.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SearchActivity extends AppCompatActivity {


    private User currentUser;
    //creating firebase instance

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("stranger_chat");
    private String temp_key;
    private Map<String, Object> map;
    private String username;
    private DatabaseReference room_root;
    private DatabaseReference empty_room;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder().name("users.realm").build();
        Realm realm = Realm.getInstance(config);
        currentUser = realm.where(User.class).findFirst();
        //getting the current user's username
        username = currentUser.getUsername();
        //generating a unique firebase key
        temp_key = root.push().getKey();
        // Firebase database references -> room_root
        map = new HashMap<String, Object>();

        //search for empty rooms with count 1
        final Query emptyRooms = root.orderByChild("count").equalTo(1);

        emptyRooms.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Boolean available = find_empty_room(dataSnapshot);
                //if yes-> join room
                //count=2
                if (available) {
                    String room_name = find_empty_room_name(dataSnapshot);
                    empty_room = root.child(room_name);
                    HashMap<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("count", 2);
                    empty_room.updateChildren(map2);
                    Toast.makeText(SearchActivity.this, room_name, Toast.LENGTH_SHORT).show();
                    goToMain();
                }

                //if no -> create a room with count 1 and join

                else{
                    room_root = root.child("room" + temp_key);
                    map.put("creator", username);
                    map.put("count", 1);
                    room_root.updateChildren(map);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SearchActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();
            }

        });








//        progressBar.setVisibility(View.INVISIBLE);


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(empty_room!=null) empty_room.removeValue();
        if(room_root!=null) room_root.removeValue();
        finish();
    }
    private String find_empty_room_name(DataSnapshot dataSnapshot) {
        Log.i("harshal", Long.toString(dataSnapshot.getChildrenCount()));
        Set<String> set = new HashSet<String>();
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            set.add(((DataSnapshot) i.next()).getKey());
        }
        //getting a random room
        int size = set.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int j = 0;
        String room_name = null;
        for(String obj : set)
        {
            if (j == item)
                room_name = obj;
            j++;
        }


        return room_name;
    }

    private Boolean find_empty_room(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getChildrenCount() >= 1) {
            return true;
        }else{
            return false;
        }
    }

    private void goToMain() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}