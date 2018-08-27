package derteuffel.com.whatsup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView _setup_image;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private ProgressBar _setting_pgb;

    private Uri mainImageUri=null;

    private EditText _setting_name;
    private Button _setting_btn;

    private String user_id;
    private boolean isChanged=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        Toolbar _setup_tlb= findViewById(R.id.setup_tlb);
        setSupportActionBar(_setup_tlb);
        getSupportActionBar().setTitle("Parametres");

        _setup_image=findViewById(R.id.setup_image);
        _setting_name=findViewById(R.id.setting_name);
        _setting_btn= findViewById(R.id.setting_btn);
        _setting_pgb=findViewById(R.id.setting_pgb);

        firebaseAuth= FirebaseAuth.getInstance();

        user_id=firebaseAuth.getCurrentUser().getUid();
        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();

        _setting_pgb.setVisibility(View.VISIBLE);
        _setting_btn.setEnabled(false);

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().exists()){
                        Toast.makeText(SettingActivity.this, "L'utilisateur existe", Toast.LENGTH_LONG).show();

                       String name= task.getResult().getString("name");
                       String image= task.getResult().getString("image");

                       mainImageUri = Uri.parse(image);

                       _setting_name.setText(name);

                       RequestOptions placeHolderRequest= new RequestOptions();
                       placeHolderRequest.placeholder(R.drawable.ic_launcher_background);
                       Glide.with(SettingActivity.this).setDefaultRequestOptions(placeHolderRequest).load(image).into(_setup_image);

                    }
                }else {
                    String error=task.getException().getMessage();
                    Toast.makeText(SettingActivity.this, "Firestore retrieve Error:"+error, Toast.LENGTH_LONG).show();
                   //Toast.makeText(SettingActivity.this, "data doesn't exist", Toast.LENGTH_LONG).show();
                }
                _setting_pgb.setVisibility(View.INVISIBLE);
                _setting_btn.setEnabled(true);
            }
        });




        _setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                final String user_name= _setting_name.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainImageUri !=null){

                    _setting_pgb.setVisibility(View.VISIBLE);
                if (isChanged){



                     user_id= firebaseAuth.getCurrentUser().getUid();

                    StorageReference image_path= storageReference.child("profile_images").child(user_id+"jpg");

                    image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if (task.isSuccessful()){

                               storeFirestore(task, user_name);
                            }else{
                                String error= task.getException().getMessage();
                                Toast.makeText(SettingActivity.this, "Image Error:"+error, Toast.LENGTH_LONG).show();

                                _setting_pgb.setVisibility(View.INVISIBLE);

                            }


                        }
                    });

                }else {
                    storeFirestore(null, user_name);
                }
            }

            }
        });


        _setup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.JELLY_BEAN){

                    if (ContextCompat.checkSelfPermission(SettingActivity.this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SettingActivity.this, "Accès refuser", Toast.LENGTH_LONG).show();
                        ActivityCompat.requestPermissions(SettingActivity.this, new String[]{READ_EXTERNAL_STORAGE},1);
                    }else{
                       // Toast.makeText(SettingActivity.this, "Accès autorisé", Toast.LENGTH_LONG).show();
                       bringImagePicker();
                    }
                }else{
                    bringImagePicker();
                }

            }
        });


    }

    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task, String user_name) {

        Uri download_uri;
        if (task != null){
            download_uri= task.getResult().getDownloadUrl();
        }else {
            download_uri= mainImageUri;
        }

        // Toast.makeText(SettingActivity.this, "L'image a ete telecharger", Toast.LENGTH_LONG).show();

        Map<String,String> userMap= new HashMap<>();
        userMap.put("name", user_name);
        userMap.put("image", download_uri.toString());
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SettingActivity.this, "Votre profile a été modifier avec succès", Toast.LENGTH_LONG).show();

                    sendToMain();


                }else {
                    String error=task.getException().getMessage();
                    Toast.makeText(SettingActivity.this, "Firestore Error:"+error, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void sendToMain() {
        Intent mainIntent= new Intent(SettingActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void bringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(SettingActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                mainImageUri= result.getUri();
                _setup_image.setImageURI(mainImageUri);
                isChanged= true;

            }else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error= result.getError();
            }
        }
    }
}
