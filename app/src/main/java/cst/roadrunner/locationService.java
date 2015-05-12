package cst.roadrunner;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class locationService extends Service {
    double lat1;
    double lon1;
    double lat2;
    double lon2;
    double currentLatitude;
    double currentLongitude;
    public android.location.LocationListener locationListener;
    boolean locationReached;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent locationListener, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        lat2 = locationListener.getExtras().getDouble("lat2");
        lon2 = locationListener.getExtras().getDouble("lon2");
        while (locationReached = false) {
            Log.d("getCurrentLatitude", "lat1");
            lat1 = currentLatitude;
            Log.d("getCurrentLongitude", "lon1");
            lon1 = currentLongitude;
            if (mapActivity.calcDistance(lat1, lon1, lat2, lon2) < 0.004) {
                locationReached = true;
                Toast.makeText(this, "Run Finished!", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(locationService.this, cameraActivity.class);
                startActivity(cameraIntent);
                stopSelf();
                break;
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void handleNewLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (locationManager != null) {
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (gpsIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            } else if (networkIsEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            }
        }
    }

    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


}

