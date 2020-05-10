package com.masjidsolutions.mymasjid;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText mEmail,mPassword;
    Button mLogin;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail      =   findViewById(R.id.email);
        mPassword   =   findViewById(R.id.pwd);
        mLogin   =   findViewById(R.id.login);
        fAuth       =  FirebaseAuth.getInstance();

        Button forgotPwdbtn = (Button) findViewById( R.id.buttonForgotPwd);
        forgotPwdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity( new Intent( getApplicationContext(), Forgot.class ) );
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = mEmail.getText().toString().trim();
                String pwd = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    return;
                }

                if (TextUtils.isEmpty(pwd)) {
                    mPassword.setError("Password is Required.");
                    return;
                }

                if (pwd.length() < 8) {
                    mPassword.setError("Password Must be 8 Characters.");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(Login.this, "Error !" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }

                    }
                });





            }
        });







    }
}
