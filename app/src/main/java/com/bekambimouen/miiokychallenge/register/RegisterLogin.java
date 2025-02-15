package com.bekambimouen.miiokychallenge.register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.challenge_home.HomeActivity;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterLogin extends AppCompatActivity {

    // widgets
    private LinearLayout linLayout_log_in, linLayout_create_account;
    private RelativeLayout main, rel_email, rel_password, rel_create_email, rel_create_password;
    private EditText edit_email, edit_password, edit_create_email, edit_create_password;
    private ProgressBar main_progressBar;

    // vars
    private RegisterLogin context;
    private boolean isScreenEnabled = true, isEmptyEmail, isEmptyPassword, isEmptyCreateEmail, isEmptyCreatePassword;

    // firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_register_login);
        context = this;

        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        main = findViewById(R.id.main);
        rel_email = findViewById(R.id.rel_email);
        rel_password = findViewById(R.id.rel_password);
        rel_create_email = findViewById(R.id.rel_create_email);
        rel_create_password = findViewById(R.id.rel_create_password);
        RelativeLayout relLayout_forgot_password = findViewById(R.id.relLayout_forgot_password);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        main_progressBar = findViewById(R.id.main_progressBar);
        linLayout_log_in = findViewById(R.id.linLayout_log_in);
        linLayout_create_account = findViewById(R.id.linLayout_create_account);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_create_email = findViewById(R.id.edit_create_email);
        edit_create_password = findViewById(R.id.edit_create_password);
        CheckBox checkBox1 = findViewById(R.id.checkBox1);
        CheckBox checkBox2 = findViewById(R.id.checkBox2);
        TextView create_your_account = findViewById(R.id.create_your_account);
        AppCompatButton btn_login = findViewById(R.id.btn_login);
        AppCompatButton btn_finish = findViewById(R.id.btn_finish);

        edit_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyEmail) {
                    isEmptyEmail = false;

                    GradientDrawable drawable = (GradientDrawable) rel_email.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyPassword) {
                    isEmptyPassword = false;

                    GradientDrawable drawable = (GradientDrawable) rel_password.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_create_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyCreateEmail) {
                    isEmptyCreateEmail = false;

                    GradientDrawable drawable = (GradientDrawable) rel_create_email.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edit_create_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isEmptyCreatePassword) {
                    isEmptyCreatePassword = false;

                    GradientDrawable drawable = (GradientDrawable) rel_create_password.getBackground();
                    drawable.mutate();
                    drawable.setStroke(1, context.getColor(R.color.grey));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // show/hide password
        checkBox1.setOnClickListener(view -> {
            boolean checked = ((CheckBox) view).isChecked();

            if (checked) {
                edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edit_password.setSelection(edit_password.getText().length());
                checkBox1.setText(context.getString(R.string.hide_password));

            } else {
                edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edit_password.setSelection(edit_password.getText().length());
                checkBox1.setText(context.getString(R.string.show_password));
            }
        });

        checkBox2.setOnClickListener(view -> {
            boolean checked = ((CheckBox) view).isChecked();

            if (checked) {
                edit_create_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edit_create_password.setSelection(edit_create_password.getText().length());
                checkBox1.setText(context.getString(R.string.hide_password));

            } else {
                edit_create_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edit_create_password.setSelection(edit_create_password.getText().length());
                checkBox1.setText(context.getString(R.string.show_password));
            }
        });

        // create account
        create_your_account.setOnClickListener(view -> {
            linLayout_log_in.setVisibility(View.GONE);
            linLayout_create_account.setVisibility(View.VISIBLE);
        });

        btn_login.setOnClickListener(v -> {
            // show permission dialog
            getSignIn();
        });

        relLayout_forgot_password.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            relLayout_forgot_password.setEnabled(false);

            String email = edit_email.getText().toString().trim();
            String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            Pattern email_pattern = Pattern.compile(email_regex);
            Matcher email_matcher = email_pattern.matcher(email);
            boolean isValid_email = email_matcher.matches();

            if (!isValid_email || TextUtils.isEmpty(email)) {
                progressBar.setVisibility(View.GONE);
                relLayout_forgot_password.setEnabled(true);
                isEmptyEmail = true;
                GradientDrawable drawable = (GradientDrawable) rel_email.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);
                edit_email.requestFocus();

            } else {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                relLayout_forgot_password.setEnabled(true);
                                // Password reset email sent successfully
                                new AlertDialog.Builder(context)
                                        .setMessage(context.getString(R.string.password_reset_email_sent))
                                        .create()
                                        .show();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                relLayout_forgot_password.setEnabled(true);
                                // Password reset email failed to send
                                new AlertDialog.Builder(context)
                                        .setMessage(context.getString(R.string.failed_to_send_password_reset_email))
                                        .create()
                                        .show();
                            }
                        });
            }
        });

        btn_finish.setOnClickListener(view -> {
            // create new account
            getCreateAccount();
        });

        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (linLayout_create_account.getVisibility() == View.VISIBLE) {
                    linLayout_create_account.setVisibility(View.GONE);
                    linLayout_log_in.setVisibility(View.VISIBLE);
                } else {
                    finishAffinity();
                }
            }
        });
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    // sign in
    private void getSignIn() {
        String email = edit_email.getText().toString().trim();
        String password = edit_password.getText().toString().trim();

        String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern email_pattern = Pattern.compile(email_regex);
        Matcher email_matcher = email_pattern.matcher(email);
        boolean isValid_email = email_matcher.matches();

        if (!isValid_email || TextUtils.isEmpty(email)) {
            isEmptyEmail = true;
            GradientDrawable drawable = (GradientDrawable) rel_email.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_email.requestFocus();

        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            isEmptyPassword = true;
            GradientDrawable drawable = (GradientDrawable) rel_password.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_password.requestFocus();

        } else {
            closeKeyboard();
            isScreenEnabled = false;
            main_progressBar.setVisibility(View.VISIBLE);
            edit_email.clearFocus();
            edit_password.clearFocus();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            main_progressBar.setVisibility(View.GONE);
                            getWindow().setExitTransition(new Slide(Gravity.END));
                            getWindow().setEnterTransition(new Slide(Gravity.START));
                            startActivity(new Intent(context, HomeActivity.class));
                            finish();

                        } else {
                            isScreenEnabled = true;
                            main_progressBar.setVisibility(View.GONE);
                            edit_email.requestFocus();
                            String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return isScreenEnabled && super.dispatchTouchEvent(ev);
    }

    // create new account
    private void getCreateAccount() {
        String email = edit_create_email.getText().toString().trim();
        String password = edit_create_password.getText().toString().trim();

        String email_regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern email_pattern = Pattern.compile(email_regex);
        Matcher email_matcher = email_pattern.matcher(email);
        boolean isValid_email = email_matcher.matches();

        if (!isValid_email || TextUtils.isEmpty(email)) {
            isEmptyCreateEmail = true;
            GradientDrawable drawable = (GradientDrawable) rel_create_email.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_create_email.requestFocus();

        } else if (TextUtils.isEmpty(password) || password.length() < 6) {
            isEmptyCreatePassword = true;
            GradientDrawable drawable = (GradientDrawable) rel_create_password.getBackground();
            drawable.mutate();
            drawable.setStroke(3, Color.RED);
            edit_create_password.requestFocus();

        } else {
            closeKeyboard();
            main_progressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    HashMap<String, Object> map_users = new HashMap<>();
                    map_users.put("status", context.getString(R.string.hi_follow_me_on_miioky));
                    map_users.put("username", "");

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    myRef.child(getString(R.string.dbname_users))
                            .child(Objects.requireNonNull(auth.getCurrentUser()).getUid())
                            .updateChildren(map_users)
                            .addOnSuccessListener(unused -> {
                                main_progressBar.setVisibility(View.GONE);
                                getWindow().setExitTransition(new Slide(Gravity.END));
                                getWindow().setEnterTransition(new Slide(Gravity.START));
                                Intent intent = new Intent(context, RegisterUserName.class);
                                startActivity(intent);
                                finish();

                            }).addOnFailureListener(e -> Toast.makeText(context, "error: "+e, Toast.LENGTH_SHORT).show());

                } else {
                    main_progressBar.setVisibility(View.GONE);
                    // Check if the error is due to a duplicate email
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        String errorCode = ((FirebaseAuthInvalidCredentialsException) task.getException()).getErrorCode();
                        if (errorCode.equals(" emailAlreadyInUse")) {
                            // Email already in use, display an error message to the user
                            Toast.makeText(context, "Email address is already in use.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    private void closeKeyboard(){
        View view = context.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckInternetStatus.internetIsConnected(context, main);
    }
}