package com.example.ecclesia.model;

import android.content.Intent;
import android.util.Log;

import com.example.ecclesia.activity.LoginActivity;
import com.example.ecclesia.fragment.ProjectMenuFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.KeyPair;
import java.util.ArrayList;

import io.jsonwebtoken.io.Encoders;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

public class ServerRequest
{
    private final String serverUrl ="https://pact2321.r2.enst.fr/"; // "http://192.168.56.1/db_api/";
    private JSONObject dataResult;
    private boolean completed=false;

    /**
     * standard method to make a post request to the server
     * @param phpScript the name of the script to be called on server
     * @param data the data to send
     */
    private void makeRequest(String phpScript, JSONObject data)
    {
        //building request
        String url =  serverUrl + phpScript;

        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(MEDIA_TYPE, data.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        Log.e("REQUEST",request.toString());

        //making request and waiting for response
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                String resp = e.getMessage();
                Log.w("failure Response", resp);
                completed = true;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String resp = response.body().string();
                Log.e("RESP",resp);

                try {
                    JSONObject result = new JSONObject(resp.substring(resp.indexOf("{"),resp.lastIndexOf("}")+1));
                    dataResult = result;
                    completed = true;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * gets the result from most recent request
     * @return the result (Json)
     */
    public JSONObject getResult()
    {
        return dataResult;
    }

    /**
     * checks wether a response has been received
     * @return true if response has been detected
     */
    public boolean isCompleted() {return completed;}

    /* ------------ List of possible request ------------*/

    public void userConnection(String email, String password)
    {

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        makeRequest("user_connection.php", postData);
    }

    public void facebookConnection(String facebook_id, String facebook_email)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("facebook_id", facebook_id);
            postData.put("facebook_email", facebook_email);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("Inputs are :", String.valueOf(postData));
        makeRequest("facebook_connection.php", postData);
    }

    public void addUser(User futureUser, String password)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("firstname", futureUser.getFirstName());
            postData.put("lastname", futureUser.getLastName());
            postData.put("birth", futureUser.getBirth());
            postData.put("gender",futureUser.getGender());
            postData.put("location",futureUser.getLocation());
            postData.put("email",futureUser.getEmail());
            postData.put("password", password);
            postData.put("picture", futureUser.getProfilePicture());
            postData.put("facebook_id",futureUser.getFacebookID());

        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("add_user.php",postData);
    }

    public void userSynchronisation(User user, String idToSync)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("sync_id", idToSync);
            postData.put("firstname", user.getFirstName());
            postData.put("lastname", user.getLastName());
            postData.put("location", user.getLocation());
            postData.put("picture", user.getProfilePicture());


        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_all_projects.php",postData);
    }

    private void setLike(String jwt, String projectId, int like){
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("project", projectId);
            postData.put("like", like);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("add_like.php", postData);
    }

    public void addLike(String jwt, String projectId){
        setLike(jwt,projectId,1);
    }

    public void deleteLike(String jwt, String projectId){
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("project", projectId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("delete_like.php", postData);
    }

    public void getLikesInfo(String jwt, String projectId)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("project", projectId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_likes_info.php", postData);
    }
    
    public void hideProject(String jwt, String projectId){
        setLike(jwt,projectId,0);
    }

    public void sendFriendRequest(String jwt, String friendId)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("acceptor_id", friendId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("send_friend_request.php",postData);
    }

    public void acceptFriendRequest(String jwt, String friendId)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("applicant_id", friendId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("add_friend.php",postData);
    }

    public void deleteFriend(String jwt, String friendId)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("user_id_2", friendId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("delete_friend.php",postData);
    }

    public void getFriendshipStatus(String jwt, String secondUserID)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("user_id_2", secondUserID);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_friendship_status.php",postData);
    }

    public void getRecommendedProjects(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_recommanded_projects.php",postData);
    }

    public void getLikedProjects(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_liked_projects.php",postData);
    }

    public void getSimilarProjects(String jwt, String projectID, String area)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("area", area);
            postData.put("project", projectID);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_similar_projects.php",postData);
    }

    public void getFriends(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_friends.php",postData);
    }

    public void getUser(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_user.php",postData);
    }

    public void getProfile(String jwt, String userID)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("user_id", userID);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_profile.php",postData);
    }

    public void searchUsers(String search, String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("search", search);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("search_users.php",postData);
    }

    public void searchProjects(String search, String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("search", search);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("search_projects.php",postData);
    }

    public void getClassification(JSONArray projectIDs, String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("projects", projectIDs);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_classification.php",postData);
    }
    public void addPrefProject(String jwt, String projectId)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("project", projectId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("add_pref_project.php", postData);
    }

    public void deletePrefProject(String jwt, String projectId)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("project", projectId);
        } catch(JSONException e){
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("delete_pref_project.php", postData);
    }

    public void getPrefProjects(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_pref_projects.php", postData);
    }


    public void getProjectsByClassification(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_classified_projects.php", postData);
    }

    public void setPreferences(String jwt, ArrayList<String> areas, ArrayList<String> categories)
    {
        JSONObject postData = new JSONObject();
        try {
            JSONArray areaList = new JSONArray(areas);
            JSONArray categoryList = new JSONArray(categories);

            postData.put("jwt", jwt);
            postData.put("areas", areaList);
            postData.put("categories", categoryList);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("set_preferences.php", postData);
    }

    public void getPreferences(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_preferences.php", postData);
    }

    public void shareProject(String jwt, String friendID, String projectID)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("receiver",friendID);
            postData.put("project",projectID);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("share_project.php", postData);
    }

    public void retrieveNotifications(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("retrieve_notifications.php", postData);
    }

    public void refreshPicture(String jwt, String picture)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("picture", picture);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("refresh_user_picture.php", postData);
    }

    public void changeEmail(String jwt, String oldEmail, String newEmail)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("old_email", oldEmail);
            postData.put("new_email", newEmail);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("change_email.php", postData);
    }

    public void changePassword(String jwt, String oldPassword, String newPassword)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("old_password", oldPassword);
            postData.put("new_password", newPassword);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("change_password.php", postData);
    }

    public void getLatLong(String address)
    {
        //building request
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+address+",+CA&key=AIzaSyDpXLnsFkVpfJo5k7dwE7XJmjoLNDKHuJQ";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.e("REQUEST",request.toString());

        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                String resp = e.getMessage();
                Log.w("failure Response", resp);
                completed = true;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String resp = response.body().string();
                try {
                    JSONObject result = new JSONObject(resp.substring(resp.indexOf("{"),resp.lastIndexOf("}")+1));
                    dataResult = result;
                    completed = true;
                    Log.e("SERVER REQUEST","completed");
                } catch (JSONException e) {e.printStackTrace();}
            }
        });
    }

    public void setCoordinates(ProjectMap projectMap)
    {
        ArrayList<ProjectMapItem> items = projectMap.getAllProjectItems();
        JSONObject postData = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for (ProjectMapItem item : items)
            {
                JSONObject json = new JSONObject();
                json.put("project_id", item.getProject().getID());
                LatLng coord = item.getPosition();
                Log.e("COORD FROM REQUEST",String.valueOf(coord));
                json.put("lat", coord.latitude);
                json.put("lng", coord.longitude);
                array.put(json);
            }
            postData.put("coordinates",array);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("set_coordinates.php", postData);
    }

    public void getCoordinates(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("get_coordinates.php", postData);
    }

    public void countNewNotifications(String jwt)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("count_new_notifications.php", postData);
    }

    public void setNotificationRead(String jwt, String timeStamping, String sender, int sharing)
    {
        JSONObject postData = new JSONObject();
        try {
            postData.put("jwt", jwt);
            postData.put("time_stamping", timeStamping);
            postData.put("sender", sender);
            postData.put("sharing", sharing);
        } catch(JSONException e)
        {
            e.printStackTrace();
            Log.e("failure", String.valueOf(e));
        }
        Log.e("INPUTS", String.valueOf(postData));
        makeRequest("set_notification_read.php", postData);
    }
}

