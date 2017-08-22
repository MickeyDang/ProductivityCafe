package mdstudios.productivitycafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import javax.xml.datatype.Duration;

import static mdstudios.productivitycafe.CoursesList.mArrayList;
import static mdstudios.productivitycafe.R.id.courseName;
import static mdstudios.productivitycafe.R.id.deleteButton;
import static mdstudios.productivitycafe.R.id.saveButton;
import static mdstudios.productivitycafe.R.id.timeDay;
import static mdstudios.productivitycafe.R.id.timeMonth;
import static mdstudios.productivitycafe.R.id.timeWeek;
import static mdstudios.productivitycafe.R.id.timeYear;

public class EditCourse extends AppCompatActivity {

    final int MILLISECONDS_IN_HOUR = 1000*60*60;
    final int MILLISECONDS_IN_MINUTE = 1000*60;
    final int MILLISECONDS_IN_SECOND = 1000;
    final String ZERO_TIME = "0";

    Course mEditedCourse;
    EditText mEditText;
    TextView mDays;
    TextView mWeeks;
    TextView mMonths;
    TextView mYears;
    FloatingActionButton mSave;
    FloatingActionButton mDelete;
    int mArrayPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        mEditText = (EditText) findViewById(courseName);
        mDays = (TextView) findViewById(timeDay);
        mWeeks = (TextView) findViewById(timeWeek);
        mMonths = (TextView) findViewById(timeMonth);
        mYears = (TextView) findViewById(timeYear);
        mSave = (FloatingActionButton) findViewById(saveButton);
        mDelete = (FloatingActionButton) findViewById(deleteButton);

        Intent myIntent = getIntent();
        Bundle bundle = myIntent.getExtras();
        mArrayPosition = (int) bundle.get("Position");
        Log.d("Cafe", "The position is " + mArrayPosition);


        mEditedCourse = CoursesList.mArrayList.get(mArrayPosition);

        mEditText.setText(mEditedCourse.getCourseName());

        String daysText = mDays.getText() + " " + getTimeInString(mEditedCourse.getTimeDay());
        String weeksText = mWeeks.getText() + " " + getTimeInString(mEditedCourse.getTimeWeek());
        String monthsText = mMonths.getText() + " " + getTimeInString(mEditedCourse.getTimeMonth());
        String yearText = mYears.getText() + " " + getTimeInString(mEditedCourse.getTimeYear());

        mDays.setText(daysText);
        mWeeks.setText(weeksText);
        mMonths.setText(monthsText);
        mYears.setText(yearText);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteBlock();
            }
        });
    }


    private void saveChanges () {
        String newName = mEditText.getText().toString();
        mArrayList.get(mArrayPosition).setCourseName(newName);

        Intent intent = new Intent(this, CoursesList.class);
        startActivity(intent);

    }

    private void deleteBlock() {
        mArrayList.remove(mArrayPosition);
        Intent intent = new Intent(this, CoursesList.class);
        startActivity(intent);
    }

    private String getTimeInString (long timeInSeconds) {
        String newString = configureTime(convertMilliseconds( (int) timeInSeconds, MILLISECONDS_IN_HOUR))
                + " : " + configureTime(convertMilliseconds( (int) timeInSeconds, MILLISECONDS_IN_MINUTE))
                + " : " + configureTime(convertMilliseconds( (int) timeInSeconds, MILLISECONDS_IN_SECOND));

        return newString;
    }

    private Integer convertMilliseconds (int timeInSeconds, int conversion) {
        return (timeInSeconds/conversion) % 60;
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
