/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.masjidsolutions.mymasjid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationBuilderWithBuilderAccessor;
import androidx.core.app.NotificationCompat;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;
import static com.masjidsolutions.mymasjid.NotificationPublisher.NOTIFICATION_ID;

class Utilities {
    private static final String TAG = MainActivity.class.getSimpleName();

    static MasjidInfo  getDatabaseData(final Activity activity){
        final MasjidInfo[] masjidObject = {null};
        final FirebaseFirestore db = getInstance();
        String UID = FirebaseAuth.getInstance().getUid();

        db.collection( "users" ).whereEqualTo( "uid",UID ).get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                                db.collection( "masjidNames" ).whereEqualTo( "name", document.get( "masjid choice" ) )
                                        .get()
                                        .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document2 : Objects.requireNonNull(task.getResult())){
                                                          masjidObject[0] = new MasjidInfo( Objects.requireNonNull(document2.get("name")).toString(), Objects.requireNonNull(document2.get("phonenumber")).toString(), Objects.requireNonNull(document2.get("postcode")).toString(),
                                                                Objects.requireNonNull(document2.get("url")).toString(), Objects.requireNonNull(document2.get("messageurl")).toString(), Objects.requireNonNull(document2.get("streetnumber")).toString(),
                                                                Objects.requireNonNull(document2.get("streetname")).toString(), Objects.requireNonNull(document2.get("videourl")).toString(), Objects.requireNonNull(document2.get("audiourl")).toString() );
                                                        downloadZipFile(activity,"https://www.masjidsolutions.com/"+ masjidObject[0].getCsvurl(),"timetable.csv");
                                                        downloadZipFile(activity,"https://www.masjidsolutions.com/"+ masjidObject[0].getMessageurl(),"message.txt");
                                                        TextView MasjidName = activity.findViewById( R.id.masjidNametxt );
                                                        MasjidName.setText( masjidObject[0].getName() );

                                                    }
                                                }

                                            }
                                        } );
                            }
                        }
                    }
                } );
        return masjidObject[0];
    }

    static private void downloadZipFile(final Activity activity, String fileURL, final String fileName) {
        OkHttpClient httpClient;
        httpClient = new OkHttpClient();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://www.masjidsolutions.com/ms/");
        Retrofit retrofit = builder.client(httpClient).build();
        RetrofitInterface downloadService = retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = downloadService.downloadFileByUrl(fileURL);
        call.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SUCCESS RESPONSE", "Got the body for the file");

                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            saveToDisk(response.body(),fileName,activity);
                            return null;
                        }
                    }.execute();

                } else {
                    Log.d(TAG, "Connection failed " + response.errorBody());
                }
            }



            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, Objects.requireNonNull(t.getMessage()));

            }
        });
    }

    static private void saveToDisk(ResponseBody body, String fileName,Activity activity) {
        try {
            File destinationFile = new File(activity.getFilesDir ().getAbsolutePath () + "/" + fileName);

            InputStream is = null;
            OutputStream os = null;

            try {
                Log.d(TAG, "File Size=" + body.contentLength());

                is = body.byteStream();
                os = new FileOutputStream(destinationFile);

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                    progress +=count;
                    Log.d(TAG, "Progress: " + progress + "/" + body.contentLength() + " >>>> " + (float) progress/body.contentLength());
                }

                os.flush();

                Log.d(TAG, "File saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to save the file!");
            } finally {
                if (is != null) is.close();
                if (os != null) os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void setToolbar(@org.jetbrains.annotations.NotNull MainActivity activity, Date todayDate){
        Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);

        toolbar.getCollapseIcon();
        UmmalquraCalendar cal = new UmmalquraCalendar();
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
        String year = String.valueOf(cal.get(Calendar.YEAR));


        toolbar.setSubtitle(day + " " + month + " " + year);
        toolbar.setSubtitleTextColor(Color.WHITE);
//        dateTimeDisplay = (TextView)findViewById(R.id.dateView);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE dd MMMM ",Locale.UK);
        String todayString = dateFormat.format(todayDate);
        Objects.requireNonNull(activity.getSupportActionBar()).setTitle(todayString);
    }

    static void fade_in_text(final Activity activity) {
        final android.os.Handler handler;

        handler = new android.os.Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                handler.postDelayed(this, 22000);
                final TextView txt = activity.findViewById(R.id.marquee);
                txt.setSelected(true);
                readTxtData(activity);



                AlphaAnimation animationIn = new AlphaAnimation(0.0f,1.0f);
                final AlphaAnimation animationOut = new AlphaAnimation(1.0f,0.0f);
                animationIn.setDuration(3000);
                animationIn.setFillAfter(true);
                animationOut.setDuration(3000);
                animationOut.setFillAfter(true);
                AnimationSet as = new AnimationSet(true);
                as.addAnimation(animationIn);
                animationOut.setStartOffset(10000);
                Animation animFadeIn = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.fade_in);
                Animation animFadeOut = AnimationUtils.loadAnimation(activity.getApplicationContext(),R.anim.fade_out);
                animFadeOut.setStartOffset(10000);
                txt.startAnimation(animFadeIn);
//                txt.startAnimation(animFadeOut);
                animationIn.setAnimationListener(new Animation.AnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        // start animation2 when animation1 ends (continue)
                        txt.startAnimation(animationOut);
                    }

                    @Override
                    public void onAnimationRepeat(Animation arg0) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationStart(Animation arg0) {
                        // TODO Auto-generated method stub

                    }
                });
                txt.startAnimation(animationIn);
            }

        };
        handler.postDelayed(runnable,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void readTxtData(Activity activity) {
        TextView marquee = activity.findViewById(R.id.marquee);
        String path = activity.getFilesDir().getAbsolutePath();
        try {
            InputStream is = activity.openFileInput("message.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8)
            );

            String line = "";
            StringBuilder output = new StringBuilder();
            try {
                //Ignore header row
                while ((line = reader.readLine()) != null) {
                    output.append(line).append('\n');

                    //Split the data by ','
                    // marquee.setText(line);
                }
                marquee.setText(output.toString());
            }catch (Exception e){e.printStackTrace();}
        } catch (Exception e){e.printStackTrace();}
    }

    static void createReminder(Activity activity, List<Notification> notification, List<String> jammatTime ){
        for(int i = 0; i< notification.size(); i++){
            if (i != 0){
                Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
                notificationIntent.putExtra( NOTIFICATION_ID, 1);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification.get(0));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long delay = getTimeDiff(jammatTime,1);
                long futureInMillis = SystemClock.elapsedRealtime() + delay;
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                assert alarmManager != null;
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
            } else {
                Intent notificationIntent = new Intent(activity, NotificationPublisher.class);
                notificationIntent.putExtra( NOTIFICATION_ID, 1);
                notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification.get(1));
                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                long delay = getTimeDiff(jammatTime,2);
                long futureInMillis = SystemClock.elapsedRealtime() + delay - 900000;
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                assert alarmManager != null;
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
            }
        }

    }

    private static long getTimeDiff(List<String> nextJammat,int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add ( Calendar.DATE, 1 );
        String notfTime="";

        Date currentdate = new Date();
        Date tomorrowObject = calendar.getTime();
        Date notifDateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy",Locale.UK);
        String _currentDate = format.format(currentdate);
        String _tomorrowDate = format.format(tomorrowObject);
        if(nextJammat.get(0).equals("Fajr2")){
            notfTime = _tomorrowDate + " " + nextJammat.get(index);
        }else{
            notfTime = _currentDate + " " + nextJammat.get(index);

        }
        try {
            format = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.UK);
            notifDateObj = format.parse(notfTime);
        }catch (Exception e){e.printStackTrace();
        }

        assert notifDateObj != null;
        return notifDateObj.getTime() - currentdate.getTime();
    }

     static List <Notification> getNotification(MainActivity activity,List<String> nextJammat) {
        String channelId = "Prayers";
        Intent notificationIntent = new Intent(activity.getApplicationContext() , LiveCallToPrayer.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(activity.getApplicationContext() , 0,
                notificationIntent, 0);
            NotificationCompat.Builder azaan = null;
            NotificationCompat.Builder jammat = null;

            if (nextJammat.get(0).equals("Fajr2")){
                azaan = new NotificationCompat.Builder(activity,channelId)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(nextJammat.get(0) + "azaan is at "+ nextJammat.get(1) + "\nPress here to hear the Azaan Live"))
                            .setContentText("Fajr Azaan is at "+ nextJammat.get(1) + "\nPress here to hear the Azaan Live.")
                            .setTicker(activity.getString(R.string.app_name))
                            .setContentIntent( intent )
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true);

                jammat = new NotificationCompat.Builder(activity,channelId)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(nextJammat.get(0) + " is at "+ nextJammat.get(2) + "\nPress Here To View All Prayer Times."))
                            .setContentText("Fajr Jammat is at "+ nextJammat.get(1) + "\nPress Here To View All Prayer Times.")
                            .setTicker(activity.getString(R.string.app_name))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true);
            }else{
                azaan = new NotificationCompat.Builder(activity, channelId)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(nextJammat.get(0) + " is at "+ nextJammat.get(1) + "\nView All Prayer Times."))
                            .setContentText(nextJammat.get(0) + " azaan is at "+ nextJammat.get(1) + "\nPress here to hear the Azaan Live.")
                            .setTicker(activity.getString(R.string.app_name))
                            .setContentIntent(intent)
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true);

                jammat = new NotificationCompat.Builder(activity,channelId)
                            .setContentTitle(activity.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(nextJammat.get(0) + " is at "+ nextJammat.get(2) + "\nPress Here To View All Prayer Times."))
                            .setContentText(nextJammat.get(0) + " azaan is at "+ nextJammat.get(2) + "\nPress Here To View All Prayer Times.")
                            .setTicker(activity.getString(R.string.app_name))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true);

            }

            List<Notification> notifications = new ArrayList<Notification>();
            notifications.add(azaan.build());
            notifications.add(jammat.build());

        return notifications;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void readCsvData(Activity activity, List<CsvSample> csvSamples) {
        String path = activity.getFilesDir ().getAbsolutePath ();
        path = path + "/timetable.csv";
        try {
            InputStream is = activity.openFileInput( "timetable.csv" );
            BufferedReader reader = new BufferedReader (
                    new InputStreamReader (is, StandardCharsets.UTF_8)
            );

            String line = "";
            try {
                //Ignore header row
                reader.readLine ();
                while ( (line = reader.readLine()) != null) {

                    //Split the data by ','
                    String[] tokens = line.split (",");
                    //Read the data
                    CsvSample sample = new CsvSample (tokens[0],tokens[1],tokens[2],tokens[3],tokens[4],tokens[5],tokens[6],tokens[7],
                            tokens[8],tokens[9],tokens[10],tokens[11],tokens[12],tokens[13],tokens[14]);

                    csvSamples.add ( sample );
                }

            } catch (IOException e) {
                Log.wtf ( "Read Error","error reading data on line" + line,e );
            }
            DatatypeConfigurationException e;
            e = new DatatypeConfigurationException();
            e.printStackTrace ();
        }catch (FileNotFoundException e) {
            e.printStackTrace ();
        }
    }







}
