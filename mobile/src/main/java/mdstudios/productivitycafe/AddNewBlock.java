package mdstudios.productivitycafe;

import android.icu.util.TimeUnit;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class AddNewBlock extends AppCompatActivity {

    final int MILLISECONDS_IN_HOUR = 1000*60*60;
    final int MILLISECONDS_IN_MINUTE = 1000*60;
    final int MILLISECONDS_IN_SECOND = 1000;
    final int SECONDS_IN_MINUTE = 60;
    final String ZERO_TIME = "0";
    final String NO_BLOCK_ALERT = "No block started";

    EditText mHoursInput;
    EditText mMinutesInput;
    TextView mSecondsView;
    Spinner mSpinner;
    ArrayAdapter<String> mArrayAdapter;
    Course mHandledCourse;
    String[] mCourseNames = new String[CoursesList.mArrayList.size()];
    CountDownTimer mCDT;
    File mFile = new File("courselist.xml");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_block);

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

    }

    public void startTime (final View v) {

        v.setEnabled(false);
        final KeyListener hoursKL = mHoursInput.getKeyListener();
        final KeyListener minutesKL = mMinutesInput.getKeyListener();

      Integer hours = Integer.parseInt(mHoursInput.getText().toString());
      Integer minutes = Integer.parseInt(mMinutesInput.getText().toString());
      Integer blockTime = hours * MILLISECONDS_IN_HOUR + minutes * MILLISECONDS_IN_MINUTE;


      mCDT = new CountDownTimer((long) blockTime, MILLISECONDS_IN_SECOND) {

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
          }

          @Override
          public void onFinish() {
              String string = ZERO_TIME + ZERO_TIME;
              mSecondsView.setText(string);
              mMinutesInput.setText(string);
              mHoursInput.setText(string);
              mHoursInput.setKeyListener(hoursKL);
              mMinutesInput.setKeyListener(minutesKL);
              mSpinner.setEnabled(true);
              long endTime = System.currentTimeMillis();
              long elapsedTime = endTime - startTime;
              saveTimeToCourse(elapsedTime);
              v.setEnabled(true);
          }

      };

      mCDT.start();
    }

    public void stopTime (View v) {
        if (mCDT != null) {
            mCDT.cancel();
            mCDT.onFinish();
        } else {
            Toast.makeText(this, NO_BLOCK_ALERT, Toast.LENGTH_SHORT).show();
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
    }
}
