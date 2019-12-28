package com.example.attendancemanager;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

public class CheckInternet extends BroadcastReceiver {
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    AlertDialog.Builder builder;
    AlertDialog a1;
    @Override
    public void onReceive(final Context context, Intent intent) {
        connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo!=null&&networkInfo.isConnected())
            {
                if(a1!=null)
                    a1.dismiss();
                Toast.makeText(context, "Connected", Toast.LENGTH_LONG).show();
            }
            else
            {
                builder = new AlertDialog.Builder(context);
                builder.setTitle("No Internet Connection!")
                        .setCancelable(false)
                        .setMessage("Your device must be connected to a data network or wifi to access this app.")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                            }
                        });
                a1 = builder.create();
                a1.show();
            }
        }
    }
}
