package mdstudios.productivitycafe;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static android.R.attr.x;
import static android.R.id.list;
import static mdstudios.productivitycafe.R.id.courseName;

public class CoursesList extends ListActivity {

    final String FIRST_COURSE_TITLE = "New Course";
    final String MAX_COURSE_ALERT = "Sorry! You have reached the maximum amount of courses!";
//            this.getString(R.string.max_course_alert);

    static ListView mCourseList;
    FloatingActionButton mAddCourse;

    static ArrayList<Course> mArrayList = new ArrayList<>();
    static CourseListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_list);

        mCourseList = (ListView) findViewById(list);
        mAddCourse = (FloatingActionButton) findViewById(R.id.addButton);

        mAdapter = new CourseListAdapter(this, R.layout.activity_course_item, mArrayList);

        mCourseList.setAdapter(mAdapter);

        //TODO set up edit feature
        //Currently unable to get a focused child so none of the code below runs!
//        try {
//            mTitle = (EditText) mCourseList.getFocusedChild().findViewById(courseName);
//        } catch (NullPointerException e) {
//            Log.d("Cafe", "Couldn't find anything!");
//        }
//        if (mTitle != null) {
//            mTitle.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//                @Override
//                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                    int focusPosition = mCourseList.getFocusedChild().getId();
//                    for (int x = 0; x < mAdapter.mListOfCourses.size() - 1 ; x++) {
//                        if (!Objects.equals(mAdapter.mListOfCourses.get(x).getCourseName(), mTitle.getText().toString())) {
//                            focusPosition = x;
//                        }
//                    }
//                    mAdapter.mListOfCourses.get(focusPosition).setCourseName(mTitle.getText().toString());
//                    mAdapter.notifyDataSetChanged();
//                    return false;
//                }
//            });
//        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, EditCourse.class);
        intent.putExtra("Position", position);
        startActivity(intent);

    }

    public void makeNewCourse(View v) {

        if (mArrayList.size() < 5) {
            Course newCourse = new Course(FIRST_COURSE_TITLE);
            mArrayList.add(newCourse);
            mAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), MAX_COURSE_ALERT, Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack (View v) {
        Intent intent = new Intent (this, OverviewPage.class);
        startActivity(intent);
    }



}
