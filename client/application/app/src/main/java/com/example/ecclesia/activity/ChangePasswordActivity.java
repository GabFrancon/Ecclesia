package com.example.ecclesia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.model.User;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;

public class ChangePasswordActivity extends AppCompatActivity {

    Toolbar toolbar;
    User mUser;

    EditText oldPassword, newPassword, confirmPassword;
    Button changePassword, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        SlidrConfig config = new SlidrConfig.Builder()
                .build();

        Slidr.attach(this, config);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user"))
        {
            Bundle bundle = intent.getExtras();
            mUser = bundle.getParcelable("user");
        }

        //Toolbar with up button to return home
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        oldPassword = findViewById(R.id.actual_password);
        newPassword = findViewById(R.id.change_password_txt);
        confirmPassword = findViewById(R.id.check_change_password);
        changePassword = findViewById(R.id.change_password_button);
        changePassword.setEnabled(false);
        cancel = findViewById(R.id.cancel_button);

        newPassword.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                changePassword.setEnabled(s.toString().length() != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        changePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String oldPasswordInput = oldPassword.getText().toString();
                String newPasswordInput = newPassword.getText().toString();
                String confirmPasswordInput = confirmPassword.getText().toString();

                if (newPasswordInput.equals(confirmPasswordInput))
                {
                    String message = mUser.changePassword(oldPasswordInput, newPasswordInput);

                    if (!message.equals(""))
                    {
                        oldPassword.setText(null);
                        newPassword.setText(null);
                        confirmPassword.setText(null);
                        new CustomToast(ChangePasswordActivity.this, message,R.drawable.ic_baseline_warning_24);
                    }
                    else
                    {
                        new CustomToast(ChangePasswordActivity.this, "Votre mot de passe a bien été mis à jour", R.drawable.ic_baseline_check_circle_24);
                        Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", mUser);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else
                {
                    confirmPassword.setText(null);
                    new CustomToast(ChangePasswordActivity.this, "le mot de passe de confirmation n'est pas le même que le nouveau mot de passe", R.drawable.ic_baseline_warning_24);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}