package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pixcy.databinding.ActivityMainBinding;
import com.example.pixcy.fragments.ComposeFragment;
import com.example.pixcy.fragments.MapsFragment;
import com.example.pixcy.fragments.MemoriesFragment;
import com.example.pixcy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ActivityMainBinding activityMainBinding;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String TAG = "MainActivity";
    protected LocationManager locationManager;
    public String userId;
    public double longitude;
    public double latitude;
    public String address;
    public String city;
    public String state;
    public String postal_code;
    public String country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        // Runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        // Get User's current location
        getLocation();

        activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                Bundle location_data = new Bundle();
                location_data.putDouble("latitude", latitude);
                location_data.putDouble("longitude", longitude);
                location_data.putString("address", address);
                location_data.putString("city", city);
                location_data.putString("state", state);
                location_data.putString("postal_code", postal_code);
                location_data.putString("country", country);
                Bundle userDetails = new Bundle();
                userDetails.putString("userId", userId);
                switch (menuItem.getItemId()) {
                    case R.id.action_compose:
                        Toast.makeText(MainActivity.this, "Compose", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        fragment.setArguments(location_data);
                        break;
                    case R.id.action_memories:
                        Toast.makeText(MainActivity.this, "Memories", Toast.LENGTH_SHORT).show();
                        fragment = new MemoriesFragment();
                        fragment.setArguments(userDetails);
                        break;
                    case R.id.action_map:
                    default:
                        Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                        fragment = new MapsFragment();
                        fragment.setArguments(location_data);
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        activityMainBinding.bottomNavigation.setSelectedItemId(R.id.action_memories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // To prevent user from returning back to Main Activity on pressing back button after logging out.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 1, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Toast.makeText(this, "Latitude: "+latitude+", Longitude: "+longitude, Toast.LENGTH_SHORT).show();
        try {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                postal_code = addresses.get(0).getPostalCode();
                country = addresses.get(0).getCountryName();

                Log.i(TAG,"Address: " + address);
                Log.i(TAG,"AddressCity: " + city);
                Log.i(TAG,"AddressState: "+ state);
                Log.i(TAG,"AddressCountry: "+ country);
                Log.i(TAG,"AddressPostal: "+ postal_code);
                Log.i(TAG,"AddressLatitude: " + latitude);
                Log.i(TAG,"AddressLongitude: " + longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        LocationListener.super.onLocationChanged(locations);
    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }
}
