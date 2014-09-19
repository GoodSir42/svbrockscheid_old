package svbrockscheid.de.svbrockscheid;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class TestFragment extends Fragment {

    //brockscheider infos
    private static final String POSITION = "posbro";
    private static final String PUNKTE = "pktbro";
    //Vorplatzierter
    private static final String POSITION_VOR = "posvor";
    private static final String MANNSCHAFT_VOR = "mschvor";
    private static final String PUNKTE_VOR = "pktvor";
    //Nachplatzierter
    private static final String POSITION_NACH = "posnach";
    private static final String MANNSCHAFT_NACH = "mschnach";
    private static final String PUNKTE_NACH = "pktnach";
    //nächstes Spiel
    private static final String NAECHSTE_MANNSCHAFT = "mschnext";
    private static final String NAECHSTES_DATUM = "dat";
    private static final String NAECHSTER_ORT = "ort1";
    //Letztes Spiel
    private static final String LETZTE_MANNSCHAFT = "mannschaft";
    private static final String LETZTES_ERGEBNIS = "erg";
    private static final String LETZTER_ORT = "ort2";

    public TestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncTask<String, Void, Map<String, String>>() {

            @Override
            protected Map<String, String> doInBackground(String... params) {
                Map<String, String> returnValues = new HashMap<String, String>();
                for (String param : params) {
                    try {
                        BufferedReader instream = new BufferedReader(new InputStreamReader(new URL("http://svbrockscheid.de/" + param).openConnection().getInputStream()));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = instream.readLine()) != null) {
                            builder.append(line);
                        }
                        //den stringbuilder parsen
                        returnValues.putAll(ValueParser.parse(builder.toString()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return returnValues;
            }

            @Override
            protected void onPostExecute(Map<String, String> values) {
                super.onPostExecute(values);
                //werte einfügen
                View view1 = getView();
                if (view1 != null) {
                    if (values.containsKey(POSITION)) {
                        TextView sv = (TextView) view1.findViewById(R.id.sv);
                        sv.setText(getString(R.string.position, values.get(POSITION), getString(R.string.sv_brockscheid)));
                    }
                    if (values.containsKey(PUNKTE)) {
                        TextView punkteBrockscheid = (TextView) view1.findViewById(R.id.punkteBrockscheid);
                        punkteBrockscheid.setText(values.get(PUNKTE));
                    }
                    if (values.containsKey(POSITION_VOR) && values.containsKey(MANNSCHAFT_VOR)) {
                        TextView posVor = (TextView) view1.findViewById(R.id.posVor);
                        posVor.setText(getString(R.string.position, values.get(POSITION_VOR), values.get(MANNSCHAFT_VOR)));
                    }
                    if (values.containsKey(POSITION_NACH) && values.containsKey(MANNSCHAFT_NACH)) {
                        TextView posNach = (TextView) view1.findViewById(R.id.posNach);
                        posNach.setText(getString(R.string.position, values.get(POSITION_NACH), values.get(MANNSCHAFT_NACH)));
                    }
                    if (values.containsKey(NAECHSTE_MANNSCHAFT)) {
                        TextView naechsteMannschaft = (TextView) view1.findViewById(R.id.naechsteMannschaft);
                        naechsteMannschaft.setText(values.get(NAECHSTE_MANNSCHAFT));
                    }
                    if (values.containsKey(NAECHSTER_ORT) && values.containsKey(NAECHSTES_DATUM)) {
                        TextView naechsteInfos = (TextView) view1.findViewById(R.id.naechsteInfos);
                        naechsteInfos.setText(values.get(NAECHSTES_DATUM) + "\n" + values.get(NAECHSTER_ORT));
                    }
                    if (values.containsKey(LETZTE_MANNSCHAFT)) {
                        TextView letzteMannschaft = (TextView) view1.findViewById(R.id.letzteMannschaft);
                        letzteMannschaft.setText(values.get(LETZTE_MANNSCHAFT));
                    }
                    if (values.containsKey(LETZTER_ORT) && values.containsKey(LETZTES_ERGEBNIS)) {
                        TextView letzteInfos = (TextView) view1.findViewById(R.id.letzteInfos);
                        letzteInfos.setText(values.get(LETZTES_ERGEBNIS) + "\n" + values.get(LETZTER_ORT));
                    }
                }
            }
        }.execute("app.php");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test, container, false);
    }
}
