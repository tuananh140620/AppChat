package com.example.project;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    EditText username, password, cfpassword, email, phone;
    Button regisButton;

    FirebaseAuth auth;
    DatabaseReference reference;

    Pattern pattern = Pattern.compile("^\\d{10}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.editTextTextPersonName);
        password = findViewById(R.id.editPassword);
        cfpassword = findViewById(R.id.editCfPassword);
        email = findViewById(R.id.editEmail);
        phone = findViewById(R.id.editPhone);
        regisButton = findViewById(R.id.btnSignUp);

        auth = FirebaseAuth.getInstance();

        regisButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_password = password.getText().toString();
                String txt_cfpassword = cfpassword.getText().toString();
                String txt_email = email.getText().toString();
                String txt_phone = phone.getText().toString();

                if(TextUtils.isEmpty(txt_username) || TextUtils.isEmpty(txt_password)
                        || TextUtils.isEmpty(txt_cfpassword) || TextUtils.isEmpty(txt_email)
                        || TextUtils.isEmpty(txt_phone)) {
                    Toast.makeText(SignUpActivity.this, "Cần nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                }else if(txt_password.length() < 6){
                    Toast.makeText(SignUpActivity.this, "Password phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                }
                else if(!txt_password.equals(txt_cfpassword)) {
                    Toast.makeText(SignUpActivity.this, "Password and RePassword phải giống nhau", Toast.LENGTH_SHORT).show();
                }else if(!txt_phone.matches(String.valueOf(pattern))){
                    Toast.makeText(SignUpActivity.this, "Số điện thoại phải có đủ 10 số", Toast.LENGTH_SHORT).show();
                }else  {
                    register(txt_username, txt_password, txt_email, txt_phone);
                }
            }
        });
    }

    private void register(String username, String password, String email, String phone){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("users").child(userid);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username);
                            hashMap.put("phone", phone);
                            hashMap.put("email", email);
                            hashMap.put("imageURL", "default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else{
                            Toast.makeText(SignUpActivity.this, "Bạn không thể đăng ký bằng email này", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}