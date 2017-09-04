package mdstudios.productivitycafe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mdstudios.productivitycafe.dummy.DummyContent;

import static mdstudios.productivitycafe.CoursesList.mFile2;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DayByDay.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DayByDay#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DayByDay extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    final String ZERO_TIME = "0";

    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    TextView ratingView;
    TextView timeView;
    TextView colourDisplay;
    CalendarView mCalendarView;
    FileInputStream is;

    public DayByDay() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DayByDay.
     */
    // TODO: Rename and change types and number of parameters
    public static DayByDay newInstance(String param1, String param2) {
        DayByDay fragment = new DayByDay();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_day_by_day, container, false);

        if (mFile2.exists()) {
            try {
                is = new FileInputStream(mFile2);
            } catch (IOException ioe) {
            }
        } else {
            try {
                mFile2.createNewFile();
                is = new FileInputStream(mFile2);
            } catch (IOException ioe) {
            }
        }

        ratingView = (TextView) rootView.findViewById(R.id.ratingView);
        timeView = (TextView) rootView.findViewById(R.id.totalTimeView);
        colourDisplay = (TextView) rootView.findViewById(R.id.colourDisplay);
        mCalendarView = (CalendarView) rootView.findViewById(R.id.myCalendar);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String s = convertDateFormats(month, dayOfMonth);
                try {
                    is = new FileInputStream(mFile2);
                } catch (IOException ioe) {
                }
                int x = parseFile(s, is);
                try {
                    is.close();
                } catch (IOException ioe) {
                }
                configureView(x);
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            is.close();
        } catch (IOException ioe) {
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void configureView (int hours) {
        final String POOR = "ZERO DAY";
        final String SATISFACTORY = "Meh";
        final String GOOD = "Pretty good";
        final String EXCELLENT = "Excellent!";
        final String HOURS = " Hours";

        if (hours > 5) {
            colourDisplay.setBackgroundColor(getResources().getColor(R.color.customBlue));
            ratingView.setText(EXCELLENT);
        } else if (hours > 2) {
            colourDisplay.setBackgroundColor(getResources().getColor(R.color.customGreen));
            ratingView.setText(GOOD);
        } else if (hours > 0) {
            colourDisplay.setBackgroundColor(getResources().getColor(R.color.customRed));
            ratingView.setText(SATISFACTORY);
        } else {
            colourDisplay.setBackgroundColor(getResources().getColor(R.color.customWhite));
            ratingView.setText(POOR);
        }

        String s = hours + HOURS;
        timeView.setText(s);
    }

    private String convertDateFormats (int Month, int DayofMonth) {
        String newDateFormat = null;

        switch (Month) {
            case 0: newDateFormat = "Jan";
                break;
            case 1: newDateFormat = "Feb";
                break;
            case 2: newDateFormat = "Mar";
                break;
            case 3: newDateFormat = "Apr";
                break;
            case 4: newDateFormat = "May";
                break;
            case 5: newDateFormat = "Jun";
                break;
            case 6: newDateFormat = "Jul";
                break;
            case 7: newDateFormat = "Aug";
                break;
            case 8: newDateFormat = "Sep";
                break;
            case 9: newDateFormat = "Oct";
                break;
            case 10: newDateFormat = "Nov";
                break;
            case 11: newDateFormat = "Dec";

        }

        newDateFormat = newDateFormat + configureTime(DayofMonth);
        return newDateFormat;
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

    public int parseFile(String inputString, FileInputStream xml) {
        int hours = 0;
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(xml);
            Element doc = dom.getDocumentElement();
            NodeList nl = doc.getChildNodes();

            for (int x = nl.getLength() -1 ; x > 0; x-=1) {

                String name = nl.item(x).getNodeName();
                if (inputString.equalsIgnoreCase(name)) {
                    Element el =  (Element) nl.item(x);
                    hours = Integer.parseInt(el.getElementsByTagName("Time").item(0).getTextContent());
                    break;
                }

            }

        } catch (ParserConfigurationException pce) {
        } catch (SAXException se) {
        } catch (IOException ioe) {
        }

        return hours;
    }
}
