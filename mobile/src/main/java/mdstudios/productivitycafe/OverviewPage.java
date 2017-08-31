package mdstudios.productivitycafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import static android.R.attr.id;
import static android.R.string.no;
import static mdstudios.productivitycafe.CoursesList.filepath;
import static mdstudios.productivitycafe.CoursesList.filepath2;
import static mdstudios.productivitycafe.CoursesList.mArrayList;
import static mdstudios.productivitycafe.CoursesList.mFile;
import static mdstudios.productivitycafe.CoursesList.mFile2;
import static mdstudios.productivitycafe.CoursesList.readFile;

public class OverviewPage extends AppCompatActivity {
    final long MILLISECONDS_IN_DAY = 1000* 60*60*24;
    final long MILLISECONDS_IN_WEEK = MILLISECONDS_IN_DAY * 7;
    final long MILLISECONDS_IN_MONTH = MILLISECONDS_IN_DAY * 365 / 12;
    final long MILLISECONDS_IN_YEAR = MILLISECONDS_IN_DAY * 365;
    final String DEFAULT_COURSE_NAME = "New Course";
    final String MAIN_MESSAGE_ONE = "You've Worked ";
    final String MAIN_MESSAGE_TWO = " Hours This Week!";
    final String STREAK_MESSAGE = "Non Zero Day ";
    final String DAY_KEY = "Day";
    final String WEEK_KEY = "Week";
    final String MONTH_KEY = "Month";
    final String YEAR_KEY = "Year";
    final String STREAK_KEY = "Streak";
    final String CHANGE_KEY = "ChangedAlready";
    final int MILLISECONDS_IN_HOUR = 1000*60*60;


    TextView mMainMessageView;
    TextView mStreakView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_page);

        getFilePath();

        SharedPreferences sharedPrefs = getPreferences(Context.MODE_PRIVATE);
        if (!sharedPrefs.contains(DAY_KEY) || !sharedPrefs.contains(WEEK_KEY)
                || !sharedPrefs.contains(MONTH_KEY) || !sharedPrefs.contains(YEAR_KEY)) {
            saveDates();
        } else {
            modifyDates(sharedPrefs);
        }
        Log.d("Cafe", "The streak number is " + sharedPrefs.getInt(STREAK_KEY, -1));

        if (!sharedPrefs.getBoolean(CHANGE_KEY, true)) {
            Log.d("Cafe", "no changes today");
        }
        mMainMessageView = (TextView) findViewById(R.id.TotalHoursCount);
        String message = MAIN_MESSAGE_ONE + calculateWeekHours() + MAIN_MESSAGE_TWO;
        mMainMessageView.setText(message);

        mStreakView = (TextView) findViewById(R.id.StreakCount);
        message = STREAK_MESSAGE + sharedPrefs.getInt(STREAK_KEY, 0);
        mStreakView.setText(message);

    }


    public void toStats (View v) {
        //goes to stats activity
        Intent toStatsIntent = new Intent(OverviewPage.this, Stats.class);
        startActivity(toStatsIntent);


    }

    public void toCourses (View v) {
        //goes to courses activity
        Intent toCoursesIntent = new Intent(OverviewPage.this, CoursesList.class);
        startActivity(toCoursesIntent);
    }

    public void addBlock (View v) {
        //goes to adding block activity
        Intent addBlockIntent = new Intent(OverviewPage.this, AddNewBlock.class);
        startActivity(addBlockIntent);

    }

    private void saveDates () {
        Date expiryDate = new Date();
        long expiry = expiryDate.getTime() + MILLISECONDS_IN_DAY;
        SharedPreferences sharedPrefs = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(DAY_KEY, expiry);

        expiry = expiryDate.getTime() + MILLISECONDS_IN_WEEK;
        editor.putLong(WEEK_KEY, expiry);

        expiry = expiryDate.getTime() + MILLISECONDS_IN_MONTH;
        editor.putLong(MONTH_KEY, expiry);

        expiry = expiryDate.getTime() + MILLISECONDS_IN_YEAR;
        editor.putLong(YEAR_KEY, expiry);

        if (calculateDayHours() == 0) {
            editor.putInt(STREAK_KEY, 0);
        }
        editor.putBoolean(CHANGE_KEY, false);
        editor.apply();
    }

    private void modifyDates(SharedPreferences prefs) {
        int level = 0;
        Date expiryDay;
        Date today = new Date();
        SharedPreferences.Editor editor = prefs.edit();
        expiryDay = new Date(prefs.getLong(DAY_KEY, -1));
        if (isExpired(expiryDay)) {
            level ++;
            editor.putLong(DAY_KEY, today.getTime() + MILLISECONDS_IN_DAY);
            editor.putBoolean(CHANGE_KEY, false);
        }
        expiryDay = new Date(prefs.getLong(WEEK_KEY, -1));
        if (isExpired(expiryDay)) {
            level ++;
            editor.putLong(WEEK_KEY, today.getTime() + MILLISECONDS_IN_WEEK);
        }
        expiryDay = new Date(prefs.getLong(MONTH_KEY, -1));
        if (isExpired(expiryDay)) {
            level ++;
            editor.putLong(MONTH_KEY, today.getTime() + MILLISECONDS_IN_MONTH);
        }
        expiryDay = new Date(prefs.getLong(YEAR_KEY, -1));
        if (isExpired(expiryDay)) {
            level ++;
            editor.putLong(YEAR_KEY, today.getTime() + MILLISECONDS_IN_YEAR);
        }

        if (level != 0) {
            editor.apply();
            clearArray(level);
            Log.d("Cafe", "Level is " +level);
            CoursesList.storeFile(this.getFilesDir().getPath() + "/courselist.xml");
        }

    }

    private boolean isExpired(Date expiry) {
        Date today = new Date();
        return today.compareTo(expiry) > 0;
    }

    private void clearArray (int level) {
        if (level >= 1) {
            for (int x = 0 ; x < CoursesList.mArrayList.size() ; x++) {
                CoursesList.mArrayList.get(x).addTimeDay(-1 * CoursesList.mArrayList.get(x).getTimeDay());
            }
            if (level >=2) {
                for (int x = 0 ; x < CoursesList.mArrayList.size() ; x++) {
                    CoursesList.mArrayList.get(x).addTimeWeek(-1 * CoursesList.mArrayList.get(x).getTimeWeek());
                }
                if (level >=3) {
                    for (int x = 0 ; x < CoursesList.mArrayList.size() ; x++) {
                        CoursesList.mArrayList.get(x).addTimeMonth(-1 * CoursesList.mArrayList.get(x).getTimeMonth());
                    }
                    if (level == 4) {
                        for (int x = 0 ; x < CoursesList.mArrayList.size() ; x++) {
                            CoursesList.mArrayList.get(x).addTimeYear(-1 * CoursesList.mArrayList.get(x).getTimeYear());
                        }
                    }
                }
            }


        }
    }

    private int calculateWeekHours () {
        int hours = 0;
        for (int x = 0 ; x < CoursesList.mArrayList.size(); x++) {
            hours = hours + Math.round(CoursesList.mArrayList.get(x).getTimeWeek()/MILLISECONDS_IN_HOUR);
        }
        return hours;
    }

    private int calculateDayHours() {
        int hours = 0;
        for (int x = 0 ; x < CoursesList.mArrayList.size(); x++) {
            hours = hours + Math.round(CoursesList.mArrayList.get(x).getTimeDay()/MILLISECONDS_IN_HOUR);
        }
        return hours;
    }

    private void loadArrayList () {
        mFile = new File(this.getFilesDir().getPath() + "/courselist.xml");

        if (mFile.exists()) {
            try {
                FileInputStream is = new FileInputStream(mFile);
                CoursesList.readFile(is);
            } catch (IOException ioe) {
                Log.d("Cafe", ioe.getMessage());
            }

        } else {
            try {
                mFile.createNewFile();
            } catch (IOException ioe) {
                Log.d("Cafe", ioe.getMessage());
            }
            Log.d("Cafe", "File did not exist");
        }
    }

    private void getFilePath() {
        mFile = new File(this.getFilesDir().getPath() + "/courselist.xml");
        mFile2 = new File(this.getFilesDir().getPath() + "/daylist.xml");
        filepath = mFile.getPath();
        filepath2 = mFile2.getPath();
        if (mArrayList.size() == 0) {
            loadArrayList();
        }
    }
}
