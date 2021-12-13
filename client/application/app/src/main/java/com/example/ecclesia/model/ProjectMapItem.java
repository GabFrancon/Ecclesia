package com.example.ecclesia.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.ecclesia.R;
import com.example.ecclesia.activity.ProjectActivity;
import com.google.android.gms.internal.maps.zzt;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectMapItem implements ClusterItem
{

    private Project project;

    public ProjectMapItem (Project project)
    {
        this.project = project;
    }

    public Project getProject() {return project;}

    public MarkerOptions createMarker(BitmapDescriptor markerItem)
    {
        return new MarkerOptions()
                .position(project.getGeolocation())
                .title(project.getTitle())
                .snippet(project.getPlace())
                .icon(markerItem);
    }

    @Override
    public LatLng getPosition() {
        return project.getGeolocation();
    }

    @Override
    public String getTitle() {
        return project.getTitle();
    }

    @Override
    public String getSnippet() { return project.getPlace(); }
}
