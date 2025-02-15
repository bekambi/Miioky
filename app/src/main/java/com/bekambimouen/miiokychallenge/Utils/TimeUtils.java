package com.bekambimouen.miiokychallenge.Utils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.bekambimouen.miiokychallenge.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {
    // chat time ___________________________________________________________________________________
    public static boolean isDateToday(long milliSeconds) {
        Calendar toCheck = Calendar.getInstance();
        toCheck.setTimeInMillis(milliSeconds);

        Date toCheckDate = toCheck.getTime();

        toCheck.setTimeInMillis(System.currentTimeMillis());
        toCheck.set(Calendar.HOUR_OF_DAY, 0);
        toCheck.set(Calendar.MINUTE, 0);
        toCheck.set(Calendar.SECOND, 0);

        Date startDate = toCheck.getTime();

        return toCheckDate.compareTo(startDate) > 0;
    }

    public static boolean isYesterday(long milliSeconds) {

        Calendar toCheck = Calendar.getInstance();
        toCheck.setTimeInMillis(milliSeconds); // your date

        Calendar yesterday = Calendar.getInstance(); // today
        yesterday.add(Calendar.DAY_OF_YEAR, -1); // yesterday

        return yesterday.get(Calendar.YEAR) == toCheck.get(Calendar.YEAR)
                && yesterday.get(Calendar.DAY_OF_YEAR) == toCheck.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isDateInCurrentWeek(long milliSeconds) {
        Calendar currentCalendar = Calendar.getInstance();
        int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
        int year = currentCalendar.get(Calendar.YEAR);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(milliSeconds);
        int targetWeek = targetCalendar.get(Calendar.WEEK_OF_YEAR);
        int targetYear = targetCalendar.get(Calendar.YEAR);

        return week == targetWeek && year == targetYear;
    }

    public static String getFormattedTimestamp(Context context, long milliSeconds) {
        if (isDateToday(milliSeconds)) {
            return formatTimestamp(milliSeconds, "HH:mm");
        } else if (isYesterday(milliSeconds)) {
            return context.getString(R.string.yesterday);
        } else if (isDateInCurrentWeek(milliSeconds)) {
            return formatTimestamp(milliSeconds, "EEEE");
        } else {
            return context.getString(R.string.the)+" "+formatTimestamp(milliSeconds, "dd/MM/yy");
        }
    }

    public static String formatTimestamp(Long timestamp, String pattern) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(timestamp);
    }

    public static String getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }
    // time on Miioky ______________________________________________________________________________
    public static String getTime(AppCompatActivity context, long date) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sfd_d = new SimpleDateFormat("dd/MM/yyyy");
        String date_passe = sfd_d.format(new Date(date));

        String tps;
        long time = (System.currentTimeMillis() - date);
        if (time >= 2*3600*24000254025L) {
            tps = date_passe;
        } else if (time >= 11*30*86400000L) {
            tps = ("11 "+context.getString(R.string.month));
        } else if (time >= 10*30*86400000L) {
            tps = " "+("10 "+context.getString(R.string.month));
        } else if (time >= 9*30*86400000L) {
            tps = " "+("9 "+context.getString(R.string.month));
        } else if (time >= 8*30*86400000L) {
            tps = " "+("8 "+context.getString(R.string.month));
        } else if (time >= 7*30*86400000L) {
            tps = " "+("7 "+context.getString(R.string.month));
        } else if (time >= 6*30*86400000L) {
            tps = " "+("6 "+context.getString(R.string.month));
        } else if (time >= 5*30*86400000L) {
            tps = " "+("5 "+context.getString(R.string.month));
        } else if (time >= 4*30*86400000L) {
            tps = " "+("4 "+context.getString(R.string.month));
        } else if (time >= 3*30*86400000L) {
            tps = " "+("3 "+context.getString(R.string.month));
        } else if (time >= 2*30*86400000L) {
            tps = " "+("2 "+context.getString(R.string.month));
        } else if (time >= 30*86400000L) {
            tps = " "+("1 "+context.getString(R.string.month));
        } else if (time >= 21*86400000L) {
            tps = " "+("3 "+context.getString(R.string.weeks));
        } else if (time >= 14*86400000L) {
            tps = " "+("2 "+context.getString(R.string.weeks));
        } else if (time >= 7*86400000L) {
            tps = " "+("1 "+context.getString(R.string.week));
        } else if (time >= 6*86400000L) {
            tps = " "+("6"+context.getString(R.string.letter_j));
        } else if (time >= 5*86400000L) {
            tps = " "+("5"+context.getString(R.string.letter_j));
        } else if (time >= 4*86400000L) {
            tps = " "+("4"+context.getString(R.string.letter_j));
        } else if (time >= 3*86400000L) {
            tps = " "+("3"+context.getString(R.string.letter_j));
        } else if (time >= 2*86400000L) {
            tps = " "+("2"+context.getString(R.string.letter_j));
        } else if (time >= 86400000) {
            tps = " "+("1"+context.getString(R.string.letter_j));
        } else if (time >= 23*3600000) {
            tps = " "+("23"+context.getString(R.string.letter_h));
        } else if (time >= 22*3600000) {
            tps = " "+("22"+context.getString(R.string.letter_h));
        } else if (time >= 21*3600000) {
            tps = " "+("21"+context.getString(R.string.letter_h));
        } else if (time >= 20*3600000) {
            tps = " "+("20"+context.getString(R.string.letter_h));
        } else if (time >= 19*3600000) {
            tps = " "+("19"+context.getString(R.string.letter_h));
        } else if (time >= 18*3600000) {
            tps = " "+("18"+context.getString(R.string.letter_h));
        } else if (time >= 17*3600000) {
            tps = " "+("17"+context.getString(R.string.letter_h));
        } else if (time >= 16*3600000) {
            tps = " "+("16"+context.getString(R.string.letter_h));
        } else if (time >= 15*3600000) {
            tps = " "+("15"+context.getString(R.string.letter_h));
        } else if (time >= 14*3600000) {
            tps = " "+("14"+context.getString(R.string.letter_h));
        } else if (time >= 13*3600000) {
            tps = " "+("13"+context.getString(R.string.letter_h));
        } else if (time >= 12*3600000) {
            tps = " "+("12"+context.getString(R.string.letter_h));
        } else if (time >= 11*3600000) {
            tps = " "+("11"+context.getString(R.string.letter_h));
        } else if (time >= 10*3600000) {
            tps = " "+("10"+context.getString(R.string.letter_h));
        } else if (time >= 9*3600000) {
            tps = " "+("9"+context.getString(R.string.letter_h));
        } else if (time >= 8*3600000) {
            tps = " "+("8"+context.getString(R.string.letter_h));
        } else if (time >= 7*3600000) {
            tps = " "+("7"+context.getString(R.string.letter_h));
        } else if (time >= 6*3600000) {
            tps = " "+("6"+context.getString(R.string.letter_h));
        } else if (time >= 5*3600000) {
            tps = " "+("5"+context.getString(R.string.letter_h));
        } else if (time >= 4*3600000) {
            tps = " "+("4"+context.getString(R.string.letter_h));
        } else if (time >= 3*3600000) {
            tps = " "+("3"+context.getString(R.string.letter_h));
        } else if (time >= 2*3600000) {
            tps = " "+("2"+context.getString(R.string.letter_h));
        } else if (time >= 3600000) {
            tps = " "+("1"+context.getString(R.string.letter_h));
        } else if (time >= 120000) {
            tps = (int)(time/60000) + " "+context.getString(R.string.minutes_ago);
        } else {
            tps = " "+("10"+context.getString(R.string.letter_s));
        }

        return tps;
    }
}

