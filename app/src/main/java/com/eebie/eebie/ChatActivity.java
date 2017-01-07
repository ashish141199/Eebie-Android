package com.eebie.eebie;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.eebie.eebie.models.Message;
import com.eebie.eebie.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("stranger_chat");
    private String myRoomName;
    private DatabaseReference conversation_root;
    private DatabaseReference room_root;
    private DatabaseReference message_root;
    private String temp_key;
    private User u = Methods.getUserInstance(this);
    private ArrayList<Message> message_list = new ArrayList<Message>();
    private MessageAdapter adapter;
    private ListView message_view;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        myRoomName = b.getString("room_name");
        room_root = root.child(myRoomName);
        conversation_root = room_root.child("conversation");



        conversation_root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                append_chat_conversatioin(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_chat_conversatioin(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        adapter = new MessageAdapter(this, message_list);
        message_view = (ListView) findViewById(R.id.listView);
        message_view.setAdapter(adapter);



    }
    private String message_text, message_username;
    private void append_chat_conversatioin(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {
            message_text = (String) ((DataSnapshot) i.next()).getValue();
            message_username = (String) ((DataSnapshot) i.next()).getValue();
            message_list.add(new Message(message_username, message_text));
            message_view.setAdapter(adapter);

        }
        
    }

    public void send_message(View view) {
        EditText message_text = (EditText) findViewById(R.id.raw_message);

        Map<String, Object> map = new HashMap<String, Object>();
        temp_key = conversation_root.push().getKey();
        message_root = conversation_root.child(temp_key);
        Map<String, Object> message_map = new HashMap<String, Object>();
        message_map.put("text", message_text.getText().toString());
        message_map.put("username", u.getUsername());
        message_root.updateChildren(message_map);



    }

}
