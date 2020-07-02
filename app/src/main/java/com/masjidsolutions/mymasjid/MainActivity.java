

package com.masjidsolutions.mymasjid;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import com.google.firebase.auth.FirebaseAuth;

import com.masjidsolutions.mymasjid.R.color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.datatype.DatatypeConfigurationException;

import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


import org.joda.time.DateTimeComparator;

import static com.masjidsolutions.mymasjid.NotificationPublisher.NOTIFICATION_ID;
import static com.masjidsolutions.mymasjid.Utilities.*;


interface RetrofitInterface {
    @Streaming
    @GET
    Call<ResponseBody> downloadFileByUrl(@Url String fileUrl);
}

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<CsvSample> csvSamples= new ArrayList<>();
    List<String> nextJammat=null;



//Object that holds Masjid information from the database
    private MasjidInfo masjidobject = null;

    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Deletes the notifications in the current buffer
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();



        TextView fajr1 =  findViewById ( R.id.fajr1 );
        TextView fajr2 =  findViewById ( R.id.fajr2 );
        TextView fajr3 =  findViewById ( R.id.fajr3 );
        TextView zuhr1 =  findViewById ( R.id.zuhr1 );
        TextView zuhr2 =  findViewById ( R.id.zuhr2 );
        TextView zuhr3 =  findViewById ( R.id.zuhr3 );
        TextView asr1  =  findViewById ( R.id.asr1  );
        TextView asr2  =  findViewById ( R.id.asr2  );
        TextView asr3  =  findViewById ( R.id.asr3  );
        TextView maghrib1  =  findViewById ( R.id.maghrib1  );
        TextView maghrib2  =  findViewById ( R.id.maghrib2  );
        TextView maghrib3  =  findViewById ( R.id.maghrib3  );
        TextView isha1  =  findViewById ( R.id.isha1 );
        TextView isha2  =  findViewById ( R.id.isha2 );
        TextView isha3  =  findViewById ( R.id.isha3 );
        TextView khutba1  =  findViewById ( R.id.khutba1 );
        CsvSample today ;
        CsvSample tomorrow;
        Calendar calendar;
        calendar = Calendar.getInstance ();
        calendar.add ( Calendar.DAY_OF_YEAR, 1 );
        Date tomorrowDateObject = calendar.getTime ();
        Date todayDate = new Date();

        Button liveButton =  findViewById( R.id.audioBtn);
        ImageButton refreshButton =  findViewById(R.id.refreshButton);
        ImageButton settingsButton =  findViewById( R.id.settingsBtn );
        Button videoButton = findViewById(R.id.vidbutton);


        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Get the masjid information from the database and assign it to masjid object
        masjidobject = Utilities.getDatabaseData(this);
//        Reads the CSV data from the file
        Utilities.readCsvData (this,csvSamples);
        //Set the toolbar at the top of the screen
        Utilities.setToolbar(this,todayDate);

        //fade in the text
        Utilities.fade_in_text(this);
        //Read the text data
        Utilities.readTxtData(this);
        //initialise the table view
        initTable();

        //handle what happens when the refresh button is pressed
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        //Handle what happens when the settings button is pressed
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
//                startActivity( new Intent( getApplicationContext(), SettingsMenu.class ) );
            }
        });

        //Handle wat happens when the liveAudio button is pressed
        liveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent audioIntent = new Intent( getApplicationContext(),LiveCallToPrayer.class );
                audioIntent.putExtra( "masjidObject" ,masjidobject);
                startActivity(audioIntent);
            }
        });

        //Handle what happens when the live Video button is pressed
        videoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent videoIntent = new Intent(getApplicationContext(),videoActivity.class);
               videoIntent.putExtra( "masjidObject",masjidobject );
                startActivity(videoIntent);

            }
        });



        //set the table data in the table view for today's information
        try {
            today = getPrayerTimes ( csvSamples, todayDate);
            fajr1.setText ( today.getFajr  () );
            fajr2.setText ( today.getFajrJ () );
            zuhr1.setText ( today.getZuhr () );
            zuhr2.setText ( today.getZuhrJ () );
            asr1.setText  ( today.getAsr  () );
            asr2.setText  ( today.getAsrJ  () );
            maghrib1.setText  ( today.getMaghrib  () );
            maghrib2.setText  ( today.getMaghribJ  () );
            isha1.setText  ( today.getIsha  () );
            isha2.setText  ( today.getIshaJ  () );
            khutba1.setText ( "Jummah  " +  today.getKhutbahJ () );


        } catch (ParseException e) {
            e.printStackTrace ();
        }
        // set the table data in the table view for tomorrow's information
        try {
            tomorrow = getPrayerTimes ( csvSamples,tomorrowDateObject );
            fajr3.setText ( tomorrow.getFajrJ () );
            zuhr3.setText ( tomorrow.getZuhrJ () );
            asr3.setText  ( tomorrow.getAsrJ  () );
            maghrib3.setText  ( tomorrow.getMaghribJ  () );
            isha3.setText  ( tomorrow.getIshaJ  () );
        } catch (ParseException e) {
            e.printStackTrace ();
        }


    }



    private  void countdown(final List<String> nextJammat){
        final android.os.Handler handler;
        final TextView countdownLabel =  findViewById(R.id.countdown);
        final Calendar calendar = Calendar.getInstance();
        calendar.add ( Calendar.DAY_OF_YEAR, 1 );

        handler = new android.os.Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                Date eventDate = new Date();

                handler.postDelayed(this, 1000);
                if (nextJammat.get(0).equals("Fajr2")){
                    Date tomorrowDate = calendar.getTime();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.UK);
                    String _tomorrowDate = dateFormat.format(tomorrowDate);
                    dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.UK);
                    try {
                        eventDate = dateFormat.parse(_tomorrowDate + " " + nextJammat.get(1) + ":00");
                    }catch (Exception e){e.printStackTrace();}
                } else{
                    Date todayDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy",Locale.UK);
                    String _currentDate = dateFormat.format(todayDate);
                    dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",Locale.UK);
                    try {
                        eventDate = dateFormat.parse(_currentDate + " " + nextJammat.get(1) + ":00");
                    }catch (Exception e){e.printStackTrace();}
                }
                try {

                    Date currentdate = new Date();

                    if (currentdate.before(eventDate)) {
                        assert eventDate != null;
                        long diff = eventDate.getTime() - currentdate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;


                        String hourString = String.format("%02d", hours);
                        String minString = String.format("%02d", minutes);
                        String secondString = String.format("%02d", seconds);


                        if(nextJammat.get(0).equals("Fajr2")){
                            countdownLabel.setText("Fajr  " + hourString + ":" + minString + ":" + secondString);
                        }else{countdownLabel.setText(nextJammat.get(0) + "  " + hourString + ":" + minString + ":" + secondString);}

                        if (minutes == 0 && seconds == 0 && hours == 0) {
                            initTable();
                            handler.removeCallbacks(this);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        handler.postDelayed(runnable,0);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTable() {
        try {
            nextJammat = getNextJamaat();
            Log.d("nextJammat","NexJammat is :" + nextJammat);
            setBanner(nextJammat);
            countdown(nextJammat);
            List <Notification> notif = Utilities.getNotification(this,nextJammat);
            createReminder(this,notif,nextJammat);
        }catch (Exception e ){
            e.printStackTrace();
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    private boolean checktimings(String time, String endtime) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);
            if(date1.before(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public void setBanner(List<String> nextJammat){
        TableRow row  = null;

        if(nextJammat.get(0).equals("Fajr")){
            row = (TableRow) findViewById(R.id.fajrRow);
            row.setBackgroundResource(R.drawable.nexthighlight);
        } else if (nextJammat.get(0).equals("Zuhr")){
            row = (TableRow) findViewById(R.id.zuhrRow);
            row.setBackgroundResource(R.drawable.nexthighlight);
        }else if (nextJammat.get(0).equals("Asr")){
            row = (TableRow) findViewById(R.id.asrRow);
            row.setBackgroundResource(R.drawable.nexthighlight);
        }else if (nextJammat.get(0).equals("Maghrib")){
            row = (TableRow) findViewById(R.id.maghribRow);
            row.setBackgroundResource(R.drawable.nexthighlight);
        }else if (nextJammat.get(0).equals("Isha")){
            row = (TableRow) findViewById(R.id.ishaRow);
            row.setBackgroundResource(R.drawable.nexthighlight);
        }else{
            row = (TableRow) findViewById(R.id.fajrRow);
            row.setBackgroundResource(R.drawable.nexthighlight);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public List<String> getNextJamaat() throws ParseException {
        CsvSample today= null;
        CsvSample tomorrow = null;
        List<String> nextJammat = new ArrayList<>();
        Date todayObject = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,1);
        Date tomorrowObject = calendar.getTime();


        SimpleDateFormat format = new SimpleDateFormat ( "HH:mm", Locale.UK );
        String todaysDateString = format.format(todayObject);

        try {
            today = getPrayerTimes(csvSamples,todayObject);
            tomorrow = getPrayerTimes(csvSamples,tomorrowObject);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(checktimings(todaysDateString,today.getFajrJ())){

            nextJammat.add("Fajr");
            nextJammat.add(today.getFajr());
            nextJammat.add(today.getFajrJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getZuhrJ())){


            nextJammat.add("Zuhr");
            nextJammat.add(today.getZuhr());
            nextJammat.add(today.getZuhrJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getAsrJ())){

            nextJammat.add("Asr");
            nextJammat.add(today.getAsr());
            nextJammat.add(today.getAsrJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getMaghribJ())){
            nextJammat.add("Maghrib");
            nextJammat.add(today.getMaghrib());
            nextJammat.add(today.getMaghribJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getIshaJ())){
            nextJammat.add("Isha");
            nextJammat.add(today.getIsha());
            nextJammat.add(today.getIshaJ());
            return nextJammat;
        }

        nextJammat.add("Fajr2");
        nextJammat.add(tomorrow.getFajr());
        nextJammat.add(tomorrow.getFajrJ());
        return nextJammat;
    }


    /*
    * Function that takes the CSVSample Array and outputs the correct sample
    * for today's date
    *
    * */



    private CsvSample getPrayerTimes(List<CsvSample> csvData,Date date) throws ParseException {
        CsvSample element;
        int retval;
        int lengthOfCSV = csvData.size();
        //format the date string to MM/dd/yyyy
        SimpleDateFormat format = new SimpleDateFormat ( "MM/dd/yyyy", Locale.UK );
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();


        //Loop over the csvSamples Array and compare dates
        for(int i=0;i<lengthOfCSV;i++){
            element = csvData.get(i);
            Date dateToCheck = format.parse ( element.getDate () );
            retval = dateTimeComparator.compare (date,dateToCheck);
//            If the date in the element selected is equal to todays date then return
            if(retval == 0) {
                return element;
            }
        }
        //if it fails then it'll return the first element
        CsvSample Error = new CsvSample ("");
        return Error;
    }

    public void  logout (View view) {
        FirebaseAuth.getInstance().signOut();
        File timetableFile = new File(getFilesDir ().getAbsolutePath () + "/" + "timetable.csv");
        File messageFile = new File(getFilesDir ().getAbsolutePath () + "/" + "message.txt");
        messageFile.delete();
        timetableFile.delete();
        startActivity(new Intent(getApplicationContext(),Home.class));
        finish();
    }

    }
