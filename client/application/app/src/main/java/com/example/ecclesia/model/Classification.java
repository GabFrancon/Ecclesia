package com.example.ecclesia.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.ecclesia.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Classification implements Parcelable
{
    private int icon;
    private String name;

    public Classification (String classificationName)
    {
        this.name=classificationName;
        switch (classificationName)
        {
            case "Travail": icon = R.drawable.ic_work; break;
            case "Technologie": icon = R.drawable.ic_tech; break;
            case "Ecologie": icon = R.drawable.ic_ecological; break;
            case "Sécurité" : icon = R.drawable.ic_security; break;
            case "Santé": icon = R.drawable.ic_health; break;
            case "Education": icon = R.drawable.ic_education; break;
            case "Urbanisme": icon = R.drawable.ic_urbanism; break;
            case "Musique": icon = R.drawable.ic_music; break;
            case "Arts plastiques": icon = R.drawable.ic_drawing; break;
            case "Audiovisuel": icon = R.drawable.ic_audiovisual; break;
            case "Spectacle vivant": icon=R.drawable.ic_show; break;
            case "Littérature": icon = R.drawable.ic_litterature; break;
            case "Restauration": icon = R.drawable.ic_cooking; break;
            case "Mode": icon = R.drawable.ic_fashion; break;
            case "Sciences": icon = R.drawable.ic_physics; break;
            case "Nature": icon = R.drawable.ic_nature; break;
            case "Humanités": icon = R.drawable.ic_history; break;
            case "Spiritualité": icon=R.drawable.ic_spirituality; break;
            case "Jeux": icon=R.drawable.ic_game; break;
            case "Sport": icon=R.drawable.ic_sport; break;
            case "Conférence": icon = R.drawable.ic_conference; break;
            case "Aménagement": icon = R.drawable.ic_building; break;
            case "Festival & Célébration": icon = R.drawable.ic_festival; break;
            case "Divertissement": icon = R.drawable.ic_entertainment; break;
            case "Rencontre": icon = R.drawable.ic_meet; break;
            case "Salon & Exposition": icon = R.drawable.ic_exposition; break;
            case "Action sociale": icon = R.drawable.ic_social_action; break;
            case "Compétition": icon = R.drawable.ic_competition; break;
            case "Excursion": icon = R.drawable.ic_excursion; break;
            case "Atelier & Formation": icon = R.drawable.ic_classroom; break;
            case "Expérience": icon = R.drawable.ic_experiment; break;
            case "Cette semaine": icon = R.drawable.ic_coming_soon; break;
            case "Tendances": icon = R.drawable.ic_trends; break;
        }
    }

    protected Classification(Parcel in)
    {
        icon = in.readInt();
        name = in.readString();
    }

    public static final Creator<Classification> CREATOR = new Creator<Classification>() {
        @Override
        public Classification createFromParcel(Parcel in) {
            return new Classification(in);
        }

        @Override
        public Classification[] newArray(int size) {
            return new Classification[size];
        }
    };

    public String getName()
    {
        return name;
    }

    public int getIcon()
    {
        return icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(icon);
        dest.writeString(name);
    }
}
