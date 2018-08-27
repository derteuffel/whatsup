package derteuffel.com.whatsup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView _bottom_bav;
    private FrameLayout _main_frame;

   // private FloatingActionButton _add_post_btn;
    private HomeFragment _homeFragment;
    private ComplementFragment _ComplementFragment;

    private FirebaseAuth mAuth =FirebaseAuth.getInstance();
    private String current_user_id;
    private FirebaseFirestore firebaseFirestore;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar _main_tlb= (Toolbar) findViewById(R.id.main_tlb);

        setSupportActionBar(_main_tlb);
        getSupportActionBar().setTitle("Actualités");

        if (mAuth.getCurrentUser()!=null){




        _bottom_bav=(BottomNavigationView)findViewById(R.id.bottom_nav);
        _main_frame=(FrameLayout)findViewById(R.id.main_frame);
       // _add_post_btn= findViewById(R.id.add_post_fbtn);

        _homeFragment= new HomeFragment();
        _ComplementFragment = new ComplementFragment();
        firebaseFirestore=FirebaseFirestore.getInstance();

        setFragment(_homeFragment);

        /*_add_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);
                finish();
            }
        });*/


        _bottom_bav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.bottom_home_menu:

                        setFragment(_homeFragment);
                        return true;

                    case R.id.dashbord_menu_bottom:
                        setFragment(_ComplementFragment);
                        return true;

                        default:
                            return false;
                }
            }
        });
        }
    }

    private void setFragment( Fragment fragment) {

        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }


    @Override
    protected void onStart() {

        super.onStart();

        FirebaseUser currentUser =mAuth.getCurrentUser();


        if (currentUser==null){
            sendToLogin();
        }else{

            current_user_id = mAuth.getCurrentUser().getUid();


            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        if (!task.getResult().exists()){
                            sendToSetting();
                        }
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error:"+error, Toast.LENGTH_LONG).show();
                    }
                }
            });


    }

    }

    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_logout:
                logOut();
                return true;
            case R.id.menu_setting:
                sendToSetting();
                return true;

            default:
                return false;
        }
    }

    private void sendToSetting() {
        Intent settingIntent =new Intent(MainActivity.this, SettingActivity.class);
        startActivity(settingIntent);
        finish();
    }

    private void logOut() {
        mAuth.signOut();
        sendToLogin();
    }

    private void sendToLogin() {
        Intent loginIntent= new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
