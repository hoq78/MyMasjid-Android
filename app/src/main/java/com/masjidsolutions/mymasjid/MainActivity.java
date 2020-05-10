

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
import android.net.Uri;
import android.os.AsyncTask;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.masjidsolutions.mymasjid.R.color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import java.net.URL;
import javax.xml.datatype.DatatypeConfigurationException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import android.icu.util.IslamicCalendar;
import com.jakewharton.threetenabp.AndroidThreeTen;

import org.joda.time.DateTimeComparator;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;
import static com.masjidsolutions.mymasjid.NotificationPublisher.NOTIFICATION_ID;


interface RetrofitInterface {
    @Streaming
    @GET
    Call<ResponseBody> downloadFileByUrl(@Url String fileUrl);
}

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<CsvSample> csvSamples= new ArrayList<>();
    private TextView dateTimeDisplay;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String date;
    List<String> nextJammat=null;
    List<String> playAdhan=null;
    TextView textView ;
    URL url ;
    String TextHolder = "" , TextHolder2 = "";
    BufferedReader bufferReader ;


//Object that holds Masjid information from the database

    private MasjidInfo masjidobject = null;

    private void getDatabaseData(){
        final FirebaseFirestore db = getInstance();
        String UID = FirebaseAuth.getInstance().getUid();

        db.collection( "users" ).whereEqualTo( "uid",UID ).get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                db.collection( "masjidNames" ).whereEqualTo( "name", document.get( "masjid choice" ) )
                                        .get()
                                        .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot document2 : task.getResult()){
                                                        masjidobject = new MasjidInfo( document2.get( "name" ).toString(), document2.get( "phonenumber" ).toString(), document2.get( "postcode" ).toString(),
                                                                document2.get( "url" ).toString(), document2.get( "messageurl" ).toString(), document2.get( "streetnumber" ).toString(),
                                                                document2.get( "streetname" ).toString(), document2.get( "videourl" ).toString(), document2.get( "audiourl" ).toString() );
                                                        downloadZipFile("https://www.masjidsolutions.com/"+masjidobject.getCsvurl(),"timetable.csv");
                                                        downloadZipFile("https://www.masjidsolutions.com/"+masjidobject.getMessageurl(),"message.txt");
                                                        TextView MasjidName = findViewById( R.id.masjidNametxt );
                                                        MasjidName.setText( masjidobject.getName() );
                                                    }
                                                }

                                            }
                                        } );
                            }
                        }
                    }
                } );
    }


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getDatabaseData();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        readCsvData ();

        CsvSample today = null;
        CsvSample tomorrow = null;
        Calendar calendar;
        calendar = Calendar.getInstance ();
        calendar.add ( Calendar.DAY_OF_YEAR, 1 );
        Date tomorrowDateObject = calendar.getTime ();
        String path = getFilesDir ().getAbsolutePath ();
        Date todayDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String todayStr = formatter.format(todayDate);

        TextView fajr1 = (TextView) findViewById ( R.id.fajr1 );
        TextView fajr2 = (TextView) findViewById ( R.id.fajr2 );
        TextView fajr3 = (TextView) findViewById ( R.id.fajr3 );
        TextView zuhr1 = (TextView) findViewById ( R.id.zuhr1 );
        TextView zuhr2 = (TextView) findViewById ( R.id.zuhr2 );
        TextView zuhr3 = (TextView) findViewById ( R.id.zuhr3 );
        TextView asr1  = (TextView) findViewById ( R.id.asr1  );
        TextView asr2  = (TextView) findViewById ( R.id.asr2  );
        TextView asr3  = (TextView) findViewById ( R.id.asr3  );
        TextView maghrib1  = (TextView) findViewById ( R.id.maghrib1  );
        TextView maghrib2  = (TextView) findViewById ( R.id.maghrib2  );
        TextView maghrib3  = (TextView) findViewById ( R.id.maghrib3  );
        TextView isha1  = (TextView) findViewById ( R.id.isha1 );
        TextView isha2  = (TextView) findViewById ( R.id.isha2 );
        TextView isha3  = (TextView) findViewById ( R.id.isha3 );
        TextView khutba1  = (TextView) findViewById ( R.id.khutba1 );




        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.getCollapseIcon();
        UmmalquraCalendar cal = new UmmalquraCalendar();
        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        String month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.UK);
        String year = String.valueOf(cal.get(Calendar.YEAR));


        toolbar.setSubtitle(day + " " + month + " " + year);
        toolbar.setSubtitleTextColor(Color.WHITE);
//        dateTimeDisplay = (TextView)findViewById(R.id.dateView);
        dateFormat = new SimpleDateFormat("EEEE dd MMMM ");
        String todayString = dateFormat.format(todayDate);
        getSupportActionBar().setTitle(todayString);

        Button button1;
        fade_in_text();
        readTxtData();
        initTable();

        ImageButton imgbutton = (ImageButton) findViewById(R.id.refreshButton);
        imgbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        ImageButton settingsButton = (ImageButton) findViewById( R.id.settingsBtn );
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
//                startActivity( new Intent( getApplicationContext(), SettingsMenu.class ) );
            }
        });

        Button livebutton = (Button) findViewById( R.id.audioBtn);
        livebutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent audioIntent = new Intent( getApplicationContext(),LiveCallToPrayer.class );
                audioIntent.putExtra( "masjidObject" ,masjidobject);
                startActivity(audioIntent);
            }
        });


        Button button = (Button) findViewById(R.id.vidbutton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
                Intent videoIntent = new Intent(getApplicationContext(),videoActivity.class);
               videoIntent.putExtra( "masjidObject",masjidobject );
                startActivity(videoIntent);

            }
        });




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


    private void fade_in_text() {
        final android.os.Handler handler;

        handler = new android.os.Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 22000);
                final TextView txt = findViewById(R.id.marquee);
                txt.setSelected(true);
                readTxtData();



                AlphaAnimation animationIn = new AlphaAnimation(0.0f,1.0f);
                final AlphaAnimation animationOut = new AlphaAnimation(1.0f,0.0f);
                animationIn.setDuration(3000);
                animationIn.setFillAfter(true);
                animationOut.setDuration(3000);
                animationOut.setFillAfter(true);
                AnimationSet as = new AnimationSet(true);
                as.addAnimation(animationIn);
                animationOut.setStartOffset(10000);
                Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
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

    private  void countdown(final List<String> nextJammat){
        final android.os.Handler handler;
        final TextView countdownLabel = (TextView) findViewById(R.id.countdown);
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
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String _tomorrowDate = dateFormat.format(tomorrowDate);
                    dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    try {
                        eventDate = dateFormat.parse(_tomorrowDate + " " + nextJammat.get(1) + ":00");
                    }catch (Exception e){e.printStackTrace();}
                } else{
                    Date todayDate = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String _currentDate = dateFormat.format(todayDate);
                    dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    try {
                        eventDate = dateFormat.parse(_currentDate + " " + nextJammat.get(1) + ":00");
                    }catch (Exception e){e.printStackTrace();}
                }

                try {

                    Date currentdate = new Date();

                    if (currentdate.before(eventDate)) {
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
            setBanner(nextJammat);
            countdown(nextJammat);
            Notification notif = getNotification(nextJammat);
            createReminder(notif,nextJammat);
        }catch (Exception e ){
            e.printStackTrace();
        }

    }

    public long getTimeDiff( List<String> nextJammat) {
        Calendar calendar = Calendar.getInstance();
        calendar.add ( Calendar.DATE, 1 );
        String notfTime="";

        Date currentdate = new Date();
        Date tomorrowObject = calendar.getTime();
        Date notifDateObj = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String _currentDate = format.format(currentdate);
        String _tomorrowDate = format.format(tomorrowObject);
        if(nextJammat.get(0).equals("Fajr2")){
            notfTime = _tomorrowDate + " " + nextJammat.get(1);
        }else{
            notfTime = _currentDate + " " + nextJammat.get(1);

        }
        try {
            format = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            notifDateObj = format.parse(notfTime);
        }catch (Exception e){e.printStackTrace();
        }

        long diffInMills = notifDateObj.getTime() - currentdate.getTime();
        return diffInMills;
    }

    private void createReminder(Notification notification,List<String> jammatTime ){
        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra( NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long delay = getTimeDiff(jammatTime);
        long futureInMillis = SystemClock.elapsedRealtime() + delay - 900000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Notification getNotification(List<String> nextJammat) {
        String channelId = "Prayers";
        Intent notificationIntent = new Intent(getApplicationContext() , LiveCallToPrayer.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(getApplicationContext() , 0,
                notificationIntent, 0);
        NotificationCompat.Builder builder = null;
        if (nextJammat.get(0).equals("Fajr1")){
            builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(nextJammat.get(0) + " is at "+ nextJammat.get(1) + "\nView All Prayer Times."))
                    .setContentText("Fajr is at "+ nextJammat.get(1) + "\nView All Prayer Times.")
                    .setTicker(getString(R.string.app_name))
                    .setContentIntent( intent )
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);
            Log.i(TAG, "notification built");
        } else {
            UsageEvents.Event context = null;
            assert context != null;

            builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(getString(R.string.app_name))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(nextJammat.get(0) + " is at "+ nextJammat.get(1) + "\nView All Prayer Times."))
                    .setContentText(nextJammat.get(0) + " is at "+ nextJammat.get(1) + "\nView All Prayer Times.")
                    .setTicker(getString(R.string.app_name))
                    .setContentIntent(intent)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);
            Log.i(TAG, "notification built");
        }


        return builder.build();
    }

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
            row.setBackgroundResource(color.red);
        } else if (nextJammat.get(0).equals("Zuhr")){
            row = (TableRow) findViewById(R.id.zuhrRow);
            row.setBackgroundResource(color.red);
        }else if (nextJammat.get(0).equals("Asr")){
            row = (TableRow) findViewById(R.id.asrRow);
            row.setBackgroundResource(color.red);
        }else if (nextJammat.get(0).equals("Maghrib")){
            row = (TableRow) findViewById(R.id.maghribRow);
            row.setBackgroundResource(color.red);
        }else if (nextJammat.get(0).equals("Isha")){
            row = (TableRow) findViewById(R.id.ishaRow);
            row.setBackgroundResource(color.red);
        }else{
            row = (TableRow) findViewById(R.id.fajrRow);
            row.setBackgroundResource(color.red);
        }

    }


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
            nextJammat.add(today.getFajrJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getZuhrJ())){


            nextJammat.add("Zuhr");
            nextJammat.add(today.getZuhrJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getAsrJ())){

            nextJammat.add("Asr");
            nextJammat.add(today.getAsrJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getMaghribJ())){
            nextJammat.add("Maghrib");
            nextJammat.add(today.getMaghribJ());
            return nextJammat;
        }else if (checktimings(todaysDateString,today.getIshaJ())){
            nextJammat.add("Isha");
            nextJammat.add(today.getIshaJ());
            return nextJammat;
        }

        nextJammat.add("Fajr2");
        nextJammat.add(tomorrow.getFajrJ());
        return nextJammat;
    }


    /*
    * Function that takes the CSVSample Array and outputs the correct sample
    * for today's date
    *
    * */

    private void readTxtData() {
        TextView marquee = findViewById(R.id.marquee);
        String path = getFilesDir().getAbsolutePath();
        path = path + "/message.txt";
        try {
            InputStream is = openFileInput("message.txt");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, Charset.forName("UTF-8"))
            );

            String line = "";
            String output = "";
            try {
                //Ignore header row
                while ((line = reader.readLine()) != null) {
                    output = output + line + '\n';

                    //Split the data by ','
                   // marquee.setText(line);
                }
                marquee.setText(output);
            }catch (Exception e){e.printStackTrace();}
        } catch (Exception e){e.printStackTrace();}
    }

    private CsvSample getPrayerTimes(List<CsvSample> csvData,Date date) throws ParseException {
        CsvSample element;
        int retval;
        int lengthOfCSV = csvData.size();
        //format the date string to MM/dd/yyyy
        SimpleDateFormat format = new SimpleDateFormat ( "MM/dd/yyyy", Locale.UK );
        String dateobjectString = date.toString ();
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
        CsvSample Error = new CsvSample ();
        return Error;
    }

    private void readCsvData() {
        String path = getFilesDir ().getAbsolutePath ();
        path = path + "/timetable.csv";
        try {
            InputStream is = openFileInput( "timetable.csv" );
            BufferedReader reader = new BufferedReader (
                    new InputStreamReader (is, Charset.forName ( "UTF-8" ) )
            );

            String line = "";
            try {
                //Ignore header row
                reader.readLine ();
                while ( (line = reader.readLine()) != null) {

                    //Split the data by ','
                    String[] tokens = line.split (",");
                    //Read the data
                    CsvSample sample = new CsvSample ();
                    sample.setDate ( tokens[0] );
                    sample.setDay ( tokens[1] );
                    sample.setFajr ( tokens[2] );
                    sample.setSunrise ( tokens[3] );
                    sample.setZuhr ( tokens[4] );
                    sample.setKhutbah(tokens[5]);
                    sample.setAsr ( tokens[6] );
                    sample.setMaghrib ( tokens[7] );
                    sample.setIsha ( tokens[8] );
                    sample.setFajrJ ( tokens[9] );
                    sample.setZuhrJ ( tokens [10] );
                    sample.setAsrJ ( tokens[11] );
                    sample.setMaghribJ ( tokens[12] );
                    sample.setIshaJ ( tokens[13] );
                    sample.setKhutbahJ ( tokens[14] );
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

//        is = getResources ().openRawResource ( path );
//        InputStream is =getResources ().openRawResource ( R.raw.timetable );

        }

    public void downloadZipFile(String fileURL, final String fileName) {
        OkHttpClient httpClient;
        httpClient = new OkHttpClient();
        Retrofit.Builder builder = new Retrofit.Builder().baseUrl("https://www.masjidsolutions.com/ms/");
        Retrofit retrofit = builder.client(httpClient).build();
        RetrofitInterface downloadService = retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> call = downloadService.downloadFileByUrl(fileURL);
        call.enqueue(new Callback<ResponseBody> () {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("SUCCESS RESPONSE", "Got the body for the file");

                    new AsyncTask<Void, Long, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            saveToDisk(response.body(),fileName);
                            return null;
                        }
                    }.execute();

                } else {
                    Log.d(TAG, "Connection failed " + response.errorBody());
                }
            }



            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, t.getMessage());

            }
        });
    }

    public void saveToDisk(ResponseBody body,String fileName) {
        try {
            File destinationFile = new File(getFilesDir ().getAbsolutePath () + "/" + fileName);

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
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (is != null) is.close();
                if (os != null) os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
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
