package niwe.swurja;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class SolarMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static GoogleMap mMap;
    Boolean toggled = false;

    double latitude_d;
    double longitude_d;
    static Marker newMarker;
    String[] data = new String[11];
    final CustomInfoWindowGoogleMap custominfowindow = new CustomInfoWindowGoogleMap(this);

    static int t = 0;
    static Marker firstmarker;
    static Marker newmarker;
    static Boolean isfirstmarker = true;
    static Polygon polygon[] = new Polygon[100];
    static PolygonOptions polygonOptions;
    static PolygonOptions polygonOptions1;
    static Double area = 0.0;
    private static final DecimalFormat df2 = new DecimalFormat(".##");
    static TextView area_textview;
    static List<LatLng> closedpath = new ArrayList<>();
    static Stack st = new Stack();
    static Stack listsize = new Stack();
    String lat_string = "0.0" ;
    String lng_string = "0.0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng raipur = new LatLng(21.2514, 81.6296);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(raipur, 4));
        final FloatingActionButton back_button = (FloatingActionButton) findViewById(R.id.fab);
        final FloatingActionButton forward_button = (FloatingActionButton) findViewById(R.id.fab1);
        area_textview = (TextView) findViewById(R.id.textView11);
        area_textview.setTextColor(Color.WHITE);
        back_button.setVisibility(View.GONE);
        forward_button.setVisibility(View.GONE);
        area_textview.setVisibility(View.GONE);

        polygonOptions = new PolygonOptions().geodesic(true).strokeColor(Color.WHITE).fillColor(0x7FFFFFFF);
        polygonOptions1 = new PolygonOptions().geodesic(true).strokeColor(Color.WHITE).fillColor(0x7FFFFFFF);

        final FloatingActionButton toggle_button = (FloatingActionButton) findViewById(R.id.fab3);
        toggle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggled == false) {
                    toggle_button.setImageResource(R.drawable.map_drop_marker_symbol);
                    toggled = true;
                    back_button.setVisibility(View.GONE);
                    forward_button.setVisibility(View.GONE);
                    area_textview.setVisibility(View.GONE);
                    isfirstmarker = true;

                    mMap.clear();
                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            if (newMarker != null) {
                                newMarker.remove();
                            }
                            newMarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(false));
                            LatLng point = newMarker.getPosition();
                            latitude_d = point.latitude;
                            longitude_d = point.longitude;
                            new fetchData().execute();
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    marker.showInfoWindow();
                                    return false;
                                }
                            });
                            mMap.setOnInfoWindowLongClickListener(new GoogleMap.OnInfoWindowLongClickListener() {
                                @Override
                                public void onInfoWindowLongClick(Marker marker) {
                                    marker.hideInfoWindow();
                                }
                            });
                        }
                    });

                }else {

                    toggle_button.setImageResource(R.drawable.area_measurement_symbol);
                    toggled = false;
                    back_button.setVisibility(View.VISIBLE);
                    forward_button.setVisibility(View.VISIBLE);
                    area_textview.setVisibility(View.VISIBLE);
                    mMap.clear();
                    st.clear();
                    listsize.clear();
                    closedpath.clear();
                    for(int j=0; j < t; j++){
                        polygon[j].remove();
                    }
                    polygonOptions = new PolygonOptions().geodesic(true).strokeColor(Color.WHITE).fillColor(0x7FFFFFFF);
                    t = 0;

                    mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            mMap.setOnMarkerClickListener(null);
                            if(isfirstmarker) {
                                firstmarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(String.valueOf(t)));
                                closedpath.add(t,firstmarker.getPosition());
                                int sizeoflist = closedpath.size();
                                for(int i=0 ; i<sizeoflist; i++){
                                    showpush(st, closedpath.get(i));
                                }
                                listsizepush(listsize, sizeoflist);
                                polygon[t] = mMap.addPolygon(polygonOptions.add(firstmarker.getPosition()));
                                t = t+1;
                                Double latitude = firstmarker.getPosition().latitude;
                                lat_string = String.valueOf(latitude);
                                Double longitude = firstmarker.getPosition().longitude;
                                lng_string = String.valueOf(longitude);
                                isfirstmarker = false;
                            }else {
                                if(t <= 2) {
                                    newmarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(String.valueOf(t)));
                                    closedpath.add(t, newmarker.getPosition());
                                    int sizeoflist = closedpath.size();
                                    for(int i=0 ; i<sizeoflist; i++){
                                        showpush(st, closedpath.get(i));
                                    }
                                    listsizepush(listsize, sizeoflist);
                                    polygon[t] = mMap.addPolygon(polygonOptions
                                            .add(newmarker.getPosition()));
                                    t = t + 1;
                                    area = SphericalUtil.computeArea(closedpath);
                                    area_textview.setText("Area : "+df2.format(area));
                                    area_textview.append(Html.fromHtml("m<sup>2</sup>"));
                                }else{
                                    newmarker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(String.valueOf(t)));
                                    closedpath.add(t, newmarker.getPosition());
                                    int sizeoflist = closedpath.size();
                                    for(int i=0 ; i<sizeoflist; i++){
                                        showpush(st, closedpath.get(i));
                                    }
                                    listsizepush(listsize, sizeoflist);
                                    polygon[t] = mMap.addPolygon(polygonOptions
                                            .add(newmarker.getPosition()));
                                    polygon[t-1].remove();
                                    t = t + 1;
                                    area = SphericalUtil.computeArea(closedpath);
                                    area_textview.setText("Area : "+df2.format(area));
                                    area_textview.append(Html.fromHtml("m<sup>2</sup>"));
                                }
                            }
                        }
                    });
                    mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                        @Override
                        public void onMarkerDragStart(Marker marker) {
                            for(int j=0; j < t; j++){
                                polygon[j].remove();
                            }
                            int title = Integer.parseInt(marker.getTitle());
                            closedpath.remove(title);
                        }

                        @Override
                        public void onMarkerDrag(Marker marker) {
                        }

                        @Override
                        public void onMarkerDragEnd(Marker marker) {
                            int title = Integer.parseInt(marker.getTitle());
                            closedpath.add(title, marker.getPosition());
                            int sizeoflist = closedpath.size();
                            for(int i=0 ; i<sizeoflist; i++){
                                showpush(st, closedpath.get(i));
                            }
                            listsizepush(listsize, sizeoflist);
                            int size = closedpath.size();
                            for (int i = 0; i < size; i++) {
                                LatLng addmarker = closedpath.get(i);
                                if(i <= 2) {
                                    polygon[i] = mMap.addPolygon(polygonOptions1
                                            .add(addmarker));
                                }else{
                                    polygon[i-1].remove();
                                    polygon[i] = mMap.addPolygon(polygonOptions1
                                            .add(addmarker));
                                }
                            }
                            polygonOptions = polygonOptions1;
                            polygonOptions1 = new PolygonOptions().geodesic(true).strokeColor(Color.WHITE).fillColor(0x7FFFFFFF);
                            area = SphericalUtil.computeArea(closedpath);
                            area_textview.setText("Area : "+df2.format(area));
                            area_textview.append(Html.fromHtml("m<sup>2</sup>"));
                        }
                    });
                    forward_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getBaseContext(), CalculationsInput.class);
                            i.putExtra("area", Double.toString(area));
                            i.putExtra("latitude",lat_string);
                            i.putExtra("longitude",lng_string);
                            startActivity(i);
                        }
                    });
                    back_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(st.isEmpty() == false) {
                                try {
                                    showpop(st);
                                } catch (EmptyStackException e) {
                                    System.out.println("empty stack");
                                }
                                listsizepop(listsize);
                                closedpath.removeAll(closedpath);
                                if(listsize.isEmpty() == false) {
                                    int listprevioussize = listsizepeek(listsize);
                                    Double array[] = new Double[1000];
                                    for (int i = 0; i < listprevioussize * 2; i++) {
                                        Double t = (Double) st.pop();
                                        array[i] = t;
                                    }
                                    int i, j;
                                    i = 0;
                                    j = listprevioussize * 2 - 1;
                                    Double temp;
                                    while (i < j) {
                                        temp = array[i];
                                        array[i] = array[j];
                                        array[j] = temp;
                                        i++;
                                        j--;
                                    }
                                    for (i = 0; i < listprevioussize * 2; i++) {
                                        Double temp_latitude = array[i];
                                        tempshowpush(st, temp_latitude);
                                        i++;
                                        Double temp_longitude = array[i];
                                        tempshowpush(st, temp_longitude);
                                        LatLng pointsremaining = new LatLng(temp_latitude, temp_longitude);
                                        closedpath.add(pointsremaining);
                                    }}else{
                                    //do nothing
                                }
                                drawPolygon();
                            }else {
                                // do nothing
                            }
                        }
                    });
                }
            }
        });
    }

    private class fetchData extends AsyncTask<Double, Void, String[]>{

        @Override
        protected String[] doInBackground(Double... doubles) {
            Double x = latitude_d;
            Double y = longitude_d;

            double lat = 0.0;
            double lang = 0.0;
            String state = "tamil nadu";
            String district = "chennai";
            String taluk = "chennai";
            double ghi = 0.0;
            double dni = 0.0;
            double dhi = 0.0;
            double cuf_s = 0.0;
            double gti = 0.0;
            double aep = 0.0;

            Boolean findgti = true;

            URL u;
            InputStream is = null;
            DataInputStream dis;
            String s = "";
            String t = "";
            try{
                u= new URL("http://14.139.172.6:6080/arcgis/rest/services/Solar_Radiation_Map_of_India/MapServer/5/query?where=&text=&objectIds=&time=&geometry=x%3D"+y+"%2Cy%3D"+x+"&geometryType=esriGeometryPoint&inSR=4326&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=false&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson");
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
                    findgti = false;
                    return null;
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

            if(findgti == true) {
                s = "";
                t = "";

                try {
                    u = new URL("http://14.139.172.6:6080/arcgis/rest/services/Solar_Wind_hybrid/MapServer/1/query?where=1%3D1&text=&objectIds=&time=&geometry=" + y + "%2C+" + x + "&geometryType=esriGeometryPoint&inSR=&spatialRel=esriSpatialRelIntersects&relationParam=&outFields=*&returnGeometry=true&returnTrueCurves=false&maxAllowableOffset=&geometryPrecision=&outSR=&returnIdsOnly=false&returnCountOnly=false&orderByFields=&groupByFieldsForStatistics=&outStatistics=&returnZ=false&returnM=false&gdbVersion=&returnDistinctValues=false&resultOffset=&resultRecordCount=&f=pjson");
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
                    JSONArray ja = (JSONArray) json.get("features");
                    String ja_string = ja.toString();
                    String empty = "[]";
                    if (ja_string.equals(empty)) {
                        System.out.println("null");
                    } else {
                        JSONObject ele1 = (JSONObject) ja.get(0);
                        JSONObject att = (JSONObject) ele1.get("attributes");
                        gti = (double) att.get("GTI");
                        aep = (long) att.get("AEP_kWh");
                    }
                } catch (ParseException e) {
                    System.out.println("parse Exception!");
                }
            }

            data[0] = Double.toString(lat);
            data[1] = Double.toString(lang);
            data[2] = state;
            data[3] = district;
            data[4] = taluk;
            data[5] = Double.toString(ghi);
            data[6] = Double.toString(dni);
            data[7] = Double.toString(dhi);
            data[8] = Double.toString(cuf_s);
            data[9] = Double.toString(gti);
            data[10] = Double.toString(aep);

            return data;
        }

        @Override
        protected void onPostExecute(String[] s) {
            super.onPostExecute(s);
            if(s == null){
                custominfowindow.setData(s);
                Log.d("solar calculator","s : "+s);
            }else {
                custominfowindow.setData(data);
                Log.d("solar calculator","data : "+data[0]);
            }
            mMap.setInfoWindowAdapter(custominfowindow);
        }

    }

    static void showpush(Stack st, LatLng latLng ) {
        Double latitude_stack = latLng.latitude;
        Double longitude_stack = latLng.longitude;
        st.push(new Double(latitude_stack));
        st.push(new Double(longitude_stack));
    }

    static void tempshowpush(Stack st, Double a) {
        st.push(new Double(a));
    }

    static void showpop(Stack st) {
        int sizeoflist = closedpath.size();
        for(int i=0 ; i<sizeoflist*2; i++){
            st.pop();
        }
    }

    static void listsizepush(Stack listsize, int b){
        listsize.push(new Integer(b));
    }

    static void listsizepop(Stack listsize){
        listsize.pop();
    }

    static int listsizepeek(Stack listsize){
        int c = (Integer) listsize.peek();
        return c;
    }

    static void drawPolygon(){
        mMap.clear();
        for(int j=0; j < t; j++){
            polygon[j].remove();
        }
        int size = closedpath.size();
        t = 0;
        isfirstmarker = true;
        for (int i = 0; i < size; i++) {
            if(isfirstmarker == true){
                LatLng addmarker = closedpath.get(i);
                firstmarker = mMap.addMarker(new MarkerOptions().position(addmarker).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(String.valueOf(t)));
                t = t+1;
                isfirstmarker = false;
            }else{
                LatLng addmarker = closedpath.get(i);
                newmarker = mMap.addMarker(new MarkerOptions().position(addmarker).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)).title(String.valueOf(t)));
                t = t+1;
            }
        }
        for (int i = 0; i < size; i++) {
            LatLng addmarker = closedpath.get(i);
            if(i <= 2) {
                polygon[i] = mMap.addPolygon(polygonOptions1
                        .add(addmarker));
            }else{
                polygon[i-1].remove();
                polygon[i] = mMap.addPolygon(polygonOptions1
                        .add(addmarker));
            }
        }
        polygonOptions = polygonOptions1;
        polygonOptions1 = new PolygonOptions().geodesic(true).strokeColor(Color.WHITE).fillColor(0x7FFFFFFF);
        area = SphericalUtil.computeArea(closedpath);
        area_textview.setText("Area : "+df2.format(area));
        area_textview.append(Html.fromHtml("m<sup>2</sup>"));
    }

}