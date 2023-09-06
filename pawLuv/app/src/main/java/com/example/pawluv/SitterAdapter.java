package com.example.pawluv;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SitterAdapter extends RecyclerView.Adapter<SitterAdapter.SitterViewHolder> {
    ArrayList<DataClassSitter> sitter;
    Context mContext;
    private boolean isLandscapeMode;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(DataClassSitter sitter);
    }


    public SitterAdapter(ArrayList<DataClassSitter> sitterList) {
        this.sitter = sitterList;
    }

    public SitterAdapter(ArrayList<DataClassSitter> sitter, Context mContext, boolean isLandscapeMode) {

        this.sitter = sitter;
        this.mContext = mContext;
        this.isLandscapeMode = isLandscapeMode;
    }

    @NonNull
    @Override
    public SitterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SitterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_sitter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SitterViewHolder holder, int position) {
        DataClassSitter thesitter=sitter.get(position);
        holder.txtName.setText(sitter.get(position).name);


        Resources res=holder.itemView.getContext().getResources();
        int id=res.getIdentifier("@drawable/"+thesitter.image,"drawable", "com.example.pawluv");
        holder.imgcover.setImageResource(id);



        holder.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                // Check if there is a next item in the list
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < sitter.size() - 1) {
                    // Get the parent RecyclerView
                    RecyclerView recyclerView = (RecyclerView) view.getRootView().findViewById(R.id.recyclerView);
                    // Scroll to the next item
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    layoutManager.scrollToPosition(currentPosition + 1);
                }
            }
        });

        holder.btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = holder.getAdapterPosition();
                // Check if there is a previous item in the list
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < sitter.size() + 1) {
                    // Get the parent RecyclerView
                    RecyclerView recyclerView = (RecyclerView) view.getRootView().findViewById(R.id.recyclerView);
                    // Scroll to the previous item
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    layoutManager.scrollToPosition(currentPosition - 1);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sitterName = thesitter.getName();
                String sitterAge = thesitter.getAge();
                String sitterMobile = thesitter.getMobile();
                String sitterIntro = thesitter.getIntro();
                showBottomDialog(sitterName,sitterAge, sitterMobile, sitterIntro,id);
            }
        });

        if (isLandscapeMode) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Display details in the RelativeLayout drawer sitter
                    // You can access the details using the 'thesitter' object
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(thesitter);
                    }

                }
            });
        }

    }

   /* private void showRightDrawerSitterInfo(String sitterName, String sitterAge, String sitterMobile, String sitterIntro, int id) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.drawer_sitter);
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationFromRight;
        dialog.getWindow().setGravity(Gravity.END);

        Button btnCall = dialog.findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtMobileNum = dialog.findViewById(R.id.txtMobileNum);
                String number = txtMobileNum.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                mContext.startActivity(intent);

            }
        });
        TextView txtSName = dialog.findViewById(R.id.txtSName);
        TextView txtSAge = dialog.findViewById(R.id.txtSAge);
        TextView txtSMobile = dialog.findViewById(R.id.txtMobileNum);
        TextView txtSIntro = dialog.findViewById(R.id.txtSIntro);
        ImageView imgSitter = dialog.findViewById(R.id.imgSitter);
        txtSName.setText(sitterName);
        txtSAge.setText(sitterAge);
        txtSMobile.setText(sitterMobile);
        txtSIntro.setText(sitterIntro);
        imgSitter.setImageResource(id);

    }*/

    private void showBottomDialog(String sitterName, String sitterAge, String sitterMobile, String sitterIntro, Integer id) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_sitterinfo);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        Button btnCall = dialog.findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtMobileNum = dialog.findViewById(R.id.txtMobileNum);
                String number = txtMobileNum.getText().toString();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + number));
                mContext.startActivity(intent);

            }
        });
        TextView txtSName = dialog.findViewById(R.id.txtSName);
        TextView txtSAge = dialog.findViewById(R.id.txtSAge);
        TextView txtSMobile = dialog.findViewById(R.id.txtMobileNum);
        TextView txtSIntro = dialog.findViewById(R.id.txtSIntro);
        ImageView imgSitter = dialog.findViewById(R.id.imgSitter);
        txtSName.setText(sitterName);
        txtSAge.setText(sitterAge);
        txtSMobile.setText(sitterMobile);
        txtSIntro.setText(sitterIntro);
        imgSitter.setImageResource(id);
    }

    @Override
    public int getItemCount() {
        return sitter.size();
    }

    public class SitterViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtAge,txtMobile, txtIntro;
        ImageView imgcover;
        ImageButton btnNext;
        ImageButton btnBefore;

        public SitterViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.txtName);
            imgcover=itemView.findViewById(R.id.imgCover);
            btnNext = itemView.findViewById(R.id.btnNext);
            btnBefore = itemView.findViewById(R.id.btnBefore);

        }
    }
}
