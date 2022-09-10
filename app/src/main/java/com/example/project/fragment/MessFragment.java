package com.example.project.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project.R;
import com.example.project.adapter.UserAdapter;
import com.example.project.model.Chat;
import com.example.project.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> users;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<String> usersList;
    EditText searchName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_mess, container, false);

        recyclerView = view.findViewById(R.id.recycleView123);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getSender().equals(fuser.getUid())) {//hiện thị ds chats
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        usersList.add(chat.getSender());
                    }
                }
                readChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //search Chats
        searchName = view.findViewById(R.id.searchName);
        searchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }


    private void readChat() {
        users = new ArrayList<>();
        List<User> gh = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                gh.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (!user.getId().equals(fuser.getUid())) {
                        gh.add(user);
                    }
                }

                List<User> testto = new ArrayList<>();
                for (int i = 0; i < gh.size(); i++) {
                    for (int j = 0; j < usersList.size(); j++) {
                        if (gh.get(i).getId().equals(usersList.get(j))) {
                            testto.add(gh.get(i));
                        }
                    }
                }


                HashMap<String, User> map = new HashMap<>();
                for (int i = 0; i < testto.size(); i++) {
                    map.put(testto.get(i).getId(), testto.get(i));
                }

                for(HashMap.Entry<String, User> test : map.entrySet()){
                    users.add(test.getValue());
                }


                userAdapter = new UserAdapter(getContext(), users, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void searchUsers(String s) {

        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("users").orderByChild("username")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    assert fUser != null;
                    if (!user.getId().equals(fUser.getUid())) {
                        users.add(user);
                    }
                }

                userAdapter = new UserAdapter(getContext(), users, false);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}