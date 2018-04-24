package niwe.swurja;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;

public class CalculationResults extends AppCompatActivity {

    private static final DecimalFormat df2 = new DecimalFormat(".##");

    String ghi_string;
    String area_string;
    String latitude_string;
    String item_selected;

    double ghi;
    double area;
    double latitude;
    int panel_potential = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation_results);
        Bundle input_data = getIntent().getExtras();
        if(input_data == null){
            return;
        }else{
            ghi_string = input_data.getString("ghi");
            area_string = input_data.getString("area");
            latitude_string = input_data.getString("latitude");
            item_selected = input_data.getString("item");
        }
        ghi = Double.parseDouble(ghi_string);
        area = Double.parseDouble(area_string);
        latitude = Double.parseDouble(latitude_string);
        if(item_selected.equals("Mono-crystalline")){
            panel_potential = 10;
        }
        if(item_selected.equals("Poly-crystalline")){
            panel_potential = 12;
        }
        calculate();
    }

    public void calculate(){
        double power_plant_capacity = area/panel_potential;
        TextView power_plant_capacity_textview = (TextView) findViewById(R.id.textView5);
        power_plant_capacity_textview.setText(df2.format(power_plant_capacity));
        double panels_required_double = power_plant_capacity/0.250;
        double panels_required = (int) panels_required_double;
        double panel_area = panels_required*1.64;
        double aep = panel_area*0.184*ghi*0.77;
        TextView aep_textview = (TextView) findViewById(R.id.textView12);
        aep_textview.setText(df2.format(aep));
        double d = 0.005;
        double b;
        int i;
        double e = aep;
        double t = aep;
        for(i=1;i<25;i++){
            b = d*e;
            e = e-b;
            t = t+e;
            d = 0.005;
        }
        double total = t;
        TextView total_textview = (TextView) findViewById(R.id.textView14);
        total_textview.setText(df2.format(total));
        double money = aep*8;
        TextView money_textview = (TextView) findViewById(R.id.textView16);
        money_textview.setText(df2.format(money));
        double cuf = (aep*100)/(power_plant_capacity*8760);
        TextView cuf_textview = (TextView) findViewById(R.id.textView18);
        if(Double.isNaN(cuf) ){
            cuf_textview.setText(".0");
        }else {
            cuf_textview.setText(df2.format(cuf));
        }
        double position = latitude;
        TextView position_textview = (TextView) findViewById(R.id.textView27);
        position_textview.setText(df2.format(position));
        double P50 = aep;
        TextView P50_textview = (TextView) findViewById(R.id.textView21);
        P50_textview.setText(df2.format(P50));
        double std = aep*0.025;
        double P70 = compute(0.3, aep, std);
        TextView P70_textview = (TextView) findViewById(R.id.textView23);
        P70_textview.setText(df2.format(P70));
        double P90 = compute(0.1, aep, std);
        TextView P90_textview = (TextView) findViewById(R.id.textView25);
        P90_textview.setText(df2.format(P90));
        double carbon_level = aep*1;
        TextView carbon_textview = (TextView) findViewById(R.id.textView29);
        carbon_textview.setText(df2.format(carbon_level));
    }


    public static double compute(double p, double mu, double sigma) {
        if(p < 0 || p > 1)
            throw new RuntimeException("The probability p must be bigger than 0 and smaller than 1");
        if(sigma < 0)
            throw new RuntimeException("The standard deviation sigma must be positive");
        if(p == 0)
            return Double.NEGATIVE_INFINITY;
        if(p == 1)
            return Double.POSITIVE_INFINITY;
        if(sigma == 0)
            return mu;
        double  q, r, val;

        q = p - 0.5;

        if(Math.abs(q) <= .425) {
            r = .180625 - q * q;
            val =
                    q * (((((((r * 2509.0809287301226727 +
                            33430.575583588128105) * r + 67265.770927008700853) * r +
                            45921.953931549871457) * r + 13731.693765509461125) * r +
                            1971.5909503065514427) * r + 133.14166789178437745) * r +
                            3.387132872796366608)
                            / (((((((r * 5226.495278852854561 +
                            28729.085735721942674) * r + 39307.89580009271061) * r +
                            21213.794301586595867) * r + 5394.1960214247511077) * r +
                            687.1870074920579083) * r + 42.313330701600911252) * r + 1);
        }

        else {

            if (q > 0) {
                r = 1 - p;
            } else {
                r = p;
            }

            r = Math.sqrt(-Math.log(r));


            if (r <= 5) {
                r += -1.6;
                val = (((((((r * 7.7454501427834140764e-4 +
                        .0227238449892691845833) * r + .24178072517745061177) *
                        r + 1.27045825245236838258) * r +
                        3.64784832476320460504) * r + 5.7694972214606914055) *
                        r + 4.6303378461565452959) * r +
                        1.42343711074968357734)
                        / (((((((r *
                        1.05075007164441684324e-9 + 5.475938084995344946e-4) *
                        r + .0151986665636164571966) * r +
                        .14810397642748007459) * r + .68976733498510000455) *
                        r + 1.6763848301838038494) * r +
                        2.05319162663775882187) * r + 1);
            } else {
                r += -5;
                val = (((((((r * 2.01033439929228813265e-7 +
                        2.71155556874348757815e-5) * r +
                        .0012426609473880784386) * r + .026532189526576123093) *
                        r + .29656057182850489123) * r +
                        1.7848265399172913358) * r + 5.4637849111641143699) *
                        r + 6.6579046435011037772)
                        / (((((((r *
                        2.04426310338993978564e-15 + 1.4215117583164458887e-7) *
                        r + 1.8463183175100546818e-5) * r +
                        7.868691311456132591e-4) * r + .0148753612908506148525)
                        * r + .13692988092273580531) * r +
                        .59983220655588793769) * r + 1);
            }

            if (q < 0.0) {
                val = -val;
            }
        }

        return mu + sigma * val;
    }

}
