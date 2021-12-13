package com.example.ecclesia.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.adapter.SpinnerAdapter;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.User;
import com.r0adkll.slidr.Slidr;

import org.json.JSONException;
import org.json.JSONObject;

public class SignInActivity extends AppCompatActivity
{
    private TextView mHeadTitle;
    private EditText mFirstNameInput;
    private EditText mLastNameInput;
    private EditText mBirthInput;
    private Button mEditBirthButton;
    private CheckBox mFemaleBox;
    private CheckBox mMaleBox;
    private CheckBox mTransgenderBox;
    private Spinner mCitySpinner;
    private Button mPictureButton;
    private ImageView mProfilePicture;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private EditText mSecondPasswordInput;
    private Button mSignInButton;

    private User mFutureUser;

    String[] cityNames={"Paris", "Nice"};
    int symbols[] = {R.drawable.paris, R.drawable.nice};

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        Slidr.attach(this);

        //Toolbar with up button to return home
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHeadTitle = findViewById(R.id.sign_in_activity_head_title);
        mFirstNameInput = (EditText) findViewById(R.id.sign_in_activity_firstname_input);
        mLastNameInput = (EditText) findViewById(R.id.sign_in_activity_lastname_input);
        mBirthInput = (EditText) findViewById(R.id.sign_in_activity_birth_input);
        mEditBirthButton = (Button) findViewById(R.id.sign_in_activity_set_birth_button);
        mFemaleBox = (CheckBox) findViewById(R.id.sign_in_activity_female_cb);
        mMaleBox = (CheckBox) findViewById(R.id.sign_in_activity_male_cb);
        mTransgenderBox = (CheckBox) findViewById(R.id.sign_in_activity_transgender_cb);
        mCitySpinner = (Spinner) findViewById(R.id.sign_in_activity_city_spinner);
        mPictureButton = (Button) findViewById(R.id.sign_in_activity_picture_btn);
        mProfilePicture = (ImageView) findViewById(R.id.sign_in_activity_profile_picture);
        mEmailInput = (EditText) findViewById(R.id.sign_in_activity_email_input);
        mPasswordInput = (EditText) findViewById(R.id.sign_in_activity_password_input);
        mSecondPasswordInput = (EditText) findViewById(R.id.sign_in_activity_password_second_input);
        mSignInButton = (Button) findViewById(R.id.sign_in_activity_valid_btn);


        ////////////////////////////////////
        //Dealing with FB first connection//
        ////////////////////////////////////
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("user"))
        {
            Bundle bundle = intent.getExtras();
            mFutureUser = bundle.getParcelable("user");
            fbAutoFill();
        }
        else
        {
            mFutureUser = new User();
        }

        /////////////////////
        //Birth select menu//
        /////////////////////
        int selectedYear = 2021;
        int selectedMonth = 1;
        int selectedDay = 1;


        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) ->
        {
            String day=String.valueOf(dayOfMonth);
            String month=String.valueOf(monthOfYear+1);

            Log.e("SIZE DATE",day.length() + " et " + month.length() );

            if (day.length()==1) day = '0'+day;
            if (month.length()==1) month = '0'+month;

            String showedBirth = day + "-" + month + "-" + String.valueOf(year);
            String birth=String.valueOf(year)+'-'+ month +'-'+ day;

            mBirthInput.setText(showedBirth, TextView.BufferType.EDITABLE);

            Log.v("date", birth);
            mFutureUser.setBirth(birth);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                dateSetListener, selectedYear, selectedMonth, selectedDay);

        mEditBirthButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                datePickerDialog.show();
            }
        });


        ////////////////////////////
        // Location drop down menu//
        ////////////////////////////


        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id)
            {
                Log.e("LOCATION",cityNames[position]);
                mFutureUser.setLocation(cityNames[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {}
        });

        SpinnerAdapter spinnerAdapter=new SpinnerAdapter(getApplicationContext(),symbols,cityNames);
        mCitySpinner.setAdapter(spinnerAdapter);


        /////////////////////
        //Gender checkboxes//
        /////////////////////

        mFemaleBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                Log.e("GENDER","female box changed");
                if (isChecked)
                {
                    mMaleBox.setChecked(false);
                    mTransgenderBox.setChecked(false);
                    mFutureUser.setGender("F");
                }
            }
        });

        mMaleBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    mFemaleBox.setChecked(false);
                    mTransgenderBox.setChecked(false);
                    mFutureUser.setGender("H");
                }
            }
        });

        mTransgenderBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isChecked)
                {
                    mFemaleBox.setChecked(false);
                    mMaleBox.setChecked(false);
                    mFutureUser.setGender("T");
                }
            }
        });


        //////////////////
        //Sign in button//
        //////////////////
        mSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String firstName = mFirstNameInput.getText().toString();
                String lastName = mLastNameInput.getText().toString();
                String email = mEmailInput.getText().toString();
                String password = mPasswordInput.getText().toString();
                String second_password = mSecondPasswordInput.getText().toString();

                if (!password.equals(second_password))
                {
                    new CustomToast(SignInActivity.this, "Les mots de passe entrés ne sont pas identiques", R.drawable.ic_baseline_warning_24);
                }
                else
                {
                    if (firstName.isEmpty() | email.isEmpty() | password.isEmpty())
                    {
                        new CustomToast(SignInActivity.this, "Vous devez remplir tous les champs obligatoires", R.drawable.ic_baseline_warning_24);
                    }
                    else
                    {
                        mFutureUser.setFirstName(firstName);
                        mFutureUser.setLastName(lastName);
                        mFutureUser.setEmail(email);

                        completeInscription(password);
                    }
                }
            }
        });
    }

    public void fbAutoFill()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                mHeadTitle.setText("Complétez votre inscription, " + mFutureUser.getFirstName() + " !");

                mFirstNameInput.setText(mFutureUser.getFirstName());
                mLastNameInput.setText(mFutureUser.getLastName());
                mEmailInput.setText(mFutureUser.getEmail());
                String picture = mFutureUser.getProfilePicture();

                if (!picture.equals("null"))
                {
                    Glide.with(SignInActivity.this).load(picture).into(mProfilePicture);
                }
            }
        });
    }

    private void completeInscription(String password)
    {
        try {
            ServerRequest addUserRequest = new ServerRequest();
            addUserRequest.addUser(mFutureUser, password);
            while (! addUserRequest.isCompleted() )
            {
                Log.e("IN PROGRESS","...");
            }
            JSONObject result = addUserRequest.getResult();
            boolean success = result.getBoolean("success");
            if (success)
            {
                String token = result.getString("token");
                mFutureUser.setToken(token);

                Intent prefActivity = new Intent(SignInActivity.this, PreferencesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mFutureUser);
                bundle.putBoolean("from_main",false);
                prefActivity.putExtras(bundle);
                startActivity(prefActivity);
            }
            else
            {
                Log.e("FAIL", result.getString("message"));
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            new CustomToast(SignInActivity.this, "L'inscription n'a pas pu être validée", R.drawable.ic_baseline_warning_24);
        }
    }
}
