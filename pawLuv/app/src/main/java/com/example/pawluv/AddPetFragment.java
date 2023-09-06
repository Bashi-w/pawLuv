package com.example.pawluv;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.pawluv.Utility.NetworkChangeListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class AddPetFragment extends Fragment {
    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    String[] types = {"Dog","Cat","Bird","Fish"};
    String[] genders = {"Male","Female"};
    TextInputEditText txtName, txtWeight, txtHeight, txtDate;
    AutoCompleteTextView txtType,txtGender;
    ArrayAdapter<String> adapterItems, adapterItems2;

    private DatePickerDialog datePickerDialog;

    TextInputLayout btnDatePicker;

    ImageView imageView;
    FloatingActionButton fab;

    Button btnContinue;
    String imageURL;

    Uri uri;
    ActivityResultLauncher<Intent> activityResultLauncher;

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_pet, container, false);


    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtName = view.findViewById(R.id.txtName);
        txtType = view.findViewById(R.id.autoCompleteTxtType);
        txtGender = view.findViewById(R.id.autoCompleteTxtGender);
        txtWeight = view.findViewById(R.id.txtWeight);
        txtHeight = view.findViewById(R.id.txtPHeight);
        imageView = view.findViewById(R.id.imgPet);
        fab = view.findViewById(R.id.btnImage);

        adapterItems = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        adapterItems2 = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, genders);

        txtType.setAdapter(adapterItems);
        txtGender.setAdapter(adapterItems2);

        initDatePicker();
        btnDatePicker = view.findViewById(R.id.btnDatePicker);
        txtDate = view.findViewById(R.id.txtDate);

        btnContinue = view.findViewById(R.id.btnContinue);



        btnDatePicker.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });


        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            if (result.getData() != null) {
                                uri = result.getData().getData();
                                imageView.setImageURI(uri);
                            } else {
                                Toast.makeText(getContext(), "No Image Selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtName.getText())){
                    txtName.setError("Name cannot be empty");
                }
                else if (TextUtils.isEmpty(txtType.getText())){
                    txtType.setError("Type cannot be empty");
                }
                else if (TextUtils.isEmpty(txtGender.getText())){
                    txtGender.setError("Gender cannot be empty");
                }
                else if (TextUtils.isEmpty(txtDate.getText())){
                    txtDate.setError("Age cannot be empty");
                }
                else if (TextUtils.isEmpty(txtWeight.getText())){
                    txtWeight.setError("Weight cannot be empty");
                }
                else if (TextUtils.isEmpty(txtHeight.getText())){
                    txtHeight.setError("Height cannot be empty");
                }
                else if (uri == null){
                    Toast.makeText(getContext(), "Please add an image of your pet", Toast.LENGTH_SHORT).show();
                }
                else{
                    saveData();
                }

            }
        });

    }

    //network change listener for fallback
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

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_image);

        ImageButton btnCamera = dialog.findViewById(R.id.btnCamera);
        ImageButton btnGallery = dialog.findViewById(R.id.btnGallery);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
        else{
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }
            else{
                Toast.makeText(getContext(), "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
            uri = getImageUri(requireContext(), photo);
        }
    }

    private Uri getImageUri(Context requireContext, Bitmap imageBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(requireContext.getContentResolver(), imageBitmap, "Title", null);
        return Uri.parse(path);
    }

    public void saveData(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Pets").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isComplete());
                Uri urlimage = uriTask.getResult();
                imageURL = urlimage.toString();
                uploadData();
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void uploadData() {
        String name = txtName.getText().toString();
        String type = txtType.getText().toString();
        String gender = txtGender.getText().toString();
        String age = txtDate.getText().toString();
        String weight = txtWeight.getText().toString();
        String height = txtHeight.getText().toString();
        String currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String ecName = null;
        String ecNum = null;

        DatabaseReference petsRef = FirebaseDatabase.getInstance().getReference("Pets");
        DatabaseReference newPetRef = petsRef.push(); // Generate a new unique ID
        String petId = newPetRef.getKey(); // Get the generated ID

        DataClassPet dataClass = new DataClassPet(name, type, age, gender, imageURL, currentuser,petId,weight,height,ecName,ecNum);

        // Set the dataClass object under the generated ID
        newPetRef.setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (isAdded()) {
                    // Fragment is attached to an activity
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "New pet added.", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }

                    Fragment myPets = new MyPetsFragment();
                    FragmentTransaction fn = requireActivity().getSupportFragmentManager().beginTransaction();
                    fn.replace(R.id.frame_layout, myPets).commit();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month +1;

                String age = calculateAge(year, month, day);
                String ageText = age;
                txtDate.setText(ageText);
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(requireContext(), dateSetListener, year, month, day);
        datePickerDialog.setTitle("Select birthday");

        //setting today's date as max
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    //calculating age from date of birth
    private String calculateAge(int year, int month, int day) {
        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentDay = currentDate.get(Calendar.DAY_OF_MONTH);

        int ageInYears = currentYear - year;
        int ageInMonths = currentMonth - month;
        int ageInDays = currentDay - day;


        String ageText = "";
        if (ageInYears > 0) {
            ageText += ageInYears + (ageInYears == 1 ? " year " : " years ");
        }
        if (ageInMonths > 0) {
            ageText += ageInMonths + (ageInMonths == 1 ? " month " : " months ");
        }
        if (ageInDays > 0) {
            ageText += ageInDays + (ageInDays == 1 ? " day" : " days");
        }

        return ageText.trim();
    }
}