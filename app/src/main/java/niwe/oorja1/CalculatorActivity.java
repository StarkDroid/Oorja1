package niwe.oorja1;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CalculatorActivity extends AppCompatActivity{

    String s ;
    String t;

    double latitude;
    double longitude;
    double lat;
    double lang;
    String state;
    String district;
    String taluk;
    double ghi;
    double dni;
    double dhi;
    double cuf_s;
    String cuf_w;
    String wpd;
    String windspeed;

    String lat_string;
    String longt_string;
    String[] data = new String[12];
    Button fetch;

    EditText elatitude;
    EditText elongitude;
    EditText elat;
    EditText elang;
    EditText estate;
    EditText edistrict;
    EditText etaluk;
    EditText eghi;
    EditText edni;
    EditText edhi;
    EditText ecuf_s;
    EditText ecuf_w;
    EditText ewpd;
    EditText ewindspeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);

        fetch = (Button) findViewById(R.id.fetch);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                elatitude = (EditText)findViewById(R.id.editText2);
                lat_string = elatitude.getText().toString();
                elongitude = (EditText) findViewById(R.id.editText3);
                longt_string = elongitude.getText().toString();
                new  fetchData().execute();
            }
        });

    }

    private class fetchData extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {

            latitude = Double.parseDouble(lat_string);
            longitude = Double.parseDouble(longt_string);

            URL u;
            InputStream is = null;
            DataInputStream dis;
            s = "";
            t = "";
            try{
                u= new URL("http://14.139.172.6:6080/arcgis/rest/services/Solar_Radiation_Map_of_India/MapServer/5/query?where=&text=&objectIds=&time=&geometry=x%3D"+longitude+"%2Cy%3D"+latitude+"&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=false&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson");
                is = u.openStream();
                dis = new DataInputStream(new BufferedInputStream(is));
                while((s = dis.readLine()) != null){
                    t = t + s;
                }
                is.close();
            }catch(IOException ioe){
                System.out.println("IO Exception!");
                System.exit(1);
            }

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(t);
                JSONArray ja = (JSONArray) json.get("features");
                String ja_string = ja.toString();
                String empty = "[]";
                if(ja_string.equals(empty)){
                    System.out.println("null");
                }else {
                    JSONObject ele1 = (JSONObject) ja.get(0);
                    JSONObject att = (JSONObject) ele1.get("attributes");
                    lat = (double) att.get("Latitude");
                    lang = (double) att.get("Longitude");
                    state = (String) att.get("STATE");
                    district = (String) att.get("DISTRICT");
                    taluk = (String) att.get("TALUK");
                    ghi = (double) att.get("GHI");
                    dni = (double) att.get("DNI");
                    dhi = (double) att.get("DHI");
                    cuf_s = (double) att.get("CUF");
                }
            }catch(ParseException e){
                System.out.println("parse Exception!"+ e);
            }

            s = "";
            t = "";
            try{
                u= new URL("http://14.139.172.6:6080/arcgis/rest/services/100_CUF_Mast/MapServer/8/query?where=&text=&objectIds=&time=&geometry="+longitude+"%2C"+latitude+"&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson");
                is = u.openStream();
                dis = new DataInputStream(new BufferedInputStream(is));
                while((s = dis.readLine()) != null){
                    t = t + s;
                }
                is.close();
            }catch(IOException ioe){
                System.out.println("IO Exception!");
                System.exit(1);
            }

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(t);
                JSONArray ja = (JSONArray) json.get("features");
                String ja_string = ja.toString();
                String empty = "[]";
                if(ja_string.equals(empty)){
                    System.out.println("null");
                }else {
                    JSONObject ele1 = (JSONObject) ja.get(0);
                    JSONObject att = (JSONObject) ele1.get("attributes");
                    cuf_w = (String) att.get("CUF");
                    wpd = (String) att.get("WPD");
                    windspeed = (String) att.get("WindSpeed");
                }
            }catch(ParseException e){
                System.out.println("parse Exception!"+ e);
            }

            data[0] = String.valueOf(lat);
            data[1] = String.valueOf(lang);
            data[2] = state;
            data[3] = district;
            data[4] = taluk;
            data[5] = String.valueOf(ghi);
            data[6] = String.valueOf(dni);
            data[7] = String.valueOf(dhi);
            data[8] = String.valueOf(cuf_s);
            data[9] = cuf_w;
            data[10] = wpd;
            data[11] = windspeed;

            return data;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            elat = (EditText) findViewById(R.id.editText);
            elat.setText(data[0]);
            elang = (EditText) findViewById(R.id.editText4);
            elang.setText(data[1]);
            estate = (EditText) findViewById(R.id.editText5);
            estate.setText(data[2]);
            edistrict = (EditText) findViewById(R.id.editText6);
            edistrict.setText(data[3]);
            etaluk = (EditText) findViewById(R.id.editText7);
            etaluk.setText(data[4]);
            eghi = (EditText) findViewById(R.id.editText8);
            eghi.setText(data[5]);
            edni = (EditText) findViewById(R.id.editText10);
            edni.setText(data[6]);
            edhi = (EditText) findViewById(R.id.editText9);
            edhi.setText(data[7]);
            ecuf_s = (EditText) findViewById(R.id.editText11);
            ecuf_s.setText(data[8]);
            ecuf_w = (EditText) findViewById(R.id.editText12);
            ecuf_w.setText(data[9]);
            ewpd = (EditText) findViewById(R.id.editText13);
            ewpd.setText(data[10]);
            ewindspeed = (EditText) findViewById(R.id.editText14);
            ewindspeed.setText(data[11]);
        }
    }
}
