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
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pixcy.R;
import com.example.pixcy.models.Post;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
    private List<Post> postList;
    private List<Post> posts = new ArrayList<>();
    private String image_url;
    private double latitude;
    private double longitude;
    private double currentZoom;
    View mCustomMarkerView;


//    private OnMapReadyCallback callback = new OnMapReadyCallback() {
//
//        /**
//         * Manipulates the map once available.
//         * This callback is triggered when the map is ready to be used.
//         * This is where we can add markers or lines, add listeners or move the camera.
//         * In this case, we just add a marker near Sydney, Australia.
//         * If Google Play services is not installed on the device, the user will be prompted to
//         * install it inside the SupportMapFragment. This method will only be triggered once the
//         * user has installed Google Play services and returned to the app.
//         */
//        @Override
//        public void onMapReady(GoogleMap googleMap) {
//           // LatLng userLocation = new LatLng();
//            queryPosts(googleMap);
//        }
//    };

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

        Bundle location_data = getArguments();
        latitude = location_data.getDouble("latitude");
        longitude = location_data.getDouble("longitude");
        postList = new ArrayList<>();

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
                    queryPosts(googleMap);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    googleMap.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .title("me")
                    );
                }
            });
        }
    }

    private void queryPosts(GoogleMap map) {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();

        // Create a reference to the posts collection
        CollectionReference postsCollectionReference = firestoredb.collection("posts");
        Query postQuery = postsCollectionReference
                .whereEqualTo("user_id", FirebaseAuth.getInstance().getCurrentUser().getUid());

        postQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d("QUERY", "res " + document.getData().get("description"));
                        Post post = document.toObject(Post.class);
                        Log.d(TAG, "res post " + post);
                        postList.add(post);
                        Log.d(TAG, "on Complete: got a new post");
                    }
                } else {
                    Log.d(TAG, "Query failed");
                }

                //here is where we add markers
                addMarkers(postList, map);
                Log.d(TAG, "on Complete: posts " + postList);
            }
        });
        Log.d(TAG, "on Complete: posts32 " + postList);
    }

    private void addMarkers(List<Post> postList, GoogleMap map) {
        Log.d(TAG, "on Complete: I AM HERE 2 " + postList);
        for(Post post : postList) {

            currentZoom = map.getCameraPosition().zoom;
            Log.d(TAG, "Min zoom: " + currentZoom);

            if (currentZoom <= 10) {
                //get bitmap from link
                Log.d(TAG, "on Complete: I AM HEREc22 " + post.getImage_url());
                Bitmap bitmap = getMarkerBitmapFromView(post);
                Log.d(TAG, "on Complete: I AM HERE " + bitmap.toString());

                //add to map
                Marker marker = map.addMarker(new MarkerOptions()
                        .position(new LatLng(post.getLatitude(), post.getLongitude()))
                        .snippet(post.getDescription())
                        .title("this is a post")
                        .icon(BitmapDescriptorFactory.fromBitmap(bitmap))

                );
            } else {
                Marker marker = map.addMarker(new MarkerOptions()
                        .title("this is a post")
                        .snippet(post.getDescription())
                        .position(new LatLng(post.latitude, post.longitude))
                );
            }

        }
    }


    /***
     * Code Aopted from StackOverflow: https://stackoverflow.com/questions/22139515/set-marker-icon-on-google-maps-v2-android-from-url
     *
     */
    public Bitmap getBitMapFromLink(String remotePath) {
        try {
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
        } catch (IOException e) {
            Log.v("MAP-FRAGMENT", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     *  * Code adopted from: https://stackoverflow.com/questions/14811579/how-to-create-a-custom-shaped-bitmap-marker-with-android-map-api-v2
//     */
    private Bitmap getMarkerBitmapFromView(Post post) {
        mCustomMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        //TextView markerSnippet = (TextView)  mCustomMarkerView.findViewById(R.id.markerSnippet);
        ImageView markerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.profile_image);

        Bitmap image = getBitMapFromLink(post.getImage_url());
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
        return returnedBitmap;
    }

}
