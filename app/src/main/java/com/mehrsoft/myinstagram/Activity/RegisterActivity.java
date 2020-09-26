package com.mehrsoft.myinstagram.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mehrsoft.myinstagram.R;
import com.orhanobut.hawk.Hawk;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.register_email_edt)
    EditText email;
    @BindView(R.id.register_password_edt)
    EditText password;
    @BindView(R.id.register_fullName_edt)
    EditText fullName;
    @BindView(R.id.register_username_edt)
    EditText userName;
    @BindView(R.id.register_button)
    Button registerButton;
    @BindView(R.id.register_txtLogin)
    TextView txtLogin;


    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        Hawk.init(this).build();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.register_txtLogin)
    public void login() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @OnClick(R.id.register_button)
    public void registerOnClick() {
        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.show();
        String str_password = password.getText().toString();
        String str_username = userName.getText().toString();
        String str_fullName = fullName.getText().toString();
        String str_email = email.getText().toString();

            register(str_username, str_email, str_fullName, str_password);

    }


    private void register(String userName, String email, String fullName, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userId = firebaseUser.getUid();


                   reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                    HashMap<String, Object> hashMap = new HashMap<>();


                    hashMap.put("id", userId);
                    hashMap.put("username", userName.toLowerCase());
                    hashMap.put("fullname", fullName);
                    hashMap.put("bio", "");
                    hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/whatsapp-6028d.appspot.com/o/instagram_logo.png?alt=media&token=351b408a-f8eb-4c3f-ba63-de802feb72f5");

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();

                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        }
                    });


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "You Cant Register with this Email Or password", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
