package com.example.sdbags;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    /**
     *
     */
    private Button CreateAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog LoadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CreateAccountButton = (Button) findViewById(R.id.register_btn);
        InputName = (EditText) findViewById(R.id.register_name_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText) findViewById(R.id.register_password_input);

        LoadingBar = new ProgressDialog(this);


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }

            private void createAccount() {
                String name = InputName.getText().toString();
                String phone = InputPhoneNumber.getText().toString();
                String password = InputPassword.getText().toString();

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this, "Please write your name...", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(phone)){
                    Toast.makeText(RegisterActivity.this, "Please input your phone number.", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Please input your password.", Toast.LENGTH_SHORT).show();
                }
                else {
                    LoadingBar.setTitle("Create Account");
                    LoadingBar.setMessage("Please waite while your credentials are being check");
                    LoadingBar.setCanceledOnTouchOutside(false);
                    LoadingBar.show();

                    validatePhoneNumber(name, phone, password);
                }
            }

            private void validatePhoneNumber(String name, String phone, String password) {

                // You need to add the firebase database dependency
                // After the dependency added, You need to reference it here.
                final DatabaseReference RootRef;
                RootRef = FirebaseDatabase.getInstance().getReference();
                RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!(snapshot.child("Users").child(phone).exists())){
                            HashMap<String, Object > userdataMap = new HashMap<>();
                            userdataMap.put("name", name);
                            userdataMap.put("phone", phone);
                            userdataMap.put("password", password);

                            RootRef.child("Users").child(phone).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Congratulations Your account has been created successfully.", Toast.LENGTH_LONG).show();
                                        LoadingBar.dismiss();

                                        /** Send the user back to the login activity if their account is created successfully */
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity2.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        LoadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network error!", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });


                        }
                        else {
                            Toast.makeText(RegisterActivity.this,  phone + "already exist.", Toast.LENGTH_LONG).show();
                            LoadingBar.dismiss();
                            Toast.makeText(RegisterActivity.this, "Please try again using another phone number", Toast.LENGTH_LONG).show();

                            /** send the user back to the main activity to create an account */
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
}