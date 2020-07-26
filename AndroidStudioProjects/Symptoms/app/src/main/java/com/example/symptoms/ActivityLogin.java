package com.example.symptoms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityLogin extends AppCompatActivity {

    //classes instances
    private EditText email, pass;
    private String loginEmail, loginPassword;
    private Button btnLogin, btnRegister, btnGuest;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        toolbar.setNavigationIcon(R.drawable.ic_back);

        //linking elements
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.login_loginBtn);
        btnRegister = findViewById(R.id.login_registerBtn);
        btnGuest = findViewById(R.id.login_guestBtn);
        //progress bar
        progressBar = findViewById(R.id.login_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        //firebasae
        mAuth = FirebaseAuth.getInstance();

        //back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLogin.this, ActivityStart.class));
            }
        });

        //input fields validation
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activeInputField(email, R.string.active_email_hint);
                } else if (!hasFocus) {
                    inactiveInputField(email, R.string.inactive_email_hint);
                    validateEmail(email, email.getText().toString().trim());
                }
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(email, email.getText().toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        pass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activeInputField(pass, R.string.active_pass_hint);
                } else if (!hasFocus) {
                    inactiveInputField(pass, R.string.inactive_pass_hint);
                    validatePass(pass, pass.getText().toString().trim());
                }
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePass(pass, pass.getText().toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //store user input into variables
                loginEmail = email.getText().toString().toLowerCase().trim();
                loginPassword = pass.getText().toString().toLowerCase().trim();

                //user feedback when attempting to login despite UI validation warning
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(ActivityLogin.this, "Enter Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(pass.getText().toString())) {
                    Toast.makeText(ActivityLogin.this, "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    //display progress bar
                    btnLogin.setVisibility(View.INVISIBLE);
                    progressBar.setVisibility(View.VISIBLE);

                    //firebase authentication
                    mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String name = (mAuth.getCurrentUser().getEmail()).split("@")[0];
                                        String s1 = name.substring(0, 1).toUpperCase();
                                        Toast.makeText(ActivityLogin.this, "Welcome " + s1+name.substring(1), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ActivityLogin.this, ActivityStart.class));
                                    } else {
                                        Toast.makeText(ActivityLogin.this, "Incorrect Email or Password!", Toast.LENGTH_SHORT).show();
                                        email.setText("");
                                        pass.setText("");
                                        //update UI
                                        btnLogin.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            });
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iRegister = new Intent(ActivityLogin.this, ActivityRegister.class);
                startActivity(iRegister);
            }
        });

        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iHome = new Intent(ActivityLogin.this, ActivityStart.class);
                startActivity(iHome);
            }
        });

    }

    //toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_right,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //menu selection
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            //handle create symptoms case
            case R.id.action_create_attraction:
                startActivity(new Intent(this, ActivitySelectBodyLocation.class));
                break;
            //handle account
            case R.id.action_account:
                startActivity(new Intent(this, ActivityAccount.class));
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    //user Input validation & UI update
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void activeInputField(EditText inputField, int hint){
        inputField.setHint(hint);
    }

    public void inactiveInputField(EditText inputField, int hint){
        inputField.setHint(hint);
        inputField.setHintTextColor(ContextCompat.getColor(getBaseContext(), R.color.black3));
        inputField.setBackgroundResource(R.drawable.edit_text);
    }

    public void updateInputFieldNotValid (EditText inputField){
        inputField.setHintTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorRedLight));
        inputField.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.red));
        inputField.setBackgroundResource(R.drawable.edit_text_active_not_valid);
    }
    public void updateInputFieldValid (EditText inputField){
        inputField.setHintTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorLight2));
        inputField.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorLight));
        inputField.setBackgroundResource(R.drawable.edit_text_active);
    }

    public void validateEmail (EditText inputField, String email){
        if(isEmailValid(email)) {
            updateInputFieldValid(inputField);
        } else {
            updateInputFieldNotValid(inputField);
        }
    }

    public void validatePass(EditText inputField, String pass){
        if (pass.length() > 5){
            updateInputFieldValid(inputField);
        } else{
            updateInputFieldNotValid(inputField);
        }
    }


}
