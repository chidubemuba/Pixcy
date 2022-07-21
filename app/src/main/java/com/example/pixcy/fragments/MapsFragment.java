package com.example.pixcy.fragments;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pixcy.LocationData;
import com.example.pixcy.R;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.parceler.Parcels;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsFragment extends Fragment {

    /**
     * Request code for location permission request.
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /**
     * Flag indicating whether a requested permission has been denied after returning in {@link
     * #onRequestPermissionsResult(int, String[], int[])}.
     */
    private static final String TAG = "MapsFragment";
    private boolean permissionDenied = false;
    private GoogleMap map;
    private List<Post> posts = new ArrayList<>();
    private String image_url;
    private double latitude;
    private double longitude;
    private double currentZoom;
    View mCustomMarkerView;
    private LocationData locationData;
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

    private enum ZoomLevel {
        CITY,
        STATE,
        COUNTRY
    }

    private ZoomLevel zoomLevel = ZoomLevel.COUNTRY;
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static MapsFragment newInstance(LocationData locationData, List<Post> posts) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        ArrayList<Parcelable> parcelableArrayList = new ArrayList<>();
        for(Post post: posts){
            parcelableArrayList.add(Parcels.wrap(post));
        }
        args.putParcelableArrayList(ARG_PARAM1, parcelableArrayList);
        args.putParcelable(ARG_PARAM2, Parcels.wrap(locationData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            locationData = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM2));
            ArrayList<Parcelable> parcelableArrayList = getArguments().getParcelableArrayList(ARG_PARAM1);
            posts = new ArrayList<>();
            for(Parcelable item: parcelableArrayList){
                posts.add(Parcels.unwrap(item));
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        latitude = locationData.getLatitude();
        longitude = locationData.getLongitude();

        //handle exception thrown by getBitMapFromLink
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(@NonNull GoogleMap googleMap) {
                    LatLng userLocation = new LatLng(latitude, longitude);
                    map = googleMap;
                    addMarkers(map);
                    googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                        @Override
                        public void onCameraIdle() {
                            ZoomLevel currentZoomLevel = getZoomLevel(googleMap.getCameraPosition().zoom);
                            System.out.println("camera zoom level: " + currentZoomLevel.toString() + "ZOOM: "+googleMap.getCameraPosition().zoom);
                            // if zoom has entered another level - state, city, country, replace markers
                            if (currentZoomLevel != zoomLevel){
                                zoomLevel = currentZoomLevel;
                                System.out.println("current zoom level: " + zoomLevel.toString());
                                addMarkers(map);
                            }
                        }
                    });
                }
            });
        }
    }

    private void addMarkers(GoogleMap map) {
        Log.d(TAG, "addMarks ran ");
        ArrayList<Post> filteredPosts = new ArrayList<>();
        switch (zoomLevel){
            case CITY:
                Set<String> citySet = new HashSet<String>();
                for(Post post: posts){
                    if (!citySet.contains(post.getCity())){
                        citySet.add(post.getCity());
                        filteredPosts.add(post);
                    }
                }
                System.out.println("City: " +filteredPosts.get(0));
                break;
            case STATE:
                Set<String> stateSet = new HashSet<String>();
                for(Post post: posts){
                    if (!stateSet.contains(post.getState())){
                        stateSet.add(post.getState());
                        filteredPosts.add(post);
                    }
                }
                System.out.println("State: " +filteredPosts.get(0));
                break;
            case COUNTRY:
                Set<String> countrySet = new HashSet<String>();
                for(Post post: posts){
                    if (!countrySet.contains(post.getCountry())){
                        countrySet.add(post.getCountry());
                        filteredPosts.add(post);
                    }
                }
                System.out.println("Country: " +filteredPosts.get(0));
                break;
            default:
                // do city
                break;
        }
        map.clear();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("filtered posts length: " + filteredPosts.size());
                for(Post filteredPost: filteredPosts){
                    System.out.println("filtered post: " + filteredPost);
                    getMarkerBitmapFromView(filteredPost, new GetMarkerBitmapFromViewCallback() {
                        @Override
                        public void done(Bitmap bitmap, Exception e) {
                            if (e == null){
                                System.out.println("Add marker is working");
                                map.addMarker(new MarkerOptions().position(new LatLng(filteredPost.getLatitude(), filteredPost.getLongitude()))
                                        .snippet(filteredPost.getDescription())
                                        .title("this is a post").icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                            } else {
                                Log.e(TAG, "error in getMarkerBitmapFromView", e);
                            }
                        }
                    });
                }
            }
        });
    }

    /***
     * Code adopted from StackOverflow: https://stackoverflow.com/questions/22139515/set-marker-icon-on-google-maps-v2-android-from-url
     *
     */

    private interface GetBitmapFromLinkCallback {
        void done(Bitmap bitmap, Exception exception);
    }

    public Bitmap getBitMapFromLink(String remotePath) throws IOException {
        URL url = new URL(remotePath);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            connection.connect();
        } catch (Exception e) {
            Log.v("MAP-FRAGMENT", e.toString());
        }
        InputStream input = connection.getInputStream();
        Bitmap imageBitmap = BitmapFactory.decodeStream(input);
        return imageBitmap;
    }

    /**
     * * Code adopted from: https://stackoverflow.com/questions/14811579/how-to-create-a-custom-shaped-bitmap-marker-with-android-map-api-v2
     * //
     */
    private interface GetMarkerBitmapFromViewCallback{
        void done(Bitmap bitmap, Exception e);
    }

    private void getMarkerBitmapFromView(Post post, GetMarkerBitmapFromViewCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap image = getBitMapFromLink(post.getImage_url());

                    mCustomMarkerView = requireActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);
                    //TextView markerSnippet = (TextView)  mCustomMarkerView.findViewById(R.id.markerSnippet);
                    ImageView markerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);

                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            markerImageView.setImageBitmap(image);
                            mCustomMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                            mCustomMarkerView.layout(0, 0, mCustomMarkerView.getMeasuredWidth(), mCustomMarkerView.getMeasuredHeight());
                            mCustomMarkerView.buildDrawingCache();
                            Bitmap returnedBitmap = Bitmap.createBitmap(mCustomMarkerView.getMeasuredWidth(), mCustomMarkerView.getMeasuredHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(returnedBitmap);
                            canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                            Drawable drawable = mCustomMarkerView.getBackground();
                            if (drawable != null)
                                drawable.draw(canvas);
                            mCustomMarkerView.draw(canvas);
                            callback.done(returnedBitmap, null);
                        }
                    });
                } catch (Exception e){
                    callback.done(null, e);
                }
            }
        });
    }

    private ZoomLevel getZoomLevel(double zoomLevel) {
        if (zoomLevel >= 1 && zoomLevel < 4) {
            return ZoomLevel.COUNTRY;
        } else if (zoomLevel >= 4 && zoomLevel <= 7) {
            return ZoomLevel.STATE;
        } else {
            return ZoomLevel.CITY;
        }
    }
}
