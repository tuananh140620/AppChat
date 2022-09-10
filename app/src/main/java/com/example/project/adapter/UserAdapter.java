package com.example.project.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.MessageActivity;
import com.example.project.R;
import com.example.project.model.Chat;
import com.example.project.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private Context context;
    private List<User> users;
    private boolean isChat;
    public UserAdapter(List<User> list){
        this.users = list;
    }
    String lastMessage;

    public UserAdapter(Context context, List<User> users, boolean isChat){
        this.users = users;
        this.context = context;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list, parent, false);
        return new MyViewHolder(v);
//        View view = LayoutInflater.from(context).inflate(R.layout.user_list, parent, false);
//        return new UserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //        //holder.circleImageView.setBackgroundResource(users.get(position).getAvatar());
        //        holder.circleImageView.setImageResource(users.get(position).getAvatar());
        //        holder.tv_username.setText(users.get(position).getUsername());

        final User user = users.get(position);
        holder.tv_username.setText(user.getUsername());
        if (user.getImageURL().equals("default")){
            holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(user.getImageURL()).into(holder.circleImageView);
        }

        if(isChat){
            lastMessage(user.getId(), holder.last_msg);
        }else{
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView tv_username;
        TextView last_msg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profileImage);
            tv_username = itemView.findViewById(R.id.username);
            last_msg = itemView.findViewById(R.id.last_msg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    Intent intent = new Intent(v.getContext(), MessageActivity.class);
                    intent.putExtra("userid", users.get(index).getId());
                    intent.putExtra("username", users.get(index).getUsername());
                    intent.putExtra("imageURL", users.get(index).getImageURL());
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    private void lastMessage(String userid, TextView last_message){
        lastMessage = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                    chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){
                            lastMessage = chat.getMessage();
                    }
                }

                switch (lastMessage){
                    case "default" :
                        last_message.setText("");
                        break;
                    default:
                        last_message.setText(lastMessage);
                        break;

                }
                lastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
