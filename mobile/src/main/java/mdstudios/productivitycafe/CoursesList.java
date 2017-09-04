package mdstudios.productivitycafe;

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import java.io.*;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.w3c.dom.*;
import java.util.Date;
import static android.R.id.list;


public class CoursesList extends ListActivity {

    final String FIRST_COURSE_TITLE = "New Course";
    final String MAX_COURSE_ALERT = "Sorry! You have reached the maximum amount of courses!";


    static ListView mCourseList;
    Button mAddCourse;

    static ArrayList<Course> mArrayList = new ArrayList<>();
    CourseListAdapter mAdapter;
    static File mFile;
    static File mFile2;
//    static FileOutputStream fos;
    static String filepath;
    static String filepath2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses_list);

        mFile = new File(this.getFilesDir().getPath() + "/courselist.xml");
        mFile2 = new File(this.getFilesDir().getPath() + "/daylist.xml");
        mCourseList = (ListView) findViewById(list);
        mAddCourse = (Button) findViewById(R.id.addButton);
        mArrayList.clear();
        filepath = mFile.getPath();
        filepath2 = mFile2.getPath();

        if (mFile.exists()) {
            try {
                FileInputStream is = new FileInputStream(mFile);
                readFile(is);
            } catch (IOException ioe) {
            }
        } else {
            try {
                mFile.createNewFile();
            } catch (IOException ioe) {
            }
        }


        mAdapter = new CourseListAdapter(this, R.layout.activity_course_item, mArrayList);
        mCourseList.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        storeFile(mFile.getName());
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Intent intent = new Intent(this, EditCourse.class);
        intent.putExtra("Position", position);
        startActivity(intent);

    }

    public void makeNewCourse(View v) {

        if (mArrayList.size() < 9) {
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

    public static boolean readFile(FileInputStream xml) {
        final String PARENT_NODE_TEXT = "course";
        final String NAME_NODE = "name";
        final String TIME_NODE_DAY = "timeDay";
        final String TIME_NODE_WEEK = "timeWeek";
        final String TIME_NODE_MONTH = "timeMonth";
        final String TIME_NODE_YEAR = "timeYear";

        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(xml);
            Element doc = dom.getDocumentElement();
            NodeList nl = doc.getChildNodes();

            if (nl != null) {
                int length = nl.getLength();
                for (int x = 0 ; x < length ; x ++) {

                    if (nl.item(x).getNodeType() == Node.ELEMENT_NODE) {
                        Element el =  (Element) nl.item(x);

                        if (el.getNodeName().contains(PARENT_NODE_TEXT)) {
                            String courseName = el.getElementsByTagName(NAME_NODE).item(0).getTextContent();
                            Course course = new Course(courseName);
                            int time = Integer.parseInt(el.getElementsByTagName(TIME_NODE_DAY).item(0).getTextContent());
                            course.addTimeDay(time);
                            time = Integer.parseInt(el.getElementsByTagName(TIME_NODE_WEEK).item(0).getTextContent());
                            course.addTimeWeek(time);
                            time = Integer.parseInt(el.getElementsByTagName(TIME_NODE_MONTH).item(0).getTextContent());
                            course.addTimeMonth(time);
                            time = Integer.parseInt(el.getElementsByTagName(TIME_NODE_YEAR).item(0).getTextContent());
                            course.addTimeYear(time);
                            mArrayList.add(course);
                        }
                    }
                }
            }

            return true;
        } catch (ParserConfigurationException pce) {
        } catch (SAXException se) {
        } catch (IOException ioe) {
        }

        return false;
    }

    public static void storeFile(String xml) {
        Document dom;
        Element e;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            Element rootEle;
            Element secondaryEle;
            String name;
            long timeDay;
            long timeWeek;
            long timeMonth;
            long timeYear;

            rootEle = dom.createElement("CourseList");
            for (int x = 0; x < mArrayList.size() ; x++) {
                name = mArrayList.get(x).getCourseName();
                timeDay = mArrayList.get(x).getTimeDay();
                timeWeek = mArrayList.get(x).getTimeWeek();
                timeMonth = mArrayList.get(x).getTimeMonth();
                timeYear = mArrayList.get(x).getTimeYear();
                secondaryEle = dom.createElement("course" + x);

                e = dom.createElement("name");
                e.appendChild(dom.createTextNode(name));
                secondaryEle.appendChild(e);

                e = dom.createElement("timeDay");
                e.appendChild(dom.createTextNode(String.valueOf(timeDay)));
                secondaryEle.appendChild(e);

                e = dom.createElement("timeWeek");
                e.appendChild(dom.createTextNode(String.valueOf(timeWeek)));
                secondaryEle.appendChild(e);

                e = dom.createElement("timeMonth");
                e.appendChild(dom.createTextNode(String.valueOf(timeMonth)));
                secondaryEle.appendChild(e);

                e = dom.createElement("timeYear");
                e.appendChild(dom.createTextNode(String.valueOf(timeYear)));
                secondaryEle.appendChild(e);

                rootEle.appendChild(secondaryEle);
            }

            dom.appendChild(rootEle);

            try {

                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filepath)));

            } catch (TransformerException te) {
            } catch (IOException ioe) {
            }

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        }
    }


}
