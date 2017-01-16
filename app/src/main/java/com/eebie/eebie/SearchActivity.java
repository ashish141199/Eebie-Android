package com.eebie.eebie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eebie.eebie.models.Room;
import com.eebie.eebie.models.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    private String myRoomName;
    private String room_owner;
    private Gson gson = new Gson();
    private DatabaseReference member_root;
    private Boolean stop = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String currentUserJson = prefs.getString("currentUser", null);
        JSONObject j=null;

        try {
            j = new JSONObject(currentUserJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        currentUser = gson.fromJson(j.toString(), User.class);
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
                Log.i("boolean", Boolean.toString(available));
                //if yes-> join room
                //count=2
                if (available) {
                    myRoomName = find_empty_room_name(dataSnapshot);
                    empty_room = root.child(myRoomName);
                    member_root = empty_room.child("members").child("member" + empty_room.push().getKey());

                    HashMap<String, Object> memberMap = new HashMap<String, Object>();
                    memberMap.put("username", currentUser.getUsername());
                    member_root.updateChildren(memberMap);
                    HashMap<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("count", 2);
                    empty_room.updateChildren(map2);
                    Toast.makeText(getApplicationContext(), myRoomName, Toast.LENGTH_SHORT).show();

                    emptyRooms.removeEventListener(this);

                    goToMain();
                }

                //if no -> create a room with count 1 and join

                else{
                    room_root = root.child(currentUser.getUsername());
                    myRoomName = "room" + temp_key;
                    map.put("owner", username);
                    map.put("count", 1);
                    room_root.updateChildren(map);
                    room_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Room room = dataSnapshot.getValue(Room.class);
                            if (room != null) {


                            if (room.getCount() == 2) {

                                goToMain();
                                room_root.removeEventListener(this);


                            }
                            }
                            emptyRooms.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(SearchActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();

                        }
                    });
                    member_root = room_root.child("members").child("member" + room_root.push().getKey());

                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("username", username);
                    member_root.updateChildren(map);
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

    @Override
    protected void onStop() {
        if (stop) {

            super.onStop();
            if (empty_room != null) empty_room.removeValue();
            if (room_root != null) room_root.removeValue();
            finish();
        }
        else{

        }
    }

    private String find_empty_room_name(DataSnapshot dataSnapshot) {
        Set<String> set = new HashSet<String>();
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            set.add(((DataSnapshot) i.next()).getKey());
        }
        //getting a random room
        int size = set.size();
        int item = new Random().nextInt(size);
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

    private String member_name;
    private void goToMain() {
        member_root.orderByChild("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("nigger", Long.toString(dataSnapshot.getChildrenCount()));
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    Toast.makeText(getApplicationContext(), (String) ((DataSnapshot) i.next()).getValue(), Toast.LENGTH_SHORT).show();

//                    User user = ((DataSnapshot) i.next()).getValue(User.class);
//                    member_name = user.getUsername();
//                    Toast.makeText(getApplicationContext(), member_name, Toast.LENGTH_SHORT).show();

//                    Log.i("loggy", (String) ((DataSnapshot) i.next()).getValue());


                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseDatabaseError", databaseError.getDetails());

            }
        });
        stop = false;
        Intent i = new Intent(this, ChatActivity.class);
        Log.i("ashichc", myRoomName);
        i.putExtra("room_name", myRoomName);
        i.putExtra("room_owner", room_owner);
        startActivity(i);
        finish();
    }
}
