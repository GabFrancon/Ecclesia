package com.example.ecclesia.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Project implements Parcelable
{
    String mID;
    String mTitle;
    String mPicture;
    String mSummary;
    String mWebsite;
    String mDate;
    String mTime;
    String mPlace;
    int mLikes;
    LatLng mGeolocation=new LatLng(0,0);
    ArrayList<Classification> mAreas=new ArrayList<>();
    ArrayList<Classification> mCategories=new ArrayList<>();
    boolean isSaved=false;
    boolean isDeleted=false;

    public Project(String mID, String mTitle, String mPicture, String mSummary, String mWebsite, String mDate, String mTime, int mLikes, String mPlace, LatLng mGeolocation)
    {
        this.mID=mID;
        this.mTitle=mTitle;
        this.mPicture=mPicture;
        this.mSummary=mSummary;
        this.mWebsite=mWebsite;
        this.mDate=mDate;
        this.mTime=mTime;
        this.mPlace=mPlace;
        this.mGeolocation=mGeolocation;
        this.mLikes=mLikes;
    }

    public Project()
    {
        this("","","","","","","",0,"",null);
    }

    protected Project(Parcel in)
    {
        mID = in.readString();
        mTitle = in.readString();
        mPicture = in.readString();
        mSummary = in.readString();
        mWebsite = in.readString();
        mDate = in.readString();
        mTime = in.readString();
        mPlace = in.readString();
        mLikes = in.readInt();
        double lat = in.readDouble();
        double lng = in.readDouble();
        mGeolocation = new LatLng(lat, lng);
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    public void setID(String id){ mID=id;}
    public void setTitle(String title ){mTitle=title;}
    public void setSummary(String summary){ mSummary=summary;}
    public void setPicture(String picture){ mPicture=picture;}
    public void setWebsite(String website){ mWebsite=website;}
    public void setDate(String date){mDate=date;}
    public void setTime(String time){mTime=time;}
    public void setLikes(int likes) {mLikes=likes;}
    public void addArea(Classification area){mAreas.add(area);}
    public void addCategory(Classification category){mCategories.add(category);}
    public void setSaved(){isSaved=true;}
    public void setDeleted(){isDeleted=true;}
    public void setPlace(String place){mPlace=place;}
    public void setGeolocation(LatLng geolocation){mGeolocation=geolocation;}

    public String getID(){return mID;}
    public String getTitle(){return mTitle;}
    public String getSummary(){return mSummary;}
    public String getPicture(){return mPicture;}
    public String getWebsite(){return mWebsite;}
    public int getLikes(){return mLikes;}
    public ArrayList<Classification> getAreas(){return mAreas;}
    public ArrayList<Classification> getCategories(){return mCategories;}
    public boolean isSaved(){return isSaved;}
    public boolean isDeleted(){return isDeleted;}
    public LatLng getGeolocation() {return mGeolocation;}

    public Project (JSONObject json) throws JSONException
    {
        this.setID(json.getString("id"));
        this.setTitle(json.getString("title"));
        this.setSummary(json.getString("summary"));
        this.setWebsite(json.getString("website"));
        this.setPicture(json.getString("picture"));

        String date = this.getDate(json.getString("meeting_date"),
                                         json.getString("end_date"));
        this.setDate(date);
        this.setTime(json.getString("meeting_time"));
        this.setPlace(json.getString("place"));
        this.setLikes((json.getInt("likes")));

        try
        {
            LatLng coordinates = new LatLng(json.getDouble("lat"), json.getDouble("lng"));
            this.setGeolocation(coordinates);
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(mID);
        dest.writeString(mTitle);
        dest.writeString(mPicture);
        dest.writeString(mSummary);
        dest.writeString(mWebsite);
        dest.writeString(mDate);
        dest.writeString(mTime);
        dest.writeString(mPlace);
        dest.writeInt(mLikes);
        dest.writeDouble(mGeolocation.latitude);
        dest.writeDouble(mGeolocation.longitude);
    }

    public void loadClassifFromJSON(JSONObject json) throws JSONException
    {
        JSONArray areaList = json.getJSONArray("areas");
        JSONArray categoryList = json.getJSONArray("categories");

        for (int i=0;i<areaList.length();i++)
        {
            String areaName = (String) areaList.get(i);
            mAreas.add(new Classification(areaName));
        }
        for (int i=0;i<categoryList.length();i++)
        {
            String categoryName = (String) categoryList.get(i);
            mCategories.add(new Classification(categoryName));
        }
    }

    public boolean loadLikesInfo (User user)
    {
        boolean hasLiked = false;
        ServerRequest likesInfoRequest = new ServerRequest();
        likesInfoRequest.getLikesInfo(user.getToken(), mID);
        while (! likesInfoRequest.isCompleted() ) {}
        JSONObject result = likesInfoRequest.getResult();
        try
        {
            boolean success = result.getBoolean("success");
            if (success)
            {
                mLikes = result.getInt("number_of_likes");
                hasLiked = result.getString("user_opinion").equals("like");
                Log.e("RESULTAT",mLikes + "/" + hasLiked);
            }
            else { Log.e("MESSAGE", result.getString("message")); }
        } catch (JSONException e) {e.printStackTrace();}

        return hasLiked;
    }

    public ArrayList<Project> loadSimilarProjects(User user)
    {
        ServerRequest request = new ServerRequest();
        request.getSimilarProjects(user.getToken(), mID, mAreas.get(0).getName());
        while (! request.isCompleted() ) {}
        JSONObject result = request.getResult();

        ArrayList<Project> mProjects = new ArrayList<>();
        try
        {
            boolean success = result.getBoolean("success");

            if (success)
            {
                JSONArray projectList = result.getJSONArray("projects");

                int n = projectList.length();
                for (int i = 0; i < n; i++)
                {
                    JSONObject json = (JSONObject) projectList.get(i);
                    Project project = new Project(json);
                    mProjects.add(project);
                }
            }
            else
            {
                Log.e("FAIL",result.getString("message"));
            }
        } catch (JSONException e) {e.printStackTrace();}
        return mProjects;
    }

    public void addLike(User user)
    {
        ServerRequest requestAddLike = new ServerRequest();
        requestAddLike.addLike(user.getToken(), mID);
        while (! requestAddLike.isCompleted() ) {}
        JSONObject result = requestAddLike.getResult();
        try
        {
            boolean success = result.getBoolean("success");
            if (success) mLikes++;
            else Log.e("FAILURE", result.getString("message"));

        } catch (JSONException e) {e.printStackTrace();}
    }

    public void removeLike(User user)
    {
        ServerRequest requestRemoveLike = new ServerRequest();
        requestRemoveLike.deleteLike(user.getToken(), mID);
        while (! requestRemoveLike.isCompleted() ) {}
        JSONObject result = requestRemoveLike.getResult();
        try
        {
            boolean success = result.getBoolean("success");
            if (success) mLikes--;
            else Log.e("FAILURE", result.getString("message"));

        } catch (JSONException e) {e.printStackTrace();}
    }

    public boolean dislike(User user)
    {
        boolean success=false;

        ServerRequest deleteProject = new ServerRequest();
        deleteProject.hideProject(user.getToken(), mID);

        while (! deleteProject.isCompleted() ) {}
        JSONObject result = deleteProject.getResult();
        try {
            success = result.getBoolean("success");
            if (!success) Log.e("MESSAGE",result.getString("message"));

        } catch (JSONException e) {e.printStackTrace();}
        return success;
    }

    public boolean save(User user)
    {
        boolean success = false;

        ServerRequest saveProject = new ServerRequest();
        saveProject.addPrefProject(user.getToken(), mID);
        while (! saveProject.isCompleted() ) {}
        JSONObject result = saveProject.getResult();
        try {
            success = result.getBoolean("success");
            if (!success) Log.e("MESSAGE",result.getString("message"));

        } catch (JSONException e) {e.printStackTrace();}
        return success;
    }

    /**Method used when we need to get coordinates from Google Maps geocoding API */
    public void loadGeolocation()
    {
        ServerRequest request = new ServerRequest();
        request.getLatLong(mPlace);
        while (!request.isCompleted()){}
        JSONObject result = request.getResult();

        try
        {
            JSONObject info = (JSONObject) result.getJSONArray("results").get(0);

            String formattedAddress = info.getString("formatted_address");
            Log.e("ADDRESS", formattedAddress);
            JSONObject geometry = info.getJSONObject("geometry");
            JSONObject jsonCoord = geometry.getJSONObject("location");

            double lat = jsonCoord.getDouble("lat");
            double lng = jsonCoord.getDouble("lng");
            this.mGeolocation = new LatLng(lat, lng);
            Log.e("GEOLOCATION", mGeolocation+"");

        } catch (JSONException e) {e.printStackTrace();}
    }

    public String getDate(String date, String endDate)
    {
        boolean isEndDate = !endDate.equals("null");
        String completeDate=TimeHandler.getWrittenDate(date, true);
        if (isEndDate) { completeDate="Du " +TimeHandler.getWrittenDate(date, false)+" au "+TimeHandler.getWrittenDate(endDate, true); }

        return completeDate;
    }
    public String getDate() { return mDate; }

    public String getTime()
    {
        try {
            return TimeHandler.getWrittenTime(mTime);
        } catch (StringIndexOutOfBoundsException e)  {return "";}
    }

    public String getPlace()
    {
        int i = mPlace.indexOf(",");
        if (i>-1)
        {
            try {
                return mPlace.substring(0,i);
            } catch (StringIndexOutOfBoundsException e) {return mPlace;}
        }
        else
        {
            return mPlace;
        }

    }
    public String getAddress()
    {
        int i = mPlace.indexOf(",");
        if (i>-1)
        {
            try {
                return mPlace.substring(i+2);
            } catch (StringIndexOutOfBoundsException e) {return "";}
        }
        else
        {
            return "";
        }
    }

    public String getPlaceMapPicture()
    {
        String url="";
        int zoom = 13;
        if (!mPlace.contains(","))
        {
            zoom=8;
        }
        if (mGeolocation.latitude!=0)
        {
            url = "https://maps.googleapis.com/maps/api/staticmap?size=420x300&zoom="+zoom+"&markers=color:0x09C1CA%7C"+mGeolocation.latitude+","+mGeolocation.longitude+"&key=AIzaSyDpXLnsFkVpfJo5k7dwE7XJmjoLNDKHuJQ";
        }
        return url;
    }
}
