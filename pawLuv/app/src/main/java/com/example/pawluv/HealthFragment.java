package com.example.pawluv;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pawluv.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HealthFragment extends Fragment {

    ImageView imgPet, imgGender, btnJournal, btnVetMap;
    TextView txtAge, txtWeight, txtHeight;
    String petID;
    Button btnContact;
    private TextInputEditText ecName;
    private TextInputEditText ecNum;
    DatabaseReference databaseReference;
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private Dialog contactDialog;

    public HealthFragment() {
        // Required empty public constructor
    }

    public static HealthFragment newInstance(String param1, String param2) {
        HealthFragment fragment = new HealthFragment();
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
        return inflater.inflate(R.layout.fragment_health, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgPet = view.findViewById(R.id.imgPet);
        txtAge = view.findViewById(R.id.txtAge);
        imgGender = view.findViewById(R.id.imgGender);
        txtWeight = view.findViewById(R.id.txtWeight);
        txtHeight = view.findViewById(R.id.txtHeight);
        btnContact = view.findViewById(R.id.btnContact);
        btnJournal = view.findViewById(R.id.btnJournal);
        btnVetMap = view.findViewById(R.id.btnVetMap);

        databaseReference = FirebaseDatabase.getInstance().getReference("Pets");


        //getting data from intent
        Bundle bundle = getArguments();
        if (bundle != null) {
            petID = bundle.getString("petID");
            String imageURL = bundle.getString("imageURL");
            String age = bundle.getString("age");
            String gender = bundle.getString("gender");
            String weight = bundle.getString("weight");
            String height = bundle.getString("height");
            String ecName = bundle.getString("ecName");
            String ecNum = bundle.getString("ecNum");

            if(gender.equals("Male")){
                imgGender.setImageResource(R.drawable.ic_male);
            } else if (gender.equals("Female")) {
                imgGender.setImageResource(R.drawable.ic_female);
            }

            String formattedAge = formatAge(age);
            txtAge.setText(formattedAge);

            txtWeight.setText(weight + "kg");
            txtHeight.setText(height + "ft");

            Glide.with(this)
                    .load(imageURL)
                    .placeholder(R.drawable.ic_mypets) // Placeholder image resource while loading
                    .error(R.drawable.ic_mypets) // Error image resource if loading fails
                    .into(imgPet);

            if (ecName==null){
                btnContact.setText("Click to add");
            }
            else{
                btnContact.setText(ecName);
            }

        }

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add emergency contact if there's none
                if (btnContact.getText().equals("Click to add")){
                    openContactDialog();
                }
                //call the emergency contact
                else{
                    String ecNum = bundle.getString("ecNum");
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + ecNum));
                    startActivity(intent);
                }
            }
        });

        btnJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment healthJournal = new JournalFragment();
                Bundle bundle = new Bundle();
                bundle.putString("petID", petID); // Pass the petID to the JournalFragment
                healthJournal.setArguments(bundle);
                FragmentTransaction fn = getActivity().getSupportFragmentManager().beginTransaction();
                fn.replace(R.id.frame_layout, healthJournal).commit();
            }
        });

        //search nearby vets on google maps
        btnVetMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location="Vet";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:0,0?q=" + location));
                startActivity(intent);
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

    private void openContactDialog() {
        contactDialog = new Dialog(getContext());
        contactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactDialog.setContentView(R.layout.bottom_sheet_contact);

        ecName = contactDialog.findViewById(R.id.txtName);
        ecNum = contactDialog.findViewById(R.id.txtMobileNum);
        Button btnSubmit = contactDialog.findViewById(R.id.btnCreate);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ecName.getText().toString().trim();
                String mobile = ecNum.getText().toString().trim();
                if (!name.isEmpty() && !mobile.isEmpty()) {
                    // Update the button text with the entered name
                    btnContact.setText(name);
                    updateContact(name,mobile);
                    contactDialog.dismiss();
                } else if(name.isEmpty()){
                    ecName.setError("Name cannot be empty");
                } else if(mobile.isEmpty()){
                    ecNum.setError("Mobile number cannot be empty");
                }
            }
        });

        contactDialog.show();
        contactDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        contactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        contactDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        contactDialog.getWindow().setGravity(Gravity.BOTTOM);
    }



    private void updateContact(String name, String mobile) {
        if (!isAdded()) {
            // Fragment is not attached to a context, handle the case appropriately
            return;
        }
        DatabaseReference petRef = databaseReference.child(petID);
        petRef.child("ecName").setValue(name);
        petRef.child("ecNum").setValue(mobile)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Contact information updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Failed to update contact information", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    //format and shorten age
    private String formatAge(String age) {
        String[] parts = age.split(" ");
        String formattedAge = "";

       /* if (parts.length >= 4 && parts[1].equals("years")) {
            formattedAge += parts[0] + " years";
        } else if (parts.length >= 4 && parts[1].equals("months")) {
            formattedAge += parts[0] + " months";
        } else if (parts.length >= 4 && parts[1].equals("days")) {
            formattedAge += parts[0] + " days";
        } else if (parts.length >= 6) {
            formattedAge += parts[2] + " months " + parts[4] + " days";
        }*/

        if (parts.length >= 6) {
            formattedAge += parts[0] + " years";
        }
        else if (parts.length >= 4 && parts[1].equals("years")) {
            formattedAge += parts[0] + " years";
        }
        else if (parts.length >= 4 && parts[1].equals("year")) {
            formattedAge += parts[0] + " year";
        }
        else if (parts.length >= 4 && parts[1].equals("months")) {
            formattedAge += parts[0] + " months";
        }
        else if (parts.length >= 4 && parts[1].equals("month")) {
            formattedAge += parts[0] + " month";
        }
        else if (parts.length >= 2 && parts[1].equals("days")) {
            formattedAge += parts[0] + " days";
        }
        else if (parts.length >= 2 && parts[1].equals("day")) {
            formattedAge += parts[0] + " day";
        }
        else if (parts.length >= 2 && parts[1].equals("months")) {
            formattedAge += parts[0] + " months";
        }
        else if (parts.length >= 2 && parts[1].equals("month")) {
            formattedAge += parts[0] + " month";
        }
        else if (parts.length >= 2 && parts[1].equals("years")) {
            formattedAge += parts[0] + " years";
        }
        else if (parts.length >= 2 && parts[1].equals("year")) {
            formattedAge += parts[0] + " year";
        }


        return formattedAge;
    }

}