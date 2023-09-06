package com.example.pawluv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClassPet> dataList;

    public MyAdapter(Context context, List<DataClassPet> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_items_pets, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.recImage);
        holder.txtName.setText(dataList.get(position).getName());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditPetFragment editPetFragment = new EditPetFragment();
                int position = holder.getAdapterPosition();

                // Pass the pet name to the fragment using a Bundle
                Bundle bundle = new Bundle();
                bundle.putString("petID",dataList.get(position).getPetID());
                bundle.putString("petName", dataList.get(position).getName());
                bundle.putString("petType", dataList.get(position).getType());
                bundle.putString("gender", dataList.get(position).getGender());
                bundle.putString("age", dataList.get(position).getAge());
                bundle.putString("weight", dataList.get(position).getWeight());
                bundle.putString("height", dataList.get(position).getHeight());
                bundle.putString("image", dataList.get(position).getImageURL());
                bundle.putString("ecName",dataList.get(position).getEcName());
                bundle.putString("ecNum",dataList.get(position).getEcNum());
                editPetFragment.setArguments(bundle);

                // Open the EditPetFragment
                ((MyPets) context).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, editPetFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                showDeleteConfirmationDialog(position);
            }
        });


    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Pet")
                .setMessage("Are you sure you want to delete this pet?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deletePet(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deletePet(int position) {
        if (position >= 0) {
            String petID = dataList.get(position).getPetID();

            // Get a reference to the database node where the pets are stored
            DatabaseReference petRef = FirebaseDatabase.getInstance().getReference("Pets");

            // Remove the pet from the database using the unique identifier
            petRef.child(petID).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                                Toast.makeText(context, "Pet deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to delete pet", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void searchDataList(ArrayList<DataClassPet> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{

    ImageView recImage;
    TextView txtName;
    ImageView btnEdit,btnDelete;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        txtName = itemView.findViewById(R.id.txtPName);
        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }
}