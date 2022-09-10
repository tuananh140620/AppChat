package com.example.project;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.project.adapter.FragmentAdapter;
import com.example.project.model.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    String user_id;
    TabLayout tabLayout;
    ViewPager2 pager2;
    FragmentAdapter adapter;

    Button close;
    Button add;

    CircleImageView avatar;
    Button close_user_profile;
    Button close_edit_profile;
    TextView username;
    TextView email;
    TextView phone;
    CircleImageView userAvatar;
    Button logout;
    ImageButton setting;
    Button resetPassword;




    FirebaseUser firebaseUser;
    DatabaseReference reference;
    FirebaseAuth auth;
    Pattern pattern = Pattern.compile("^\\d{10}$");

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //
        add = findViewById(R.id.btn_add);
        close = findViewById(R.id.btn_close);
        tabLayout = findViewById(R.id.tab_layout);
        pager2 = findViewById(R.id.view_paper2);
        avatar = findViewById(R.id.avatar_mini);

        //
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        //
        Toast.makeText(this, "Đăng nhập thành công!!!", Toast.LENGTH_SHORT).show();

        //
        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        pager2.setAdapter(adapter);
        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Friend"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                    if (user.getImageURL().equals("default")) {
                        avatar.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getBaseContext()).load(user.getImageURL()).into(avatar);
                    }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error", "Can't get data from database");
            }
        });



        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUserProfileDialog(Gravity.CENTER);
            }
        });


    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(){
        final ProgressDialog pd = new ProgressDialog(HomeActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null){
            final  StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }

                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", ""+mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(HomeActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(HomeActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(HomeActivity.this, "Upload in preogress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    //Display user profile dialog
    private void openUserProfileDialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_user_profile);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);

        close_user_profile = dialog.findViewById(R.id.cancel_profile_dialog);
        username = dialog.findViewById(R.id.userName);
        email = dialog.findViewById(R.id.userEmail);
        phone = dialog.findViewById(R.id.userPhone);
        userAvatar = dialog.findViewById(R.id.userAvatar);
        logout = dialog.findViewById(R.id.logoutBtn);
        setting = dialog.findViewById(R.id.setting);


        //Display setting dialog event
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openEditProfileDialog(Gravity.CENTER);
            }
        });

        //Display user info in user's profile
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                if (user.getImageURL().equals("default")) {
                    userAvatar.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(dialog.getContext()).load(user.getImageURL()).into(userAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error", "Can't get data from database");
            }
        });

        //Logout account. Stop auto logging event
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(v.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //Close User profile dialog
        close_user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    //Edit profile dialog
    private void openEditProfileDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_profile);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);

        TextView editUserName, editPhone, editPassword, editCfPassword, editEmail;
        CircleImageView editAvatar;
        ImageButton editImage;

        close_edit_profile = dialog.findViewById(R.id.done);
        editUserName = dialog.findViewById(R.id.editUserName);
        editPhone = dialog.findViewById(R.id.editPhone);
        editEmail = dialog.findViewById(R.id.editEmail);
        resetPassword = dialog.findViewById(R.id.resetpass);
//        editPassword = dialog.findViewById(R.id.editPassword);
//        editCfPassword = dialog.findViewById(R.id.editCfPassword);
        editAvatar = dialog.findViewById(R.id.editUserAvatar);
        Button done = dialog.findViewById(R.id.done);
        Button reset = dialog.findViewById(R.id.resetpass);

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickResetPassword();
            }
        });
        //Display dialog edit profile event
        close_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        //Display user info in user's edit profile
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                editUserName.setText(user.getUsername());
                editPhone.setText(user.getPhone());
                
                editEmail.setText(user.getEmail());
                user_id = user.getId();
                if (user.getImageURL().equals("default")) {
                    editAvatar.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(dialog.getContext()).load(user.getImageURL()).into(editAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Error", "Can't get data from database");
            }
        });

        editAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        //Update function for edit profile
        auth = FirebaseAuth.getInstance();

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = editUserName.getText().toString();
                String txt_phone = editPhone.getText().toString();

                if(!txt_phone.matches(String.valueOf(pattern))){
                    Toast.makeText(HomeActivity.this, "Phone must contains number and 11 characters", Toast.LENGTH_SHORT).show();
                }else if(editUserName == null || editUserName.length() == 0 ) {
                    Toast.makeText(HomeActivity.this, "Username must not blank", Toast.LENGTH_SHORT).show();
                }else{
                    reference = FirebaseDatabase.getInstance().getReference("users");
                    reference.child(user_id).child("username").setValue(txt_username);
                    reference.child(user_id).child("phone").setValue(txt_phone);
                    dialog.dismiss();
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        dialog.show();

    }

        private void onClickResetPassword(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email.getText().toString();;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Kiểm tra Email", Toast.LENGTH_SHORT).show();

                        } else{
                            Toast.makeText(HomeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}