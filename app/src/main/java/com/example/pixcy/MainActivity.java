package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pixcy.databinding.ActivityMainBinding;
import com.example.pixcy.fragments.ComposeFragment;
import com.example.pixcy.fragments.MapsFragment;
import com.example.pixcy.fragments.MemoriesFragment;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private ActivityMainBinding activityMainBinding;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String TAG = "MainActivity";
    protected LocationManager locationManager;
    public String userId;
    public User user;
    public double longitude;
    public double latitude;
    public String address;
    public String city;
    public String state;
    public String postalCode;
    public String country;
    private FusedLocationProviderClient fusedLocationClient;

    private interface GetPostsCallback {
        void done(List<Post> posts, Exception e);
    }

    private interface GetLocationCallback {
        void done(LocationData location, Exception e);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        user = Parcels.unwrap(intent.getParcelableExtra("user"));
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Location runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        MemoriesFragment memoriesFragment = MemoriesFragment.newInstance(user, new ArrayList<>());
        fragmentManager.beginTransaction().replace(R.id.flContainer, memoriesFragment).commit();

        // Set default selection - empty
        activityMainBinding.bottomNavigation.setSelectedItemId(R.id.action_memories);

        // get location
        getLocation(new GetLocationCallback() {
            @Override
            public void done(LocationData locationData, Exception e) {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        // get posts
                        queryPosts(new GetPostsCallback() {
                            @Override
                            public void done(List<Post> posts, Exception e) {
                                if (e == null) {
                                    System.out.println("posts are fetched!");
                                    activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                                        @Override
                                        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                                            Fragment fragment;
                                            switch (menuItem.getItemId()) {
                                                case R.id.action_compose:
                                                    Toast.makeText(MainActivity.this, "Compose", Toast.LENGTH_SHORT).show();
                                                    fragment = ComposeFragment.newInstance(locationData, userId);
                                                    break;
                                                case R.id.action_memories:
                                                    Toast.makeText(MainActivity.this, "Memories", Toast.LENGTH_SHORT).show();
                                                    fragment = MemoriesFragment.newInstance(user, posts);
                                                    break;
                                                case R.id.action_map:
                                                default:
                                                    Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                                                    fragment = MapsFragment.newInstance(locationData, posts);
                                                    break;
                                            }
                                            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                                            return true;
                                        }
                                    });
                                    // default when posts are fetched
                                    fragmentManager.beginTransaction().replace(R.id.flContainer, MemoriesFragment.newInstance(user, posts)).commit();
                                } else {
                                    Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "error getting user posts", e);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void queryPosts(GetPostsCallback callback) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();
        firestoredb.collection("posts")
                .whereEqualTo("user_id", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null || value == null) {
                            Log.w(TAG, "Listen failed.", e);
                            callback.done(null, e);
                            return;
                        }

                        ArrayList<Post> posts = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Log.d("QUERY", "res " + documentSnapshot.getData().get("description"));
                            Post post = documentSnapshot.toObject(Post.class);
                            Log.d("QUERY", "res post " + post);
                            posts.add(post);
                            Log.d(TAG, "on Complete: got a new post");
                        }
                        Log.d(TAG, "Current cites in CA: " + posts);
                        callback.done(posts, null);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_password) {
            Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            // To prevent user from returning back to Main Activity on pressing back button after logging out.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("MissingPermission")
    private void getLocation(GetLocationCallback callback) {
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                try {
                                    longitude = location.getLongitude();
                                    latitude = location.getLatitude();

                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        address = addresses.get(0).getAddressLine(0);
                                        city = addresses.get(0).getLocality();
                                        state = addresses.get(0).getAdminArea();
                                        postalCode = addresses.get(0).getPostalCode();
                                        country = addresses.get(0).getCountryName();

                                        System.out.println("onSuccess location ran");
                                        Log.i(TAG, "Address: " + address);
                                        Log.i(TAG, "AddressCity: " + city);
                                        Log.i(TAG, "AddressState: " + state);
                                        Log.i(TAG, "AddressCountry: " + country);
                                        Log.i(TAG, "AddressPostal: " + postalCode);
                                        Log.i(TAG, "AddressLatitude: " + latitude);
                                        Log.i(TAG, "AddressLongitude: " + longitude);

                                        LocationData locationData = new LocationData();
                                        locationData.setLatitude(latitude);
                                        locationData.setPostalCode(postalCode);
                                        locationData.setLongitude(longitude);
                                        locationData.setAddress(address);
                                        locationData.setState(state);
                                        locationData.setCity(city);
                                        locationData.setCountry(country);

                                        callback.done(locationData, null);
                                    }
                                } catch (Exception e) {
                                    callback.done(null, e);
                                }
                            }
                        }
                    });
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 1, this);
        } catch (Exception e) {
            Log.e(TAG, "error in getLocation()", e);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

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
