package com.mehrsoft.myinstagram.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mehrsoft.myinstagram.Model.User;
import com.mehrsoft.myinstagram.R;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    //init EditText
    @BindView(R.id.activity_editProfile_bioEditText)
    MaterialEditText bioInput;
    @BindView(R.id.activity_editProfile_FullNameEditText)
    MaterialEditText fullNameInput;
    @BindView(R.id.activity_editProfile_userNameEditText)
    MaterialEditText userNameInput;
    // init Button
    @BindView(R.id.activity_editProfile_changePhotoButton)
    MaterialButton changePhotoButton;
    @BindView(R.id.activity_editProfile_saveButton)
    MaterialButton saveButton;
    //init ImageView
    @BindView(R.id.activity_editProfile_CloseImageView)
    ImageView closeImageView;
    @BindView(R.id.activity_editProfile_profileImageView)
    CircleImageView profileImageView;

    private FirebaseUser firebaseUser;
    private Uri imageUri;

    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                userNameInput.setText(user.getUsername());
                fullNameInput.setText(user.getFullname());
                bioInput.setText(user.getBio());

                Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImageView);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        changePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(fullNameInput.getText().toString(), userNameInput.getText().toString(), bioInput.getText().toString());
            }
        });


    }

    private void updateProfile(String fullName, String userName, String bio) {
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("Updating");

        dialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().
                getReference("Users").child(firebaseUser.getUid());


        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("fullname",fullName);
        hashMap.put("username",userName);
        hashMap.put("bio",bio);


        reference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    dialog.dismiss();

                }else
                    Toast.makeText(EditProfileActivity.this, "Please Try Again!!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getFileExist(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    private void uploadImage(){

        ProgressDialog dialog=new ProgressDialog(this);

        dialog.setMessage("Posting");

        dialog.show();

        if (imageUri!=null){
            StorageReference fileRefrence=storageReference.child(System.currentTimeMillis() + "." +getFileExist(imageUri));

            uploadTask =fileRefrence.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {

                    if (!task.isComplete())
                    {
                        throw task.getException();
                    }
                    return fileRefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                      String  myUrl=downloadUri.toString();


                        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());




                        HashMap<String,Object>hashMap=new HashMap<>();




                        hashMap.put("imageurl",""+myUrl);


                        reference.updateChildren(hashMap);
                        dialog.dismiss();




                    }else{
                        Toast.makeText(EditProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(EditProfileActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "NoImageSelected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            imageUri = result.getUri();

         uploadImage();


        } else {

            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            finish();
        }
    }
}
