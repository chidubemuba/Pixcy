package com.example.pixcy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Parcelable;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemoriesFragment extends Fragment {

    private FragmentMemoriesBinding fragmentMemoriesBinding;
    private SwipeRefreshLayout swipeRefreshLayout;
    public static final String TAG = "MemoriesFragment";
    private List<Post> posts;
    private PostsAdapter adapter;
    public User user;
    public static final String ARG_PARAM1 = "param1";
    public static final String ARG_PARAM2 = "param2";

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

    public static MemoriesFragment newInstance(User user, List<Post> posts) {
        MemoriesFragment memoriesFragment = new MemoriesFragment();
        Bundle args = new Bundle();
        ArrayList<Parcelable> parcelableArrayList = new ArrayList<>();
        for(Post post: posts){
            parcelableArrayList.add(Parcels.wrap(post));
        }
        args.putParcelableArrayList(ARG_PARAM1, parcelableArrayList);
        args.putParcelable(ARG_PARAM2, Parcels.wrap(user));
        memoriesFragment.setArguments(args);
        return memoriesFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            user = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM2));
            ArrayList<Parcelable> parcelableArrayList = getArguments().getParcelableArrayList(ARG_PARAM1);
            posts = new ArrayList<>();
            for(Parcelable item: parcelableArrayList){
                posts.add(Parcels.unwrap(item));
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the adapter
        adapter = new PostsAdapter(getContext(), posts);

        // Bind the adapter and layout manager to the RV
        fragmentMemoriesBinding.rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        fragmentMemoriesBinding.rvPosts.setAdapter(adapter);
        fragmentMemoriesBinding.tvUsername.setText(user.getUsername());

        // query posts from Firestore
//        ExecutorService executorService  = Executors.newSingleThreadExecutor();
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                queryPosts();
//            }
//        });

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

//        // Get User's details
//        executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                queryUser();
//            }
//        });
    }

    private void fetchTimelineAsync(int i) {
        adapter.clear();
        posts.clear();
//        queryPosts();
        // todo: create a method in parent activity to fetch posts and use a callback to pass the posts to this fragment
        fragmentMemoriesBinding.swipeContainer.setRefreshing(false);
    }


}