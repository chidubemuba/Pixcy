package com.example.pixcy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.pixcy.PostsAdapter;
import com.example.pixcy.databinding.FragmentMemoriesBinding;
import com.example.pixcy.models.Post;
import com.example.pixcy.models.User;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MemoriesFragment extends Fragment {

    private FragmentMemoriesBinding fragmentMemoriesBinding;
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
        fragmentMemoriesBinding = FragmentMemoriesBinding.inflate(getLayoutInflater(), container, false);
        View view = fragmentMemoriesBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentMemoriesBinding = null;
    }

    public static MemoriesFragment newInstance(User user, List<Post> posts) {
        MemoriesFragment memoriesFragment = new MemoriesFragment();
        Bundle args = new Bundle();
        ArrayList<Parcelable> parcelableArrayList = new ArrayList<>();
        for (Post post : posts) {
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
        if (getArguments() != null) {
            user = Parcels.unwrap(getArguments().getParcelable(ARG_PARAM2));
            ArrayList<Parcelable> parcelableArrayList = getArguments().getParcelableArrayList(ARG_PARAM1);
            posts = new ArrayList<>();
            for (Parcelable item : parcelableArrayList) {
                posts.add(Parcels.unwrap(item));
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new PostsAdapter(getContext(), posts);        // Create the adapter

        // Bind the adapter and layout manager to the RV
        fragmentMemoriesBinding.rvPosts.setLayoutManager(new GridLayoutManager(getContext(), 3));
        fragmentMemoriesBinding.rvPosts.setAdapter(adapter);
        fragmentMemoriesBinding.tvUsername.setText(user.getUsername());
        fragmentMemoriesBinding.tvBio.setText(user.getBio());
        Glide.with(getContext())
                .load(user.getProfile_pic())
                .into(fragmentMemoriesBinding.ivProfilePicture);
    }
}
