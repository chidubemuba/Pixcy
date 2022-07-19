package com.example.pixcy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pixcy.DetailActivity;
import com.example.pixcy.PostsAdapter;
import com.example.pixcy.R;
import com.example.pixcy.databinding.FragmentMemoriesBinding;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemoriesFragment extends Fragment {

    private FragmentMemoriesBinding fragmentMemoriesBinding;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "MemoriesFragment";
    private List<Post> postList;
    private List<Post> posts = new ArrayList<>();
    private PostsAdapter adapter;
    public String userId;

    public MemoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentMemoriesBinding = FragmentMemoriesBinding.inflate(getLayoutInflater(),container, false);
        View view = fragmentMemoriesBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        fragmentMemoriesBinding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle userDetails = getArguments();
        userId = userDetails.getString("userId");

        // Create the layout file which represents one post
        // Create data source
        postList = new ArrayList<>();
        // Create the adapter
        adapter = new PostsAdapter(getContext(), postList);

        // Bind the adapter and layout manager to the RV
        fragmentMemoriesBinding.rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        fragmentMemoriesBinding.rvPosts.setAdapter(adapter);
        // query posts from Firestore
        queryPosts();

        // Lookup the swipe container view
        swipeRefreshLayout = view.findViewById(R.id.swipeContainer);

        fragmentMemoriesBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        // Configure the refreshing colors
        fragmentMemoriesBinding.swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Get User's details
        queryUser();
    }

    private void fetchTimelineAsync(int i) {
        adapter.clear();
        posts.clear();
        queryPosts();
        fragmentMemoriesBinding.swipeContainer.setRefreshing(false);
    }

    private void queryPosts() {
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
                        Log.d("QUERY", "res post " + post);
                        posts.add(post);
                        Log.d(TAG, "on Complete: got a new post");
                    }
                    postList.clear();
                    postList.addAll(posts);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Query failed");
                }
            }
        });
    }

    public void queryUser() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore firestoredb = FirebaseFirestore.getInstance();

        // Create a reference to the users document
        DocumentReference docRef = firestoredb.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User user = document.toObject(User.class);
                        Log.d(TAG, "DocumentSnapshot data USER: " + user);
                        Log.d(TAG, "DocumentSnapshot data binding : " + fragmentMemoriesBinding.tvUsername);
                        fragmentMemoriesBinding.tvUsername.setText(user.getUsername());
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtra("user", Parcels.wrap(user));
                        Log.d(TAG, "User data: " + user);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

}