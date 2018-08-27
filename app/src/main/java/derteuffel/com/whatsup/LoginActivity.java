package derteuffel.com.whatsup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText _login_email;
    private EditText _login_passwd;
    private Button _login_btn;
    private ProgressBar _login_pgb;
    private TextView _link_reg;


    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        _login_email = (EditText)findViewById(R.id.login_email);
        _login_passwd = (EditText)findViewById(R.id.login_password);
        _login_pgb = (ProgressBar)findViewById(R.id.login_pgb);
        _login_btn = (Button)findViewById(R.id.login_btn);
        _link_reg = (TextView)findViewById(R.id.link_reg);

        mAuth =FirebaseAuth.getInstance();


        _login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= _login_email.getText().toString();
                String password= _login_passwd.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    _login_pgb.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                sendToMain();
                            }else {
                                String errorMessage=task.getException().getMessage();
                                Toast.makeText(LoginActivity.this,"Error:"+errorMessage,Toast.LENGTH_LONG).show();
                            }

                            _login_pgb.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });

        _link_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToRegister();
            }
        });
    }

    private void sendToRegister() {
        Intent regeisterIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(regeisterIntent);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser= mAuth.getCurrentUser();

        if (currentUser!=null){
            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent= new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
