package com.example.richardsenyange.breedlocator2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword,inputName,inputContact;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        Button btnSignIn = findViewById(R.id.sign_in_button);
        Button btnSignUp = findViewById(R.id.sign_up_button);
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        inputName=findViewById(R.id.Name);
        inputContact=findViewById(R.id.Contact);
        progressBar = findViewById(R.id.progressBar);

        //get firebase reference
        reference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://finalyear-eb76a.firebaseio.com/");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String contact=inputContact.getText().toString().trim();
                final String name=inputName.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                //adding user object
                if (TextUtils.isEmpty(contact)) {inputContact.setError("Enter Contact"); return; }
                if (TextUtils.isEmpty(name)) {inputName.setError("Enter Name"); return; }

                if (TextUtils.isEmpty(email)) {inputEmail.setError("Enter email address"); return; }

                if (TextUtils.isEmpty(password)) {inputPassword.setError("Enter password"); return; }
                if (password.length() < 6) { inputPassword.setError("Password too short, enter minimum 6 characters!"); return; }

                progressBar.setVisibility(View.VISIBLE);
                //create user
               inputContact.setEnabled(false);
               inputEmail.setEnabled(false);
               inputPassword.setEnabled(false);
               inputName.setEnabled(false);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                 startActivity(new Intent(getApplicationContext(),LoginActivity.class));

                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.

                                if (!task.isSuccessful()) {

                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),

                                            Toast.LENGTH_SHORT).show();
                                    inputContact.setEnabled(true);
                                    inputEmail.setEnabled(true);
                                    inputPassword.setEnabled(true);
                                    inputName.setEnabled(true);
                                } else {
                                    //get current created user's ID
                                    Intent getUserCategory=getIntent();
                                    String usercategory=getUserCategory.getStringExtra("usercategory");
                                    String userId =auth.getCurrentUser().getUid();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name).build();

                                    auth.getCurrentUser().updateProfile(profileUpdates);

                                    //save user to Firebase Database using ID
                                    saveUserToFirebase(email,name,contact, userId,usercategory,reference);
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        });

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    void saveUserToFirebase(String name, String email, String contact, String Id,String usercat, DatabaseReference ref){
        Users users = new Users( email,name,contact,usercat);

        ref.child("Users").child(Id).setValue(users);
    }


}