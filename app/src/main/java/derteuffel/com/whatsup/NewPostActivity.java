package derteuffel.com.whatsup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {


    //private static final int MAX_LENGTH = 100;
    private Toolbar _new_post_tlb;

    private ImageView _new_image_post;
    private EditText _title_post;
    private EditText _post_desc;
    private Button _add_post_btn;
    private ProgressBar _new_post_pgb;

    private Uri post_image_uri=null;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;

    private String current_user_id;

    private Bitmap compressedImageFile;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);


        _new_post_tlb = findViewById(R.id.new_post_tlb);

        setSupportActionBar(_new_post_tlb);
        getSupportActionBar().setTitle("Ajouter une publiction !");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        _new_image_post= findViewById(R.id.new_image_post);
        _title_post= findViewById(R.id.title_post);
        _post_desc= findViewById(R.id.post_desc);
        _add_post_btn= findViewById(R.id.add_post_btn);
        _new_post_pgb= findViewById(R.id.new_post_pgb);



        firebaseFirestore= FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth= FirebaseAuth.getInstance();

        current_user_id=firebaseAuth.getCurrentUser().getUid();


        _new_image_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1,1)
                        .start(NewPostActivity.this);
            }
        });

        _add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String title= _title_post.getText().toString();
                final String desc= _post_desc.getText().toString();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc) && post_image_uri !=null){
                    _new_post_pgb.setVisibility(View.VISIBLE );

                    final String randomName= UUID.randomUUID().toString();
                    StorageReference filePath= storageReference.child("post_images").child(randomName+"jpg");

                    filePath.putFile(post_image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {

                            final String downloadUri= task.getResult().getDownloadUrl().toString();
                            if (task.isSuccessful()){

                                File newImageFile= new File(post_image_uri.getPath());
                                try {
                                    compressedImageFile= new Compressor(NewPostActivity.this)
                                            .setMaxHeight(100)
                                            .setMaxWidth(100)
                                            .setQuality(2)
                                            .compressToBitmap(newImageFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                ByteArrayOutputStream baos= new ByteArrayOutputStream();
                                compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] thumbData= baos.toByteArray();

                                UploadTask uploadTask= storageReference.child("post_image/thumbs")
                                        .child(randomName+".jpg").putBytes(thumbData);

                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String downloadThummbUri = taskSnapshot.getDownloadUrl().toString();

                                        Map<String, Object> postMap= new HashMap<>();
                                        postMap.put("image_url", downloadUri);
                                        postMap.put("image_thumb", downloadThummbUri);
                                        postMap.put("title", title);
                                        postMap.put("desc", desc);
                                        postMap.put("user_id",current_user_id);
                                        postMap.put("timeStamp",FieldValue.serverTimestamp() );

                                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if (task.isSuccessful()){
                                                    Toast.makeText(NewPostActivity.this, "Votre publication a bien été ajouter", Toast.LENGTH_LONG).show();

                                                    sendToMain();

                                                }else {

                                                }
                                                _new_post_pgb.setVisibility(View.INVISIBLE);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Error handLing
                                    }
                                });

                            }else {

                                _new_post_pgb.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                }

            }
        });
    }

    private void sendToMain() {
        Intent mainIntent= new Intent(NewPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){

                post_image_uri=  result.getUri();
                _new_image_post.setImageURI(post_image_uri);
            }else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error= result.getError();
            }
        }
    }

   /* public static String random(){
        Random generator= new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength= generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i=0; i<randomLength;i++){
            tempChar =(char)(generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return  randomStringBuilder.toString();
    }*/
}
