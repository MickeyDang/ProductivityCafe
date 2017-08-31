package mdstudios.productivitycafe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NonZeroDays.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NonZeroDays#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NonZeroDays extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    private static Integer mZeroDayCount = 0;
    TextView mSpinnerLabel;
    TextView mZeroDays;
    TextView mNonZeroDays;
    Spinner mSpinner;
    ProgressBar mProgressBar;
    static ArrayAdapter<String> mAdapter;

    private OnFragmentInteractionListener mListener;

    public NonZeroDays() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NonZeroDays.
     */
    // TODO: Rename and change types and number of parameters
    public static NonZeroDays newInstance(String param1, String param2) {
        NonZeroDays fragment = new NonZeroDays();
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

        mSpinnerLabel = (TextView) getActivity().findViewById(R.id.spinnerLabel);
        mZeroDays = (TextView) getActivity().findViewById(R.id.zeroText);
        mNonZeroDays = (TextView) getActivity().findViewById(R.id.nonZeroText);
        mProgressBar = (ProgressBar) getActivity().findViewById(R.id.percentageBar);

        mSpinner = (Spinner) getActivity().findViewById(R.id.spinner);
        String[] timeSpans = {"Week", "Month", "Quarter"};
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, timeSpans);
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        mSpinner.setAdapter(mAdapter);
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                configureView();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_non_zero_days, container, false);
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

    public boolean readFile2(FileInputStream xml) {
        final String PARENT_NODE_TEXT = "course";
        final String NAME_NODE = "name";
        final String TIME_NODE_DAY = "timeDay";
        final String TIME_NODE_WEEK = "timeWeek";
        final String TIME_NODE_MONTH = "timeMonth";
        final String TIME_NODE_YEAR = "timeYear";
        final int mCriteria = 60*60;
        Document dom;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();
//            Log.d("Cafe", "Document being read");
            dom = db.parse(xml);
            Element doc = dom.getDocumentElement();
            NodeList nl = doc.getChildNodes();

            if (nl != null) {
                int length = nl.getLength();
                int stop = getNumberOfDays();

                for (int x = length ; x > (length - stop) ; x -=1) {

                    if (nl.item(x).getNodeType() == Node.ELEMENT_NODE) {
                        Element el =  (Element) nl.item(x);
                        int time = Integer.valueOf(el.getTextContent());
                        if (time < mCriteria) {
                            mZeroDayCount ++;
                        }

                    }
                }
            }

            return true;
        } catch (ParserConfigurationException pce) {
            Log.d("Cafe", pce.getMessage());
        } catch (SAXException se) {
            Log.d("Cafe",se.getMessage());
        } catch (IOException ioe) {
            Log.d("Cafe",ioe.getMessage());
        }

        return false;
    }

    private int getNumberOfDays () {
        if (mSpinner.getSelectedItemPosition() == 0) {
            return 7;
        } else if (mSpinner.getSelectedItemPosition() == 1) {
            return 30;
        } else if (mSpinner.getSelectedItemPosition() == 2) {
           return 120;
        } else {
            return 7;
        }
    }

    private float calculatePercentageZeroDays (int zeroDays) {
        return (float) zeroDays/ (float) getNumberOfDays();
    }

    private void configureView() {
        //3 is a holder till read file thing works
        float percentage = calculatePercentageZeroDays(3) * 100;
        String text = percentage + "%";
        mZeroDays.setText(text);
        text = (100-percentage) + "%";
        mNonZeroDays.setText(text);
        mProgressBar.setMax(100);
        mProgressBar.incrementProgressBy(Math.round(100-percentage));
    }
}
