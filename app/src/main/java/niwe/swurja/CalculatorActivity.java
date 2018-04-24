package niwe.swurja;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CalculatorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText latitude_edittext;
    EditText longitude_edittext;
    EditText area_edittext;
    Button fetch;
    Button calculation;
    TextView ghi_textview;

    String lat_string = "0.0";
    String longt_string;
    double latitude;
    double longitude;
    String s;
    String t;
    String str;
    String item;

    String ghi;
    String area;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);

        fetch = (Button) findViewById(R.id.button1);
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

        calculation = (Button) findViewById(R.id.button3);
        calculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ghi_textview = (TextView) findViewById(R.id.textView9);
                if(ghi_textview.getText().toString().equals("0.0")) {
                    ghi = "0.0";
                }else {
                    ghi = ghi_textview.getText().toString();
                }
                area_edittext = (EditText) findViewById(R.id.editText8);
                if(area_edittext.getText().toString().equals("")){
                    area = "0.0";
                }else {
                    area = area_edittext.getText().toString();
                }
                Intent i = new Intent(getBaseContext(), CalculationResults.class);
                i.putExtra("ghi", ghi);
                i.putExtra("area", area);
                i.putExtra("latitude",lat_string);
                i.putExtra("item", item);
                startActivity(i);
            }
        });

    }

    private class fetchghi extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            latitude = Double.parseDouble(lat_string);
            longitude = Double.parseDouble(longt_string);
            double ghi_value = 0.0;

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
                    ghi_value = (double) att.get("GHI");
                }
            }catch(ParseException e){
                System.out.println("parse Exception!"+ e);
            }
            str = Double.toString(ghi_value);
            return str;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ghi_textview = (TextView) findViewById(R.id.textView9);
            ghi_textview.setText(str);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item = parent.getItemAtPosition(position).toString();
        Toast.makeText(parent.getContext(), "Panel type Selected: " + item, Toast.LENGTH_LONG).show();

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }

}