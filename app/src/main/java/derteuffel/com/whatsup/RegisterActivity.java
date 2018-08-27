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

public class RegisterActivity extends AppCompatActivity {

    private EditText _reg_email;
    private EditText _reg_passwd;
    private EditText _reg_passwd_confirm;
    private Button _reg_btn;
    private ProgressBar _reg_pgb;
    private TextView _link_login;

    private FirebaseAuth mAuth ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        _reg_email = (EditText)findViewById(R.id.reg_email);
        _reg_passwd = (EditText)findViewById(R.id.reg_password);
        _reg_passwd_confirm = (EditText)findViewById(R.id.reg_password_confirm);
        _reg_pgb = (ProgressBar)findViewById(R.id.reg_pgb);
        _reg_btn = (Button)findViewById(R.id.reg_btn);
        _link_login = (TextView)findViewById(R.id.link_login);


        mAuth =FirebaseAuth.getInstance();


        _reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email =_reg_email.getText().toString();
                String password = _reg_passwd.getText().toString();
                String confirm_password =_reg_passwd_confirm.getText().toString();



                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) & !TextUtils.isEmpty(confirm_password)){

                    if (password.equals(confirm_password)){
                        _reg_pgb.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    sendToMain();
                                }else {
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(RegisterActivity.this,"Error:"+errorMessage, Toast.LENGTH_LONG).show();

                                }

                                _reg_pgb.setVisibility(View.INVISIBLE);
                            }
                        });

                    }else {
                        Toast.makeText(RegisterActivity.this,"Veuillez entrer deux mots de passe identique", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        _link_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendToLogin();
            }
        });
    }

    private void sendToLogin() {
        Intent loginItent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginItent);
        finish();
    }


    @Override
    public  void onStart() {

        super.onStart();

        FirebaseUser currentUser =mAuth.getCurrentUser();


        if (currentUser!= null){

            sendToMain();
        }
    }

    private void sendToMain() {
        Intent mainIntent =new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
