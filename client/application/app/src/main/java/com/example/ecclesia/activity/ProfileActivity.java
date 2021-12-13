package com.example.ecclesia.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.CustomToast;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.TimeHandler;
import com.example.ecclesia.model.User;
import com.r0adkll.slidr.Slidr;

import org.json.JSONException;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity
{

    ImageView mProfilePicture;
    TextView mName;
    TextView mPublicLocation;
    TextView mLocation;
    TextView mGender;
    TextView mBirth;
    TextView mEmail;
    ConstraintLayout mPersonalInfo;
    ProgressBar mProgressBar;
    Handler handler;

    Button mEditButton;
    Button mAddFriendButton;
    Button mDeleteFriendButton;

    Button mAcceptFriendButton;
    Button mRefuseFriendButton;
    LinearLayout mRequestAcceptor;

    Toolbar toolbar;
    User mUser;
    User mSecondUser;

    ImageButton mEditPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Slidr.attach(this);

        mSecondUser=new User();
        handler = new Handler();

        mProfilePicture = findViewById(R.id.profile_activity_picture);
        mEditPicture = findViewById(R.id.profile_activity_picture_button);
        mEditPicture.setVisibility(View.GONE);

        mName = findViewById(R.id.profile_activity_name_txt);
        mPublicLocation = findViewById(R.id.profile_activity_city_txt);

        mPersonalInfo = findViewById(R.id.profile_activity_constraint_info);
        mPersonalInfo.setVisibility(View.GONE);

        mEditButton = findViewById(R.id.profile_activity_edit_button);
        mEditButton.setVisibility(View.GONE);

        mAddFriendButton = findViewById(R.id.profile_activity_add_friend_button);
        mAddFriendButton.setVisibility(View.GONE);

        mDeleteFriendButton = findViewById(R.id.profile_activity_delete_friend_button);
        mDeleteFriendButton.setVisibility(View.GONE);

        mAcceptFriendButton = findViewById(R.id.profile_activity_accept_friend_button);
        mRefuseFriendButton = findViewById(R.id.profile_activity_refuse_friend_button);
        mRequestAcceptor  = findViewById(R.id.profile_activity_request_buttons);
        mRequestAcceptor.setVisibility(View.GONE);

        mLocation = findViewById(R.id.profile_activity_location_txt);
        mGender = findViewById(R.id.profile_activity_gender_txt);
        mBirth = findViewById(R.id.profile_activity_birth_txt);
        mEmail = findViewById(R.id.profile_activity_email_txt);

        mProgressBar = findViewById(R.id.progress_bar);

        //Toolbar with up button to return home
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Retrieving id of the current user, then load its profile
        Intent intent =  getIntent();
        if (intent != null && intent.hasExtra("user"))
        {
            Bundle bundle = intent.getExtras();
            mUser = bundle.getParcelable("user");

            if (intent.hasExtra("secondUserID"))
            {
                String secondUserID = bundle.getString("secondUserID");

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        mSecondUser.loadProfile(mUser.getToken(), secondUserID);
                        handler.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                try {
                                    displaySecondUserProfile();
                                    adaptButtonVisibility(mUser.testFriendship(mSecondUser));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                thread.start();
            }
            else
            {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        mUser.loadUser();
                        handler.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                displayUserProfile();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                thread.start();
            }
        }

        mEditButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new CustomToast(ProfileActivity.this, "Fonctionnalité momentanément désactivée", R.drawable.ic_hide);
            }
        });

        mEditPicture.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new CustomToast(ProfileActivity.this, "Fonctionnalité momentanément désactivée", R.drawable.ic_hide);
            }
        });

        mAddFriendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean success = mUser.sendFriendRequest(mSecondUser);

                if (success)
                {
                    mAddFriendButton.setVisibility(View.GONE);
                    mDeleteFriendButton.setVisibility(View.VISIBLE);
                    mDeleteFriendButton.setText("Annuler ma demande d'ami");
                    new CustomToast(ProfileActivity.this, "Demande d'ami envoyée", R.drawable.ic_baseline_check_circle_24);
                }
                else
                {
                    Log.e("FAIL","une erreur technique est survenue");
                }

            }
        });

        mDeleteFriendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean success = mUser.deleteFriend(mSecondUser);
                 if (success)
                 {
                     mDeleteFriendButton.setVisibility(View.GONE);
                     mAddFriendButton.setVisibility(View.VISIBLE);
                     new CustomToast(ProfileActivity.this, "Contact supprimé", R.drawable.ic_baseline_person_off_24);
                 }
                 else
                 {
                     Log.e("FAIL","une erreur technique est survenue");
                 }
            }
        });

        mAcceptFriendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean success = mUser.acceptFriendRequest(mSecondUser);
                if (success)
                {
                    mRequestAcceptor.setVisibility(View.GONE);
                    mDeleteFriendButton.setVisibility(View.VISIBLE);
                    mDeleteFriendButton.setText("Supprimer de mes amis");
                }
                else
                {
                    Log.e("FAIL","une erreur technique est survenue");
                }
            }
        });

        mRefuseFriendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                boolean success = mUser.deleteFriend(mSecondUser);
                if (success)
                {
                    mRequestAcceptor.setVisibility(View.GONE);
                    mAddFriendButton.setVisibility(View.VISIBLE);
                }
                else
                {
                    Log.e("FAIL","une erreur technique est survenue");
                }
            }
        });
    }


    private void displaySecondUserProfile()
    {
        String picture = mSecondUser.getProfilePicture();
        String name = mSecondUser.getName();
        String location = mSecondUser.getLocation();

        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                displayProfilePicture(picture);
                if (!name.equals(" "))
                {
                    Log.e("NAME", name);
                    mName.setText(name);
                }
                if (!location.equals(""))
                {
                    mPublicLocation.setText(location);
                    mPublicLocation.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void adaptButtonVisibility (ArrayList<Object> userStatus)
    {
        boolean friendLink = (boolean) userStatus.get(0);

        if (friendLink)
        {
            boolean areFriends = (boolean) userStatus.get(1);

            if (!areFriends)
            {
                String status = (String) userStatus.get(2);

                if (status.equals("acceptor"))
                {
                    mRequestAcceptor.setVisibility(View.VISIBLE);
                }
                else if (status.equals("applicant"))
                {
                    mDeleteFriendButton.setVisibility(View.VISIBLE);
                    mDeleteFriendButton.setText("retirer ma demande d'ami");
                }
            }
            else
            {
                mDeleteFriendButton.setVisibility(View.VISIBLE);
                mDeleteFriendButton.setText("supprimer de ma liste d'amis");
            }
        }
        else
        {
            mAddFriendButton.setVisibility(View.VISIBLE);
        }
    }

    private void displayUserProfile()
    {
        String picture = mUser.getProfilePicture();
        String name = mUser.getName();
        String location = mUser.getLocation();
        String gender = mUser.getGender();
        String birth = mUser.getBirth();
        String email = mUser.getEmail();

        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                displayProfilePicture(picture);
                if (!name.equals(" "))
                {
                    Log.e("NAME", name);
                    mName.setText(name);
                }
                if (!location.equals(""))
                {
                    Log.e("LOCATION", location);
                    mLocation.setText(location);
                }
                if (!gender.equals(""))
                {
                    String writtenGender="sexe";
                    if (gender.startsWith("H")) writtenGender="Homme";
                    if (gender.startsWith("F")) writtenGender="Femme";
                    if (gender.startsWith("T")) writtenGender="Transgenre";
                    Log.e("GENDER", writtenGender);
                    mGender.setText(writtenGender);
                }
                if (!email.equals(""))
                {
                    Log.e("EMAIL", email);
                    mEmail.setText(email);
                }
                if (!birth.equals(""))
                {

                    mBirth.setText(new TimeHandler().getWrittenDate(birth,true));
                }
                mPersonalInfo.setVisibility(View.VISIBLE);
                mEditPicture.setVisibility(View.VISIBLE);
                mEditButton.setVisibility(View.VISIBLE);
            }
        });
    }

    public void displayProfilePicture(String picture)
    {
        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_profile).circleCrop();
        Glide.with(ProfileActivity.this)
                .load(picture)
                .apply(ro)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mProfilePicture);
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