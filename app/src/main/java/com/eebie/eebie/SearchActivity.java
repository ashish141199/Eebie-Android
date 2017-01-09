package com.eebie.eebie;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eebie.eebie.models.Room;
import com.eebie.eebie.models.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
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
    private String myRoomName;
    private String room_owner;
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

//        root.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Room room = mutableData.getValue(Room.class);
//                if (room == null) {
//                    return Transaction.success(mutableData);
//                }
//
//
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
//
//            }
//        });


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
                    DatabaseReference member_root = empty_room.child("members").child("member" + empty_room.push().getKey());
                    HashMap<String, Object> memberMap = new HashMap<String, Object>();
                    memberMap.put("username", currentUser.getUsername());
                    member_root.updateChildren(memberMap);
                    HashMap<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("count", 2);
//                    map2.put("owner", currentUser.getUsername());
                    empty_room.updateChildren(map2);
                    Toast.makeText(getApplicationContext(), myRoomName, Toast.LENGTH_SHORT).show();

                    emptyRooms.removeEventListener(this);

                    goToMain();
                }

                //if no -> create a room with count 1 and join

                else{
                    room_root = root.child("room" + temp_key);
                    myRoomName = "room" + temp_key;
                    map.put("owner", username);
                    map.put("count", 1);
                    room_root.updateChildren(map);
                    room_root.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Room room = dataSnapshot.getValue(Room.class);

                            if (room.getCount() == 2) {
                                DatabaseReference member_root = room_root.child("members").child("member" + room_root.push().getKey());
                                HashMap<String, Object> memberMap = new HashMap<String, Object>();
                                memberMap.put("username", currentUser.getUsername());
                                member_root.updateChildren(memberMap);
                                goToMain();
                                room_root.removeEventListener(this);


                            }

                            emptyRooms.removeEventListener(this);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(SearchActivity.this, databaseError.getDetails(), Toast.LENGTH_SHORT).show();

                        }
                    });
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
        super.onStop();
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
        Intent i = new Intent(this, ChatActivity.class);
        Log.i("ashichc", myRoomName);
        i.putExtra("room_name", myRoomName);
        i.putExtra("room_owner", room_owner);
        startActivity(i);
        finish();
    }
}
