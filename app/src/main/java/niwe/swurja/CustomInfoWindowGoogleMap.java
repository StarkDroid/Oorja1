package niwe.swurja;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    String[] data = new String[11];
    String latitude;
    String longitude;
    String state;
    String district;
    String taluk;
    String ghi;
    String dhi;
    String dni;
    String cuf;
    String gti;
    String aep;
    Boolean withinindia = true;

    private Context context;
    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    public void setData(String[] s) {
        if(s == null){
            withinindia = false;
        }else {
            withinindia = true;
            data = s;
            latitude = data[0];
            longitude = data[1];
            state = data[2];
            district = data[3];
            taluk = data[4];
            ghi = data[5];
            dhi = data[6];
            dni = data[7];
            cuf = data[8];
            gti = data[9];
            aep = data[10];
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        if(withinindia == false){
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.outofindia, null);
            return view;
        }

        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.infowindow, null);
        TextView latitude_textview = view.findViewById(R.id.textView2);
        TextView longitude_textview = view.findViewById(R.id.textView4);
        TextView state_textview = view.findViewById(R.id.textView6);
        TextView district_textview = view.findViewById(R.id.textView8);
        TextView taluk_textview = view.findViewById(R.id.textView10);
        TextView ghi_textview = view.findViewById(R.id.textView14);
        TextView dhi_textview = view.findViewById(R.id.textView16);
        TextView dni_textview = view.findViewById(R.id.textView18);
        TextView cuf_textview = view.findViewById(R.id.textView21);
        TextView gti_textview = view.findViewById(R.id.textView31);
        TextView aep_textview = view.findViewById(R.id.textView33);
        latitude_textview.setText(latitude);
        longitude_textview.setText(longitude);
        state_textview.setText(state);
        district_textview.setText(district);
        taluk_textview.setText(taluk);
        ghi_textview.setText(ghi);
        dhi_textview.setText(dhi);
        dni_textview.setText(dni);
        cuf_textview.setText(cuf);
        gti_textview.setText(gti);
        aep_textview.setText(aep);
        return view;

    }

}