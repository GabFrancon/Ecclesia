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

public class ChangeEmailActivity extends AppCompatActivity {

    Toolbar toolbar;
    User mUser;
    EditText oldEmail, newEmail, confirmEmail;
    Button changeEmail, cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        Slidr.attach(this);

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

        oldEmail = findViewById(R.id.actual_email);
        newEmail = findViewById(R.id.change_email_txt);
        confirmEmail = findViewById(R.id.check_change_email);
        changeEmail = findViewById(R.id.change_email_button);
        changeEmail.setEnabled(false);
        cancel = findViewById(R.id.cancel_button);

        newEmail.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                changeEmail.setEnabled(s.toString().length() != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        changeEmail.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String oldEmailInput = oldEmail.getText().toString();
                String newEmailInput = newEmail.getText().toString();
                String confirmEmailInput = confirmEmail.getText().toString();

                if (newEmailInput.equals(confirmEmailInput))
                {
                    mUser.setEmail(oldEmailInput);
                    String message = mUser.changeEmail(newEmailInput);

                    if (!message.equals(""))
                    {
                        oldEmail.setText(null);
                        newEmail.setText(null);
                        confirmEmail.setText(null);
                        new CustomToast(ChangeEmailActivity.this, message,R.drawable.ic_baseline_warning_24);
                    }
                    else
                    {
                        new CustomToast(ChangeEmailActivity.this, "Votre email a bien été mis à jour",R.drawable.ic_baseline_check_circle_24);
                        Intent intent = new Intent(ChangeEmailActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user", mUser);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                else
                {
                    confirmEmail.setText(null);
                    new CustomToast(ChangeEmailActivity.this, "l'email de confirmation n'est pas le même que le nouvel email",R.drawable.ic_baseline_warning_24);
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