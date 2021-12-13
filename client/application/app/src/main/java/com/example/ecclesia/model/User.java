package com.example.ecclesia.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class User implements Parcelable
{
    private String mToken;
    private String mID;
    private String mFirstName;
    private String mLastName;
    private String mGender;
    private String mBirth;
    private String mEmail;
    private String mLocation;
    private String mProfilePicture;
    private String mFacebookID;


    public User(String id, String firstName, String lastName,
                String gender, String birth, String email,
                String location, String profilePicture, String facebookID)
    {
        mID = id;
        mFirstName = firstName;
        mLastName = lastName;
        mGender = gender;
        mBirth = birth;
        mEmail = email;
        mLocation = location;
        mProfilePicture = profilePicture;
        mFacebookID = facebookID;
    }

    public User() {
        this("", "", "", "", "", "", "", "", "");
    }


    protected User(Parcel in) {
        mToken = in.readString();
        mID = in.readString();
        mFirstName = in.readString();
        mLastName = in.readString();
        mGender = in.readString();
        mBirth = in.readString();
        mEmail = in.readString();
        mLocation = in.readString();
        mProfilePicture = in.readString();
        mFacebookID = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    //all getters
    public String getToken() {return mToken; }

    public String getID() {
        return mID;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public String getGender() {
        return mGender;
    }

    public String getBirth() {
        return mBirth;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getLocation() {
        return mLocation;
    }

    public String getProfilePicture() {
        return mProfilePicture;
    }

    public String getFacebookID()
    {
        if (mFacebookID.equals(""))
        {
            return "0";
        }
        return mFacebookID;
    }

    public String getName() {return mFirstName+" "+mLastName;}

    //all setters
    public void setToken(String token) { mToken=token; }

    public void setID(String id) {
        mID = id;
    }

    public void setFirstName(String name) {
        mFirstName = name;
    }

    public void setLastName(String name) {
        mLastName = name;
    }

    public void setGender(String gender) {
        mGender = gender;
    }

    public void setBirth(String birth) {
        mBirth = birth;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public void setProfilePicture(String image) {
        mProfilePicture = image;
    }

    public void setFacebookID(String id) {
        mFacebookID = id;
    }

    public void loadUser() {
        ServerRequest request = new ServerRequest();

        try {
            request.getUser(mToken);

            while (!request.isCompleted()) {
                //Wait...
                Log.e("WAIT FOR RESPONSE", "...");
            }
            JSONObject result = request.getResult();
            boolean success = result.getBoolean("success");

            if (success) {
                JSONObject user = result.getJSONObject("user");

                mFirstName = user.getString("firstname");
                mLastName = user.getString("lastname");
                mBirth = user.getString("birth");
                mLocation = user.getString("location");
                mGender = user.getString("gender");
                mEmail = user.getString("email");
                mProfilePicture = user.getString("profile_picture");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadProfile(String token, String id) {
        this.setID(id);
        ServerRequest request = new ServerRequest();

        try {
            request.getProfile(token, id);

            while (!request.isCompleted()) {
                //Wait...
                Log.e("WAIT FOR RESPONSE", "");
            }
            JSONObject result = request.getResult();
            boolean success = result.getBoolean("success");

            if (success) {
                JSONObject profile = result.getJSONObject("profile");

                mFirstName = profile.getString("firstname");
                mLastName = profile.getString("lastname");
                mLocation = profile.getString("location");
                mProfilePicture = profile.getString("profile_picture");
            } else {
                Log.e("FAILURE", result.getString("message"));
            }


        } catch (JSONException e) {
            Log.e("FAILURE", "Request failed");
            e.printStackTrace();
        }
    }

    public ArrayList<Object> testFriendship(User user) throws JSONException {
        boolean friendLink;
        boolean areFriends;
        String userStatus;

        ArrayList<Object> status = new ArrayList<>();

        ServerRequest request = new ServerRequest();
        request.getFriendshipStatus(mToken, user.getID());
        while (!request.isCompleted()) {
            Log.e("WAIT", "...");
        }
        JSONObject result = request.getResult();

        boolean success = result.getBoolean("success");

        if (success) {
            friendLink = result.getBoolean("friend_link");
            status.add(friendLink);

            if (friendLink) {
                areFriends = result.getBoolean("are_friends");
                status.add(areFriends);

                if (!areFriends) {
                    userStatus = result.getString("user_status");
                    status.add(userStatus);
                }
            }
        }
        return status;
    }

    public boolean sendFriendRequest(User user) {
        ServerRequest request = new ServerRequest();
        request.sendFriendRequest(mToken, user.getID());
        while (!request.isCompleted()) {
            Log.e("WAIT", "...");
        }
        JSONObject result = request.getResult();
        boolean success = false;

        try {
            success = result.getBoolean("success");
            if (success) {
                Log.e("SUCCESS", "friend request sent");
            } else {
                String message = result.getString("message");
                Log.e("EXCEPTION", message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean deleteFriend(User friend) {
        String friendID = friend.getID();

        ServerRequest request = new ServerRequest();
        request.deleteFriend(mToken, friendID);
        while (!request.isCompleted()) {
            Log.e("WAIT", "...");
        }
        JSONObject result = request.getResult();
        boolean success = false;

        try {
            success = result.getBoolean("success");
            if (success) {
                Log.e("SUCCESS", "friend or friend request deleted");
            } else {
                String message = result.getString("message");
                Log.e("FAIL", message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean acceptFriendRequest(User user) {
        ServerRequest request = new ServerRequest();
        request.acceptFriendRequest(mToken, user.getID());
        while (!request.isCompleted()) {
            Log.e("WAIT", "...");
        }
        JSONObject result = request.getResult();
        boolean success = false;

        try {
            success = result.getBoolean("success");
            if (success) {
            } else {
                Log.e("FAIL",result.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return success;
    }

    public boolean setPreferences(ArrayList<String> areas, ArrayList<String> categories) {
        boolean success=false;
        ServerRequest request = new ServerRequest();
        request.setPreferences(mToken, areas, categories);
        while (!request.isCompleted()) {
            Log.e("WAIT", "...");
        }
        JSONObject result = request.getResult();

        try {
            success = result.getBoolean("success");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return success;
    }

    public ArrayList<Classification> [] getPreferences()
    {
        ArrayList<Classification> areas = new ArrayList<>();
        ArrayList<Classification> categories = new ArrayList<>();

        ServerRequest request = new ServerRequest();
        request.getPreferences(mToken);
        while (!request.isCompleted())
        {
            Log.e("WAIT", "...");
        }
        JSONObject result = request.getResult();
        try {
            boolean success = result.getBoolean("success");

            if (success) {
                JSONArray areaList = result.getJSONArray("areas");
                int n = areaList.length();
                for (int i = 0; i < n; i++) {
                    String areaName = (String) areaList.get(i);
                    areas.add(new Classification(areaName));
                }

                JSONArray categoryList = result.getJSONArray("categories");
                int m = categoryList.length();
                for (int i = 0; i < m; i++) {
                    String categoryName = (String) categoryList.get(i);
                    categories.add(new Classification(categoryName));
                }
            } else {
                String message = result.getString("message");
                Log.e("FAIL", message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayList<Classification> response []= new ArrayList[]{areas, categories};
        return response;
    }

    public User (JSONObject json, boolean complete) throws JSONException
    {
        this();
        this.setID(json.getString("id"));
        this.setFirstName(json.getString("firstname"));
        this.setLastName(json.getString("lastname"));
        this.setProfilePicture(json.getString("profile_picture"));

        if (complete)
        {
            this.setLocation(json.getString("location"));
            this.setEmail(json.getString("email"));
            this.setBirth(json.getString("birth"));
            this.setGender(json.getString("gender"));
        }
    }

    public boolean shareProject(User friend, Project project)
    {
        boolean success = false;

        ServerRequest request = new ServerRequest();
        request.shareProject(mToken, friend.getID(), project.getID());

        while (!request.isCompleted())
        {
            Log.e("WAIT","...");
        }
        JSONObject result = request.getResult();
        try {
            success = result.getBoolean("success");
            if (!success) {Log.e("FAIL", result.getString("message"));}
        } catch (JSONException e) {e.printStackTrace();}
        return success;
    }
    public void refreshPicture()
    {
        ServerRequest request = new ServerRequest();
        request.refreshPicture(mToken, mProfilePicture);
        while (!request.isCompleted())
        {
            Log.e("REFRESHING PICTURE","...");
        }
        JSONObject result = request.getResult();
        try {
            Boolean success = result.getBoolean("success");
            if (!success) {Log.e("FAIL",result.getString("message"));}
        } catch (JSONException e) {e.printStackTrace();}
    }

    public String changeEmail(String newEmail)
    {
        ServerRequest request = new ServerRequest();
        request.changeEmail(mToken, mEmail, newEmail);
        while (!request.isCompleted()) { }
        JSONObject result = request.getResult();
        try {
            Boolean success = result.getBoolean("success");
            if (!success) return result.getString("message");
            else{
                mEmail = newEmail;
                return "";
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
            return "Impossible de mettre à jour votre email";
        }
    }

    public String changePassword(String oldPassword, String newPassword)
    {
        ServerRequest request = new ServerRequest();
        request.changePassword(mToken, oldPassword, newPassword);
        while (!request.isCompleted()) { }
        JSONObject result = request.getResult();
        try {
            Boolean success = result.getBoolean("success");
            if (!success) return result.getString("message");
            else return "";
        } catch (JSONException e)
        {
            e.printStackTrace();
            return "Impossible de mettre à jour votre mot de passe";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mToken);
        dest.writeString(mID);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
        dest.writeString(mGender);
        dest.writeString(mBirth);
        dest.writeString(mEmail);
        dest.writeString(mLocation);
        dest.writeString(mProfilePicture);
        dest.writeString(mFacebookID);
    }
}
