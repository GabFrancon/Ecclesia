package com.example.ecclesia.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.ecclesia.R;
import com.example.ecclesia.adapter.RecyclerViewAdapter;
import com.example.ecclesia.model.Classification;
import com.example.ecclesia.model.Project;
import com.example.ecclesia.model.ServerRequest;
import com.example.ecclesia.model.TimeHandler;
import com.example.ecclesia.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FullProjectFragment extends Fragment
{
    //for the layout
    private TextView title, description, date, place, time, locality;
    private ImageView image, areaIcon, categoryIcon, mapPlacePicture, mapButton;
    private TextView likesTextView;
    private TextView areaName, categoryName;
    private ImageButton likeIcon;
    private LinearLayout mapLayout;
    private ProgressBar progressBar;

    //for the similar projects
    private RecyclerViewAdapter adapter;
    private ScrollView scrollView;
    private TextView seeMore;
    private RecyclerView similarProjectRecyclerView;

    //project data
    private Project project;
    private ArrayList<Project> similarProjects;

    //user data
    private User user;
    private boolean hasLiked;
    private boolean hasTouched=false;

    public FullProjectFragment(Project project, User user)
    {
        this.user=user;
        this.project=project;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_project, container, false);

        final Animation animScale = AnimationUtils.loadAnimation(getContext(), R.anim.anim_scale);

        //layout
        progressBar = view.findViewById(R.id.progress_bar);

        image =view. findViewById(R.id.image_of_one_project);
        title = view.findViewById(R.id.title_of_one_project);
        description = view.findViewById(R.id.description_of_one_project);
        date = view.findViewById(R.id.project_date);
        place = view.findViewById(R.id.project_place);
        time = view.findViewById(R.id.project_time);
        locality = view.findViewById(R.id.project_locality);
        mapPlacePicture = view.findViewById(R.id.map_picture);
        mapPlacePicture.setVisibility(View.GONE);
        mapButton = view.findViewById(R.id.map_button);

        areaName = view.findViewById(R.id.area_of_project);
        areaIcon = view.findViewById(R.id.area_icon);

        categoryName = view.findViewById(R.id.category_of_project);
        categoryIcon = view.findViewById(R.id.category_icon);

        likesTextView = view.findViewById(R.id.number_of_likes);
        likeIcon = view.findViewById(R.id.like_icon);

        mapLayout = view.findViewById(R.id.map_layout);

        similarProjectRecyclerView = view.findViewById(R.id.similar_project_recycler_view);
        scrollView = view.findViewById(R.id.project_scrollView);
        seeMore = view.findViewById(R.id.voir_egalement);

        Handler handler = new Handler();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run()
            {
                hasLiked = project.loadLikesInfo(user);

                handler.post(new Runnable() {
                    @Override
                    public void run()
                    {
                        setLikeInfo();
                        setData();
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
        thread.start();

        likeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                v.startAnimation(animScale);
                                if (hasLiked)
                                {
                                    likeIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                                    likesTextView.setText(String.valueOf(project.getLikes()-1));
                                }
                                else
                                {
                                    likeIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
                                    likesTextView.setText(String.valueOf(project.getLikes()+1));
                                }
                            }
                        });
                        if (hasLiked) project.removeLike(user);
                        else project.addLike(user);
                        hasLiked = !hasLiked;
                    }
                });
                thread.start();
            }
        });

        similarProjects = new ArrayList<>();
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if (!hasTouched)
                        {
                            hasTouched=true;
                            similarProjects = project.loadSimilarProjects(user);
                            loadClassification();
                        }

                        handler.post(new Runnable() {
                            @Override
                            public void run()
                            {
                                if (!similarProjects.isEmpty())
                                {
                                    adapter = new RecyclerViewAdapter(getContext(), similarProjects, user);
                                    similarProjectRecyclerView.setAdapter(adapter);
                                    similarProjectRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                }
                                else
                                {
                                    seeMore.setVisibility(View.GONE);
                                    similarProjectRecyclerView.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
                thread.start();
                return false;
            }
        });
        String mapPicture = project.getPlaceMapPicture();
        if (!mapPicture.equals(""))
        {
            mapLayout.setOnClickListener(new View.OnClickListener()
            {
                boolean open = false;

                @Override
                public void onClick(View v)
                {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run()
                        {
                            handler.post(new Runnable() {
                                @Override
                                public void run()
                                {
                                    if (!open)
                                    {
                                        mapPlacePicture.setVisibility(View.VISIBLE);
                                        RequestOptions roo = new RequestOptions();
                                        roo.transforms(new CenterCrop(), new RoundedCorners(25));
                                        Glide.with(getContext())
                                                .applyDefaultRequestOptions(roo)
                                                .load(mapPicture)
                                                .transition(DrawableTransitionOptions.withCrossFade())
                                                .into(mapPlacePicture);
                                        mapButton.setImageResource(R.drawable.ic_baseline_expand_less_24);
                                        open = true;
                                    }
                                    else
                                    {
                                        mapPlacePicture.setVisibility(View.GONE);
                                        open = false;
                                        mapButton.setImageResource(R.drawable.ic_baseline_expand_more_24);
                                    }
                                }
                            });
                        }
                    });

                    thread.start();
                }
            });
        }
        else
        {
            mapButton.setVisibility(View.GONE);
            LinearLayout layout = view.findViewById(R.id.map_layout);
            layout.setClickable(false);
        }
        return view;
    }

    private void setData()
    {
        title.setText(project.getTitle());
        description.setText(project.getSummary());
        date.setText(project.getDate());
        place.setText(project.getPlace());
        if (!project.getTime().equals("")) time.setText(project.getTime());
        else time.setVisibility(View.GONE);

        if (!project.getAddress().equals("")) locality.setText(project.getAddress());
        else locality.setVisibility(View.GONE);

        RequestOptions ro = new RequestOptions();
        ro.error(R.drawable.default_project).transform(new CenterCrop());
        Glide.with(getContext())
                .applyDefaultRequestOptions(ro)
                .load(project.getPicture())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image);


        ArrayList<Classification> areas = project.getAreas();
        areaName.setText(areas.get(0).getName());
        areaIcon.setImageResource(areas.get(0).getIcon());

        ArrayList<Classification> categories = project.getCategories();
        categoryName.setText(categories.get(0).getName());
        categoryIcon.setImageResource(categories.get(0).getIcon());
    }

    private void setLikeInfo()
    {
        if (hasLiked) likeIcon.setImageResource(R.drawable.ic_baseline_favorite_24);
        else likeIcon.setImageResource(R.drawable.ic_baseline_favorite_border_24);

        likesTextView.setText(String.valueOf(project.getLikes()));

    }

    public void loadClassification()
    {
        ServerRequest request = new ServerRequest();
        JSONArray projectIDs=new JSONArray();
        for (Project project : similarProjects)
        {
            projectIDs.put(project.getID());
        }
        request.getClassification(projectIDs, user.getToken());
        while (! request.isCompleted())
        {
            Log.e("loading Classif","...");
        }
        JSONObject result = request.getResult();

        try {
            boolean success = result.getBoolean("success");

            if (success)
            {
                JSONArray classifications = result.getJSONArray("classifications");
                for (int i=0;i<classifications.length();i++)
                {
                    JSONObject json = (JSONObject) classifications.get(i);
                    similarProjects.get(i).loadClassifFromJSON(json);
                }
            } else { Log.e("FAIL", result.getString("message")); }
        } catch (JSONException e) {e.printStackTrace();}
    }

}