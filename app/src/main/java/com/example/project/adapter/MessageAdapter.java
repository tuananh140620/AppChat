package com.example.project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project.R;
import com.example.project.model.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

//    public static  final int MSG_STYLE_LEFT = 0;
//    public static  final int MSG_STYLE_RIGHT = 1;
    public static  final int MSG_STYLE_LEFT = 0;
    public static  final int MSG_STYLE_RIGHT = 1;

    private Context context;
    private List<Chat> chats;
    private String imageurl;

    FirebaseUser fuser;

    //private boolean isChat;
//    public MessageAdapter(List<User> list){
//        this.users = list;
//    }
    String lastMessage;

    public MessageAdapter(Context context, List<Chat> chats, String imageurl){
        this.chats = chats;
        this.context = context;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//make view
        if(viewType == MSG_STYLE_RIGHT) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.MyViewHolder(v);
        }else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.MyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MyViewHolder holder, int position) {//gắn data vào view
     Chat chat = chats.get(position);//get position
     holder.show_message.setText(chat.getMessage());//get Message
     if(imageurl.equals("default")){
         holder.circleImageView.setImageResource(R.mipmap.ic_launcher);
     }else{
         Glide.with(context).load(imageurl).into(holder.circleImageView);
     }
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView circleImageView;
        TextView show_message;

        public MyViewHolder(@NonNull View itemView) {//show logo and content
            super(itemView);
            circleImageView = itemView.findViewById(R.id.image_profile);
            show_message = itemView.findViewById(R.id.show_message);
        }
    }

//    class MyViewHolder extends RecyclerView.ViewHolder{
//
//        CircleImageView circleImageView;
//        TextView show_message;
//
//        public MyViewHolder(@NonNull View itemView) {//show logo and content
//            super(itemView);
//            circleImageView = itemView.findViewById(R.id.image_profile);
//
//        }
//    }

    @Override
    public int getItemViewType(int position) {//Trả lại kiểu xem của mục tại vị trí
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getSender().equals(fuser.getUid())){
            return MSG_STYLE_RIGHT;
        }else{
            return MSG_STYLE_LEFT;
        }
    }
}
