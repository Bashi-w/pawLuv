package com.example.pawluv;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pawluv.Utility.NetworkChangeListener;
import com.example.pawluv.databinding.ActivityScheduleBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    ActivityScheduleBinding binding;
    TextView txtPetID;
    TextInputEditText txtName,txtDate,txtStartTime,txtEndTime;
    String imageUrl;
    ImageView imgPet;
    private Context context;
    FloatingActionButton fabAdd;
    private DatePickerDialog datePickerDialog;
    List<DataClassSchedule> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    String currentUser;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    String petID;
    TextView txtTitle;
    Button btnPets;
    ImageView btnClose;
    View cal;
    private EventAdapter adapter;
    RelativeLayout rlForm;
    boolean hasEvents = false;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        dataList = new ArrayList<>();
        binding.bottomNavigationView2.setBackground(null);
        imgPet = findViewById(R.id.imgPet);
        fab = findViewById(R.id.fab);
        fabAdd = findViewById(R.id.fabAdd);
        txtTitle = findViewById(R.id.txtTitle);
        btnPets = findViewById(R.id.btnPets);
        rlForm = findViewById(R.id.rlForm);
        btnClose = findViewById(R.id.btnClose);

        recyclerView = findViewById(R.id.rvEvents);
        linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Schedule");
        adapter = new EventAdapter(this, dataList, databaseReference);
        recyclerView.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String selectedDate = year + "-" + (month + 1) + "-" + day;


        // Show today's events by default
        retrieveEvents(selectedDate);


        //getting date from calendar view
        CalendarView calendarView=(CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String selectedDate = year + "-" + (month + 1) + "-" + day;
                retrieveEvents(selectedDate);
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView2);

        bottomNavigationView.setSelectedItemId(R.id.schedule);
        fab.setImageResource(R.drawable.ic_schedule);

        //id of pet
        Intent intent=getIntent();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            //txtPetID.setText(intent.getStringExtra("petID"));

        }

        imgPet = findViewById(R.id.imgPet);
        imageUrl = intent.getStringExtra("petImage");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_mypets) // Placeholder image resource while loading
                .error(R.drawable.ic_mypets) // Error image resource if loading fails
                .into(imgPet);


        //different layouts depending on orientation
        boolean isLandscapeMode = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLandscapeMode == true){
                    rlForm.setVisibility(View.VISIBLE);

                    //close add event view
                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            rlForm.setVisibility(View.INVISIBLE);
                        }
                    });

                    Button btnAdd = findViewById(R.id.btnAdd);
                    TextInputLayout btnDatePicker = findViewById(R.id.btnDatePicker);
                    TextInputLayout btnStartTime = findViewById(R.id.btnStartTime);
                    TextInputLayout btnEndTime = findViewById(R.id.btnEndtime);
                    txtName = findViewById(R.id.txtEventName);
                    txtDate = findViewById(R.id.txtDate);
                    txtStartTime = findViewById(R.id.txtStartTime);
                    txtEndTime = findViewById(R.id.txtEndTime);

                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    final int[] hour = {0};
                    final int[] minute = {0};


                    //date picker dialog
                    datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int day) {
                            String selectedDate = year + "-" + (month + 1) + "-" + day;
                            txtDate.setText(selectedDate);
                        }
                    }, year, month, day);

                    //for start time
                    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            hour[0] = selectedHour;
                            minute[0] = selectedMinute;
                            txtStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour[0], minute[0]));
                        }
                    };

                    //for end time
                    TimePickerDialog.OnTimeSetListener onTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            hour[0] = selectedHour;
                            minute[0] = selectedMinute;
                            txtEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour[0], minute[0]));
                        }
                    };

                    //for start time
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, onTimeSetListener, hour[0], minute[0], true);
                    timePickerDialog.setTitle("Select Time");

                    //for end time
                    TimePickerDialog timePickerDialog2 = new TimePickerDialog(context, onTimeSetListener2, hour[0], minute[0], true);
                    timePickerDialog.setTitle("Select Time");

                    // Setting today's date as minimum
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                    btnDatePicker.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            datePickerDialog.show();
                        }
                    });



                    btnStartTime.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            timePickerDialog.show();
                        }
                    });

                    btnEndTime.setEndIconOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            timePickerDialog2.show();
                        }
                    });


                    // Disable input for the TextInputEditTexts
                    txtDate.setKeyListener(null);
                    txtDate.setFocusable(false);
                    txtDate.setCursorVisible(false);
                    txtStartTime.setKeyListener(null);
                    txtStartTime.setFocusable(false);
                    txtStartTime.setCursorVisible(false);
                    txtEndTime.setKeyListener(null);
                    txtEndTime.setFocusable(false);
                    txtEndTime.setCursorVisible(false);


                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            uploadData();
                            rlForm.setVisibility(View.INVISIBLE);
                        }
                    });

                }
                else{
                    showBottomDialog();
                }
            }
        });


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
                    return true;
                } else if (itemId == R.id.pets) {
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

    private void retrieveEvents(String selectedDate) {
        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                hasEvents = false;
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    DataClassSchedule dataClass = itemSnapshot.getValue(DataClassSchedule.class);
                    Intent intent = getIntent();
                    String petID = intent.getStringExtra("petID");

                    if (dataClass != null && dataClass.getCurrentUser().equals(currentUser)  && dataClass.getDate().equals(selectedDate)) {
                        // Events of selected pet or all pets of the user
                        if (petID == null || (dataClass.getPetID() != null && dataClass.getPetID().equals(petID))) {
                            dataList.add(dataClass);
                            hasEvents = true;
                        }
                    }
                }

                adapter.notifyDataSetChanged();

                Intent intent = getIntent();
                String petID = intent.getStringExtra("petID");

                if (!hasEvents && petID == null) {
                    txtTitle.setText("No events"); // Update the title if no events are found
                    btnPets.setVisibility(View.VISIBLE);
                    fabAdd.setVisibility(View.INVISIBLE);
                } else if (!hasEvents && petID != null) {
                    txtTitle.setText("No events");
                    fabAdd.setVisibility(View.VISIBLE);
                } else if (hasEvents && petID == null){
                    txtTitle.setText("All events lined up");
                    btnPets.setVisibility(View.VISIBLE);
                    fabAdd.setVisibility(View.INVISIBLE);
                } else if(hasEvents && petID != null){
                    txtTitle.setText("All events lined up");
                    btnPets.setVisibility(View.GONE);
                    fabAdd.setVisibility(View.VISIBLE);
                }

                btnPets.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent petsIntent =new Intent(getApplicationContext(),MyPets.class);
                        startActivity(petsIntent);
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        Button btnAdd = dialog.findViewById(R.id.btnAdd);
        TextInputLayout btnDatePicker = dialog.findViewById(R.id.btnDatePicker);
        TextInputLayout btnStartTime = dialog.findViewById(R.id.btnStartTime);
        TextInputLayout btnEndTime = dialog.findViewById(R.id.btnEndtime);
        txtName = dialog.findViewById(R.id.txtEventName);
        txtDate = dialog.findViewById(R.id.txtDate);
        txtStartTime = dialog.findViewById(R.id.txtStartTime);
        txtEndTime = dialog.findViewById(R.id.txtEndTime);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int[] hour = {0};
        final int[] minute = {0};


        //date picker dialog
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String selectedDate = year + "-" + (month + 1) + "-" + day;
                txtDate.setText(selectedDate);
            }
        }, year, month, day);

        //for start time
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour[0] = selectedHour;
                minute[0] = selectedMinute;
                txtStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour[0], minute[0]));
            }
        };

        //for end time
        TimePickerDialog.OnTimeSetListener onTimeSetListener2 = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                hour[0] = selectedHour;
                minute[0] = selectedMinute;
                txtEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour[0], minute[0]));
            }
        };

        //for start time
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour[0], minute[0], true);
        timePickerDialog.setTitle("Select Time");

        //for end time
        TimePickerDialog timePickerDialog2 = new TimePickerDialog(this, onTimeSetListener2, hour[0], minute[0], true);
        timePickerDialog.setTitle("Select Time");

        // Setting today's date as minimum
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        btnDatePicker.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });



        btnStartTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog.show();
            }
        });

        btnEndTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog2.show();
            }
        });


        // Disable input for the TextInputEditTexts
        txtDate.setKeyListener(null);
        txtDate.setFocusable(false);
        txtDate.setCursorVisible(false);
        txtStartTime.setKeyListener(null);
        txtStartTime.setFocusable(false);
        txtStartTime.setCursorVisible(false);
        txtEndTime.setKeyListener(null);
        txtEndTime.setFocusable(false);
        txtEndTime.setCursorVisible(false);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(txtName.getText())){
                    txtName.setError("Name cannot be empty");
                }
                else if(TextUtils.isEmpty(txtDate.getText())){
                    txtDate.setError("Date cannot be empty");
                }
                else if(TextUtils.isEmpty(txtStartTime.getText())){
                    txtStartTime.setError("Add start time");
                }
                else if(TextUtils.isEmpty(txtEndTime.getText())){
                    txtEndTime.setError("Add end time");
                }
                else{
                    uploadData();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void uploadData() {
        String name = txtName.getText().toString().trim();
        String date = txtDate.getText().toString();
        String startTime = txtStartTime.getText().toString();
        String endTime = txtEndTime.getText().toString();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Intent intent=getIntent();
        String petID = intent.getStringExtra("petID");
        String status = "pending";

        DatabaseReference scheduleRef = FirebaseDatabase.getInstance().getReference("Schedule");
        DatabaseReference newEventRef = scheduleRef.push(); // Generate a new unique ID
        String scheduleID= newEventRef.getKey(); // Get the generated ID

        DataClassSchedule dataClass = new DataClassSchedule(name,date,startTime,endTime,currentuser,petID,scheduleID,status);

        // Set the dataClass object under the generated ID
        newEventRef.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ScheduleActivity.this,"Event added to schedule", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context,"Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}
