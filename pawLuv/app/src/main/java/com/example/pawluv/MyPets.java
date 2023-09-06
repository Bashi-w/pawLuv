package com.example.pawluv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.pawluv.Utility.NetworkChangeListener;
import com.example.pawluv.databinding.ActivityMyPetsBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyPets extends AppCompatActivity {
    private FloatingActionButton fab;
    com.example.pawluv.databinding.ActivityMyPetsBinding binding;

    List<DataClassPet> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    String currentUser;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyPetsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dataList = new ArrayList<>();
        fab = findViewById(R.id.fab);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Pets");

        //checking if user has pets and displaying placeholder if not
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClassPet dataClass = itemSnapshot.getValue(DataClassPet.class);

                    if (dataClass != null && dataClass.getCurrentUser().equals(currentUser)) {
                        dataList.add(dataClass);
                    }
                }
                if (dataList.isEmpty()) {
                    replaceFragment(new PlaceholderFragment());
                    fab.setImageResource(R.drawable.ic_mypets);
                } else {
                    replaceFragment(new MyPetsFragment());
                    fab.setImageResource(R.drawable.ic_mypets);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.pets);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    fab.setImageResource(R.drawable.ic_home);
                    Intent homeIntent =new Intent(getApplicationContext(),Home.class);
                    startActivity(homeIntent);
                    return true;

                } else if (itemId == R.id.sitters) {
                    fab.setImageResource(R.drawable.ic_sitter);
                    Intent sitterIntent =new Intent(getApplicationContext(), SittersActivity.class);
                    startActivity(sitterIntent);
                    return true;
                } else if (itemId == R.id.schedule) {
                    fab.setImageResource(R.drawable.ic_schedule);
                    Intent schedIntent =new Intent(getApplicationContext(),ScheduleActivity.class);
                    startActivity(schedIntent);
                    return true;
                }else if (itemId == R.id.pets) {
                    fab.setImageResource(R.drawable.ic_mypets);
                    replaceFragment(new MyPetsFragment());
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

}