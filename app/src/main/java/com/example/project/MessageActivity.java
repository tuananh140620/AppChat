package com.example.project;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.adapter.MessageAdapter;
import com.example.project.model.Chat;
import com.example.project.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    CircleImageView circleImageView;
    TextView textView;
    Intent intent;
    Button done;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    EditText text_send;

    MessageAdapter messageAdapter;
    List<Chat> chats;

    RecyclerView recyclerView;

    String user_id;

    TextView name, phone, email;
    CircleImageView friendAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);//để chat đẩy từ dưới lên
        recyclerView.setLayoutManager(linearLayoutManager);


        textView = findViewById(R.id.user_name);
        circleImageView = findViewById(R.id.profile_image);
        intent = getIntent();
        user_id = intent.getStringExtra("userid");
        String user_name = intent.getStringExtra("username");

        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), user_id, msg);
                }else{
                    Toast.makeText(MessageActivity.this, "Nội dung tin nhắn đang trống", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });



        reference = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            //display message
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);//only read data
                textView.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    circleImageView.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(circleImageView);
                }
                //Glide display image
                readMessage(fuser.getUid(), user_id, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error", "Can't get data from database");
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFriendProfile(Gravity.CENTER);
            }
        });
    }

    public void handleBack(View view) {
        finish();
    }
    //gửi tin nhắn
    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();//đọc và ghi

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);
    }
    //hiện tin nhắn
    private void readMessage(String myid, String userid, String imageurl){
        chats = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        chats.add(chat);//tìm id người gửi và nhận giống nhau thì hiện content chat
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chats, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void openFriendProfile(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.friend_profile);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);//back dialog

        name = dialog.findViewById(R.id.userName);
        phone = dialog.findViewById(R.id.userPhone);
        email = dialog.findViewById(R.id.userEmail);
        friendAvatar = dialog.findViewById(R.id.userAvatar);

        reference = FirebaseDatabase.getInstance().getReference("users").child(user_id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                name.setText(user.getUsername());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                if (user.getImageURL().equals("default")) {
                    friendAvatar.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(friendAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        done = dialog.findViewById(R.id.cancel_friend_profile_dialog);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}