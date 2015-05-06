package cst.roadrunner;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class mapActivity extends FragmentActivity implements LocationListener, OnMapClickListener, OnMapLongClickListener {

    LatLng startLocation;
    LatLng endLocation;
    private GoogleMap mMap;
    private android.location.LocationListener locationListener;
    String markerTitle = "";
    boolean markerStartExists, markerEndExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {

        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(false);
                mMap.getUiSettings().setTiltGesturesEnabled(true);
                //mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.setMyLocationEnabled(true);
                mMap.setOnMapClickListener(this);
                mMap.setOnMapLongClickListener(this);
            }
        }
    }

    private void handleNewLocation(Location location) {
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
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

    public void chooseStartPosition(View view) {
        markerTitle = "Jog Start";

    }

    public void chooseEndPosition(View view) {
        markerTitle = "Jog End";

    }

    public void onMapClick(LatLng latLng) {
        Log.d("mapActivity", latLng.toString());
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    public void onMapLongClick(LatLng latLng) {
        if (markerTitle == "Jog Start") {
            Marker start = mMap.addMarker(new MarkerOptions().position(latLng).title("Jog Start").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            Log.d("mapActivity", "Start marker added" + latLng.toString());
            startLocation = start.getPosition();
            markerStartExists = true;
        } else if (markerTitle == "Jog End") {
            Marker end = mMap.addMarker(new MarkerOptions().position(latLng).title("Jog End").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            Log.d("mapActivity", "End marker added" + latLng.toString());
            endLocation = end.getPosition();
            markerEndExists = true;
        }
    }

    public void addLines(View view) {
        if ((markerStartExists = true) && (markerEndExists = true)) {
            mMap.addPolyline((new PolylineOptions()).add(startLocation, endLocation).width(10).color(Color.rgb(102, 204, 255)).geodesic(true));
        }
    }
}
