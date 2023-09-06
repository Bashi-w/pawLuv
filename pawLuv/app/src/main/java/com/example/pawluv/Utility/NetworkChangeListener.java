package com.example.pawluv.Utility;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.pawluv.FallbackActivity;
import com.example.pawluv.R;

public class NetworkChangeListener extends BroadcastReceiver {
    private Button btnRetry;
    private Context context;
    private Intent intent;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        this.intent = intent;
        if(!Common.isConnectedToInternet(context)){
            Intent fallbackIntent = new Intent(context, FallbackActivity.class);
            fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fallbackIntent);

        }
    }

    public void setBtnRetry(Button btnRetry) {
        this.btnRetry = btnRetry;
        this.btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onReceive(context,intent);
            }
        });
    }
}
