package com.mkandeel.dalelelemanupdate.HelperClasses;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mkandeel.countrycity.Cities;
import com.mkandeel.countrycity.Countries;
import com.mkandeel.dalelelemanupdate.R;
import com.mkandeel.dalelelemanupdate.praying_time;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

public class Tools {

    private Context context;
    private static Tools tools;
    private static final String channelID = "MyAppChannelID";

    public Tools(Context context) {
        this.context = context;
    }

    public static Tools getInstance(Context context) {
        if (tools == null) {
            tools = new Tools(context);
        }
        return tools;
    }

    private Map<Integer, String> GetMonth() {
        Map<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "Jan");
        map.put(2, "Feb");
        map.put(3, "March");
        map.put(4, "April");
        map.put(5, "May");
        map.put(6, "Jun");
        map.put(7, "Jul");
        map.put(8, "Aug");
        map.put(9, "Sep");
        map.put(10, "Oct");
        map.put(11, "Nov");
        map.put(12, "Dec");
        return map;
    }

    private Map<Integer, ArrayList<String>> FillMethod() {
        Map<Integer, ArrayList<String>> map = new LinkedHashMap<>();

        String[] MWL_arr = {"Albania", "Andorra", "Anguilla", "Antigua And Barbuda", "Argentina", "Armenia",
                "Aruba", "Austria", "Azerbaijan", "Barbados", "Belarus", "Belgium", "Bolivia",
                "Bosnia And Herzegovina", "Brazil", "Bulgaria", "Cambodia", "Chile", "China",
                "Colombia", "Croatia", "Cyprus", "Czech Republic", "Denmark", "Ecuador", "El Salvador",
                "Estonia", "Finland", "France", "Georgia", "Germany", "Gibraltar", "Greece", "Hong Kong",
                "Hungary", "Iceland", "Indonesia", "Ireland", "Italy", "Japan", "Latvia", "Liechtenstein",
                "Lithuania", "Luxembourg", "Macedonia", "Malta", "Moldova", "Monaco", "Mongolia", "Montenegro",
                "Myanmar", "Netherlands", "Nicaragua", "Norway", "Paraguay", "Philippines", "Poland",
                "Portugal", "Romania", "Serbia", "Singapore", "Slovakia", "Slovenia", "Spain", "Sri Lanka",
                "Swaziland", "Sweden", "Switzerland", "Taiwan", "Thailand", "Turkey", "Ukraine", "United Kingdom",
                "Venezuela", "VietNam"};
        ArrayList<String> MWL = new ArrayList<>(Arrays.asList(MWL_arr));

        String[] IS_arr = {"Bahamas", "Canada", "Costa Rica", "Cuba", "Greenland", "Jamaica", "Mexico", "Palau",
                "Panama", "Papua New Guinea", "Peru", "Puerto Rico", "United States"};
        ArrayList<String> ISNA = new ArrayList<>(Arrays.asList(IS_arr));

        String[] EG_arr = {"Algeria", "Angola", "Benin", "Botswana", "Burkina Faso", "Burundi", "Cameroon",
                "Central African Republic", "Chad", "Comoros", "Congo", "Djibouti", "Egypt",
                "Equatorial Guinea", "Eritrea", "Ethiopia", "Gabon", "Gambia", "Ghana", "Kenya", "Lebanon",
                "Liberia", "Madagascar", "Malawi", "Malaysia", "Mali", "Mauritania", "Morocco", "Mozambique",
                "Namibia", "Niger", "Nigeria", "Rwanda", "Senegal", "Somalia", "South Africa", "Sudan",
                "Syrian Arab Republic", "Tanzania", "Togo", "Tonga", "Tunisia", "Uganda", "Uruguay",
                "Western Sahara", "Zambia", "Zimbabwe"};
        ArrayList<String> EGY = new ArrayList<>(Arrays.asList(EG_arr));

        String[] MAK_arr = {"Bahrain", "Iraq", "Jordan", "Kuwait", "Oman", "Qatar", "Saudi Arabia",
                "United Arab Emirates", "Yemen", "Palestine"};
        ArrayList<String> MAKKAH = new ArrayList<>(Arrays.asList(MAK_arr));

        String[] KAR_arr = {"Afghanistan", "Australia", "Bangladesh", "India", "Kazakhstan", "Kyrgyzstan",
                "New Caledonia", "New Zealand", "Pakistan", "Tajikistan", "Turkmenistan", "Uzbekistan"};
        ArrayList<String> KARACHI = new ArrayList<>(Arrays.asList(KAR_arr));

        map.put(1, KARACHI);
        map.put(2, ISNA);
        map.put(3, MWL);
        map.put(4, MAKKAH);
        map.put(5, EGY);

        return map;
    }

    private Long GetMin(ArrayList<Long> list) {
        long min = list.get(0);
        for (long l : list) {
            if (l < min) {
                min = l;
            }
        }
        return min;
    }

    private Map<Integer, Integer> months() {
        Map<Integer, Integer> map = new LinkedHashMap<>();
        map.put(1, 31);
        map.put(2, 28);
        map.put(3, 31);
        map.put(4, 30);
        map.put(5, 31);
        map.put(6, 30);
        map.put(7, 30);
        map.put(8, 31);
        map.put(9, 30);
        map.put(10, 31);
        map.put(11, 30);
        map.put(12, 31);
        return map;
    }

    public String Format(String time) {
        String format = "";
        String[] arr = time.split(":");
        if (Integer.parseInt(arr[0]) > 12) {
            int hour = Integer.parseInt(arr[0]) - 12;
            format = hour + ":" + arr[1] + " PM";
        } else if (Integer.parseInt(arr[0]) == 12) {
            if (Integer.parseInt(arr[1]) > 0) {
                format = arr[0] + ":" + arr[1] + " PM";
            }
        } else {
            format = arr[0] + ":" + arr[1] + " AM";
        }
        return format;
    }

    private String Difference(String now, String next) {
        String[] arr_next = next.split(":");
        String[] arr_now = now.split(":");
        int temp_min, temp_hour;

        if (Integer.parseInt(arr_next[1]) < Integer.parseInt(arr_now[1])) {
            temp_min = Integer.parseInt(arr_next[1]) + 60;
            temp_hour = Integer.parseInt(arr_next[0]) - 1;
        } else {
            temp_min = Integer.parseInt(arr_next[1]);
            temp_hour = Integer.parseInt(arr_next[0]);
        }
        int hours = Math.abs(temp_hour - Integer.parseInt(arr_now[0]));
        int min = Math.abs(temp_min - Integer.parseInt(arr_now[1]));
        String hour, mins;
        if (hours < 10) {
            hour = "0" + hours;
        } else {
            hour = hours + "";
        }
        if (min < 10) {
            mins = "0" + min;
        } else {
            mins = min + "";
        }
        String diff = hour + " : " + mins + " : 00";
        return diff;
    }

    public void Message(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //return "" if no next time and time if there is next time found
    @SuppressLint("SimpleDateFormat")
    public String GetNext(String time, List<String> times,boolean update) {
        String Next = "";
        if (!update) {
            Map<String, Long> diffs = new LinkedHashMap<>();

            long mMillis;
            try {
                Date mdate = new SimpleDateFormat("HH:mm").parse(time);
                assert mdate != null;
                mMillis = mdate.getTime();
                for (String t : times) {
                    Date date = new SimpleDateFormat("HH:mm").parse(t);
                    assert date != null;
                    long millis = date.getTime();
                    long diff = millis - mMillis;

                    diffs.put(t, diff);
                }
            } catch (Exception e) {
                System.out.println("From GetNext " + e.getMessage());
                Log.d("GetNext", Objects.requireNonNull(e.getMessage()));
            }
            ArrayList<Long> list = new ArrayList<>();
            for (Map.Entry<String, Long> entry : diffs.entrySet()) {
                if (entry.getValue() > 0) {
                    list.add(entry.getValue());
                }
            }

            if (list.size() > 0) {
                long min = GetMin(list);
                for (Map.Entry<String, Long> entry : diffs.entrySet()) {
                    if (entry.getValue() == min) {
                        Next = entry.getKey();
                    }
                }
            } else {
                Next = "";
            }
        } else {
            Next = times.get(0);
        }

        return Next;
    }

    //return tomorrow if time after isha prayer
    public String Remain(String time, List<String> times,boolean update) {
        String remain = "Tomorrow";
        String Next = GetNext(time, times,update);
        System.out.println("Next from Remain " + Next);
        try {
            if (Next.trim().equals("")) {
                remain = "Tomorrow";
            } else {
                remain = Difference(time, Next);
            }

        } catch (Exception e) {
            System.out.println("From Remain : " + e.getMessage());
        }
        return remain;
    }

    // calculate difference between current time and TOMORROW Fajr prayer
    public String UpdateRemain(String time, List<String> times,boolean update) {
        String Remain = Remain(time, times,update);
        String r;
        if (Remain.trim().equals("Tomorrow")) {
            List<String> times_2 = new ArrayList<>(times);

            String fajr = times.get(0);
            String[] arr_fajr = fajr.split(":");
            //5:16      20:24
            int temp_hour = Integer.parseInt(arr_fajr[0]) + 24;
            String new_fajr = temp_hour + ":" + arr_fajr[1];
            times_2.set(0, new_fajr);
            //System.out.println(time + "\n" + times);
            r = Difference(time, times_2.get(0));
        } else {
            r = Remain;
        }
        return r;
    }

    //baseURL like: https://server8.mp3quran.net/afs
    //or https://server8.mp3quran.net/afs/
    public ArrayList<String> Generate_URLs(String baseURL) {
        ArrayList<String> list = new ArrayList<>();
        String count, url = "";
        char last = baseURL.charAt(baseURL.length() - 1);
        if (last == '/') {
            for (int i = 1; i <= 114; i++) {
                if (i < 10) {
                    count = "00" + i + ".mp3";
                } else if (i < 100) {
                    count = "0" + i + ".mp3";
                } else {
                    count = "" + i + ".mp3";
                }
                url = baseURL + count;
                list.add(url);
            }
        } else {
            baseURL += "/";
            for (int i = 1; i <= 114; i++) {
                if (i < 10) {
                    count = "00" + i + ".mp3";
                } else if (i < 100) {
                    count = "0" + i + ".mp3";
                } else {
                    count = "" + i + ".mp3";
                }
                url = baseURL + count;
                list.add(url);
            }
        }
        return list;
    }

    public void ChangeFont(String font, TextView... txts) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), font);
        for (TextView txt : txts) {
            txt.setTypeface(face);
        }
    }

    public void ChangeButtonFont(String font, Button... buttons) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), font);
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setTypeface(face);
        }
    }

    public void ChangeEditTextFont(String font, EditText... txts) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), font);
        for (EditText txt : txts) {
            txt.setTypeface(face);
        }
    }

    public void SetHintFont(String font, String[] hint, EditText... txts) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), font);
        TypefaceSpan typefaceSpan = new CustomTypefaceSpan(face);
        if (hint.length == txts.length) {
            for (int i = 0; i < hint.length; i++) {
                SpannableString spannableString = new SpannableString(hint[i]);
                spannableString.setSpan(typefaceSpan, 0, spannableString.length(),
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                txts[i].setHint(spannableString);
            }
        }
    }

    public void ChangeHintColor(String mode, EditText... texts) {
        for (int i = 0; i < texts.length; i++) {
            if (mode.trim().toLowerCase().equals("dark")) {
                texts[i].setHintTextColor(context.getResources().getColor(R.color.Hint_Color_Dark));
                texts[i].setTextColor(context.getResources().getColor(R.color.white));
            } else {
                texts[i].setTextColor(context.getResources().getColor(R.color.Hint_Color_Light));
                texts[i].setTextColor(context.getResources().getColor(R.color.black));
            }
        }
    }

    //return true if any text is empty, false otherwise
    public boolean isEmpty(EditText... texts) {
        boolean flag = true;

        for (int i = 0; i < texts.length; i++) {
            String txt = texts[i].getText().toString();
            if (!txt.trim().equals("")) {
                flag = false;
            } else {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void ClearIfAnyEmpty(EditText... texts) {
        boolean flag = isEmpty(texts);
        if (flag) {
            for (int i = 0; i < texts.length; i++) {
                texts[i].setText("");
            }
        }
        texts[0].requestFocus();
    }

    public void ClearAll(EditText... texts) {
        for (int i = 0; i < texts.length; i++) {
            texts[i].setText("");
        }
        texts[0].requestFocus();
    }

    public void Change_Color(String color, TextView... txt) {
        for (int i = 0; i < txt.length; i++) {
            if (color.trim().toLowerCase().equals("black")) {
                txt[i].setTextColor(context.getResources().getColor(R.color.black));
            } else if (color.trim().toLowerCase().equals("white")) {
                txt[i].setTextColor(context.getResources().getColor(R.color.white));
            } else if (color.trim().toLowerCase().equals("green")) {
                txt[i].setTextColor(context.getResources().getColor(R.color.surah_name_light));
            } else if (color.trim().toLowerCase().equals("brown_night")) {
                txt[i].setTextColor(context.getResources().getColor(R.color.brown_night));
            } else {
                txt[i].setTextColor(context.getResources().getColor(R.color.brown_sunrise));
            }
        }
    }

    public ArrayList<spn_data> initCountry() {
        Countries countries = new Countries();
        countries.getInstance();
        ArrayList<String> list = countries.getAllCountries();
        ArrayList<spn_data> mlist = new ArrayList<>();
        for (String country : list) {
            mlist.add(new spn_data(country));
        }
        return mlist;
    }

    public ArrayList<String> Countries() {
        Countries countries = new Countries();
        countries.getInstance();
        ArrayList<String> list = countries.getAllCountries();
        return list;
    }

    public ArrayList<String> Cities(Context context, String country) {
        Cities cities = new Cities(context);
        cities.getInstance(context);
        ArrayList<String> city = cities.getCitiesByCountryName(country);
        return city;
    }

    public ArrayList<spn_data> initCity(Context context, String country) {
        ArrayList<spn_data> mlist = new ArrayList<>();
        Cities cities = new Cities(context);
        cities.getInstance(context);
        ArrayList<String> city = cities.getCitiesByCountryName(country);
        for (String c : city) {
            mlist.add(new spn_data(c));
        }
        return mlist;
    }

    public String Generate(int size, ArrayList<String> IDs) {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        while (sb.length() < size) {
            int index = random.nextInt(str.length());
            sb.append(str.charAt(index));
        }
        String ID = sb.toString();

        if (IDs.size() > 0) {
            if (IDs.contains(ID))
                Generate(size, IDs);
        }

        return ID;
    }

    public void Copy(String label, String msg) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText(label, msg);
        manager.setPrimaryClip(data);
    }

    //return true if username is valid , false otherwise
    public boolean CheckValidation(ArrayList<String> list, String username) {
        boolean flag = true;
        if (list.size() > 0) {
            if (list.contains(username)) {
                flag = false;
            } else {
                flag = true;
            }
        }
        return flag;
    }

    // date in format DD-MM-YYYY
    public String NextDay(String date) {
        String[] arr = date.split("-");
        Map<Integer, Integer> map = months();
        int months = Integer.parseInt(arr[1]);
        int days = Integer.parseInt(arr[0]);
        int year = Integer.parseInt(arr[2]);
        if (days == 31 && months == 12) {
            year++;
            days = 1;
            months = 1;
        } else {
            int num_of_days = map.get(months);
            if (days < num_of_days) {
                days++;
            } else {
                days = 1;
                months++;
            }
        }
        String next_date = days + "-" + months + "-" + year;
        return next_date;
    }

    @SuppressLint("SimpleDateFormat")
    public String GetDay() {
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        return new SimpleDateFormat("EEE").format(c.getTime());
    }

    @SuppressLint("SimpleDateFormat")
    public String GetDate() {
        String mdate = "";
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String temp = sdf.format(date);
        String[] arr = temp.split("/");
        Map<Integer, String> map = GetMonth();

        mdate = arr[0] + " " + map.get(Integer.parseInt(arr[1])) + " , " + arr[2];
        return mdate;
    }

    public int GetMethod(String country) {
        int method = 0;
        //get all methods from map
        Map<Integer, ArrayList<String>> methods = FillMethod();
        for (Map.Entry<Integer, ArrayList<String>> entry : methods.entrySet()) {
            ArrayList<String> values = entry.getValue();
            if (values.contains(country)) {
                method = entry.getKey();
                break;
            }
        }
        return method;
    }

    @SuppressLint("SimpleDateFormat")
    public String Today(boolean today) {
        String mydate = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        mydate = simpleDateFormat.format(date);

        if (!today) {
            mydate = NextDay(mydate);
        }

        return mydate;
    }

    @SuppressLint("SimpleDateFormat")
    public String GetTime() {
        Date d = new Date();
        return new SimpleDateFormat("HH:mm").format(d);
    }

    private Map<String, String> hijriCaledar() {
        Map<String, String> hijri = new LinkedHashMap<>();
        hijri.put("01", "Muharram");
        hijri.put("02", "Safar");
        hijri.put("03", "Rabi Al Awwal");
        hijri.put("04", "Rabi Al Thani");
        hijri.put("05", "Jumada Al Oula");
        hijri.put("06", "Jumada Al Akhira");
        hijri.put("07", "Rajab");
        hijri.put("08", "Sha'ban");
        hijri.put("09", "Ramadan");
        hijri.put("10", "Shawwal");
        hijri.put("11", "Dhul Qidah");
        hijri.put("12", "Dhul Hijjah");
        return hijri;
    }

    //19-05-1443        {19,05,1443}
    public String FormatHijri(String date) {
        String[] arr = date.split("-");
        Map<String, String> hijri = hijriCaledar();
        String month = hijri.get(arr[1]);
        String mdate = arr[0] + " " + month + " , " + arr[2];
        return mdate;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void ShowNotification(String title, String message, Class<?> acticity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelID, "Dalel el eman",
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        Uri sound = Uri.parse("android.resource://"
                + context.getPackageName() + "/" + R.raw.azan);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID);
        builder.setSmallIcon(R.drawable.ic_azan)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setSound(sound)
                .setAutoCancel(true);

        Intent intent = new Intent(context, acticity);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        //intent.putExtra("clicked", true);
        PendingIntent pi = PendingIntent.getActivity(context, 99,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);

        NotificationManagerCompat mcompact = NotificationManagerCompat.from(context);

        mcompact.notify(99, builder.build());
    }

    public String[] GetHoursAndMin(String text) {
        String time = getnum(text);
        String[] arr = time.split(":");
        return arr;
    }

    private String getnum(String text) {
        char[] chars = text.toCharArray();
        String value = "";
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '0' || chars[i] == '1' || chars[i] == '2' || chars[i] == '3' ||
                    chars[i] == '4' || chars[i] == '5' || chars[i] == '6' || chars[i] == '7' ||
                    chars[i] == '8' || chars[i] == '9' || chars[i] == ':') {
                value += chars[i];
            }
        }
        return value;
    }

    public long GetMillis(String time) {
        String[] arr = time.trim().split(" : ");
        long millis;
        int Hours = Integer.parseInt(arr[0]);
        int minutes = Integer.parseInt(arr[1]);
        int seconds = Integer.parseInt(arr[2]);
        millis = (Hours * 3600000L) + (minutes * 60000L) + (seconds * 1000L);
        return millis;
    }

    public ArrayList<String> Suraah_name() {
        ArrayList<String> list = new ArrayList<>();
        list.add("الفاتحة");
        list.add("البقرة");
        list.add("آلعمران");
        list.add("النساء");
        list.add("المائدة");
        list.add("الأنعام");
        list.add("الأعراف");
        list.add("الأنفال");
        list.add("التوبة");
        list.add("يونس");
        list.add("هود");
        list.add("يوسف");
        list.add("الرعد");
        list.add("إبراهيم");
        list.add("الحجر");
        list.add("النحل");
        list.add("الإسراء");
        list.add("الكهف");
        list.add("مريم");
        list.add("طه");
        list.add("الأنبياء");
        list.add("الحج");
        list.add("المؤمنون");
        list.add("النور");
        list.add("الفرقان");
        list.add("الشعراء");
        list.add("النمل");
        list.add("القصص");
        list.add("العنكبوت");
        list.add("الروم");
        list.add("لقمان");
        list.add("السجدة");
        list.add("الأحزاب");
        list.add("سبأ");
        list.add("فاطر");
        list.add("يس");
        list.add("الصافات");
        list.add("ص");
        list.add("الزمر");
        list.add("غافر");
        list.add("فصلت");
        list.add("الشورى");
        list.add("الزخرف");
        list.add("الدخان");
        list.add("الجاثية");
        list.add("الأحقاف");
        list.add("محمد");
        list.add("الفتح");
        list.add("الحجرات");
        list.add("ق");
        list.add("الذاريات");
        list.add("الطور");
        list.add("النجم");
        list.add("القمر");
        list.add("الرحمن");
        list.add("الواقعة");
        list.add("الحديد");
        list.add("المجادلة");
        list.add("الحشر");
        list.add("الممتحنة");
        list.add("الصف");
        list.add("الجمعة");
        list.add("المنافقون");
        list.add("التغابن");
        list.add("الطلاق");
        list.add("التحريم");
        list.add("الملك");
        list.add("القلم");
        list.add("الحاقة");
        list.add("المعارج");
        list.add("نوح");
        list.add("الجن");
        list.add("المزمل");
        list.add("المدثر");
        list.add("القيامة");
        list.add("الإنسان");
        list.add("المرسلات");
        list.add("النبأ");
        list.add("النازعات");
        list.add("عبس");
        list.add("التكوير");
        list.add("الإنفطار");
        list.add("المطففين");
        list.add("الإنشقاق");
        list.add("البروج");
        list.add("الطارق");
        list.add("الأعلى");
        list.add("الغاشية");
        list.add("الفجر");
        list.add("البلد");
        list.add("الشمس");
        list.add("الليل");
        list.add("الضحى");
        list.add("الشرح");
        list.add("التين");
        list.add("العلق");
        list.add("القدر");
        list.add("البينة");
        list.add("الزلزلة");
        list.add("العاديات");
        list.add("القارعة");
        list.add("التكاثر");
        list.add("العصر");
        list.add("الهمزة");
        list.add("الفيل");
        list.add("قريش");
        list.add("الماعون");
        list.add("الكوثر");
        list.add("الكافرون");
        list.add("النصر");
        list.add("المسد");
        list.add("الإخلاص");
        list.add("الفلق");
        list.add("الناس");
        return list;
    }

    public ArrayList<String> ListFiles(String path) {
        File f = new File(path);
        File[] files = f.listFiles();
        if (files != null) {
            ArrayList<String> list = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                list.add(files[i].getName());
            }
            return list;
        }
        return null;
    }

    public Map<String, String> GetName() {
        Map<String, String> map = new LinkedHashMap<>();
        ArrayList<String> names = Suraah_name();
        ArrayList<String> nums = new ArrayList<>();
        for (int i = 1; i <= 114; i++) {
            String count;
            if (i < 10) {
                count = "00" + i + ".mp3";
            } else if (i < 100) {
                count = "0" + i + ".mp3";
            } else {
                count = "" + i + ".mp3";
            }
            nums.add(count);
        }
        for (int i = 0; i < nums.size(); i++) {
            map.put(nums.get(i), names.get(i));
        }
        return map;
    }

    public String loadJSONFromAsset(String jsonName, AppCompatActivity activity) {
        String json = "";
        try {
            InputStream is = activity.getAssets().open(jsonName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public boolean InBetweenTime(String initialTime, String finalTime, String currentTime) {

        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9])$";

        try {
            if (initialTime.matches(reg) && finalTime.matches(reg) &&
                    currentTime.matches(reg)) {
                //Start Time
                //all times are from java.util.Date
                Date inTime = new SimpleDateFormat("HH:mm").parse(initialTime);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(inTime);
                boolean valid = false;
                //Current Time
                Date checkTime = new SimpleDateFormat("HH:mm").parse(currentTime);
                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(checkTime);

                //End Time
                Date finTime = new SimpleDateFormat("HH:mm").parse(finalTime);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(finTime);

                if (finalTime.compareTo(initialTime) < 0) {
                    calendar2.add(Calendar.DATE, 1);
                    calendar3.add(Calendar.DATE, 1);
                }

                java.util.Date actualTime = calendar3.getTime();
                if ((actualTime.after(calendar1.getTime()) ||
                        actualTime.compareTo(calendar1.getTime()) == 0) &&
                        actualTime.before(calendar2.getTime())) {
                    valid = true;
                    return valid;
                } else {
                    valid = false;
                    return valid;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public int GetRank(Map<String, Integer> map, String Target) {
        List<String> keys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            keys.add(entry.getKey());
        }
        int index = keys.indexOf(Target);
        index++;
        return index;
    }

    public Map<String, Integer> BuildFehres() {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("الفاتحة", 1);
        map.put("البقرة", 2);
        map.put("آلعمران", 45);
        map.put("النساء", 69);
        map.put("المائدة", 95);
        map.put("الأنعام", 115);
        map.put("الأعراف", 136);
        map.put("الأنفال", 160);
        map.put("التوبة", 169);
        map.put("يونس", 187);
        map.put("هود", 199);
        map.put("يوسف", 212);
        map.put("الرعد", 225);
        map.put("ابراهيم", 231);
        map.put("الحجر", 237);
        map.put("النحل", 242);
        map.put("الإسراء", 255);
        map.put("الكهف", 266);
        map.put("مريم", 277);
        map.put("طه", 284);
        map.put("الأنبياء", 294);
        map.put("الحج", 302);
        map.put("المؤمنون", 311);
        map.put("النور", 319);
        map.put("الفرقان", 329);
        map.put("الشعراء", 335);
        map.put("النمل", 345);
        map.put("القصص", 354);
        map.put("العنكبوت", 364);
        map.put("الروم", 371);
        map.put("لقمان", 377);
        map.put("السجدة", 381);
        map.put("الأحزاب", 383);
        map.put("سبإ", 393);
        map.put("فاطر", 399);
        map.put("يس", 404);
        map.put("الصافات", 410);
        map.put("ص", 417);
        map.put("الزمر", 422);
        map.put("غافر", 431);
        map.put("فصلت", 439);
        map.put("الشورى", 445);
        map.put("الزخرف", 451);
        map.put("الدخان", 457);
        map.put("الجاثية", 460);
        map.put("الأحقاف", 464);
        map.put("محمد", 468);
        map.put("الفتح", 472);
        map.put("الحجرات", 477);
        map.put("ق", 479);
        map.put("الذاريات", 482);
        map.put("الطور", 485);
        map.put("النجم", 487);
        map.put("القمر", 490);
        map.put("الرحمن", 493);
        map.put("الواقعة", 496);
        map.put("الحديد", 499);
        map.put("المجادلة", 504);
        map.put("الحشر", 507);
        map.put("الممتحنة", 510);
        map.put("الصف", 513);
        map.put("الجمعة", 515);
        map.put("المنافقون", 516);
        map.put("التغابن", 518);
        map.put("الطلاق", 520);
        map.put("التحريم", 522);
        map.put("الملك", 524);
        map.put("القلم", 526);
        map.put("الحاقة", 529);
        map.put("المعارج", 531);
        map.put("نوح", 533);
        map.put("الجن", 534);
        map.put("المزمل", 537);
        map.put("المدثر", 538);
        map.put("القيامة", 540);
        map.put("الانسان", 542);
        map.put("المرسلات", 544);
        map.put("النبإ", 545);
        map.put("النازعات", 547);
        map.put("عبس", 548);
        map.put("التكوير", 550);
        map.put("الإنفطار", 551);
        map.put("المطففين", 552);
        map.put("الإنشقاق", 553);
        map.put("البروج", 554);
        map.put("الطارق", 555);
        map.put("الأعلى", 556);
        map.put("الغاشية", 556);
        map.put("الفجر", 557);
        map.put("البلد", 559);
        map.put("الشمس", 559);
        map.put("الليل", 560);
        map.put("الضحى", 561);
        map.put("الشرح", 561);
        map.put("التين", 561);
        map.put("العلق", 562);
        map.put("القدر", 563);
        map.put("البينة", 563);
        map.put("الزلزلة", 564);
        map.put("العاديات", 564);
        map.put("القارعة", 565);
        map.put("التكاثر", 565);
        map.put("العصر", 566);
        map.put("الهمزة", 566);
        map.put("الفيل", 566);
        map.put("قريش", 567);
        map.put("الماعون", 567);
        map.put("الكوثر", 567);
        map.put("الكافرون", 568);
        map.put("النصر", 568);
        map.put("المسد", 568);
        map.put("الإخلاص", 569);
        map.put("الفلق", 569);
        map.put("الناس", 569);
        return map;
    }

    public Map<String, Integer> Replace(Map<String, Integer> map, ArrayList<String> newKeys) {
        Map<String, Integer> mmap = new LinkedHashMap<>();
        int i = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            mmap.put(newKeys.get(i), entry.getValue());
            i++;
        }
        return mmap;
    }

    @SuppressLint("SimpleDateFormat")
    public String[] GetSupportDateAndTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss a");
        return new String[]{sdf.format(date), sdf2.format(date)};
    }

    public void OpenGooglePlay(String pkgName) {
        if (pkgName.trim().equals("")) {
            Message("Error in Package Name");
        } else {
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkgName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + pkgName)));
            }
        }
    }

    public boolean isPackageAvailable(String pkgName) {
        PackageManager manager = context.getPackageManager();
        List<PackageInfo> list = manager.getInstalledPackages(0);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).packageName.equalsIgnoreCase(pkgName)) {
                return true;
            }
        }
        return false;
    }

    public Map<String, Integer> SortMapByValue(Map<String, Integer> map) {
        Map<String, Integer> sorted = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(map.entrySet());
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
        for (Map.Entry<String, Integer> aa : list) {
            sorted.put(aa.getKey(), aa.getValue());
        }
        return sorted;
    }

    public String ExtFromURI(Uri contentURI, boolean GetExtentsion) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx;
            if (GetExtentsion) {
                idx = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
            } else {
                idx = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            }
            result = cursor.getString(idx);
            cursor.close();
        }
        if (GetExtentsion) {
            if (result != null) {
                return result.split("/")[1];
            } else {
                return "";
            }
        } else {
            return result;
        }
    }
}
