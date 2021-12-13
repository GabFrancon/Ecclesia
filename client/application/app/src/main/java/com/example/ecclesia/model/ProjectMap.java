package com.example.ecclesia.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.ecclesia.R;
import com.example.ecclesia.activity.ProjectActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;

public class ProjectMap {
    private GoogleMap map;
    private Context context;
    private User user;
    private ClusterManager clusterManager;
    private ArrayList<ProjectMapItem> allProjectItems;
    private final BitmapDescriptor markerItem;

    public ProjectMap(GoogleMap map, Context context, User user)
    {
        this.map = map;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style));
        this.context = context;
        this.user=user;
        this.markerItem=vectorToBitmap(R.drawable.ic_pin_map, Color.parseColor("#FF09C1CA"));
        this.allProjectItems = new ArrayList<>();
        this.clusterManager = new ClusterManager(context, map);
    }

    public ArrayList<ProjectMapItem> getAllProjectItems(){return allProjectItems;}

    public void displayProjectsOnMap()
    {

        clusterManager.setOnClusterItemClickListener( new ClusterManager.OnClusterItemClickListener() {
            @Override
            public boolean onClusterItemClick(ClusterItem clusterItem)
            {
                return false;
            }
        });

        clusterManager.setOnClusterItemInfoWindowClickListener( new ClusterManager.OnClusterItemInfoWindowClickListener() {
            @Override
            public void onClusterItemInfoWindowClick(ClusterItem clusterItem)
            {
                Project project = ((ProjectMapItem)clusterItem).getProject();
                ArrayList<Project> projects = getProjectList();
                int position = projects.indexOf(project);

                Intent intent = new Intent(context, ProjectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", user);
                bundle.putInt("position", position);
                bundle.putParcelableArrayList("projects", projects);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener() {
            @Override
            public boolean onClusterClick(Cluster cluster)
            {
                map.moveCamera(CameraUpdateFactory.zoomIn());
                return false;
            }
        });

        CustomInfoWindowAdapter adapter = new CustomInfoWindowAdapter();
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(adapter);

        CustomRenderer renderer = new CustomRenderer(context, map, clusterManager);
        clusterManager.setRenderer(renderer);

        map.setOnMarkerClickListener(clusterManager);
        map.setOnInfoWindowClickListener(clusterManager);
        map.setOnCameraIdleListener(clusterManager);
        map.setInfoWindowAdapter(clusterManager.getMarkerManager());
        clusterManager.cluster();
    }

    public ArrayList<Project> getProjectList()
    {
        ArrayList<Project> projectArrayList = new ArrayList<>();
        for (ProjectMapItem item : allProjectItems)
        {
            projectArrayList.add(item.getProject());
        }
        return projectArrayList;
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color)
    /**Convert drawable to BitmapDescriptor that can be used as a pin map */
    {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), id, null);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        private final View mContents;

        CustomInfoWindowAdapter()
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            mContents = inflater.inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            Project project = (Project) marker.getTag();
            ImageView pictureUI = mContents.findViewById(R.id.project_picture);
            TextView mTitle = mContents.findViewById(R.id.project_title);
            TextView mMeeting = mContents.findViewById(R.id.project_date);
            TextView mPlace = mContents.findViewById(R.id.project_place);

            RequestListener listener = new RequestListener()
            {
                int i=0;
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource)
                {
                    if (i<2 && marker.isInfoWindowShown() )
                    {
                        marker.hideInfoWindow();
                        marker.showInfoWindow();
                        i++;
                        Log.e("COUNT", String.valueOf(i));
                    }
                    return false;
                }
            };

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    RequestOptions ro = new RequestOptions();
                    ro.error(R.drawable.default_project).transforms(new CenterCrop());
                    Glide.with(context)
                            .applyDefaultRequestOptions(ro)
                            .load(project.getPicture())
                            .listener(listener)
                            .into(pictureUI);

                    mTitle.setText(project.getTitle());
                    mMeeting.setText(project.getDate());
                    mPlace.setText(project.getPlace());
                }
            });
            return mContents;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            return null;
        }
    }

    class CustomRenderer extends DefaultClusterRenderer
    {
        public CustomRenderer(Context context, GoogleMap map, ClusterManager clusterManager)
        {
            super(context, map, clusterManager);
        }

        @Override
        protected void onBeforeClusterItemRendered(ClusterItem item, MarkerOptions markerOptions)
        {
            markerOptions.icon(markerItem);
            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onClusterItemRendered(ClusterItem clusterItem, Marker marker)
        {
            Project project = ((ProjectMapItem) clusterItem).getProject();
            marker.setTag(project);
            super.onClusterItemRendered(clusterItem, marker);
        }

        @Override
        public void setOnClusterItemClickListener(ClusterManager.OnClusterItemClickListener listener)
        {
            super.setOnClusterItemClickListener(listener);
            Log.e("ON CLICK","...");
        }

        @Override
        public void setOnClusterItemInfoWindowClickListener(ClusterManager.OnClusterItemInfoWindowClickListener listener) {
            super.setOnClusterItemInfoWindowClickListener(listener);
        }
    }

    public void fetchProjectCoordinates(JSONObject result)
    {
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

                    /**project.loadGeolocation() when loading/updating coordinates from google maps*/
                    //project.loadGeolocation();

                    ProjectMapItem item = new ProjectMapItem(project);
                    allProjectItems.add(item);
                    clusterManager.addItem(item);
                }
                /*ServerRequest fetchRequest = new ServerRequest();
                fetchRequest.setCoordinates(this);
                Log.e("REQUEST SENT","...");
                while (!fetchRequest.isCompleted()){}
                Log.e("SET RESPONSE",fetchRequest.getResult().toString());*/
            }
            else { Log.e("MESSAGE", result.getString("message")); }
        } catch (JSONException e) {e.printStackTrace();}
    }
}


