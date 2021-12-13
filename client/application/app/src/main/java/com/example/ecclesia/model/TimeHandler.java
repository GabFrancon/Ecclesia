package com.example.ecclesia.model;

import android.text.format.DateUtils;
import android.text.format.Time;
import java.util.TimeZone;

public class TimeHandler
{
    public TimeHandler()
    {
    }

    public static String getWrittenDate(String date, boolean fullDate)
    {
        String writtenMonth;
        String year = date.substring(0,4);
        String month = date.substring(5,7);
        String day = date.substring(8,10);
        switch (month)
        {
            case "01":writtenMonth="Janvier"; break;
            case "02":writtenMonth="Février"; break;
            case "03":writtenMonth="Mars"; break;
            case "04":writtenMonth="Avril"; break;
            case "05":writtenMonth="Mai"; break;
            case "06":writtenMonth="Juin"; break;
            case "07":writtenMonth="Juillet"; break;
            case "08":writtenMonth="Août"; break;
            case "09":writtenMonth="Septembre"; break;
            case "10":writtenMonth="Octobre"; break;
            case "11":writtenMonth="Novembre"; break;
            case "12":writtenMonth="Décembre"; break;
            default :writtenMonth=""; break;
        }
        if (fullDate) return day+" "+writtenMonth+" "+year;
        else return day+" "+writtenMonth;
    }

    public static String getWrittenTime(String time)
    {
        String hour = time.substring(0,2);
        String min = time.substring(3,5);

        return hour+'h'+min;
    }

    public CharSequence getRelativeTime(long timestamp)
    {
        int offset = TimeZone.getTimeZone(Time.getCurrentTimezone()).getRawOffset() + TimeZone.getTimeZone(Time.getCurrentTimezone()).getDSTSavings();

        CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp, System.currentTimeMillis()-offset, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);

        return relTime;
    }
}
