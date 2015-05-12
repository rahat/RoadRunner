package cst.roadrunner;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;


public class mapActivity extends FragmentActivity implements LocationListener, OnMapClickListener, OnMapLongClickListener {

    LatLng startLocation;
    LatLng endLocation;
    double runDistance;
    double lat1;
    double lon1;
    double lat2;
    double lon2;
    public GoogleMap mMap;
    public android.location.LocationListener locationListener;
    String markerTitle = "";
    boolean markerStartExists, markerEndExists = false;
    public List<LatLng> latLngs = new ArrayList<>();
    public List<Marker> markers = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setUpMapIfNeeded();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            runDistance = extras.getDouble("runDistance");
            Log.d("runDistance:", String.valueOf(runDistance));
        }
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

    public void handleNewLocation(Location location) {
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
            lat1 = startLocation.latitude;
            lon1 = startLocation.longitude;
            markerStartExists = true;
        } else if (markerTitle == "Jog End") {
            Marker end = mMap.addMarker(new MarkerOptions().position(latLng).title("Jog End").draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            Log.d("mapActivity", "End marker added" + latLng.toString());
            endLocation = end.getPosition();
            lat2 = endLocation.latitude;
            lon2 = endLocation.longitude;
            markerEndExists = true;
        }
    }

    public static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6372.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c * 0.62137;
    }


    public void addLines(View view) {
        double distanceCalculated = calcDistance(lat1, lon1, lat2, lon2);
        System.out.println("Distance Calculated: " + distanceCalculated);
        if ((markerStartExists = true) & (markerEndExists = true)) {
            if (distanceCalculated >= runDistance) {
                GeoApiContext context = new GeoApiContext();
                context.setApiKey("AIzaSyBbBEsSFL6AZ5YyG2Kh_2c7nDgQ4ccPJxs");
                DirectionsRoute[] routes = new DirectionsRoute[0];
                try {
                    routes = DirectionsApi.getDirections(context, startLocation.latitude + "," + startLocation.longitude, endLocation.latitude + "," + endLocation.longitude).mode(TravelMode.WALKING).await();
                    ArrayList<LatLng> coordList = new ArrayList<LatLng>();
                    for (com.google.maps.model.LatLng latLng : routes[0].overviewPolyline.decodePath()) {
                        LatLng point = new LatLng(latLng.lat, latLng.lng);
                        coordList.add(point);
                    }
                    PolylineOptions options = new PolylineOptions().addAll(coordList).width(10).color(Color.rgb(102, 204, 255)).geodesic(true);
                    mMap.addPolyline(options);
                    Intent locationListener = new Intent(this, locationService.class);
                    locationListener.putExtra("lat2", lat2);
                    locationListener.putExtra("lon2", lon2);
                    startService(locationListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new AlertDialog.Builder(this).setTitle("Error").setMessage("You need to run a larger distance in order to burn those calories. Choose new start and end points and try again.").setNeutralButton("Close", null).show();
                mMap.clear();
            }
        }
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), locationService.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), locationService.class));
    }
}

