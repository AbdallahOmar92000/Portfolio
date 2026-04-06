package com.sarrawi.mymaps;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;


import androidx.fragment.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.cardview.widget.CardView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.sarrawi.mymaps.entities.Address;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.responses.DirectionsResponse;
import com.sarrawi.mymaps.responses.DistanceBetweenLocations;
import com.sarrawi.mymaps.responses.GeocodeResponse;
import com.sarrawi.mymaps.utils.LdgoApi;
import com.sarrawi.mymaps.utils.LdgoGoogleMapsApi;
import com.sarrawi.mymaps.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DirectionsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    private EditText destinationInput;
    private TextView timeItTakes, distanceTextView;
    private CardView startButton;

    private boolean isTracking = false;

    private static final int LOCATION_REQUEST_CODE = 1001;

    LdgoGoogleMapsApi googleMapsApi;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMapsApi = RetrofitClient.getRetrofitInstance2().create(LdgoGoogleMapsApi.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        destinationInput = findViewById(R.id.destination);
        startButton = findViewById(R.id.startButton);
        timeItTakes = findViewById(R.id.timeItTakes);
        distanceTextView = findViewById(R.id.distanceTextView);

        startButton.setEnabled(false);

        fetchUser();

        startButton.setOnClickListener(view -> {
            if (!isTracking) {
                isTracking = true;
                startTracking();
            }
        });
    }

    private void fetchUser() {
        // هنا ضع كود جلب المستخدم كما عندك
        // وبعد نجاح التحميل:
        // user = response.body();
        // startButton.setEnabled(true);
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }
    }

    private void startTracking() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;

                currentLocation = locationResult.getLastLocation();

                String origin = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                String dest = destinationInput.getText().toString();

                if (!dest.isEmpty()) {
                    drawRouteFromText(origin, dest);
                    updateDistanceAndTime(origin, dest);
                }
            }
        }, Looper.getMainLooper());
    }

    private void updateDistanceAndTime(String origin, String dest) {
        if (user == null) return;

        String unit = user.getUseMetric() ? "metric" : "imperial";

        googleMapsApi.getDistanceBetweenLocations(unit, dest, origin)
                .enqueue(new Callback<DistanceBetweenLocations>() {
                    @Override
                    public void onResponse(Call<DistanceBetweenLocations> call, Response<DistanceBetweenLocations> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            distanceTextView.setText(response.body().getRows().get(0).getElements().get(0).getDistance().getText());
                            timeItTakes.setText(response.body().getRows().get(0).getElements().get(0).getDuration().getText());
                        }
                    }

                    @Override
                    public void onFailure(Call<DistanceBetweenLocations> call, Throwable t) {}
                });
    }

    private void drawRouteFromText(String originText, String destText) {
        googleMapsApi.geocode(originText).enqueue(new Callback<GeocodeResponse>() {
            @Override
            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().results.isEmpty()) {
                    double originLat = response.body().results.get(0).geometry.location.lat;
                    double originLng = response.body().results.get(0).geometry.location.lng;

                    googleMapsApi.geocode(destText).enqueue(new Callback<GeocodeResponse>() {
                        @Override
                        public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
                            if (response.isSuccessful() && response.body() != null && !response.body().results.isEmpty()) {
                                double destLat = response.body().results.get(0).geometry.location.lat;
                                double destLng = response.body().results.get(0).geometry.location.lng;

                                String originLatLng = originLat + "," + originLng;
                                String destLatLng = destLat + "," + destLng;

                                drawRoute(originLatLng, destLatLng);
                            }
                        }

                        @Override
                        public void onFailure(Call<GeocodeResponse> call, Throwable t) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<GeocodeResponse> call, Throwable t) {}
        });
    }

    private void drawRoute(String originLatLng, String destLatLng) {
        googleMapsApi.getDirections(originLatLng, destLatLng, "YOUR_API_KEY")
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<DirectionsResponse.Route> routes = response.body().routes;
                            if (routes != null && !routes.isEmpty()) {
                                String polyline = routes.get(0).overviewPolyline.points;
                                List<LatLng> path = decodePolyline(polyline);

                                PolylineOptions opts = new PolylineOptions()
                                        .addAll(path)
                                        .width(10)
                                        .color(Color.RED); // لون أحمر

                                mMap.clear();
                                mMap.addPolyline(opts);

                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                for (LatLng point : path) builder.include(point);
                                LatLngBounds bounds = builder.build();

                                int padding = 120;
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Toast.makeText(DirectionsActivity.this, "Failed to draw route", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng(
                    ((double) lat / 1E5),
                    ((double) lng / 1E5)
            );
            poly.add(p);
        }
        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        enableMyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableMyLocation();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



//| الخدمة               | ماذا تفعل           |
//| -------------------- | ------------------- |
//| Maps SDK for Android | عرض الخريطة         |
//| Directions API       | رسم المسار          |
//| Distance Matrix API  | المسافة والوقت      |
//| Places API           | البحث               |
//| Geocoding API        | تحويل نص ↔ إحداثيات |













//package com.sarrawi.mymaps;
//
//import androidx.fragment.app.FragmentActivity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//import androidx.cardview.widget.CardView;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PolylineOptions;
//
//import com.sarrawi.mymaps.entities.Address;
//import com.sarrawi.mymaps.entities.User;
//import com.sarrawi.mymaps.responses.DirectionsResponse;
//import com.sarrawi.mymaps.responses.DistanceBetweenLocations;
//import com.sarrawi.mymaps.responses.GeocodeResponse;
//import com.sarrawi.mymaps.utils.LdgoApi;
//import com.sarrawi.mymaps.utils.LdgoGoogleMapsApi;
//import com.sarrawi.mymaps.utils.RetrofitClient;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class DirectionsActivity extends FragmentActivity implements OnMapReadyCallback {
//
//    User user;
//    private GoogleMap mMap;
//    private SharedPreferences sp;
//    private ArrayList<Address> fetchedSeaches = new ArrayList<>();
//
//    EditText originInput, destinationInput;
//    TextView timeItTakes, distanceTextView;
//    CardView startButton;
//
//    public String INITIAL_DESTINATION;
//    public String INITIAL_LONGITUDE;
//    public String INITIAL_LATITUDE;
//
//    public String FINAL_DESTINATION;
//    public String FINAL_LONGITUDE;
//    public String FINAL_LATITUDE;
//
//    LdgoGoogleMapsApi googleMapsApi;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.directions);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        googleMapsApi = RetrofitClient.getRetrofitInstance2().create(LdgoGoogleMapsApi.class);
//        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
//
//        Intent intent = getIntent();
//        fetchUser();
//
//        originInput = findViewById(R.id.origin);
//        destinationInput = findViewById(R.id.destination);
//        startButton = findViewById(R.id.startButton);
//        startButton.setEnabled(false);
//
//        distanceTextView = findViewById(R.id.distanceTextView);
//        timeItTakes = findViewById(R.id.timeItTakes);
//
//        INITIAL_DESTINATION = intent.getStringExtra(MapsActivity.INITIAL_DESTINATION);
//        INITIAL_LONGITUDE = intent.getStringExtra(MapsActivity.INITIAL_LONGITUDE);
//        INITIAL_LATITUDE = intent.getStringExtra(MapsActivity.INITIAL_LATITUDE);
//
//        FINAL_DESTINATION = intent.getStringExtra(MapsActivity.FINAL_DESTINATION);
//        FINAL_LATITUDE = intent.getStringExtra(MapsActivity.FINAL_LATITUDE);
//        FINAL_LONGITUDE = intent.getStringExtra(MapsActivity.FINAL_LONGITUDE);
//
//        originInput.setText(INITIAL_DESTINATION);
//        destinationInput.setText(FINAL_DESTINATION);
//
//        startButton.setOnClickListener(view -> searchForLocation());
//    }
//
//    public void searchForLocation() {
//
//        if (user == null) {
//            Toast.makeText(this, "User not loaded yet", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        String myLocation = originInput.getText().toString();
//        String finalDestination = destinationInput.getText().toString();
//
//        if (myLocation.isEmpty() || finalDestination.isEmpty()) {
//            Toast.makeText(this, "Please enter origin and destination", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        updateDistanceAndTime(myLocation, finalDestination);
//    }
//
//    private void updateDistanceAndTime(String origin, String dest) {
//        String unit = user.getUseMetric() ? "metric" : "imperial";
//
//        googleMapsApi.getDistanceBetweenLocations(unit, dest, origin)
//                .enqueue(new Callback<DistanceBetweenLocations>() {
//                    @Override
//                    public void onResponse(Call<DistanceBetweenLocations> call, Response<DistanceBetweenLocations> response) {
//                        if (response.isSuccessful() && response.body() != null &&
//                                response.body().getRows().size() > 0 &&
//                                response.body().getRows().get(0).getElements().size() > 0) {
//
//                            distanceTextView.setText(response.body().getRows().get(0).getElements().get(0).getDistance().getText());
//                            timeItTakes.setText(response.body().getRows().get(0).getElements().get(0).getDuration().getText());
//
//                            // رسم المسار من النص
//                            drawRouteFromText(origin, dest);
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<DistanceBetweenLocations> call, Throwable t) {}
//                });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        getLocations();
//    }
//    private void drawRouteFromText(String originText, String destText) {
//        // 1) Geocode origin
//        googleMapsApi.geocode(originText).enqueue(new Callback<GeocodeResponse>() {
//            @Override
//            public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    double originLat = response.body().results.get(0).geometry.location.lat;
//                    double originLng = response.body().results.get(0).geometry.location.lng;
//
//                    // 2) Geocode destination
//                    googleMapsApi.geocode(destText).enqueue(new Callback<GeocodeResponse>() {
//                        @Override
//                        public void onResponse(Call<GeocodeResponse> call, Response<GeocodeResponse> response) {
//                            if (response.isSuccessful() && response.body() != null) {
//                                double destLat = response.body().results.get(0).geometry.location.lat;
//                                double destLng = response.body().results.get(0).geometry.location.lng;
//
//                                // 3) الآن ارسم المسار بالإحداثيات
//                                String originLatLng = originLat + "," + originLng;
//                                String destLatLng = destLat + "," + destLng;
//                                drawRoute(originLatLng, destLatLng);
//                            }
//                        }
//                        @Override
//                        public void onFailure(Call<GeocodeResponse> call, Throwable t) {}
//                    });
//                }
//            }
//            @Override
//            public void onFailure(Call<GeocodeResponse> call, Throwable t) {}
//        });
//    }
//
//
//    public void getLocations(){
//        double myLatitudes = Double.parseDouble(INITIAL_LATITUDE);
//        double myLongitude = Double.parseDouble(INITIAL_LONGITUDE);
//
//        Double finalLatitudes = Double.parseDouble(FINAL_LATITUDE);
//        Double finalLongitudes = Double.parseDouble(FINAL_LONGITUDE);
//
//        showLocationOnMaps(INITIAL_DESTINATION, myLongitude, myLatitudes, FINAL_DESTINATION, finalLatitudes, finalLongitudes);
//    }
//
//    public void showLocationOnMaps(String myLocation, Double myLongitude, Double myLatitudes,
//                                   String finalDestination, Double finalLatitudes, Double finalLongitudes){
//        LatLng origin = new LatLng(myLatitudes, myLongitude);
//        mMap.addMarker(new MarkerOptions().position(origin).title(myLocation));
//
//        LatLng dest = new LatLng(finalLatitudes, finalLongitudes);
//        mMap.addMarker(new MarkerOptions().position(dest).title(finalDestination));
//
//        drawRoute(myLatitudes + "," + myLongitude, finalLatitudes + "," + finalLongitudes);
//
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14));
//    }
//
//    public void fetchUser () {
//        LdgoApi ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
//        String userID = sp.getString("userID", "");
//
//        if(userID.isEmpty()){
//            Toast.makeText(this, "UserID is empty", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        Call<User> call = ldgoApi.getUser(userID);
//        call.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    user = response.body();
//                    startButton.setEnabled(true);
//                } else {
//                    Toast.makeText(DirectionsActivity.this, "User not found", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                Toast.makeText(DirectionsActivity.this, "Failed to load user: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void drawRoute(String originLatLng, String destLatLng) {
//        googleMapsApi.getDirections(originLatLng, destLatLng, "AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc")
//                .enqueue(new Callback<DirectionsResponse>() {
//                    @Override
//                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                        if (response.isSuccessful() && response.body() != null) {
//                            List<DirectionsResponse.Route> routes = response.body().routes;
//                            if (routes != null && !routes.isEmpty()) {
//                                String polyline = routes.get(0).overviewPolyline.points;
//
//                                List<LatLng> path = decodePolyline(polyline);
//
//                                PolylineOptions opts = new PolylineOptions()
//                                        .addAll(path)
//                                        .width(8)
//                                        .color(Color.RED); // <-- هنا تغير اللون
//
//                                mMap.addPolyline(opts);
//
//                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                                for (LatLng point : path) {
//                                    builder.include(point);
//                                }
//                                LatLngBounds bounds = builder.build();
//                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {}
//                });
//    }
//
//    private List<LatLng> decodePolyline(String encoded) {
//        List<LatLng> poly = new ArrayList<>();
//        int index = 0, len = encoded.length();
//        int lat = 0, lng = 0;
//
//        while (index < len) {
//            int b, shift = 0, result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lat += dlat;
//
//            shift = 0;
//            result = 0;
//            do {
//                b = encoded.charAt(index++) - 63;
//                result |= (b & 0x1f) << shift;
//                shift += 5;
//            } while (b >= 0x20);
//            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
//            lng += dlng;
//
//            LatLng p = new LatLng(((double) lat / 1E5), ((double) lng / 1E5));
//            poly.add(p);
//        }
//        return poly;
//    }
//}
//
