package com.example.kishorebaktha.firebaseimage;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
    private Button mSelectImage;
    private StorageReference mStorage;
    private static int GALLERY_INTENT=101;
    private static int Camera_Request_code=1;
    private ProgressDialog mProgressDialog;
    ImageView img;
    private Button mUploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStorage= FirebaseStorage.getInstance().getReference();
        mSelectImage=(Button)findViewById(R.id.img);
        mUploadImage=(Button)findViewById(R.id.retimg);
        mProgressDialog = new ProgressDialog(this);
        img=(ImageView)findViewById(R.id.imageView);
        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);

            }
        });
        mUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,Camera_Request_code);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK)
        {
            mProgressDialog.setMessage("Uploading....");
            mProgressDialog.show();
            Uri uri=data.getData();
            StorageReference filepath=mStorage.child("PHOTOS").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(),"UPLOAD DONE",Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                }
            });
        }
         if(requestCode==Camera_Request_code&&resultCode==RESULT_OK)
        {
            final Uri uri=data.getData();
            mProgressDialog.setMessage("Uploading....");
            mProgressDialog.show();
            StorageReference filepath=mStorage.child("PHOTOS").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    mProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"UPLOAD DONE",Toast.LENGTH_SHORT).show();
                      Uri downloaduri= taskSnapshot.getDownloadUrl();
                    Picasso.with(MainActivity.this).load(downloaduri).fit().centerCrop().into(img);

                }
            });

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
