package com.eebie.eebie;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eebie.eebie.models.Message;
import com.eebie.eebie.models.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot().child("stranger_chat");
    private String myRoomName;
    private DatabaseReference conversation_root;
    private DatabaseReference room_root;
    private DatabaseReference message_root;
    private String temp_key;

    private ArrayList<Message> message_list = new ArrayList<Message>();
    private MessageAdapter adapter;
    private ListView message_view;
    private LinearLayoutManager mLinearLayoutManager;
    private String room_owner;
    private DatabaseReference member_root;

    private User currentUser;

    private String chatTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        currentUser = Methods.getUserInstance(this);

        Bundle b = getIntent().getExtras();
        myRoomName = b.getString("room_name");
        room_owner = b.getString("room_owner");
        room_root = root.child(myRoomName);
        member_root = root.child("members");
        conversation_root = room_root.child("conversation");
        member_root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<User> u = new HashSet<User>();

                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    u.add(new User(((DataSnapshot) i.next()).getKey()));
                }
                for (User user : u) {
                    if (!user.getUsername().equals(currentUser.getUsername())) {
                        chatTitle = user.getUsername();

                    }else{
                        chatTitle = "Eebie";
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(room_owner);

        setSupportActionBar(toolbar);

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
                //code to display the user a toast saying "Chat Disconnected" and goToMain()

                Toast.makeText(ChatActivity.this, "", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        message_view = (ListView) findViewById(R.id.message_View);
        adapter = new MessageAdapter(ChatActivity.this, message_list);
        message_view.setDivider(null);
        message_view.setDividerHeight(0);
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
        if (!message_text.getText().toString().equals("")) {


            Map<String, Object> map = new HashMap<String, Object>();
            temp_key = conversation_root.push().getKey();
            message_root = conversation_root.child(temp_key);
            Map<String, Object> message_map = new HashMap<String, Object>();
            message_map.put("text", message_text.getText().toString());
            message_map.put("username", currentUser.getUsername());
            message_root.updateChildren(message_map);
            message_text.setText("");
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        //delete the room existing on firebase
        room_root.removeValue();
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //delete the room existing on firebase
        room_root.removeValue();
        finish();
    }
}
