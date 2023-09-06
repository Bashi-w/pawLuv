package com.example.pawluv;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<MyViewHolder2>{

    private Context context;
    private List<DataClassSchedule> dataList;
    private DatabaseReference databaseReference;


    public EventAdapter(Context context, List<DataClassSchedule> dataList, DatabaseReference databaseReference) {
        this.context = context;
        this.dataList = dataList;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_event, parent, false);
        return new MyViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        DataClassSchedule data = dataList.get(position);
        holder.txtName.setText(dataList.get(position).getName());
        holder.txtStartTime.setText(dataList.get(position).getStartTime());
        holder.txtEndTime.setText(dataList.get(position).getEndTime());

        //check mark if status=done
        if (data.getStatus().equals("done")) {
            holder.btnDone.setBackgroundResource(R.drawable.ic_check);
        } else {
            holder.btnDone.setBackgroundResource(R.drawable.circle_bg);
        }

        holder.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStatus(data.getScheduleID());
            }
        });
    }

    private void updateStatus(String scheduleID) {
        databaseReference.child(scheduleID).child("status").setValue("done")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Marked as done", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


}

class MyViewHolder2 extends RecyclerView.ViewHolder{

    TextView txtName;
    TextView txtStartTime;
    TextView txtEndTime;
    Button btnDone;


    public MyViewHolder2(@NonNull View itemView) {
        super(itemView);
        txtName = itemView.findViewById(R.id.txtName);
        txtStartTime = itemView.findViewById(R.id.txtStartTime);
        txtEndTime = itemView.findViewById(R.id.txtEndTime);
        btnDone = itemView.findViewById(R.id.btnDone);


    }
}
