package com.mehrsoft.myinstagram.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.mehrsoft.myinstagram.R;
import com.orhanobut.hawk.Hawk;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostActivity extends AppCompatActivity {


    @BindView(R.id.activity_post_addedImageView)
    ImageView addedImageView;
    @BindView(R.id.activity_post_closeImageView)
    ImageView closeImageView;
    @BindView(R.id.activity_post_desEditText)
    EditText descriptionEdt;
    @BindView(R.id.post_activity_postTextView)
    TextView postTextView;


    Uri imageUri;
    String myUrl;
    StorageTask uploadTask;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        storageReference = FirebaseStorage.getInstance().getReference("posts");
        Hawk.init(this).build();

        ButterKnife.bind(this);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this, MainActivity.class));

            }
        });


        postTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(this);



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
                        throw Objects.requireNonNull(task.getException());
                    }
                    return fileRefrence.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
               if (task.isSuccessful()){
                   Uri downloadUri=task.getResult();
                   myUrl=downloadUri.toString();


                   DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");

                   String postId=reference.push().getKey();

                   HashMap<String,Object>hashMap=new HashMap<>();


                   hashMap.put("postid",postId);

                   hashMap.put("postimage",myUrl);
                   hashMap.put("description",descriptionEdt.getText().toString());
                   hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());



                   reference.child(postId).setValue(hashMap);
                   startActivity(new Intent(PostActivity.this,MainActivity.class));
                   finish();



               }else{
                   Toast.makeText(PostActivity.this, "Failed", Toast.LENGTH_SHORT).show();
               }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(PostActivity.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
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

            addedImageView.setImageURI(imageUri);


        } else {

            startActivity(new Intent(PostActivity.this, MainActivity.class));
            finish();
        }
    }
}
