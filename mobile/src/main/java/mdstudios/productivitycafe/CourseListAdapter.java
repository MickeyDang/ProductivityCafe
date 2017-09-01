package mdstudios.productivitycafe;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import android.widget.Button;

import android.widget.TextView;



import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static mdstudios.productivitycafe.R.id.courseName;

/**
 * Created by mickeydang on 2017-08-08.
 */

public class CourseListAdapter extends ArrayAdapter<Course> {

    final int MILLISECONDS_IN_HOUR = 1000*60*60;
    final int MILLISECONDS_IN_MINUTE = 1000*60;
    final int MILLISECONDS_IN_SECOND = 1000;
    final int SECONDS_IN_MINUTE = 60;
    private final String TITLE_DEFAULT = "New Course";
    private final String TIME_STARTER_TEXT_1 = "Time This Week: ";
    private final String TIME_STARTER_TEXT_2 = "Time Today: ";
    final String ZERO_TIME = "0";

    private Context mContext;
    private int mLayoutResourceID;
    private ArrayList<Course> mListOfCourses = null;
    private Course mSelectedItem;

    public Course getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedItem(Course selectedItem) {
        mSelectedItem = selectedItem;
    }



    public CourseListAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList<Course> data) {
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
            holder.mTitle = (TextView) convertView.findViewById(courseName);
            holder.mTitle.setText(TITLE_DEFAULT);
            holder.mBody1 = (TextView) convertView.findViewById(R.id.timeDisplayWeek);
            holder.mBody2 = (TextView) convertView.findViewById(R.id.timeDisplayDay);
            holder.mDeleteButton = (Button) convertView.findViewById(R.id.deleteButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Course changedCourse = getSelectedItem();
        if (changedCourse != null) {
            changedCourse.setCourseName(holder.mTitle.getText().toString());
            notifyDataSetChanged();
//            Log.d("Cafe", changedCourse.getCourseName() + ": " + position);
            setSelectedItem(null);
        } else {

        }

        Course course = mListOfCourses.get(position);

        holder.mTitle.setText(course.getCourseName());

        String timeText = TIME_STARTER_TEXT_1 + getTimeInString(course.getTimeWeek());
        holder.mBody1.setText(timeText);
        timeText = TIME_STARTER_TEXT_2 + getTimeInString(course.getTimeDay());
        holder.mBody2.setText(timeText);

        storeChanges();

        return convertView;
    }

    private void storeChanges () {
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };

        try {
            inputStream.close();
        } catch (IOException e) {

        }

    }

    static class ViewHolder {
        TextView mTitle;
        TextView mBody1;
        TextView mBody2;
        Button mDeleteButton;
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
