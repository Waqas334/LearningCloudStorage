package com.androidbull.learningcloudstorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private StorageReference storageReference;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageview);

        storageReference = FirebaseStorage.getInstance().getReference();
//        storageReference.putFile()

    }

    //Request Code for starting activity for result method
    private final int PICK_IMAGE = 001;
    //Saving image uri so that we can use it to upload the file
    private Uri imageUri;

    //This function will be called when "Select Image" button will be clicked
    public void oneClicked(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    //This function will be called when user selects an image and returns to app
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //We need to check if the request code is same and user actually picked some image
        if(requestCode ==PICK_IMAGE && resultCode == RESULT_OK){
            //Getting the Image URI
            imageUri = data.getData();
            try{
                //Setting the image to ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                mImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadPhoto(View view) {
        if(imageUri!=null)
            storageReference.child("images/test_image.png").putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Upload completed", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(MainActivity.this, "Something went wrong: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onComplete: " +task.getException().getLocalizedMessage() );
                    }
                }
            });
    }

    public void clearImage(View view) {
        //We already have the reference of Image view, so now we are setting null to it
        mImageView.setImageBitmap(null);

    }

    public void downloadPhoto(View view) {
        StorageReference imageReference = FirebaseStorage.getInstance().getReference().child("images/1.png");

        imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Setting the image to imageView using Glide Library
                Glide.with(MainActivity.this).load(uri.toString()).into(mImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: something went wrong: "+ e.getMessage() );

            }
        });

    }
}
