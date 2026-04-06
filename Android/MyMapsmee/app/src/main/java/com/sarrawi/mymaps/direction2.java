package com.sarrawi.mymaps;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.sarrawi.mymaps.entities.User;
import com.sarrawi.mymaps.responses.*;
import com.sarrawi.mymaps.utils.LdgoGoogleMapsApi;
import com.sarrawi.mymaps.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class direction2 extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private TextToSpeech tts;

    private EditText destinationInput;
    private TextView timeItTakes, distanceTextView, instructionText;
    private CardView startButton, instructionCard;

    private String travelMode = "driving";
    private int routeColor = Color.BLUE;
    private boolean isNavigationMode = false;
    private List<DirectionsResponse.Step> routeSteps = new ArrayList<>();
    private int currentStepIndex = 0;

    private static final int LOCATION_REQUEST_CODE = 1001;
    LdgoGoogleMapsApi googleMapsApi;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direction2);

        initViews();
        setupTTS();
        setupModeButtons();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleMapsApi = RetrofitClient.getRetrofitInstance2().create(LdgoGoogleMapsApi.class);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        startButton.setOnClickListener(view -> {
            isNavigationMode = true;
            instructionCard.setVisibility(View.VISIBLE);
            startTracking();
        });
    }

    private void initViews() {
        destinationInput = findViewById(R.id.destination);
        startButton = findViewById(R.id.startButton);
        timeItTakes = findViewById(R.id.timeItTakes);
        distanceTextView = findViewById(R.id.distanceTextView);
        instructionText = findViewById(R.id.instructionText);
        instructionCard = findViewById(R.id.instructionCard);
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("ar"));
            }
        });
    }

    private void setupModeButtons() {
        findViewById(R.id.btnDriving).setOnClickListener(v -> { travelMode = "driving"; routeColor = Color.BLUE; });
        findViewById(R.id.btnWalking).setOnClickListener(v -> { travelMode = "walking"; routeColor = Color.GREEN; });
        findViewById(R.id.btnTransit).setOnClickListener(v -> { travelMode = "transit"; routeColor = Color.RED; });
    }

    private void startTracking() {
        LocationRequest lr = LocationRequest.create()
                .setInterval(2000).setFastestInterval(1000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return;

        fusedLocationClient.requestLocationUpdates(lr, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult lr) {
                if (lr == null) return;
                currentLocation = lr.getLastLocation();

                if (isNavigationMode) updateCameraForNavigation(currentLocation);

                String origin = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
                String dest = destinationInput.getText().toString();

                if (!dest.isEmpty()) {
                    drawRoute(origin, dest);
                    updateDistanceAndTime(origin, dest);
                    checkVoiceGuidance(currentLocation);
                }
            }
        }, Looper.getMainLooper());
    }

    private void updateCameraForNavigation(Location loc) {
        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());
        CameraPosition cp = new CameraPosition.Builder()
                .target(pos).zoom(18f).bearing(loc.getBearing()).tilt(45).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    private void checkVoiceGuidance(Location loc) {
        if (routeSteps.isEmpty() || currentStepIndex >= routeSteps.size()) return;

        DirectionsResponse.Step step = routeSteps.get(currentStepIndex);
        Location stepLoc = new Location("");
        stepLoc.setLatitude(step.startLocation.lat);
        stepLoc.setLongitude(step.startLocation.lng);

        if (loc.distanceTo(stepLoc) < 40) { // المسافة للتنبيه 40 متر
            String cleanHint = Html.fromHtml(step.htmlInstructions).toString();
            instructionText.setText(cleanHint);
            tts.speak(cleanHint, TextToSpeech.QUEUE_FLUSH, null, null);
            currentStepIndex++;
        }
    }

    private void drawRoute(String origin, String dest) {
        googleMapsApi.getDirectionsgi(origin, dest, travelMode, "YOUR_API_KEY")
                .enqueue(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.isSuccessful() && response.body().routes != null && !response.body().routes.isEmpty()) {
                            routeSteps = response.body().routes.get(0).legs.get(0).steps;
                            String poly = response.body().routes.get(0).overviewPolyline.points;

                            mMap.clear();
                            mMap.addPolyline(new PolylineOptions().addAll(decodePolyline(poly)).color(routeColor).width(15f));
                        }
                    }
                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {}
                });
    }

    private void updateDistanceAndTime(String origin, String dest) {
        // يجب إضافة مفتاح الـ API هنا (البارامتر الرابع)
        String myApiKey = "YOUR_API_KEY_HERE";

        googleMapsApi.getDistanceBetweenLocationsgi("metric", origin, dest, myApiKey)
                .enqueue(new Callback<DistanceBetweenLocations>() {
                    @Override
                    public void onResponse(Call<DistanceBetweenLocations> call, Response<DistanceBetweenLocations> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // تأكد من وجود بيانات قبل العرض لتجنب الـ NullPointerException
                            if (!response.body().getRows().isEmpty() &&
                                    !response.body().getRows().get(0).getElements().isEmpty()) {

                                distanceTextView.setText(response.body().getRows().get(0).getElements().get(0).getDistance().getText());
                                timeItTakes.setText(response.body().getRows().get(0).getElements().get(0).getDuration().getText());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DistanceBetweenLocations> call, Throwable t) {
                        // تعامل مع الخطأ هنا
                    }
                });
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do { b = encoded.charAt(index++) - 63; result |= (b & 0x1f) << shift; shift += 5; } while (b >= 0x20);
            lat += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            shift = 0; result = 0;
            do { b = encoded.charAt(index++) - 63; result |= (b & 0x1f) << shift; shift += 5; } while (b >= 0x20);
            lng += ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            poly.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }
        return poly;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        super.onDestroy();
    }
}
