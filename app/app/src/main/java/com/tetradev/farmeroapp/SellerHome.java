package com.tetradev.farmeroapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


public class SellerHome extends AppCompatActivity {


    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private ImageView imageView;
    private EditText txtImageName;
    private EditText txtImageDesc;
    private EditText txtImagePrice;
    private EditText txtImageContact;
    private Uri imgUri;


    private Button btncamera;
    private Bitmap image;

    //longpressed
    private long backPressedTime;
    private Toast backToast;


    public static final String FB_STORAGE_PATH = "image/";
    public static final String FB_DATABASE_PATH = "image";
    public static final int REQUEST_CODE = 1234;

    Button btnLogout;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference(FB_DATABASE_PATH);


        imageView = (ImageView) findViewById(R.id.image_view_detail);
        txtImageName = (EditText) findViewById(R.id.txtImageName);
        txtImageDesc = (EditText) findViewById(R.id.txtImageDesc);
        txtImagePrice = (EditText) findViewById(R.id.txtImagePrice);
        txtImageContact = (EditText) findViewById(R.id.txtImageContact);


        btncamera = findViewById(R.id.btnOpenCamera);

        if (ContextCompat.checkSelfPermission(SellerHome.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(SellerHome.this, new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
            }
        });



        btnLogout = findViewById(R.id.logout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intToMain = new Intent(SellerHome.this, SellerSignIn.class);
                startActivity(intToMain);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {


            if (backPressedTime + 2000 > System.currentTimeMillis() ){
                backToast.cancel();
                super.onBackPressed();
                return;
            }
            else{
                backToast = Toast.makeText(getBaseContext(),"Press back again to exit",Toast.LENGTH_SHORT);
                backToast.show();
            }
            backPressedTime = System.currentTimeMillis();

        }


    public void btnBrowse_Click(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_CODE);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imgUri = data.getData();

            try {
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bm);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        else if (requestCode == 100){
            try {
                imgUri = data.getData();
                image = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(image);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public String getImageExt(Uri uri) {


        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }


    @SuppressWarnings("VisibleForTests")
    public void btnUpload_Click(View view) {

        if (imgUri != null) {

            String name = txtImageName.getText().toString().trim();
            String desc = txtImageDesc.getText().toString().trim();
            String price = txtImagePrice.getText().toString().trim();
            String contact = txtImageContact.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Product Name is mandatory", Toast.LENGTH_LONG).show();
            }
            else if (desc.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Description is mandatory", Toast.LENGTH_LONG).show();
            }
            else if (price.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Price is mandatory", Toast.LENGTH_LONG).show();
            }
            else if (contact.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Contact Details is mandatory", Toast.LENGTH_LONG).show();
            }

            else {


                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Uploading");
                dialog.show();

                //Get the storage reference
                StorageReference ref = mStorageRef.child(FB_STORAGE_PATH + System.currentTimeMillis() + "." + getImageExt(imgUri));

                //Add file to reference

                ref.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        //Dimiss dialog when success
                        dialog.dismiss();
                        //Display success toast msg
                        Toast.makeText(getApplicationContext(), "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        //  ImageUpload imageUpload = new ImageUpload(txtImageName.getText().toString(),txtImageDesc.getText().toString(),txtImagePrice.getText().toString(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

//                    ImageUpload imageUpload1 = new ImageUpload(txtImageDesc.getText().toString(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                    ImageUpload imageUpload2 = new ImageUpload(txtImagePrice.getText().toString(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                        //Save image info in to firebase database
                        //  String uploadId = mDatabaseRef.push().getKey();
                        //   mDatabaseRef.child(uploadId).setValue(imageUpload);


//
//                    mDatabaseRef.child(uploadId).setValue(imageUpload1);
//                    mDatabaseRef.child(uploadId).setValue(imageUpload2);


                        Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();

                        while (!urlTask.isSuccessful()) ;

                        Uri downloadUrl = urlTask.getResult();


                        ImageUpload upload = new ImageUpload(txtImageName.getText().toString().trim(), txtImageDesc.getText().toString().trim(),
                                txtImagePrice.getText().toString().trim(), txtImageContact.getText().toString().trim(), downloadUrl.toString());


                        String uploadId = mDatabaseRef.push().getKey();


                        mDatabaseRef.child(uploadId).setValue(upload);


                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                //Dimiss dialog when error
                                dialog.dismiss();
                                //Display err toast msg
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                //Show upload progress

                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded " + (int) progress + "%");
                            }
                        });
            }

        }
        else if (image!=null) {

            String name = txtImagePrice.getText().toString().trim();
            String desc = txtImageDesc.getText().toString().trim();
            String price = txtImagePrice.getText().toString().trim();
            String contact = txtImageContact.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Product Name is mandatory", Toast.LENGTH_LONG).show();
            } else if (desc.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Description is mandatory", Toast.LENGTH_LONG).show();
            } else if (price.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Price is mandatory", Toast.LENGTH_LONG).show();
            } else if (contact.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Contact Details is mandatory", Toast.LENGTH_LONG).show();
            } else {

                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setTitle("Uploading");
                dialog.show();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                final String random = UUID.randomUUID().toString();
                StorageReference imageRef = mStorageRef.child("image/" + random);

                byte[] b = stream.toByteArray();
                imageRef.putBytes(b)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUri = uri;
                                    }
                                });

                                dialog.dismiss();

                                Toast.makeText(SellerHome.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();

                                while (!urlTask.isSuccessful()) ;

                                Uri downloadUrl = urlTask.getResult();

                                ImageUpload upload = new ImageUpload(txtImageName.getText().toString().trim(), txtImageDesc.getText().toString().trim(),
                                        txtImagePrice.getText().toString().trim(), txtImageContact.getText().toString().trim(), downloadUrl.toString());
                                String uploadId = mDatabaseRef.push().getKey();

                                mDatabaseRef.child(uploadId).setValue(upload);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                dialog.dismiss();
                                Toast.makeText(SellerHome.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                                //Show upload progress

                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                dialog.setMessage("Uploaded " + (int) progress + "%");
                                //dialog.dismiss();
                            }
                        });

            }

        }
        else {
            Toast.makeText(getApplicationContext(), "Please select image", Toast.LENGTH_SHORT).show();
        }
    }
}