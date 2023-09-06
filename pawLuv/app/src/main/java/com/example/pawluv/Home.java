package com.example.pawluv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.pawluv.Utility.NetworkChangeListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity {
    private Context context;
    TextView txtGreeting,txtCount;
    ImageButton btnSitters,btnMyPets,btnSchedule;
    FirebaseAuth auth;
    ImageButton btnLogout;
    FirebaseUser user;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DatabaseReference userRef;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    List<DataClassSchedule> dataList;
    String currentUser;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = this;
        txtGreeting = findViewById(R.id.txtGreeting);
        txtCount = findViewById(R.id.txtCount);
        btnSitters = findViewById(R.id.btnSitters);
        btnMyPets = findViewById(R.id.btnPets);
        btnSchedule = findViewById(R.id.btnSchedule);

        auth = FirebaseAuth.getInstance();
        btnLogout = findViewById(R.id.btnLogout);
        TextView txtName = findViewById(R.id.txtName);
        user = auth.getCurrentUser();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account!=null){
            String name = account.getDisplayName();
            String[] nameParts = name.split(" ");
            String firstName = "";
            if (nameParts.length > 0) {
                firstName = nameParts[0];
            }
            txtName.setText("Hey " + firstName + "!");
        }
        else{
            txtName.setText("Hi");
        }


        if(user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else{

            // Get the current user ID
            String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Get a reference to the user's data in the database
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentuser);

            // Read the user's data from the database
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the name from the snapshot
                        String name = snapshot.child("name").getValue(String.class);

                        // Trim the name to get only the first name
                        String firstName = "";
                        if (name != null) {
                            String[] nameParts = name.split(" ");
                            if (nameParts.length > 0) {
                                firstName = nameParts[0];
                            }
                        }

                        // Display the first name in the txtName TextView
                        txtName.setText("Hey " + firstName + "!");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule");

        // Get the current date and format it
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        String currentDate = dateFormat.format(new Date());

        //show count of events lined up for today
        Query queryCurrentUser = scheduleRef.orderByChild("currentUser").equalTo(currentUser);
        Query queryDate = scheduleRef.orderByChild("date").equalTo(currentDate);

        queryCurrentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotCurrentUser) {
                queryDate.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotDate) {
                        int count = 0;
                        for (DataSnapshot snapshotCurrentUser : dataSnapshotCurrentUser.getChildren()) {
                            for (DataSnapshot snapshotDate : dataSnapshotDate.getChildren()) {
                                if (snapshotCurrentUser.getKey().equals(snapshotDate.getKey())) {
                                    count++;
                                    break;
                                }
                            }
                        }

                        txtCount.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                gsc.signOut();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();


            }
        });


        //show greeting depending on time of day
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay >= 0 && timeOfDay < 12){
            txtGreeting.setText("Good Morning");
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            txtGreeting.setText("Good Afternoon");
        }else if(timeOfDay >= 16 && timeOfDay < 21){
            txtGreeting.setText("Good Evening");
        }else if(timeOfDay >= 21 && timeOfDay < 24){
            txtGreeting.setText("Good Night");
        }

        btnSitters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sittersIntent =new Intent(getApplicationContext(), SittersActivity.class);
                startActivity(sittersIntent);
            }
        });

        btnMyPets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent petsIntent =new Intent(getApplicationContext(),MyPets.class);
                startActivity(petsIntent);

            }
        });

        btnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scheduleIntent =new Intent(getApplicationContext(),ScheduleActivity.class);
                startActivity(scheduleIntent);
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
}