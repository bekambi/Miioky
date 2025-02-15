package com.bekambimouen.miiokychallenge.register;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;
import com.bekambimouen.miiokychallenge.internet_status.CheckInternetStatus;
import com.ozcanalasalvar.library.view.datePicker.DatePicker;

import java.util.Calendar;

public class RegisterBirthDay extends AppCompatActivity {
    private static final String TAG = "RegisterBirthDay";

    // widgets
    private LinearLayout main;
    private RelativeLayout rel_olds, result_olds;
    private TextView tv_day;
    private TextView tv_month;
    private TextView tv_year;
    private TextView tv_user_year_olds;
    private TextView tv_olds;

    // vars
    private RegisterBirthDay context;
    private String number_of_month, birth_day, phoneNumber, full_name, town_name, neighborhood_name, native_country_name,
            hometown_name;
    private int user_olds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set navigation bar color
        getWindow().setNavigationBarColor(getColor(R.color.white));
        setContentView(R.layout.activity_register_birth_day);
        context = this;

        try {
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            full_name = getIntent().getStringExtra("full_name");
            town_name = getIntent().getStringExtra("town_name");
            neighborhood_name = getIntent().getStringExtra("neighborhood_name");
            native_country_name = getIntent().getStringExtra("native_country_name");
            hometown_name = getIntent().getStringExtra("hometown_name");
        } catch (NullPointerException e) {
            Log.d(TAG, "onCreate: "+e.getMessage());
        }

        RelativeLayout arrowBack = findViewById(R.id.arrowBack);
        main = findViewById(R.id.main);
        rel_olds = findViewById(R.id.rel_olds);
        result_olds = findViewById(R.id.result_olds);
        tv_day = findViewById(R.id.tv_day);
        tv_month = findViewById(R.id.tv_month);
        tv_year = findViewById(R.id.tv_year);
        tv_user_year_olds = findViewById(R.id.tv_user_year_olds);
        tv_olds = findViewById(R.id.tv_olds);
        Button btn_birthday = findViewById(R.id.btn_next);
        DatePicker datePicker = findViewById(R.id.datepicker);

        datePicker.setDataSelectListener((date, day, month, year) -> {
            rel_olds.setVisibility(View.VISIBLE);
            GradientDrawable drawable = (GradientDrawable) result_olds.getBackground();
            drawable.mutate();
            drawable.setStroke(1, context.getColor(R.color.black_semi_transparent2));

            tv_day.setText(String.valueOf(day));
            number_of_month = String.valueOf(month + 1);
            tv_month.setText(GetMonthOfYear());
            tv_year.setText(String.valueOf(year));

            birth_day = day +" "+GetMonthOfYear()+" "+ year;

            int Year = Calendar.getInstance().get(Calendar.YEAR);
            user_olds = Year - year;
            tv_user_year_olds.setText(String.valueOf(user_olds));

            if (user_olds <= 0)
                rel_olds.setVisibility(View.GONE);
            if (user_olds <= 13) {
                tv_user_year_olds.setTextColor(Color.RED);
                tv_olds.setTextColor(Color.RED);
            } else {
                tv_user_year_olds.setTextColor(Color.BLACK);
                tv_olds.setTextColor(Color.BLACK);
            }
        });

        btn_birthday.setOnClickListener(view1 -> {
            if (user_olds <= 13) {
                GradientDrawable drawable = (GradientDrawable) result_olds.getBackground();
                drawable.mutate();
                drawable.setStroke(3, Color.RED);

            } else {
                closeKeyboard();
                Intent intent = getIntent1();
                startActivity(intent);

            }
        });

        arrowBack.setOnClickListener(view1 -> {
            getWindow().setExitTransition(new Slide(Gravity.END));
            getWindow().setEnterTransition(new Slide(Gravity.START));
            finish();
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getWindow().setExitTransition(new Slide(Gravity.END));
                getWindow().setEnterTransition(new Slide(Gravity.START));
                finish();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private @NonNull Intent getIntent1() {
        getWindow().setExitTransition(new Slide(Gravity.END));
        getWindow().setEnterTransition(new Slide(Gravity.START));

        Intent intent = new Intent(context, RegisterSubscription.class);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("full_name", full_name);
        intent.putExtra("birth_day", birth_day);
        intent.putExtra("town_name", town_name);
        intent.putExtra("neighborhood_name", neighborhood_name);
        intent.putExtra("native_country_name", native_country_name);
        intent.putExtra("hometown_name", hometown_name);
        return intent;
    }

    // to prevent text size change from system
    @Override
    protected void attachBaseContext(Context newBase) {
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
        super.attachBaseContext(newBase);
    }

    private void closeKeyboard() {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // 12 months of Year
    public String GetMonthOfYear() {
        String MonthName = "";

        String[] my = this.getResources().getStringArray(R.array.month);
        for (String s: my) {
            String[] g = s.split(",");
            if (g[0].trim().equals(number_of_month)) {
                MonthName = g[1];
                break;
            }
        }
        return MonthName;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckInternetStatus.internetIsConnected(context, main);
    }
}