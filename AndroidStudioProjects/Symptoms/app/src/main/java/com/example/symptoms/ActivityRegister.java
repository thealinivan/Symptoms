package com.example.symptoms;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;


public class ActivityRegister extends AppCompatActivity {

    //classes instances
    private EditText email, pass, passConf, gender, yearOfBirth;
    private Button btnLogin, btnRegister, btnGuest;
    private ProgressBar progressBar;
    private DatabaseReference dbRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //linking elements
        gender = findViewById(R.id.register_gender);
        yearOfBirth = findViewById(R.id.register_yob);
        email = findViewById(R.id.register_email);
        pass = findViewById(R.id.register_password);
        passConf = findViewById(R.id.register_passwordConfirm);
        btnLogin = findViewById(R.id.register_loginBtn);
        btnRegister = findViewById(R.id.register_registerBtn);
        btnGuest = findViewById(R.id.register_guestBtn);
        //progress bar
        progressBar = findViewById(R.id.register_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        //firebase
        dbRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        toolbar.setNavigationIcon(R.drawable.ic_back);

        //back button
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
            }
        });

        //input fields validation (live validation on text change)
        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activeInputField(gender, R.string.active_gender_hint);
                } else if (!hasFocus) {
                    inactiveInputField(gender, R.string.inactive_gender_hint);
                    validateGender(gender, gender.getText().toString().toLowerCase().trim());
                }
            }
        });

        gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateGender(gender, gender.getText().toString().toLowerCase().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        yearOfBirth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activeInputField(yearOfBirth, R.string.active_yob_hint);
                } else if (!hasFocus) {
                    inactiveInputField(yearOfBirth, R.string.inactive_yob_hint);
                    validateYOB(yearOfBirth, yearOfBirth.getText().toString().trim());
                }
            }
        });

        yearOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateYOB(yearOfBirth, yearOfBirth.getText().toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

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

        passConf.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    activeInputField(passConf, R.string.active_pass_conf_hint);
                } else if (!hasFocus) {
                    inactiveInputField(passConf, R.string.inactive_pass_conf_hint);
                    validatePassConfirmation(passConf, pass.getText().toString().trim(), passConf.getText().toString().trim());
                }
            }
        });

        passConf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassConfirmation(passConf, pass.getText().toString().trim(), passConf.getText().toString().trim());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        //handle registration
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Store inputs in variables
                String userGender = gender.getText().toString().toLowerCase().trim();
                String userEmail = email.getText().toString().toLowerCase().trim();
                String userPassword = pass.getText().toString().trim();
                String userPassConf = passConf.getText().toString().trim();

                //user feedback when attempting to register despite UI validation warning
                if(TextUtils.isEmpty(userGender) ) {
                    Toast.makeText(ActivityRegister.this, "Enter Gender !", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if ( !( userGender.equals("male")  ||  userGender.equals("female") ) ){
                    Toast.makeText(ActivityRegister.this, " 'male' or 'female' !", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(yearOfBirth.getText().toString().trim()) || yearOfBirth.getText().toString().trim().length() != 4 ){
                    Toast.makeText(ActivityRegister.this, "Enter Year of Birth!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ActivityRegister.this, "Enter Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(userPassword) || userPassword.length() < 6){
                    Toast.makeText(ActivityRegister.this, "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(TextUtils.isEmpty(userPassConf)){
                    Toast.makeText(ActivityRegister.this, "Confirm Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!(userPassword.equals(userPassConf))) {
                    pass.setText("");
                    passConf.setText("");
                    Toast.makeText(ActivityRegister.this, "Passwords must match!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!(isEmailValid(userEmail))){
                    Toast.makeText(ActivityRegister.this, "Wrong email!", Toast.LENGTH_SHORT).show();
                }
                else{
                    int YOB = Integer.parseInt(yearOfBirth.getText().toString().trim());
                    if(YOB < 1900 || YOB > Calendar.getInstance().get(Calendar.YEAR) - 18) {
                        Toast.makeText(ActivityRegister.this, "YOB must be 1990 to present. Over 18 only!", Toast.LENGTH_SHORT).show();
                    } else {
                        int userYOB = Integer.parseInt(yearOfBirth.getText().toString().trim());
                        registerNewUser(userGender, userYOB, userEmail);
                    }

                }
            }
        });

        //go back on login page
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityRegister.this, ActivityLogin.class));
            }
        });

        //go on home page as a guest
        btnGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityRegister.this, ActivityStart.class));
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

    private void registerNewUser(String userGender, int userYOB, String userEmail){

        //show progress bar
        btnRegister.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        //create new user object
        final User user = new User (userGender, userYOB, userEmail);

        //create firebase authentication account
        mAuth.createUserWithEmailAndPassword(email.getText().toString().toLowerCase().trim(), pass.getText().toString().toLowerCase().trim())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //store user information in firebase
                            dbRef.child("User").child(mAuth.getCurrentUser().getUid()).setValue(user);
                            Toast.makeText(ActivityRegister.this, "New account has been created !", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent (ActivityRegister.this, ActivityLogin.class));
                        } else {
                            Toast.makeText(ActivityRegister.this, "Email already in use !", Toast.LENGTH_SHORT).show();
                            email.setText("");
                            pass.setText("");
                            passConf.setText("");
                            //update UI
                            btnRegister.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
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

    public void validateGender (EditText inputField, String userGender){
        if (!TextUtils.isEmpty(userGender) && ( userGender.equals("male") || userGender.equals("female") ) ){
            updateInputFieldValid(inputField);
        } else {
           updateInputFieldNotValid(inputField);
        }
    }

    public void validateYOB (EditText inputField, String YOB){
        if(YOB.length() == 4 && Integer.parseInt(YOB) > 1899 && Integer.parseInt(YOB) <= Calendar.getInstance().get(Calendar.YEAR) - 17) {
            updateInputFieldValid(inputField);
        } else {
            updateInputFieldNotValid(inputField);
        }
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
    public void validatePassConfirmation(EditText inputField, String pass, String passConf){
        if (passConf.length() > 5 && pass.equals(passConf)) {
            updateInputFieldValid(inputField);
        } else {
            updateInputFieldNotValid(inputField);
        }
    }



}
