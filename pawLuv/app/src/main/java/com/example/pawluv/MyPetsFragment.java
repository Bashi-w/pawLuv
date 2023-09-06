package com.example.pawluv;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawluv.Utility.NetworkChangeListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MyPetsFragment extends Fragment {
    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    List<DataClassPet> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    String currentUser;
    ImageView btnSchedule, btnSitters, btnHealth;
    TextView txtDescription;
    Button btnGo;
    FloatingActionButton fabAdd;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_pets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv = view.findViewById(R.id.horizontalRV);

        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        rv.setLayoutManager(linearLayoutManager);

        dataList = new ArrayList<>();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        MyAdapter adapter = new MyAdapter(getContext(), dataList);
        rv.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Pets");


        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClassPet dataClass = itemSnapshot.getValue(DataClassPet.class);

                    //show pets of current user
                    if (dataClass != null && dataClass.getCurrentUser().equals(currentUser)) {
                        dataList.add(dataClass);

                        if (!dataList.isEmpty()) {
                            String petID = dataList.get(0).getPetID();
                            //TextView txtName = view.findViewById(R.id.txtName);
                            //txtName.setText(petID);
                        }

                    }
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //accessing pet in center
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int centerPosition = getCenterVisibleItemPosition(linearLayoutManager);
                    if (centerPosition != RecyclerView.NO_POSITION) {
                        String petID = dataList.get(centerPosition).getPetID();
                        //access the pet --test
                        //TextView txtName = view.findViewById(R.id.txtName);
                       // txtName.setText(petID);

                    }
                }
            }
        });

        //display description when button is clicked
        btnSchedule = view.findViewById(R.id.btnSchedule);
        btnHealth = view.findViewById(R.id.btnHealth);
        btnSitters = view.findViewById(R.id.btnSitters);
        txtDescription = view.findViewById(R.id.txtDescription);
        btnGo = view.findViewById(R.id.btnGo);

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDescription.setText("Plan your pet's day, set reminders and make things easier for you and your lil one!");
                btnGo.setText("Go to Schedule >");
                btnSchedule.setImageResource(R.drawable.schedule);
                btnHealth.setImageResource(R.drawable.healthw);
                btnSitters.setImageResource(R.drawable.sitterw);
            }
        });

        btnHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDescription.setText("Discover trusted veterinarians nearby and access a comprehensive health journal to keep track of your pet's medical records and health history.");
                btnGo.setText("Go to Health >");
                btnHealth.setImageResource(R.drawable.healthh);
                btnSchedule.setImageResource(R.drawable.schedulew);
                btnSitters.setImageResource(R.drawable.sitterw);

            }
        });
        btnSitters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtDescription.setText("Discover trustworthy pet sitters who offer personalized care and affection for your furry friend while you're away.");
                btnGo.setText("Go to Sitters >");
                btnSitters.setImageResource(R.drawable.sitter);
                btnSchedule.setImageResource(R.drawable.schedulew);
                btnHealth.setImageResource(R.drawable.healthw);
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btnGo.getText() == "Go to Schedule >"){
                    //pass petID to schedule screen
                    int centerPosition = getCenterVisibleItemPosition(linearLayoutManager);
                    if (centerPosition != RecyclerView.NO_POSITION) {
                        String petID = dataList.get(centerPosition).getPetID();
                        String imageURL = dataList.get(centerPosition).getImageURL();
                        Intent scheduleIntent = new Intent(getActivity(), ScheduleActivity.class);
                        scheduleIntent.putExtra("petID", petID);
                        scheduleIntent.putExtra("petImage", imageURL);
                        startActivity(scheduleIntent);
                    }
                } else if (btnGo.getText() == "Go to Health >") {
                    int centerPosition = getCenterVisibleItemPosition(linearLayoutManager);
                    if (centerPosition != RecyclerView.NO_POSITION) {
                        String petID = dataList.get(centerPosition).getPetID();
                        String imageURL = dataList.get(centerPosition).getImageURL();
                        String age = dataList.get(centerPosition).getAge();
                        String gender = dataList.get(centerPosition).getGender();
                        String weight = dataList.get(centerPosition).getWeight();
                        String height = dataList.get(centerPosition).getHeight();
                        String ecName = dataList.get(centerPosition).getEcName();
                        String ecNum = dataList.get(centerPosition).getEcNum();

                        // Create a new instance of HealthFragment
                        HealthFragment healthFragment = new HealthFragment();

                        // Pass the petID as an argument to the HealthFragment
                        Bundle bundle = new Bundle();
                        bundle.putString("petID", petID);
                        bundle.putString("imageURL", imageURL);
                        bundle.putString("age", age);
                        bundle.putString("gender", gender);
                        bundle.putString("weight", weight);
                        bundle.putString("height", height);
                        bundle.putString("ecName", ecName);
                        bundle.putString("ecNum", ecNum);

                        healthFragment.setArguments(bundle);

                        // Replace the current fragment with the HealthFragment
                        FragmentTransaction fn = getActivity().getSupportFragmentManager().beginTransaction();
                        fn.replace(R.id.frame_layout, healthFragment).commit();
                    }


                } else if (btnGo.getText() == "Go to Sitters >") {
                    Intent sitterIntent = new Intent(getActivity(), SittersActivity.class);
                    startActivity(sitterIntent);

                }
                else{
                    int centerPosition = getCenterVisibleItemPosition(linearLayoutManager);
                    if (centerPosition != RecyclerView.NO_POSITION) {
                        String petID = dataList.get(centerPosition).getPetID();
                        String imageURL = dataList.get(centerPosition).getImageURL();
                        Intent scheduleIntent = new Intent(getActivity(), ScheduleActivity.class);
                        scheduleIntent.putExtra("petID", petID);
                        scheduleIntent.putExtra("petImage", imageURL);
                        startActivity(scheduleIntent);
                    }
                }
            }
        });

        fabAdd = view.findViewById(R.id.fabAdd);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment addPet = new AddPetFragment();
                FragmentTransaction fn = getActivity().getSupportFragmentManager().beginTransaction();
                fn.replace(R.id.frame_layout, addPet).commit();
            }
        });

    }

    @Override
    public void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkChangeListener,filter);
        super.onStart();
    }

    @Override
    public void onStop() {
        getContext().unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    private int getCenterVisibleItemPosition(LinearLayoutManager layoutManager) {
        int centerViewX = rv.getWidth() / 2;
        int centerViewPosition = -1;
        int minDistance = Integer.MAX_VALUE;

        for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition(); i++) {
            View itemView = layoutManager.findViewByPosition(i);
            if (itemView != null) {
                int viewCenterX = (itemView.getLeft() + itemView.getRight()) / 2;
                int distance = Math.abs(viewCenterX - centerViewX);

                if (distance < minDistance) {
                    minDistance = distance;
                    centerViewPosition = i;
                }
            }
        }

        return centerViewPosition;
    }

}