//package com.sarrawi.mymaps;
//
//
//
//
//
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.cardview.widget.CardView;
//import androidx.constraintlayout.widget.Constraints;
//import androidx.recyclerview.widget.DefaultItemAnimator;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import com.sarrawi.mymaps.components.RecyclerAdapter;
//import com.sarrawi.mymaps.entities.Address;
//import com.sarrawi.mymaps.entities.FavouriteLocation;
//import com.sarrawi.mymaps.entities.User;
//import com.sarrawi.mymaps.requests.FavouriteLocationRequest;
//import com.sarrawi.mymaps.responses.DirectionsResponse;
//import com.sarrawi.mymaps.responses.DistanceBetweenLocations;
//import com.sarrawi.mymaps.responses.FavouriteLocationResponse;
//import com.sarrawi.mymaps.responses.SearchesResponse;
//import com.sarrawi.mymaps.utils.LdgoApi;
//import com.sarrawi.mymaps.utils.LdgoGoogleMapsApi;
//import com.sarrawi.mymaps.utils.LdgoHelpers;
//import com.sarrawi.mymaps.utils.RetrofitClient;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//import java.util.ArrayList;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import android.content.DialogInterface;
//import android.content.pm.PackageManager;
//import android.location.Location;
//import android.os.Bundle;
//
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.gms.maps.model.PointOfInterest;
//import com.google.android.gms.maps.model.PolylineOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.model.PlaceLikelihood;
//import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
//import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.maps.model.DirectionsLeg;
//import com.google.maps.model.DirectionsStep;
//import com.google.maps.model.EncodedPolyline;
//import com.squareup.picasso.Picasso;
//
//import java.util.Arrays;
//import java.util.List;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.FragmentActivity;
//
//import android.os.Bundle;
//
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.maps.DirectionsApi;
//import com.google.maps.DirectionsApiRequest;
//import com.google.maps.GeoApiContext;
//import com.google.maps.model.DirectionsResult;
//import com.google.maps.model.DirectionsRoute;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DirectionsActivity2 extends FragmentActivity implements OnMapReadyCallback {
//
//    User user;
//    private GoogleMap mMap;
//    private String TAG = "so47492459";
//    private SharedPreferences sp;
//    private ArrayList<Address> fetchedSeaches = new ArrayList<>();
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
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        googleMapsApi = RetrofitClient.getRetrofitInstance2().create(LdgoGoogleMapsApi.class);
//
//        sp = getSharedPreferences("user", Context.MODE_PRIVATE);
//
//        Intent intent = getIntent();
//
//        fetchUser();
//
//        originInput = findViewById(R.id.origin);
//        destinationInput = findViewById(R.id.destination);
//
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
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                searchForLocation();
//            }
//        });
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
//        String unit = user.getUseMetric() ? "metric" : "imperial";
//
//
//        Call<DistanceBetweenLocations> call =
//                googleMapsApi.getDistanceBetweenLocations(unit , myLocation, finalDestination);
//
//        call.enqueue(new Callback<DistanceBetweenLocations>() {
//            @Override
//            public void onResponse(Call<DistanceBetweenLocations> call, Response<DistanceBetweenLocations> response) {
//                if (response.isSuccessful()){
//                    try{
//                        distanceTextView.setText(response.body().getRows().get(0).getElements().get(0).getDistance().getText());
//                        timeItTakes.setText(response.body().getRows().get(0).getElements().get(0).getDuration().getText());
//                        originInput.setText(response.body().getOrigin_addresses().get(0));
//                        destinationInput.setText(response.body().getDestination_addresses().get(0));
//                    }catch (Exception e){
//                        Toast.makeText(DirectionsActivity.this, "Not found", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DistanceBetweenLocations> call, Throwable t) {
//                Toast.makeText(DirectionsActivity.this, "Request failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        mMap = googleMap;
//        getLocations();
//    }
//
//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//        super.onPointerCaptureChanged(hasCapture);
//    }
//
//    public void getLocations(){
//        double myLatitudes = Double.parseDouble(INITIAL_LATITUDE);
//        double myLongitude = Double.parseDouble(INITIAL_LONGITUDE);
//        String myLocation = INITIAL_DESTINATION;
//
//        Double finalLatitudes = Double.parseDouble(FINAL_LATITUDE);
//        Double finalLongitudes = Double.parseDouble(FINAL_LONGITUDE);
//
//        String finalDestination = FINAL_DESTINATION;
//
//        try {
//            showLocationOnMaps(myLocation, myLongitude, myLatitudes, finalDestination, finalLatitudes, finalLongitudes);
//        }catch (Exception e) {
//        }
//    }
//
//    public void showLocationOnMaps2(String myLocation, Double myLongitude, Double myLatitudes, String finalDestination, Double finalLatitudes, Double finalLongitudes){
//        try{
//            LatLng barcelona = new LatLng(myLatitudes, myLongitude);
//            mMap.addMarker(new MarkerOptions().position(barcelona).title(myLocation));
//
//            LatLng madrid = new LatLng(finalLatitudes, finalLongitudes);
//            mMap.addMarker(new MarkerOptions().position(madrid).title(finalDestination));
//
//            //Define list to get all latlng for the route
//            List<LatLng> path = new ArrayList<>();
//
//
//            //Execute Directions API request
//            GeoApiContext context = new GeoApiContext.Builder()
////                    .apiKey("AIzaSyBPOr1V_ffIyE9VXuVvmAzHJlEEx5mykU4")
//                    .apiKey("AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc")
//                    .build();
//            DirectionsApiRequest req = DirectionsApi.getDirections(context, myLocation, finalDestination);
//
//            try {
//                DirectionsResult res = req.await();
//
//                //Loop through legs and steps to get encoded polylines of each step
//                if (res.routes != null && res.routes.length > 0) {
//                    DirectionsRoute route = res.routes[0];
//
//                    if (route.legs !=null) {
//                        for(int i=0; i<route.legs.length; i++) {
//                            DirectionsLeg leg = route.legs[i];
//                            if (leg.steps != null) {
//                                for (int j=0; j<leg.steps.length;j++){
//                                    DirectionsStep step = leg.steps[j];
//                                    if (step.steps != null && step.steps.length >0) {
//                                        for (int k=0; k<step.steps.length;k++){
//                                            DirectionsStep step1 = step.steps[k];
//                                            EncodedPolyline points1 = step1.polyline;
//                                            if (points1 != null) {
//                                                //Decode polyline and add points to list of route coordinates
//                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                                for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                    path.add(new LatLng(coord1.lat, coord1.lng));
//                                                }
//                                            }
//                                        }
//                                    } else {
//                                        EncodedPolyline points = step.polyline;
//                                        if (points != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                            for (com.google.maps.model.LatLng coord : coords) {
//                                                path.add(new LatLng(coord.lat, coord.lng));
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch(Exception ex) {
//                Log.e(TAG, ex.getLocalizedMessage());
//            }
//
//            //Draw the polyline
//            if (path.size() > 0) {
//                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
//                mMap.addPolyline(opts);
//            }
//
//            mMap.getUiSettings().setZoomControlsEnabled(true);
//
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barcelona, 14));
//            //searchForLocation();
//        }catch(Exception e) {}
//    }
//
//    public void showLocationOnMaps(String myLocation, Double myLongitude, Double myLatitudes, String finalDestination, Double finalLatitudes, Double finalLongitudes){
//        try{
//            LatLng origin = new LatLng(myLatitudes, myLongitude);
//            mMap.addMarker(new MarkerOptions().position(origin).title(myLocation));
//
//            LatLng dest = new LatLng(finalLatitudes, finalLongitudes);
//            mMap.addMarker(new MarkerOptions().position(dest).title(finalDestination));
//
//            // draw route using Retrofit
//            String originLatLng = myLatitudes + "," + myLongitude;
//            String destLatLng = finalLatitudes + "," + finalLongitudes;
//            drawRoute(originLatLng, destLatLng);
//
//            mMap.getUiSettings().setZoomControlsEnabled(true);
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 14));
//        }catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void searchForPlaceOnTheMap(String input) {
//        Call<SearchesResponse> call = googleMapsApi.searchForPlace(input);
//        call.enqueue(new Callback<SearchesResponse>() {
//            @Override
//            public void onResponse(Call<SearchesResponse> call, Response<SearchesResponse> response) {
//                if(response.isSuccessful()){
//                    fetchedSeaches = response.body().getResults();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SearchesResponse> call, Throwable t) {
//
//            }
//        });
//    }
//
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
//
//
//
//    private void drawRoute(String originLatLng, String destLatLng) {
//        Call<DirectionsResponse> call = googleMapsApi.getDirections(
//                originLatLng,
//                destLatLng,
//                "AIzaSyCJCOfp00o1_KKwcx2ndAm1_uOb_fa_lKc"
//        );
//
//        call.enqueue(new Callback<DirectionsResponse>() {
//            @Override
//            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    List<DirectionsResponse.Route> routes = response.body().routes;
//                    if (routes != null && !routes.isEmpty()) {
//                        String polyline = routes.get(0).overviewPolyline.points;
//
//                        List<LatLng> path = decodePolyline(polyline);
//
//                        PolylineOptions opts = new PolylineOptions()
//                                .addAll(path)
//                                .width(8)
//                                .color(Color.BLUE);
//
//                        mMap.addPolyline(opts);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
//                Toast.makeText(DirectionsActivity.this, "Failed to draw route", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
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
//            LatLng p = new LatLng(
//                    ((double) lat / 1E5),
//                    ((double) lng / 1E5)
//            );
//            poly.add(p);
//        }
//        return poly;
//    }
//
//
//
//
//}