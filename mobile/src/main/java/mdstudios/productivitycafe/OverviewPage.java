package mdstudios.productivitycafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class OverviewPage extends AppCompatActivity {

    final String DEFAULT_COURSE_NAME = "New Course";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_page);

        if (CoursesList.mArrayList.size() == 0) {
            CoursesList.mArrayList.add(new Course(DEFAULT_COURSE_NAME));
        }


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
}
