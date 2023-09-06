package com.example.pawluv;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pawluv.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class JournalFragment extends Fragment {

    FloatingActionButton fabAdd;
    TextInputEditText txtEntry;
    String petID;
    List<DataClassJournal> dataList;
    DatabaseReference databaseReference;
    ValueEventListener eventListener;
    String currentUser;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    EntryAdapter adapter;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    public JournalFragment() {
        // Required empty public constructor
    }


    public static JournalFragment newInstance(String param1, String param2) {
        JournalFragment fragment = new JournalFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_journal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fabAdd = view.findViewById(R.id.fabAdd);
        txtEntry = view.findViewById(R.id.txtEntry);
        dataList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rvEntries);

        linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new EntryAdapter(getContext(), dataList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference("Journal");


        Bundle bundle = getArguments();
        if (bundle != null) {
            petID = bundle.getString("petID"); // Assign the value of petID from the bundle
        }

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()) {
                    DataClassJournal dataClass = itemSnapshot.getValue(DataClassJournal.class);
                    if (dataClass != null && dataClass.getCurrentUser() != null && dataClass.getPetID() != null &&
                            dataClass.getCurrentUser().equals(currentUser) && dataClass.getPetID().equals(petID)) {
                        dataList.add(dataClass);
                    }
                }adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
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

    private void uploadData() {
        String entry = txtEntry.getText().toString();
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference journalRef = FirebaseDatabase.getInstance().getReference("Journal");
        DatabaseReference newEntryRef = journalRef.push(); // Generate a new unique ID
        String entryID= newEntryRef.getKey(); // Get the generated ID

        DataClassJournal dataClass = new DataClassJournal(entry,date,currentuser,petID,entryID);

        // Set the dataClass object under the generated ID
        newEntryRef.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(),"New entry added", Toast.LENGTH_SHORT).show();
                    txtEntry.setText(null);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),"Failed to add entry", Toast.LENGTH_SHORT).show();
            }
        });
    }
}