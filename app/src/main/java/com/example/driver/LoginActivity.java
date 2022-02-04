package com.example.driver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.annotations.NotNull;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;


import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
   String mverificationId;
    FirebaseAuth mfirebaseaut;
    PhoneAuthProvider.ForceResendingToken mforceResendingToken;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    String Mobile_No;
    String Name;
    OtpView otpView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mfirebaseaut=FirebaseAuth.getInstance();
        otpView=findViewById(R.id.otp_view);
        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull @org.jetbrains.annotations.NotNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull @org.jetbrains.annotations.NotNull FirebaseException e) {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(s, token);
                mverificationId=s;
                mforceResendingToken=token;
                Toast.makeText(LoginActivity.this, "OTP send sucssesfuly", Toast.LENGTH_SHORT).show();
            }
        };

        Intent intent=getIntent();
         Mobile_No=intent.getStringExtra("Mobile_No");
         String lastname=intent.getStringExtra("lastname");
        Name= intent.getStringExtra("Name");
        Name=Name+" "+lastname;
        if(Mobile_No!=null){
            sendotp(Mobile_No);
            Toast.makeText(this, Name, Toast.LENGTH_SHORT).show();
        }

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(mverificationId,otp.trim());
                singInWithAuth(phoneAuthCredential);

            }
        });



    }

    private void singInWithAuth(PhoneAuthCredential phoneAuthCredential) {

        mfirebaseaut.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent=new Intent(LoginActivity.this,MapsActivity.class);
                            intent.putExtra("name",Name);
                            startActivity(intent);
                            finishAffinity();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    @Override
    protected void onStart() {
        super.onStart();

    }
    public void sendotp(String PhoneNumber){

        PhoneAuthOptions phoneAuthOptions=PhoneAuthOptions.newBuilder(mfirebaseaut)
                .setPhoneNumber(PhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(LoginActivity.this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);

    }
}