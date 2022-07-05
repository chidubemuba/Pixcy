package com.example.pixcy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.pixcy.databinding.ActivityMainBinding;
import com.example.pixcy.fragments.ComposeFragment;
import com.example.pixcy.fragments.MapsFragment;
import com.example.pixcy.fragments.MemoriesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding activityMainBinding;
    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set or remove the title
        getSupportActionBar().hide();

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        activityMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_compose:
                        Toast.makeText(MainActivity.this, "Compose", Toast.LENGTH_SHORT).show();
                        fragment = new ComposeFragment();
                        break;
                    case R.id.action_memories:
                        Toast.makeText(MainActivity.this, "Memories", Toast.LENGTH_SHORT).show();
                        fragment = new MemoriesFragment();
                        break;
                    case R.id.action_map:
                    default:
                        Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                        fragment = new MapsFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        activityMainBinding.bottomNavigation.setSelectedItemId(R.id.action_memories);
    }
}
