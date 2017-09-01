package mdstudios.productivitycafe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static mdstudios.productivitycafe.CoursesList.filepath2;

/**
 * Created by mickeydang on 2017-08-31.
 */

public class StorageService extends BroadcastReceiver {
    final String EXTRA_TAG = "Time";
    @Override
    public void onReceive(Context context, Intent intent) {
        long time = intent.getLongExtra(EXTRA_TAG, 0);
        storeFile2(time);
    }

    public static void storeFile2(long time) {
        Document dom;
        Element e;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            try {
                dom = db.parse(filepath2);
            } catch (IOException ioe) {
                dom = db.newDocument();
            } catch (SAXException saxe) {
                dom = db.newDocument();
            }

            NodeList nl = dom.getElementsByTagName("TimeList");
            Node root;
            Node middle;
            Date day = new Date();

            if (nl.getLength() == 0) {
                root = dom.createElement("TimeList");
                dom.appendChild(root);
            } else {
                root = nl.item(0);
            }
            //format is dow mon dd
            String dayTag = day.toString().substring(4,10);
            dayTag = dayTag.replace(" ", "");
            //format is mondd
            e = dom.createElement("Time");
            e.appendChild(dom.createTextNode(String.valueOf(time)));
            middle = dom.createElement(dayTag);
            middle.appendChild(e);
            root.appendChild(middle);

            try {

                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

                tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(filepath2)));

//                Log.d("Cafe", "File stored successfully");
            } catch (TransformerException te) {
                Log.d("Cafe", te.getMessage());
            } catch (IOException ioe) {
                Log.d("Cafe", (ioe.getMessage()));
            }

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        }
    }


}
