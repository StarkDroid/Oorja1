package niwe.swurja;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CalculationsInput extends AppCompatActivity implements OnItemSelectedListener {

    private static final DecimalFormat df2 = new DecimalFormat(".##");

    EditText latitude_edittext;
    EditText longitude_edittext;
    EditText area_edittext;
    Button fetch;
    Button calculation;
    TextView gti_textview;

    String lat_string = "0.0";
    String longt_string = "0.0";
    double latitude;
    double longitude;
    String s;
    String t;
    String item;
    String life_time = "0";
    String locality = "";
    String[] data = new String[2];

    String gti = "0.0";
    String area;
    String address = "";

    String area_string;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculations_input);

        Bundle input_data = getIntent().getExtras();
        if(input_data == null){
            return;
        }else{
            area_string = input_data.getString("area");
            area_edittext = (EditText) findViewById(R.id.editText8);
            double area = Double.parseDouble(area_string);
            area_edittext.setText(df2.format(area));
            lat_string = input_data.getString("latitude");
            latitude_edittext = (EditText) findViewById(R.id.editText1);
            latitude_edittext.setText(lat_string);
            longt_string = input_data.getString("longitude");
            longitude_edittext = (EditText) findViewById(R.id.editText2);
            longitude_edittext.setText(longt_string);
            new fetchghi().execute();
        }

        fetch = (Button) findViewById(R.id.button);
        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latitude_edittext = (EditText) findViewById(R.id.editText1);
                if(latitude_edittext.getText().toString().equals("")){
                    lat_string = "0.0";
                }else {
                    lat_string = latitude_edittext.getText().toString();
                }
                longitude_edittext = (EditText) findViewById(R.id.editText2);
                if(longitude_edittext.getText().toString().equals("")) {
                    longt_string = "0.0";
                }else{
                    longt_string = longitude_edittext.getText().toString();
                }
                new fetchghi().execute();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
        spinner.setOnItemSelectedListener(this);
        List<String> panels = new ArrayList<String>();
        panels.add("Mono-crystalline");
        panels.add("Poly-crystalline");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, panels);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        Spinner spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setOnItemSelectedListener(this);
        List<String> lifetime = new ArrayList<String>();
        lifetime.add("1");
        lifetime.add("2");
        lifetime.add("3");
        lifetime.add("4");
        lifetime.add("5");
        lifetime.add("6");
        lifetime.add("7");
        lifetime.add("8");
        lifetime.add("9");
        lifetime.add("10");
        lifetime.add("11");
        lifetime.add("12");
        lifetime.add("13");
        lifetime.add("14");
        lifetime.add("15");
        lifetime.add("16");
        lifetime.add("17");
        lifetime.add("18");
        lifetime.add("19");
        lifetime.add("20");
        lifetime.add("21");
        lifetime.add("22");
        lifetime.add("23");
        lifetime.add("24");
        lifetime.add("25");
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lifetime);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter1);

        calculation = (Button) findViewById(R.id.button3);
        calculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gti_textview = (TextView) findViewById(R.id.textView9);
                if(gti_textview.getText().toString().equals("0.0")) {
                    gti = "0.0";
                }else {
                    gti = gti_textview.getText().toString();
                }
                area_edittext = (EditText) findViewById(R.id.editText8);
                if(area_edittext.getText().toString().equals("")){
                    area = "0.0";
                }else {
                    area = area_edittext.getText().toString();
                }
                Intent i = new Intent(getBaseContext(), CalculationResults.class);
                i.putExtra("gti", gti);
                i.putExtra("area", area);
                i.putExtra("latitude", lat_string);
                i.putExtra("longitude", longt_string);
                i.putExtra("locality",locality);
                i.putExtra("item", item);
                i.putExtra("life_time", life_time);
                startActivity(i);
            }
        });

    }

    private class fetchghi extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            latitude = Double.parseDouble(lat_string);
            longitude = Double.parseDouble(longt_string);
            double gti_value = 0.0;
            Boolean findlocality = true;

            URL u;
            InputStream is = null;
            DataInputStream dis;
            s = "";
            t = "";
            try{
                u= new URL("http://14.139.172.6:6080/arcgis/rest/services/Solar_Wind_hybrid/MapServer/1/query?where=1%3D1&text=&objectIds=&time=&geometry="+longitude+"%2C+"+latitude+"&geometryType=esriGeometryPoint&inSR=&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson");
                is = u.openStream();
                dis = new DataInputStream(new BufferedInputStream(is));
                while((s = dis.readLine()) != null){
                    t = t + s;
                }
                is.close();
            }catch(IOException ioe){
                System.out.println("IO Exception!");
            }

            try {
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(t);
                JSONArray ja = (JSONArray) json.get("features");
                String ja_string = ja.toString();
                String empty = "[]";
                if(ja_string.equals(empty)){
                    System.out.println("null");
                    findlocality = false;
                }else{
                    JSONObject ele1 = (JSONObject) ja.get(0);
                    JSONObject att = (JSONObject) ele1.get("attributes");
                    gti_value = (double) att.get("GTI");
                }
            }catch(ParseException e){
                System.out.println("parse Exception!"+ e);
            }

            if(findlocality == true) {
                s = "";
                t = "";

                try {
                    u = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&key=AIzaSyC4b5Mvg2dAlDOx8oin6P_3ucsQw0QAWOo");
                    is = u.openStream();
                    dis = new DataInputStream(new BufferedInputStream(is));
                    while ((s = dis.readLine()) != null) {
                        t = t + s;
                    }
                    is.close();
                } catch (IOException ioe) {
                    System.out.println("IO Exception!");
                }

                try {
                    JSONParser parser = new JSONParser();
                    JSONObject json = (JSONObject) parser.parse(t);
                    JSONArray ja = (JSONArray) json.get("results");
                    String ja_string = ja.toString();
                    String empty = "[]";
                    if(ja_string.equals(empty)){
                        System.out.println("null");
                    }else {
                        JSONObject ele1 = (JSONObject) ja.get(1);
                        address = (String) ele1.get("formatted_address");
                    }
                } catch (ParseException e) {
                    System.out.println("parse Exception!" + e);
                }
            }
            data[0] = Double.toString(gti_value);
            data[1] = address;
            return data;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            gti_textview = (TextView) findViewById(R.id.textView9);
            gti_textview.setText(data[0]);
            locality = data[1];
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner)parent;
        Spinner spinner1 = (Spinner)parent;
        if(spinner.getId() == R.id.spinner2) {
            item = parent.getItemAtPosition(position).toString();
            Toast.makeText(parent.getContext(), "Panel type Selected: " + item, Toast.LENGTH_LONG).show();
        }
        if(spinner1.getId() == R.id.spinner){
            life_time = parent.getItemAtPosition(position).toString();
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }

}