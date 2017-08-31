package mdstudios.productivitycafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeUnit;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;
import java.io.File;
import java.util.Map;
import java.util.Set;

import static android.R.attr.x;
import static mdstudios.productivitycafe.CoursesList.mArrayList;
import static mdstudios.productivitycafe.CoursesList.mFile;
import static mdstudios.productivitycafe.CoursesList.mFile2;
import static mdstudios.productivitycafe.R.id.screen;
import static mdstudios.productivitycafe.R.id.startButton;


public class AddNewBlock extends AppCompatActivity {

    final int MILLISECONDS_IN_HOUR = 1000*60*60;
    final int MILLISECONDS_IN_MINUTE = 1000*60;
    final int MILLISECONDS_IN_SECOND = 1000;
    final int SECONDS_IN_MINUTE = 60;
    final String ZERO_TIME = "0";
    final String NO_BLOCK_ALERT = "No block started";
    final String DAY_KEY = "Day";
    final String WEEK_KEY = "Week";
    final String MONTH_KEY = "Month";
    final String YEAR_KEY = "Year";
    final String STREAK_KEY = "Streak";
    final String CHANGE_KEY = "ChangedAlready";
    final String NOTIFICATION_TITLE = "Productivity Cafe";
    final String NOTIFICATION_MESSAGE = "Time is up!";
    final String NOTIFICATION_COUNTDOWN = "Time Remaining: ";
    final String NOTIFICATION_CANCEL = "Countdown Canceled";
    final int FINISHED_NOTIF_ID = 1;
    final int COUNTDOWN_NOTIF_ID = 2;
    final int CANCELED_NOTIF_ID = 3;

    SoundPool mSoundPool;
    int mSoundID;
    Boolean alarmOn = false;
    private final float LEFT_VOLUME = 1.0f;
    private final float RIGHT_VOLUME = 1.0f;
    private final int LOOP_TIME = 2;
    private final int PRIORITY = 0;
    private final float NORMAL_PLAY_RATE = 1.0f;
    private String mTime;


    EditText mHoursInput;
    EditText mMinutesInput;
    TextView mSecondsView;
    Spinner mSpinner;
    ArrayAdapter<String> mArrayAdapter;
    Course mHandledCourse;
    String[] mCourseNames = new String[CoursesList.mArrayList.size()];
    static CountDownTimer mCDT;
    NotificationManager mNotificationManager;
    Boolean screenRotating = false;

    @Override
    protected void onStart() {
        super.onStart();
        stopAlarm();
        Log.d("Cafe", "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Cafe", "onResume");
        stopAlarm();
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        long x = prefs.getLong("timeLeft", -1);
//        Log.d("Cafe", "onResume " + String.valueOf(x));
        if (x > 1 && mCDT ==null) {
            startCDT(x, findViewById(R.id.startButton));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_block);

        Log.d("Cafe", "onCreate");
        for (int x = 0 ; x < CoursesList.mArrayList.size() ; x++) {
            mCourseNames[x] = CoursesList.mArrayList.get(x).getCourseName();
        }

        mHoursInput = (EditText) findViewById(R.id.setTimeHour);
        mMinutesInput = (EditText) findViewById(R.id.setTimeMinute);
        mSecondsView = (TextView) findViewById(R.id.timeSecond);
        mSpinner = (Spinner) findViewById(R.id.coursesSelection);
        mArrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mCourseNames);
        mArrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mSpinner.setAdapter(mArrayAdapter);

//        Log.d("Cafe", "The length of the array is " + mCourseNames.length);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mHandledCourse = CoursesList.mArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build())
                .build();
        mSoundID = mSoundPool.load(getApplicationContext(), R.raw.analog_watch_alarm, 1);

        if (mCDT != null) {
            mCDT.cancel();
            //prevents the alarm from triggering in mCDT.onFinish()
            alarmOn = true;
//            Log.d("Cafe", alarmOn.toString());
            mCDT.onFinish();
            mSoundPool.autoPause();
            mSoundPool.stop(mSoundID);
            stopAlarm();
            makeNotification(NOTIFICATION_TITLE, mTime, false, COUNTDOWN_NOTIF_ID);
        }

        stopAlarm();

        if (savedInstanceState != null) {
            long timeLeft = (long) savedInstanceState.get("timeLeft");
            startCDT(timeLeft, findViewById(R.id.startButton));
        }



    }

    public void startTime (final View v) {

        v.setEnabled(false);

      Integer hours = Integer.parseInt(mHoursInput.getText().toString());
      Integer minutes = Integer.parseInt(mMinutesInput.getText().toString());
      Integer blockTime = hours * MILLISECONDS_IN_HOUR + minutes * MILLISECONDS_IN_MINUTE;

      startCDT(blockTime, v);


        new AlertDialog.Builder(this)
                .setIcon(R.drawable.coffeecup)
                .setMessage("We have screen pinned your screen to ensure productivity. You can disable this " +
                        "at anytime by long pressing the 'back' and 'previous apps' buttons simultaneously")
                .setPositiveButton("Got it!", null)
                .setTitle("Screen Pinning")
                .show();

      startLockTask();

    }

    public void stopTime (View v) {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("timeLeft", 0);
//        Log.d("Cafe", "onStop " + String.valueOf(getTimeRemaining()));
        editor.apply();

        if (alarmOn) {
            stopAlarm();
        } else if (mCDT != null) {
            mCDT.cancel();
            mCDT.onFinish();
            stopAlarm();
            changeStreak();
        } else {
            Toast.makeText(this, NO_BLOCK_ALERT, Toast.LENGTH_SHORT).show();
            stopAlarm();
        }

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

    private void saveTimeToCourse (long Time) {

        Integer position = mSpinner.getSelectedItemPosition();
        Course course = CoursesList.mArrayList.get(position);

        course.addTimeDay(Time);
        course.addTimeWeek(Time);
        course.addTimeMonth(Time);
        course.addTimeYear(Time);
        CoursesList.storeFile(mFile.getName());
        CoursesList.storeFile2(mFile2.getName(), mArrayList.get(position).getTimeDay());
    }

    private void changeStreak () {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);

        if (!prefs.getBoolean(CHANGE_KEY, false)) {
            Log.d("Cafe","Streak changed!");
            int newStreak = prefs.getInt(STREAK_KEY, 0) + 1;
            Log.d("Cafe", "New number is " + prefs.getInt(STREAK_KEY, -1));
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(STREAK_KEY, newStreak);
            editor.putBoolean(CHANGE_KEY, true);
            editor.apply();
        }
    }

    private void soundAlarm() {
        Log.d("Cafe", "Alarm sounding!");
        if (alarmOn) {
            makeNotification(NOTIFICATION_TITLE, NOTIFICATION_MESSAGE, true, FINISHED_NOTIF_ID);
        }
        mSoundPool.play(mSoundID, LEFT_VOLUME, RIGHT_VOLUME, LOOP_TIME, PRIORITY, NORMAL_PLAY_RATE);
        alarmOn = true;

    }

    private void stopAlarm() {
        mSoundPool.stop(mSoundID);
        mSoundPool.autoPause();
        alarmOn = false;
    }

    private void makeNotification (String title, String message, boolean autoCancel, int IDNumber) {

        Intent intent = new Intent(this, getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.coffeecup)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(autoCancel);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotificationManager.notify(IDNumber, notifBuilder.build());

    }

    @Override
    protected void onPause() {
        super.onPause();
        //for screen rotation
        Bundle outState = new Bundle();
        outState.putLong("timeLeft", getTimeRemaining());
        screenRotating = true;
        Log.d("Cafe", "onPause");
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Cafe", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Cafe", "onDestroy");
        if (mCDT != null) {
            mCDT.cancel();
            mCDT.onFinish();
            mNotificationManager.cancel(FINISHED_NOTIF_ID);
            if (!screenRotating) {
                makeNotification(NOTIFICATION_TITLE, NOTIFICATION_CANCEL, true, CANCELED_NOTIF_ID);
            }

        }
        stopAlarm();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("timeLeft", getTimeRemaining());
        screenRotating = true;

    }

    private long getTimeRemaining() {
        long x = Integer.valueOf(mHoursInput.getText().toString()) * MILLISECONDS_IN_HOUR +
                Integer.valueOf(mMinutesInput.getText().toString()) * MILLISECONDS_IN_MINUTE +
                Integer.valueOf(mSecondsView.getText().toString()) * MILLISECONDS_IN_SECOND;
        return x;
    }

    private void startCDT (long blockTime, final View v) {
        final KeyListener hoursKL = mHoursInput.getKeyListener();
        final KeyListener minutesKL = mMinutesInput.getKeyListener();

        mCDT = new CountDownTimer(blockTime, MILLISECONDS_IN_SECOND) {
            long startTime = System.currentTimeMillis();

            @Override
            public void onTick(long millisUntilFinished) {

                Integer X = Math.round(millisUntilFinished/MILLISECONDS_IN_HOUR);
                mHoursInput.setText(configureTime(X));
                X = Math.round((millisUntilFinished/MILLISECONDS_IN_MINUTE)%SECONDS_IN_MINUTE);
                mMinutesInput.setText(configureTime(X));
                X = Math.round((millisUntilFinished/MILLISECONDS_IN_SECOND)%SECONDS_IN_MINUTE);
                mSecondsView.setText(configureTime(X));
                mHoursInput.setKeyListener(null);
                mMinutesInput.setKeyListener(null);
                mSpinner.setEnabled(false);

                mTime =  NOTIFICATION_COUNTDOWN + mHoursInput.getText() + " : "
                        + mMinutesInput.getText() + " : "
                        + mSecondsView.getText();

                makeNotification(NOTIFICATION_TITLE, mTime, false, COUNTDOWN_NOTIF_ID);

                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putLong("timeLeft", getTimeRemaining());
                editor.apply();

                if (millisUntilFinished <= MILLISECONDS_IN_SECOND) {
                    if (!alarmOn) {
                        soundAlarm();
                    }
                }
            }

            @Override
            public void onFinish() {
//                Log.d("Cafe", alarmOn.toString());

                mNotificationManager.cancel(COUNTDOWN_NOTIF_ID);
                String string = ZERO_TIME + ZERO_TIME;

                mSecondsView.setText(string);
                mMinutesInput.setText(string);
                mHoursInput.setText(string);

                mHoursInput.setKeyListener(hoursKL);
                mMinutesInput.setKeyListener(minutesKL);
                mSpinner.setEnabled(true);
                v.setEnabled(true);

                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                saveTimeToCourse(elapsedTime);

                changeStreak();
                mCDT = null;

            }

        };

        mCDT.start();
    }
}
