package mdstudios.productivitycafe;

import org.json.JSONException;
import org.json.JSONObject;
import android.R.layout.*;

import java.math.BigDecimal;
import java.util.Calendar;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;

/**
 * Created by mickeydang on 2017-08-06.
 */

public class Course {
    //constants
    final String COURSE_NAME_ID = "name";

    //member varriables
    private String mCourseName;
    private long mTimeDay;
    private long mTimeWeek;
    private long mTimeMonth;
    private long mTimeYear;

    public Course (String name) {
        mCourseName = name;
        mTimeDay = 0;
        mTimeMonth = 0;
        mTimeWeek = 0;
        mTimeYear = 0;

    }

    public String getCourseName() {
        return mCourseName;
    }

    public void setCourseName(String courseName) {
        mCourseName = courseName;
    }

    public long getTimeDay() {
        return mTimeDay;
    }

    public void addTimeDay(long Time) {
        mTimeDay = mTimeDay + Time;
    }

    public long getTimeWeek() {
        return mTimeWeek;
    }

    public void addTimeWeek(long Time) {
        mTimeWeek = mTimeWeek + Time;
    }

    public long getTimeMonth() {
        return mTimeMonth;
    }

    public void addTimeMonth(long Time) {
        mTimeMonth = mTimeMonth + Time;
    }

    public long getTimeYear() {
        return mTimeYear;
    }

    public void addTimeYear(long Time) {
        mTimeYear = mTimeYear + Time;
    }
}
