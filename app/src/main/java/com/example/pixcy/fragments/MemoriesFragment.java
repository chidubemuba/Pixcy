package com.example.pixcy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pixcy.R;
import com.example.pixcy.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemoriesFragment extends Fragment {

    public static final String TAG = "MemoriesFragment";
    public List<Post> postList = new ArrayList<>();;

    public MemoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_memories, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Create the layout file which represents one post
        // Create data source

        // Create the adapter
        // Bind the adapter and layout manager to the RV


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
                        Post post = document.toObject(Post.class);
                        postList.add(post);
                        Log.d(TAG, "on Complete: got a new post");
                    }
                } else {
                    Log.d(TAG, "Query failed");
                }
            }
        });



//        postsCollectionReference.limit(20);
//        postsCollectionReference.orderBy("creation_time_ms", Query.Direction.DESCENDING);
//        postsCollectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null || snapshots == null) {
//                    Log.e(TAG, "Exception when querying posts", e);
//                    return;
//                }
//
//                postList = snapshots.toObjects(Post.class);
//                System.out.println(" Hello " + postList);
//
//                for (Post post : postList) {
////                    if (dc.getType() == DocumentChange.Type.ADDED) {
////                        Log.d(TAG, "Post: " + post.getDocument().getData());
////                    }
//                    Log.d(TAG, "Post: " + post);
//
//                }
//            }
//        });
    }
}