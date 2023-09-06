package com.example.pawluv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawluv.Utility.NetworkChangeListener;
import com.example.pawluv.databinding.ActivityMyPetsBinding;
import com.example.pawluv.databinding.ActivitySittersBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SittersActivity extends AppCompatActivity implements SitterAdapter.OnItemClickListener{

    private FloatingActionButton fab;
    ActivitySittersBinding binding;
    RecyclerView mRecyclerview;
    ArrayList<DataClassSitter> sitterList=new ArrayList<>();
    TextView txtSName,txtSAge,txtSMobile;
    ImageView imgSitter;
    Button btnContact;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySittersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sitterList = DataClassSitter.getSitters("sitters.json", this);

        mRecyclerview = findViewById(R.id.recyclerView);
        boolean isLandscapeMode = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Pass the isLandscapeMode value to the SitterAdapter constructor

        SitterAdapter adapter = new SitterAdapter(sitterList,this,isLandscapeMode);
        adapter.setOnItemClickListener(this);
        mRecyclerview.setAdapter(adapter);


        mRecyclerview.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_sitter);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.sitters);


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
                    Intent petIntent =new Intent(getApplicationContext(),MyPets.class);
                    startActivity(petIntent);
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

    public void onItemClick(DataClassSitter sitter) {
        // Handle the item click event
        // Display details in the RelativeLayout drawer sitter
        String sitterName = sitter.getName();
        String sitterAge = sitter.getAge();
        String sitterMobile = sitter.getMobile();
        String sitterIntro = sitter.getIntro();
        int id = getResources().getIdentifier("@drawable/" + sitter.getImage(), "drawable", "com.example.pawluv");
        showRightDrawerSitterInfo(sitterName, sitterAge, sitterMobile, sitterIntro, id);
    }
    private void showRightDrawerSitterInfo(String sitterName, String sitterAge, String sitterMobile, String sitterIntro, int id) {
        txtSAge = findViewById(R.id.txtSAge);
        txtSName = findViewById(R.id.txtSName);
        txtSMobile = findViewById(R.id.txtMobileNum);
        imgSitter = findViewById(R.id.imgSitter);
        btnContact = findViewById(R.id.btnCall);
        txtSAge.setText(sitterAge);
        txtSName.setText(sitterName);
        txtSMobile.setText(sitterMobile);
        imgSitter.setImageResource(id);

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number = txtSMobile.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
        });
    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}