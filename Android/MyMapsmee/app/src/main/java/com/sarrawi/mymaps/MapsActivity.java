package com.sarrawi.mymaps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sarrawi.mymaps.components.RecyclerAdapter;
import com.sarrawi.mymaps.entities.Address;
import com.sarrawi.mymaps.entities.FavouriteLocation;
import com.sarrawi.mymaps.requests.FavouriteLocationRequest;
import com.sarrawi.mymaps.responses.FavouriteLocationResponse;
import com.sarrawi.mymaps.responses.SearchesResponse;
import com.sarrawi.mymaps.utils.LdgoApi;
import com.sarrawi.mymaps.utils.LdgoGoogleMapsApi;
import com.sarrawi.mymaps.utils.LdgoHelpers;
import com.sarrawi.mymaps.utils.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.google.android.libraries.places.api.Places;
import com.sarrawi.mymaps.BuildConfig;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPoiClickListener {

    private GoogleMap mMap;
    private ArrayList<Address> fetchedSeaches = new ArrayList<>();
    private FavouriteLocation selectedLocation;

    public static final String INITIAL_LONGITUDE = "INITIAL_LONGITUDE";
    public static final String INITIAL_LATITUDE = "INITIAL_LATITUDE";
    public static final String INITIAL_DESTINATION = "INITIAL_DESTINATION";

    public static final String FINAL_LONGITUDE = "FINAL_LONGITUDE";
    public static final String FINAL_LATITUDE = "FINAL_LATITUDE";
    public static final String FINAL_DESTINATION = "FINAL_DESTINATION";

    EditText searchInputField;
    ImageButton goToProfile;
    RecyclerView recyclerViewSearches;
    private RecyclerAdapter.RecyclerViewClickListener listener;
    ImageView locationImage;
    TextView locationAddress, locationName, closeDirectionsCard;
    ImageButton cancelBtn, goToFavourites;
    CardView locationSummaryCard, directionsSummaryCard, saveLocationBtn, directionsBtn;
    RecyclerAdapter adapter;
    private SharedPreferences user_sp;
    private SharedPreferences location_sp;
    LdgoApi ldgoApi;
    LdgoGoogleMapsApi googleMapsApi;
    double myLatitudes = -26.1952602;
    double myLongitude = 28.0337497;

    // Everything thing below is from the documention

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private LdgoHelpers ldgoHelpers;

    // The entry point to the Places API.
    private PlacesClient placesClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(myLatitudes, myLongitude);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

        locationSummaryCard = findViewById(R.id.locationSummaryCard);
        cancelBtn = findViewById(R.id.cancelBtn);

//        AIzaYourKeyHere
        // Construct a PlacesClient
        //Places.initialize(getApplicationContext(), "AIzaYourKeyHere");
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        placesClient = Places.createClient(this);

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        goToProfile = findViewById(R.id.goToProfile);
        searchInputField = findViewById(R.id.searchInputField);
        recyclerViewSearches = findViewById(R.id.recyclerViewSearches);
        saveLocationBtn = findViewById(R.id.saveLocationBtn);
        directionsSummaryCard = findViewById(R.id.directionsSummaryCard);
        directionsBtn = findViewById(R.id.directionsBtn);
        closeDirectionsCard = findViewById(R.id.closeDirectionsCard);
        goToFavourites = findViewById(R.id.goToFavourites);

        ldgoApi = RetrofitClient.getRetrofitInstance().create(LdgoApi.class);
        googleMapsApi = RetrofitClient.getRetrofitInstance2().create(LdgoGoogleMapsApi.class);
        user_sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        location_sp = getSharedPreferences("location", Context.MODE_PRIVATE);

        searchInputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    String input = searchInputField.getText().toString();
                     searchForPlaceOnTheMap(input);
                    return true;
                }
                return false;
            }
        });

        goToFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, FavouriteActivity.class);
                startActivity(intent);
            }
        });


        directionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 1- تأكد أنه تم اختيار موقع
                if (selectedLocation == null) {
                    Toast.makeText(MapsActivity.this, "Please select a location first", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2- صلاحية الموقع
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            100);
                    return;
                }

                // 3- احصل على موقع المستخدم الحالي
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {

                        String myLat = String.valueOf(location.getLatitude());
                        String myLng = String.valueOf(location.getLongitude());

                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        String addressText = "My Current Location";

                        try {
                            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                addressText = addresses.get(0).getAddressLine(0);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        Intent in = new Intent(MapsActivity.this, DirectionsActivity.class);

                        in.putExtra(INITIAL_LONGITUDE, myLng);
                        in.putExtra(INITIAL_LATITUDE, myLat);
                        in.putExtra(INITIAL_DESTINATION, addressText);

                        in.putExtra(FINAL_LONGITUDE, selectedLocation.getLongitudes());
                        in.putExtra(FINAL_LATITUDE, selectedLocation.getLatitudes());
                        in.putExtra(FINAL_DESTINATION, selectedLocation.getFormatted_address());

                        startActivity(in);
                    } else {
                        Toast.makeText(MapsActivity.this, "Cannot get your location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




        closeDirectionsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                directionsSummaryCard.setVisibility(View.GONE);
            }
        });

        saveLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                user_sp = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                String jwt = user_sp.getString( "jwt", "");
                FavouriteLocationRequest favourite = new FavouriteLocationRequest(selectedLocation);
                Call<FavouriteLocationResponse> call = ldgoApi.addFavouritesLocations(jwt, favourite);
                call.enqueue(new Callback<FavouriteLocationResponse>() {
                    @Override
                    public void onResponse(Call<FavouriteLocationResponse> call, Response<FavouriteLocationResponse> response) {
                        if(!response.isSuccessful()){
                            Toast.makeText(MapsActivity.this, "Location not saved, Already Added", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(MapsActivity.this, "Location Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<FavouriteLocationResponse> call, Throwable t) {

                    }
                });
            }
        });

        goToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locationSummaryCard.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu, menu);
        return true;
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.option_get_place) {
            showCurrentPlace();
        }
        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnPoiClickListener(this);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission")
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {

//                                myLatitudes = lastKnownLocation.getLatitude();
//                                myLongitude = lastKnownLocation.getLongitude();

                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        updateLocationUI();
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (map == null) {
            return;
        }

        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS,
                    Place.Field.LAT_LNG);

            // Use the builder to create a FindCurrentPlaceRequest.
            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            @SuppressWarnings("MissingPermission") final
            Task<FindCurrentPlaceResponse> placeResult =
                    placesClient.findCurrentPlace(request);
            placeResult.addOnCompleteListener (new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        // Set the count, handling cases where less than 5 entries are returned.
                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;
                        likelyPlaceNames = new String[count];
                        likelyPlaceAddresses = new String[count];
                        likelyPlaceAttributions = new List[count];
                        likelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {
                            // Build a list of likely places to show the user.
                            likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            likelyPlaceAttributions[i] = placeLikelihood.getPlace()
                                    .getAttributions();
                            likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                            i++;
                            if (i > (count - 1)) {
                                break;
                            }
                        }

                        // Show a dialog offering the user the list of likely places, and add a
                        // marker at the selected place.
                        MapsActivity.this.openPlacesDialog();
                    }
                    else {
                        Log.e(TAG, "Exception: %s", task.getException());
                    }
                }
            });
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.");

            // Add a default marker, because the user hasn't selected a place.
            map.addMarker(new MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(defaultLocation)
                    .snippet(getString(R.string.default_info_snippet)));

            // Prompt the user for permission.
            getLocationPermission();
        }
    }

    /**
     * Displays a form allowing the user to select a place from a list of likely places.
     */
    private void openPlacesDialog() {
        // Ask the user to choose the place where they are now.
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // The "which" argument contains the position of the selected item.
                LatLng markerLatLng = likelyPlaceLatLngs[which];
                String markerSnippet = likelyPlaceAddresses[which];
                if (likelyPlaceAttributions[which] != null) {
                    markerSnippet = markerSnippet + "\n" + likelyPlaceAttributions[which];
                }

                // Add a marker for the selected place, with an info window
                // showing information about that place.
                map.addMarker(new MarkerOptions()
                        .title(likelyPlaceNames[which])
                        .position(markerLatLng)
                        .snippet(markerSnippet));

                // Position the map's camera at the location of the marker.
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                        DEFAULT_ZOOM));
            }
        };

        // Display the dialog.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.pick_place)
                .setItems(likelyPlaceNames, listener)
                .show();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
//                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
        Call<SearchesResponse> call = googleMapsApi.searchForPlace(pointOfInterest.name);
        call.enqueue(new Callback<SearchesResponse>() {
            @Override
            public void onResponse(Call<SearchesResponse> call, Response<SearchesResponse> response) {
                if(response.isSuccessful()){
                    try{
                        showLocationCard(response.body().getFirstFavouriteLocation());
                        return;
                    } catch (Exception e) {
                        Toast.makeText(MapsActivity.this, "You must enable Billing on the Google Cloud Project", Toast.LENGTH_LONG).show();
                        Log.e("clickerror", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchesResponse> call, Throwable t) {

            }
        });
    }

    private void searchForPlaceOnTheMap(String input) {
        Call<SearchesResponse> call = googleMapsApi.searchForPlace(input);
        call.enqueue(new Callback<SearchesResponse>() {
            @Override
            public void onResponse(Call<SearchesResponse> call, Response<SearchesResponse> response) {
                if(response.isSuccessful()){
                    fetchedSeaches = response.body().getResults();
                    setOnClickListner();
                    adapter = new RecyclerAdapter(fetchedSeaches, listener);
                    RecyclerView.LayoutManager lm = new LinearLayoutManager(getApplicationContext());
                    recyclerViewSearches.setLayoutManager(lm);
                    recyclerViewSearches.setItemAnimator(new DefaultItemAnimator());
                    recyclerViewSearches.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<SearchesResponse> call, Throwable t) {

            }
        });
    }

    private void setOnClickListner () {
        listener = new RecyclerAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                try{
                    showLocationCard(fetchedSeaches.get(position));
                }catch (Exception e){
                    Log.d("eera", e.getMessage());
                }
            }
        };
    }

    private void showLocationCard (Address address) {
        try{
            locationName = findViewById(R.id.locationName);
            locationAddress = findViewById(R.id.locationAddress);
            locationImage = findViewById(R.id.locationImage);

            String name = address.getName();
            String latitudes = address.getLatitudes();
            String longitudes = address.getLongitudes();
            String formatted_address = address.getFormatted_address();
            String image = address.getFirstImage();
            String place_id = address.getPlace_id();

            selectedLocation = new FavouriteLocation(name, longitudes, latitudes, formatted_address, image, place_id);
            locationName.setText(name);
            locationAddress.setText(formatted_address);
            Picasso.get().load(ldgoHelpers.googpeMapsImage(image)).into(locationImage);
            fetchedSeaches.clear();
            adapter.notifyDataSetChanged();
            locationSummaryCard.setVisibility(View.VISIBLE);
        }catch (Exception e){
            Log.d("errv", e.getMessage());
        }
    }

    private void showLocationCard (FavouriteLocation favouriteLocation) {
        try{
            locationName = findViewById(R.id.locationName);
            locationAddress = findViewById(R.id.locationAddress);
            locationImage = findViewById(R.id.locationImage);

            try{
                myLongitude = Double.parseDouble(favouriteLocation.getLongitudes());
                myLatitudes = Double.parseDouble(favouriteLocation.getLatitudes());
            }catch (Exception e){

            }

            locationName.setText(favouriteLocation.getName());
            locationAddress.setText(favouriteLocation.getFormatted_address());
            Picasso.get().load(ldgoHelpers.googpeMapsImage(favouriteLocation.getPhoto_reference())).into(locationImage);
            selectedLocation = favouriteLocation;
            locationSummaryCard.setVisibility(View.VISIBLE);
        }catch (Exception e) {
            Log.d("errr", e.getMessage());
        }
    }
}