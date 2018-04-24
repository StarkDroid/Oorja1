package niwe.swurja;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

public class MainActivity extends AppCompatActivity  {

    CircleMenu circleMenu;

    private static final String TAG = "MainActivity";

    private static final int ERROR_DIALOG_REQUEST = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CircleMenu circleMenu = (CircleMenu)findViewById(R.id.circle_menu);

        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"),R.drawable.ic_add, R.drawable.ic_remove)
                                    .addSubMenu(Color.parseColor("#ffffff"), R.drawable.wind)
                                    .addSubMenu(Color.parseColor("#ffffff"), R.drawable.ic_sun)
                                    .addSubMenu(Color.parseColor("#ffffff"), R.drawable.hybrid)
                                    .addSubMenu(Color.parseColor("#ffffff"), R.drawable.calculator)
                                    .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                                        @Override
                                        public void onMenuSelected(int index) {
                                            switch (index) {
                                                case 0:
                                                    Toast.makeText(MainActivity.this, "Wind map clicked", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this, WindMapActivity.class));
                                                    break;
                                                case 1:
                                                    Toast.makeText(MainActivity.this, "Solar map clicked", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this, SolarMapActivity.class));
                                                    break;
                                                case 2:
                                                    Toast.makeText(MainActivity.this, "Hybrid map clicked", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this, HybridMapActivity.class));
                                                    break;
                                                case 3:
                                                    Toast.makeText(MainActivity.this, "Calculator button clicked", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(MainActivity.this, CalculatorActivity.class));
                                                    break;
                                            }
                                        }
                                    });

    }

    @Override
    public void onBackPressed() {
        if (circleMenu.isOpened())
            circleMenu.closeMenu();
        else
            finish();
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}






















