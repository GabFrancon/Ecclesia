package com.example.ecclesia.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.R;
import com.example.ecclesia.model.User;
import com.example.ecclesia.model.ServerRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonIOException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;



public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;

    private CallbackManager mCallbackManager;
    private User mUser;

    //Login page
    private LinearLayout mLoginLayout;
    private EditText mEmailInput;
    private EditText mPasswordInput;
    private Button mLoginButton;
    private LoginButton mFbLoginButton;
    private Button mSignInButton;

    //Welcome page
    private LinearLayout mWelcomeLayout;
    private TextView mWelcomeText;
    private ImageView mProfilePicture;
    private Button mContinueButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ////////////////////////////
        //Preparation of interface//
        ////////////////////////////

        mLoginLayout = findViewById(R.id.main_activity_login_layout);
        mEmailInput = findViewById(R.id.main_activity_email_txt_bar);
        mPasswordInput = findViewById(R.id.main_activity_password_txt_bar);
        mLoginButton = findViewById(R.id.main_activity_login_btn);
        mFbLoginButton = findViewById(R.id.main_activity_fb_login_button);
        mSignInButton = findViewById(R.id.main_activity_sign_in_btn);
        mWelcomeLayout = findViewById(R.id.main_activity_welcome_layout);
        mWelcomeText = findViewById(R.id.main_activity_welcome_txt);
        mProfilePicture = findViewById(R.id.main_activity_profile_picture);
        mContinueButton = findViewById(R.id.main_activity_continue_btn);

        mUser = new User ();

        //////////////////////////////
        //Dealing with Classic Login//
        //////////////////////////////

        //disable of login button as long as the email field is not field
        mLoginButton.setEnabled(false);

        mEmailInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mLoginButton.setEnabled(s.toString().length() != 0);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        mLoginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String email = mEmailInput.getText().toString();
                String password = mPasswordInput.getText().toString();
                ServerRequest connexionRequest = new ServerRequest();
                connexionRequest.userConnection(email, password);
                while (! connexionRequest.isCompleted() )
                {
                    Log.e("CONNECTING","...");
                }
                login(connexionRequest.getResult());
            }
        });

        ///////////////////////////////
        //Dealing with Facebook Login//
        ///////////////////////////////

        FacebookSdk.sdkInitialize(getApplicationContext());
        registerCallBackMethod();

        ////////////////////////
        //Dealing with Sign in//
        ////////////////////////

        mSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent signInActivity = new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(signInActivity);
            }
        });
    }

    protected void registerCallBackMethod()
    //method that handles the facebook login and the recovery of user data
    {
        mCallbackManager = CallbackManager.Factory.create();
        mFbLoginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        mFbLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            //called when the Facebook connexion has been successful
            {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response)
                            {
                                try {
                                    //all the facebook data we need is retrieved from the JSON object
                                    Log.e("FB DATA", String.valueOf(response.getJSONObject()));
                                    String firstName = response.getJSONObject().getString("first_name");
                                    String lastName = response.getJSONObject().getString("last_name");
                                    String email = response.getJSONObject().getString("email");
                                    String facebookID = response.getJSONObject().getString("id");
                                    String urlPicture = response.getJSONObject().getJSONObject("picture").getJSONObject("data").getString("url");

                                    if (firstName == null || lastName == null || email == null || facebookID == null || urlPicture == null) {
                                        //to ensure that all the necessary data has been retrieved from Facebook
                                        Log.e("FB ERROR", "fb data is partly null");
                                    } else {
                                        //the profile of the current user mUser is updated
                                        mUser.setFirstName(firstName);
                                        mUser.setLastName(lastName);
                                        mUser.setEmail(email);
                                        mUser.setFacebookID(facebookID);
                                        mUser.setProfilePicture(urlPicture);

                                        ServerRequest fbConnection = new ServerRequest();
                                        fbConnection.facebookConnection(facebookID, email);
                                        while (!fbConnection.isCompleted()) {}
                                        fbLogin(fbConnection.getResult());
                                    }
                                } catch (JSONException e) {e.printStackTrace();}
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,first_name,last_name,picture.width(600).height(600)");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {}
            @Override
            public void onError(FacebookException error) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void login(JSONObject result)
    {
        if (result==null) Log.e("TAG","json is null");
        else
        {
            try
            {
                boolean success = result.getBoolean("success");

                if (success)
                {
                    String token = result.getString("token");
                    mUser.setToken(token);

                    JSONObject profile = result.getJSONObject("profile");
                    String id = profile.getString("id");
                    String firstName = profile.getString("firstname");
                    String profilePicture = profile.getString("profile_picture");
                    mUser.setID(id);
                    mUser.setFirstName(firstName);
                    mUser.setProfilePicture(profilePicture);

                    displayWelcomeScreen();
                }
                else
                //something went wrong during the verification of the identifiers
                {
                    String message = result.getString("message");
                    mEmailInput.setText(null);
                    mPasswordInput.setText(null);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {e.printStackTrace();}
        }
    }

    private void fbLogin(JSONObject result)
    {
        if (result == null)
            Log.e("ERROR REQUEST", "json is null");
        else
        {
            try
            {
                boolean success = result.getBoolean("success");
                if (success)
                {
                    boolean hasFbAccount = result.getBoolean("fb_account");

                    if (hasFbAccount)
                    {
                        String token = result.getString("token");
                        mUser.setToken(token);

                        JSONObject profile = result.getJSONObject("profile");
                        String id = profile.getString("id");
                        mUser.setID(id);
                        displayWelcomeScreen();
                    }
                    else
                    {
                        JSONObject profile;
                        profile = result.getJSONObject("profile");

                        String syncID = profile.getString("id");
                        String syncFirstName = profile.getString("firstname");
                        String syncLastName = profile.getString("lastname");
                        String syncProfilePicture = profile.getString("profile_picture");

                        Toast.makeText(LoginActivity.this, "Sychroniser avec " + syncFirstName + " " + syncLastName + " ?", Toast.LENGTH_SHORT).show();
                        //Create fragment synchronization and if answer is yes send synchro request
                    }
                }
                else
                {
                    Log.e("FAIL", result.getString("message"));
                    Intent signInActivity = new Intent(LoginActivity.this, SignInActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", mUser);
                    signInActivity.putExtras(bundle);
                    startActivity(signInActivity);
                }
            }catch (JSONException e) {e.printStackTrace();}
        }
    }

    protected void displayWelcomeScreen()
    {
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
            @Override
            public void run()
            {
                //hiding keyboard
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(mPasswordInput.getWindowToken(), 0);

                mWelcomeText.setText("Content de vous revoir, " + mUser.getFirstName() + " !");
                mLoginLayout.setVisibility(View.GONE);
                mSignInButton.setVisibility(View.GONE);

                RequestOptions ro = new RequestOptions();
                ro.error(R.drawable.default_profile);
                Glide.with(LoginActivity.this)
                        .applyDefaultRequestOptions(ro)
                        .load(mUser.getProfilePicture())
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(mProfilePicture);

                //shows the welcome page
                mWelcomeLayout.setVisibility(View.VISIBLE);

                mContinueButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent homeActivity = new Intent(LoginActivity.this, MainActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("user",mUser);
                        homeActivity.putExtras(bundle);
                        startActivity(homeActivity);
                    }
                });
            }
        });
    }

}
