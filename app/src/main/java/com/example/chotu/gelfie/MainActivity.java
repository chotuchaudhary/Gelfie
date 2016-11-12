package com.example.chotu.gelfie;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.chotu.gelfie.R;
import com.example.chotu.gelfie.grid.MainActivity1;
import com.example.chotu.gelfie.model.UserDetails;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends Activity {
    public String e_mail;
    Button login;
    Button signUp;
    String TAG = "from main activity";
    EditText email;
    EditText password;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    Firebase mFirebase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);


        mAuth=FirebaseAuth.getInstance();
        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null)
                {
                    Intent intent=new Intent(MainActivity.this,MainActivity1.class);
                    e_mail=firebaseAuth.getCurrentUser().getEmail();
                    e_mail=e_mail.substring(0,e_mail.indexOf("@"));
                    intent.putExtra("email",e_mail);
                    startActivity(intent);
                }
            }
        };
        signUp=(Button)findViewById(R.id.signup_main);
        login = (Button) findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startSignIn();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setContentView(R.layout.sign_up);
                Button btn=(Button)findViewById(R.id.signup);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        startSignUp();

                    }
                });

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    public void startSignIn() {
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        String emailText = email.getText().toString();
         // e_mail=emailText.substring(0,emailText.indexOf("@"));

        String passwordText = password.getText().toString();
        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)) {
            Toast.makeText(this,"wrong username or password",Toast.LENGTH_LONG).show();

        } else {
            mAuth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {


                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {

                                Toast.makeText(MainActivity.this, " successfully sign in..",
                                        Toast.LENGTH_SHORT).show();
                            }

                            //
                        }
                    });
        }
    }
    public void startSignUp() {

        EditText editEmail=(EditText)findViewById(R.id.email_sign) ;
        EditText editConfirm=(EditText)findViewById(R.id.confirm_sign);
        EditText editPass=(EditText)findViewById(R.id.password_sign);
        final String emailText = editEmail.getText().toString();
        e_mail=emailText;
        String passwordText = editPass.getText().toString();
        String confirmText=editConfirm.getText().toString();
        if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText)||!passwordText.equals(confirmText)) {
            Toast.makeText(this,"wrong username or password",Toast.LENGTH_LONG).show();

        } else {
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           // Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "you have successfully registered!",
                                        Toast.LENGTH_SHORT).show();
                                 // intialiseUser(emailText);

                            }else
                            {
                                Toast.makeText(MainActivity.this, "please enter a valid email and at least 6 character password ! ",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }

    public void intialiseUser(String email)
    {


     //   UserDetails user=new UserDetails();
       // childRef.setValue(user);


    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();



    }
    @Override
    public void onBackPressed() {
        // finish() is called in super: we only override this method to be able to override the transition
        //  super.onBackPressed();

        moveTaskToBack(true);

        // overridePendingTransition(R.anim.slide_out, R.anim.slide_in);
    }



}




