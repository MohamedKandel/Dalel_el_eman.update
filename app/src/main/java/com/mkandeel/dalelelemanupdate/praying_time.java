package com.mkandeel.dalelelemanupdate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mkandeel.dalelelemanupdate.HelperClasses.AlarmService;
import com.mkandeel.dalelelemanupdate.HelperClasses.DBConnection;
import com.mkandeel.dalelelemanupdate.HelperClasses.State;
import com.mkandeel.dalelelemanupdate.HelperClasses.Time;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class praying_time extends AppCompatActivity {

    private TextView txt_day, txt_hijri, txt_remain, txt_salah, txt_date,
            txt_pt_main, txt_fajr_ar, txt_fajr_en, txt_sun_ar, txt_sun_en,
            txt_duhr_ar, txt_duhr_en, txt_asr_ar, txt_asr_en, txt_mag_en, txt_mag_ar,
            txt_isha_en, txt_isha_ar, txt_fajr, txt_sun, txt_duhr, txt_asr, txt_mag, txt_isha;
    private Button btn_main;
    private TextClock txt_clock;
    private LinearLayout main_layout, comp_layout;
    private Tools tools;
    private State state;
    private Context context;
    private DBConnection connection;
    private List<String> times;
    private String CountryCity, URL, Day;
    private RequestQueue request;
    private WifiManager wifiManager;

    private static long START_TIME_IN_MILLIS;
    private CountDownTimer timer;
    private boolean isTimerRunning;
    private long Time_Left;
    //private Time[] time;
    private ArrayList<Time> time;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_praying_time);
        context = this;

        connection = new DBConnection(this);
        //DBConnection.getInstance(this);

        tools = Tools.getInstance(context);
        state = State.getInstance(context);
        boolean isDark = state.getState();
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        boolean isAvailable = tools.isPackageAvailable("com.example.firstproject");
        //old version found
        if (isAvailable) {
            Log.d("Note","Application found");
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("تحديث تطبيق دليل الإيمان");
            builder.setMessage("تم العثور على اصدار قديم من تطبيق دليل الايمان التي تم ايقاف الدعم عنه\nهل تريد حذف الاصدار القديم");
            builder.setPositiveButton("نعم احذفها", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent uninstall = new Intent(Intent.ACTION_DELETE);
                    uninstall.setData(Uri.parse("package:com.example.firstproject"));
                    startActivity(uninstall);
                    dialog.dismiss();
                    //PackageInstaller installer = context.getPackageManager().getPackageInstaller();
                }
            });
            builder.setNegativeButton("لا اريد حذفها", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        //will be true if activity opened from notification clicked
        /*boolean clicked = getIntent().getBooleanExtra("clicked", false);
        if (clicked) {
            if (tools.mp != null) {
                if (tools.mp.isPlaying()) {
                    tools.mp.stop();
                    tools.mp.release();
                }
            }
        }*/

        inflates();

        // from 5:00 AM to 5:00 PM
        if (tools.InBetweenTime("05:00", "17:00", tools.GetTime())) {
            main_layout.setBackgroundResource(R.drawable.sunrise);
            tools.Change_Color("brown_sunrise", txt_clock, txt_day, txt_hijri, txt_date,
                    txt_salah, txt_remain);
        } else {
            main_layout.setBackgroundResource(R.drawable.night);
            tools.Change_Color("brown_night", txt_clock, txt_day, txt_hijri, txt_date,
                    txt_salah, txt_remain);
        }

        txt_day.setText(" , " + tools.GetDay());
        txt_date.setText(tools.GetDate());
        tools.ChangeFont("aldhabi.ttf", txt_pt_main);
        tools.ChangeButtonFont("AdobeArabic.otf", btn_main);
        tools.ChangeFont("AdobeNaskh.otf", txt_fajr_ar, txt_fajr_en, txt_fajr,
                txt_sun, txt_sun_ar, txt_sun_en, txt_duhr, txt_duhr_ar, txt_duhr_en,
                txt_asr, txt_asr_ar, txt_asr_en, txt_mag, txt_mag_ar, txt_mag_en,
                txt_isha, txt_isha_ar, txt_isha_en);
        tools.ChangeFont("calibri.ttf", txt_day, txt_hijri, txt_remain, txt_date);

        if(isNetworkAvailable()) {
            request = Volley.newRequestQueue(this);
        } else {
            tools.Message("عذرًا .. لا يوجد اتصال بالانترنت ، تأكد من اتصال هاتفك بالانترنت لتتمكن من معرفة اوقات الصلاة");
        }
        CountryCity = connection.GetCountryAndCity();
        String[] arr = CountryCity.split("\n");

        times = connection.GetTimes();


        //if DB found in local storage
        if (times.size() > 0) {
            /*if (tools.isMyServiceRunning(AlarmService.class)) {
                Intent intent = new Intent(praying_time.this, AlarmService.class);
                stopService(intent);
            }*/
            //Get Current Date (Today)
            String DBDate = times.get(0);

            List<String> times_Copy = new ArrayList<>(times);
            times_Copy.remove(0);       //remove date
            times_Copy.remove(2);       //remove sunrise
            times_Copy.remove(0);       //remove hijri

            //if user change his location
            if (connection.GetCountryAndCity().equals(connection.GetCountryCityFromPT())) {
                //if GetNext method Return ""
                if (tools.GetNext(tools.GetTime(), times_Copy).trim().equals("")) {
                    //Fetch Data for Tomorrow from API and send update as true
                    //use UpdateRemaining to claculate remaining from new time list
                    Day = tools.Today(false);

                    Log.d("Note Day", times.get(0));
                    Log.d("Note Day Function", Day);

                    if (times.get(0).equals(Day)) {
                        times_Copy = new ArrayList<>(times);
                        times_Copy.remove(0);       //remove date
                        times_Copy.remove(2);       //remove sunrise
                        times_Copy.remove(0);       //remove hijri
                        setNextAndRemain(times_Copy);

                        txt_hijri.setText(tools.FormatHijri(times.get(1)));
                        txt_fajr.setText(tools.Format(times.get(2)));
                        txt_sun.setText(tools.Format(times.get(3)));
                        txt_duhr.setText(tools.Format(times.get(4)));
                        txt_asr.setText(tools.Format(times.get(5)));
                        txt_mag.setText(tools.Format(times.get(6)));
                        txt_isha.setText(tools.Format(times.get(7)));

                        /*Intent intent = new Intent(praying_time.this, AlarmService.class);
                        stopService(intent);*/
                        time = new ArrayList<>();

                        for (int i = 0; i < times_Copy.size(); i++) {
                            time.add(new Time(Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[0]),
                                    Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[1]), 0));
                        }

                        mtimes = new ArrayList<>(time);

                        if (!tools.isMyServiceRunning(AlarmService.class)) {
                            Intent intent = new Intent(praying_time.this, AlarmService.class);
                            //intent.putExtra("time", time);
                            //startService(intent);
                            ContextCompat.startForegroundService(this, intent);
                        }

                        updateTextView();

                        Log.d("Note", "From DB(TOMORROW)");
                        tools.Message("From DB(TOMORROW)");

                    } else {
                        txt_hijri.setText(tools.FormatHijri(times.get(1)));
                        URL = "http://api.aladhan.com/v1/timingsByCity/" + Day + "?country="
                                + arr[0] + "&city=" + arr[1] + "&method=" + state.getMethod();
                        JSONParse(URL, Day, true);

                        tools.Message("From API (TOMORROW)");
                        Log.d("Note", "From API (TOMORROW)");
                    }
                }
                //if it return time
                else {
                    //if current Date = Date in local storage
                    if (tools.Today(true).trim().equals(DBDate)) {
                        Day = tools.Today(true);
                        Log.d("Note Day", times.get(0));
                        Log.d("Note Day Function", Day);

                        //Display times from DB
                        times_Copy = new ArrayList<>(times);
                        times_Copy.remove(0);       //remove date
                        times_Copy.remove(2);       //remove sunrise
                        times_Copy.remove(0);       //remove hijri
                        setNextAndRemain(times_Copy);

                        txt_hijri.setText(tools.FormatHijri(times.get(1)));
                        txt_fajr.setText(tools.Format(times.get(2)));
                        txt_sun.setText(tools.Format(times.get(3)));
                        txt_duhr.setText(tools.Format(times.get(4)));
                        txt_asr.setText(tools.Format(times.get(5)));
                        txt_mag.setText(tools.Format(times.get(6)));
                        txt_isha.setText(tools.Format(times.get(7)));

                        /*Intent intent = new Intent(praying_time.this, AlarmService.class);
                        stopService(intent);*/

                        time = new ArrayList<>();

                        for (int i = 0; i < times_Copy.size(); i++) {
                            time.add(new Time(Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[0]),
                                    Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[1]), 0));
                        }
                        mtimes = new ArrayList<>(time);
                        if (!tools.isMyServiceRunning(AlarmService.class)) {
                            Intent intent = new Intent(praying_time.this, AlarmService.class);
                            //intent.putExtra("time", time);
                            //startService(intent);
                            ContextCompat.startForegroundService(this, intent);
                        }

                        updateTextView();

                        tools.Message("From DB (TODAY)");
                        Log.d("Note", "From DB (TODAY)");
                    }
                    //if Tomorrow = Date in local storage
                    else if (tools.Today(false).equals(DBDate)) {
                        //Display times from DB
                        Day = tools.Today(false);
                        Log.d("Note Day", times.get(0));
                        Log.d("Note Day Function", Day);

                        times_Copy = new ArrayList<>(times);
                        times_Copy.remove(0);       //remove date
                        times_Copy.remove(2);       //remove sunrise
                        times_Copy.remove(0);       //remove hijri
                        setNextAndRemain(times_Copy);

                        txt_hijri.setText(tools.FormatHijri(times.get(1)));
                        txt_fajr.setText(tools.Format(times.get(2)));
                        txt_sun.setText(tools.Format(times.get(3)));
                        txt_duhr.setText(tools.Format(times.get(4)));
                        txt_asr.setText(tools.Format(times.get(5)));
                        txt_mag.setText(tools.Format(times.get(6)));
                        txt_isha.setText(tools.Format(times.get(7)));

                        /*Intent intent = new Intent(praying_time.this, AlarmService.class);
                        stopService(intent);*/

                        time = new ArrayList<>();

                        for (int i = 0; i < times_Copy.size(); i++) {
                            time.add(new Time(Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[0]),
                                    Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[1]), 0));
                        }

                        mtimes = new ArrayList<>(time);

                        if (!tools.isMyServiceRunning(AlarmService.class)) {
                            Intent intent = new Intent(praying_time.this, AlarmService.class);
                            //intent.putExtra("time", time);
                            ContextCompat.startForegroundService(this, intent);
                        }

                        updateTextView();

                        tools.Message("From DB (TOMORROW)");
                        Log.d("Note", "From DB (TOMORROW)");
                    }
                    //else fetch new Data from API with Date and send update as false
                    else {
                        Day = tools.Today(true);

                        URL = "http://api.aladhan.com/v1/timingsByCity/" + Day + "?country="
                                + arr[0] + "&city=" + arr[1] + "&method=" + state.getMethod();
                        JSONParse(URL, Day, false);

                        tools.Message("More days before opening application");
                        Log.d("Note", "More days before opening application");
                    }
                }
            } else {
                Day = tools.Today(true);
                URL = "http://api.aladhan.com/v1/timingsByCity/" + Day + "?country="
                        + arr[0] + "&city=" + arr[1] + "&method=" + state.getMethod();
                JSONParse(URL, Day, false);
                tools.Message("no DB found");
                Log.d("Note", "Change Location");
            }
        }
        //if no DB found (call first time application opened only)
        else {
            //Fetch Data from API and Set Date as Today
            Day = tools.Today(true);

            URL = "http://api.aladhan.com/v1/timingsByCity/" + Day + "?country="
                    + arr[0] + "&city=" + arr[1] + "&method=" + state.getMethod();

            JSONParse(URL, Day, false);
            tools.Message("no DB found");
            Log.d("Note", "no DB found");
        }

        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(praying_time.this, Home_Activity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        comp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(praying_time.this, Tarteeb.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    private static List<Time> mtimes;

    public static List<Time> getTimes() {
        return mtimes;
    }

    private void JSONParse(String url, String Date, boolean update) {
        boolean activeNetwork = isNetworkAvailable();
        if (activeNetwork) {
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject timing = data.getJSONObject("timings");
                        String fajr = timing.getString("Fajr");
                        String sun = timing.getString("Sunrise");
                        String duhr = timing.getString("Dhuhr");
                        String asr = timing.getString("Asr");
                        String mag = timing.getString("Maghrib");
                        String isha = timing.getString("Isha");
                        ///////////////////////////////////////////////
                        txt_fajr.setText(tools.Format(fajr));
                        txt_sun.setText(tools.Format(sun));
                        txt_duhr.setText(tools.Format(duhr));
                        txt_asr.setText(tools.Format(asr));
                        txt_mag.setText(tools.Format(mag));
                        txt_isha.setText(tools.Format(isha));
                        ///////////////////////////////////////////////
                        //insert timings to database
                        if (!update) {
                            JSONObject date = data.getJSONObject("date");
                            JSONObject hijri = date.getJSONObject("hijri");
                            String hijri_date = hijri.getString("date");

                            txt_hijri.setText(tools.FormatHijri(hijri_date));

                            if (connection.GetTimes().size() > 0) {
                                connection.DeleteTimes();
                            }
                            connection.InsertPraying(1, Date, hijri_date, fajr, sun,
                                    duhr, asr, mag, isha, CountryCity.split("\n")[0],
                                    CountryCity.split("\n")[1]);
                            Log.d("Note", "Delete and insert (or insert new)");

                        } else if (update) {
                            connection.UpdateTimes(1, Date, fajr, sun, duhr, asr, mag, isha);
                            Log.d("Note", "Update");
                        }

                        times = connection.GetTimes();
                        List<String> times_Copy = new ArrayList<>(times);
                        times_Copy.remove(0);       //remove date
                        times_Copy.remove(2);       //remove sunrise
                        times_Copy.remove(0);       //remove hijri

                        setNextAndRemain(times_Copy);
                        if (!tools.UpdateRemain(tools.GetTime(), times_Copy).equals("Tomorrow")) {
                            time = new ArrayList<>();
                            for (int i = 0; i < times_Copy.size(); i++) {
                                time.add(new Time(Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[0]),
                                        Integer.parseInt(tools.GetHoursAndMin(times_Copy.get(i))[1]), 0));
                            }

                            mtimes = new ArrayList<>(time);
                            Intent intent = new Intent(praying_time.this, AlarmService.class);
                            intent.putExtra("update",update);
                            ContextCompat.startForegroundService(praying_time.this, intent);
                            //if (!tools.isMyServiceRunning(AlarmService.class)) {
                            //intent.putExtra("time", time);
                            //}
                            updateTextView();
                        }

                    } catch (Exception e) {
                        tools.Message(e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null) {
                        if (error.getMessage().contains("megaplusredirection.tedata.net")) {
                            tools.Message("Restart your router or use mobile data");
                        } else {
                            tools.Message(error.getMessage());
                        }
                    } else {
                        tools.Message("Error in loading data");
                    }
                }
            });
            request.add(objectRequest);
        } else {
            tools.Message("عذرًا لا يوجد اتصال بالانترنت ... تأكد من اتصالك بالانترنت");
        }
    }

    private void inflates() {
        txt_date = findViewById(R.id.txt_date);
        txt_day = findViewById(R.id.txt_day);
        txt_hijri = findViewById(R.id.txt_hijri);
        txt_remain = findViewById(R.id.txt_remain);
        txt_salah = findViewById(R.id.txt_salah_name);
        txt_clock = findViewById(R.id.textclock);
        txt_pt_main = findViewById(R.id.txt_pt_main);

        txt_fajr_en = findViewById(R.id.fajr_en);
        txt_fajr = findViewById(R.id.fajr_time);
        txt_fajr_ar = findViewById(R.id.fajr_ar);

        txt_sun_en = findViewById(R.id.sun_en);
        txt_sun = findViewById(R.id.sun_time);
        txt_sun_ar = findViewById(R.id.sun_ar);

        txt_duhr_en = findViewById(R.id.Duhr_en);
        txt_duhr = findViewById(R.id.duhr_time);
        txt_duhr_ar = findViewById(R.id.duhr_ar);

        txt_asr_en = findViewById(R.id.asr_en);
        txt_asr = findViewById(R.id.asr_time);
        txt_asr_ar = findViewById(R.id.asr_ar);

        txt_mag_en = findViewById(R.id.mag_en);
        txt_mag = findViewById(R.id.mag_time);
        txt_mag_ar = findViewById(R.id.mag_ar);

        txt_isha_en = findViewById(R.id.isha_en);
        txt_isha = findViewById(R.id.isha_time);
        txt_isha_ar = findViewById(R.id.isha_ar);

        main_layout = findViewById(R.id.main_layout);
        comp_layout = findViewById(R.id.comp);

        btn_main = findViewById(R.id.btn_main);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network nw = connectivityManager.getActiveNetwork();
            if (nw == null) return false;
            NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
            return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
        } else {
            NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
            return nwInfo != null && nwInfo.isConnected();
        }
    }

    //use this method to set Next and remaining time
    private void setNextAndRemain(List<String> times_Copy) {
        String rem = tools.UpdateRemain(tools.GetTime(), times_Copy);
        txt_remain.setText(rem);

        START_TIME_IN_MILLIS = tools.GetMillis(tools.UpdateRemain(tools.GetTime(), times_Copy));
        Time_Left = START_TIME_IN_MILLIS;

        startTimer();

        String next = tools.GetNext(tools.GetTime(), times_Copy);
        if (next.trim().equals("")) {
            txt_salah.setText("Remaining for Fajr prayer");
        } else {
            int index = times_Copy.indexOf(next);
            switch (index) {
                case 0:     //fajr
                    txt_salah.setText("Remaining for Fajr prayer");
                    break;
                case 1:     //duhr
                    txt_salah.setText("Remaining for Duhur prayer");
                    break;
                case 2:     //asr
                    txt_salah.setText("Remaining for Asr prayer");
                    break;
                case 3:     //maghrib
                    txt_salah.setText("Remaining for Maghrib prayer");
                    break;
                case 4:     //isha
                    txt_salah.setText("Remaining for Isha prayer");
                    break;
            }
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(Time_Left, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Time_Left = millisUntilFinished;
                updateTextView();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
            }
        }.start();
        isTimerRunning = true;
    }

    private void updateTextView() {
        int minute = (int) (Time_Left / 1000) / (60);
        int seconds = (int) (Time_Left / 1000) % 60;
        String temp = String.format("%02d : %02d", minute, seconds);
        String[] arr = temp.split(" : ");
        String Hours_Min = GetHours_Min(Integer.parseInt(arr[0]));
        String time = Hours_Min + " : " + arr[1];
        txt_remain.setText(time);
    }

    private String GetHours_Min(int minutes) {
        int hours = minutes / 60;
        int minute = minutes % 60;
        String time = String.format("%02d : %02d", hours, minute);
        return time;
    }
}
