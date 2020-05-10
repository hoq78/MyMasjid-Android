
package com.masjidsolutions.mymasjid;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;


public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseAuth fAuth;

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();

        }

        Button buttonSignUP = (Button) findViewById(R.id.sign_up);
        buttonSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegisterActivity();
            }
        });

        Button buttonLogin = (Button) findViewById(R.id.login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLoginActivity();
            }
        });
    }

    public void openRegisterActivity() {
        Intent intent;intent = new Intent(this, Register.class);
        startActivity(intent);

        }


    public void openLoginActivity() {
        Intent intent;intent = new Intent(this, Login.class);
        startActivity(intent);


//        //Download Script
//        Uri uri= Uri.parse("https://www.masjidsolutions.com/ms/msbs/csv/timetable.csv");
//        DownloadManager downloadManager = (DownloadManager) getSystemService( Context.DOWNLOAD_SERVICE);
//        DownloadManager.Request request = new DownloadManager.Request(uri);
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
//                DownloadManager.Request.NETWORK_MOBILE);
//
//// set title and description
//        request.setTitle("Timetable");
//        request.setDescription("Salaat Timetable for your Masjid.");
//
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//
////set the local destination for download file to a path within the application's external files directory
//        request.setDestinationInExternalPublicDir( Environment.DIRECTORY_DOWNLOADS, "timetable.csv" );
//        request.setMimeType("*/*");
//        assert downloadManager != null;
//        downloadManager.enqueue(request);
//        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        registerReceiver(downloadReceiver, filter);
//
//    }
//
//    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
//
//        @Override
//        public void onReceive(final Context context, Intent intent) {
//
////download is finished ...you can write the code for next task here
//            File file = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "timetable.csv");
//        }
    };
}