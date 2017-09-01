package mdstudios.productivitycafe;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.x;
import static mdstudios.productivitycafe.R.id.CourseName;
import static mdstudios.productivitycafe.R.id.TimeView;
import static mdstudios.productivitycafe.R.id.courseName;
import static mdstudios.productivitycafe.R.id.percentageView;
import static mdstudios.productivitycafe.R.id.portionBar;

/**
 * Created by mickeydang on 2017-08-27.
 */

public class CourseByCourseAdapter extends ArrayAdapter<Course>{

    private final int MILLISECONDS_IN_HOUR = 1000*60*60;
    private final int MILLISECONDS_IN_MINUTE = 1000*60;
    private final int MILLISECONDS_IN_SECOND = 1000;
    private final int SECONDS_IN_MINUTE = 60;
    private String ZERO_TIME = "0";

    private ArrayList<Course> mListOfCourses;
    private Context mContext;
    private int mLayoutResourceID;


    public CourseByCourseAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<Course> data) {
        super(context, resource, data);
        mListOfCourses = data;
        mContext = context;
        mLayoutResourceID = resource;
    }


    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListOfCourses.size();
    }

    @Override
    public Course getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater =  ((Activity)mContext).getLayoutInflater();
            convertView = inflater.inflate(mLayoutResourceID, parent, false);

            holder = new ViewHolder();
            holder.mName = (TextView) convertView.findViewById(CourseName);
            holder.mTime = (TextView) convertView.findViewById(TimeView);
            holder.mPercent = (TextView) convertView.findViewById(percentageView);
            holder.mProgressBar = (ProgressBar) convertView.findViewById(portionBar);
//            Log.d("Cafe", String.valueOf(convertView.getId()) + " " + convertView.getContentDescription());
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Course course = mListOfCourses.get(position);

//        Log.d("Cafe", course.getCourseName());
        holder.mName.setText(course.getCourseName());
        String timeText = "Time (Week): " + getTimeInString(course.getTimeWeek());
        holder.mTime.setText(timeText);

        long totalTime = 0;

        for (int x = 0; x < mListOfCourses.size() ; x++) {
            totalTime = totalTime + mListOfCourses.get(x).getTimeWeek();
        }

        float fraction = (float) course.getTimeWeek()/ (float) totalTime;
        int percent = Math.round(fraction*100);

        holder.mProgressBar.setMax(100);
        holder.mProgressBar.setProgress(percent);
        String percentText = percent + "%";
        holder.mPercent.setText(percentText);
        return convertView;
    }

    private static class ViewHolder {
        TextView mName;
        TextView mTime;
        TextView mPercent;
        ProgressBar mProgressBar;
    }

    private String getTimeInString (long timeInSeconds) {
        String newString = configureTime((int) timeInSeconds / MILLISECONDS_IN_HOUR)
                + " : " + configureTime(convertMilliseconds( (int) timeInSeconds, MILLISECONDS_IN_MINUTE))
                + " : " + configureTime(convertMilliseconds( (int) timeInSeconds, MILLISECONDS_IN_SECOND));

        return newString;
    }

    private Integer convertMilliseconds (int timeInSeconds, int conversion) {
        return (timeInSeconds/conversion) % SECONDS_IN_MINUTE;
    }

    private String configureTime (Integer X) {
        String newString;
        if (X < 10) {
            newString = ZERO_TIME + X;
        } else {
            newString = X.toString();
        }

        return newString;
    }

}
