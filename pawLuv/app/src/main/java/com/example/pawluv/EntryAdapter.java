package com.example.pawluv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EntryAdapter extends RecyclerView.Adapter<MyViewHolder3>{

    private Context context;
    private List<DataClassJournal> dataList;

    public EntryAdapter(Context context, List<DataClassJournal> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_journal, parent, false);
        return new MyViewHolder3(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder3 holder, int position) {
        holder.txtDate.setText(dataList.get(position).getDate());
        holder.txtEntry.setText(dataList.get(position).getEntry());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(position);
                }
            }

            });
        }


    private void showDeleteConfirmationDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEntry(position);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void deleteEntry(int position) {
        if (position >= 0 && position < dataList.size()) {
            String entryId = dataList.get(position).getEntryID();

            // Get a reference to the database node where the entries are stored
            DatabaseReference entriesRef = FirebaseDatabase.getInstance().getReference("Journal");

            // Remove the entry from the database using the unique identifier
            entriesRef.child(entryId).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Entry deleted from journal", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Failed to delete entry", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle the case when the position is out of range or the dataList is empty
            Toast.makeText(context, "Invalid position or empty dataList", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        if (dataList == null) {
            return 0;
        }
        return dataList.size();
    }
}
class MyViewHolder3 extends RecyclerView.ViewHolder{
    TextView txtDate;
    TextView txtEntry;
    ImageView btnDelete;

    public MyViewHolder3(@NonNull View itemView) {
        super(itemView);
        txtDate = itemView.findViewById(R.id.txtDate);
        txtEntry = itemView.findViewById(R.id.txtEntry);
        btnDelete = itemView.findViewById(R.id.btnDelete);
    }
}


