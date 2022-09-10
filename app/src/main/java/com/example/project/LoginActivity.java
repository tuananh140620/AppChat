package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username, password;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;



    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Tự động đăng nhập
        if (firebaseUser != null) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        auth = FirebaseAuth.getInstance();

        username = (EditText) findViewById(R.id.editTextTextPersonName);
        password = (EditText) findViewById(R.id.editTextTextPassWord);


        Button loginButton = (Button) findViewById(R.id.btnSignIn);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty((txt_username)) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(LoginActivity.this, "Cần nhập đủ Email vs Password", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(txt_username, txt_password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Sai Email hoặc Password", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        Button registerButton = (Button) findViewById(R.id.btnSignUp);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        Button forgotButton = (Button) findViewById(R.id.forgotPassword);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.forgotPassword:
            break;
        }
    }
}