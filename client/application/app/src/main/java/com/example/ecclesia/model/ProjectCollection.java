package com.example.ecclesia.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectCollection
{
    ArrayList<Project> projectList;
    Classification collection;

    public ProjectCollection(ArrayList<Project> projectList, Classification collection)
    {
        this.projectList=projectList;
        this.collection=collection;
    }

    public ProjectCollection(JSONObject json)
    {
        this(new ArrayList<>(), null);
        this.fetchCollectionFromJSON(json);
    }

    public ArrayList<Project> getProjectList()
    {
        return projectList;
    }

    public Classification getCollection()
    {
        return collection;
    }

    private void fetchCollectionFromJSON(JSONObject result)
    {
        projectList.clear();
        try
        {
            JSONArray projects = result.getJSONArray("projects");

            int n = projects.length();
            for (int i = 0; i < n; i++)
            {
                JSONObject json = (JSONObject) projects.get(i);
                Project project = new Project(json);
                projectList.add(project);
            }
            String classification = result.getString("classification");
            collection = new Classification(classification);

        } catch (JSONException e) {e.printStackTrace();}
    }
}
