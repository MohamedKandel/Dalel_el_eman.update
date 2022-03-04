package com.mkandeel.dalelelemanupdate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mkandeel.dalelelemanupdate.HelperClasses.Contest;
import com.mkandeel.dalelelemanupdate.HelperClasses.DBConnection;
import com.mkandeel.dalelelemanupdate.HelperClasses.State;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;
import com.mkandeel.dalelelemanupdate.HelperClasses.ValueComparator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Sebha extends AppCompatActivity {

    private TextView txt_main, textViewtarteeb, txt_count, txt_name, txt_country, txt_tarteeb,
            txtViewName, txtViewCountry, txtViewTarteeb;
    private ImageView img_count/*,img_cup*/;
    private LinearLayout layout;
    private Tools tools;
    private State state;
    private int OldCount, NewCount;
    private DBConnection connection;
    private ValueComparator comparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sebha);

        Inflates();
        tools = Tools.getInstance(this);
        state = State.getInstance(this);
        connection = new DBConnection(this);

        boolean isDark = state.getState();
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            tools.Change_Color("white", txt_main, textViewtarteeb,txt_name,
                    txt_country,txt_tarteeb,txtViewName,txtViewTarteeb,txtViewCountry);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            tools.Change_Color("black", txt_main, textViewtarteeb);
            tools.Change_Color("green",txt_name,txt_country,txt_tarteeb,txtViewCountry,
                    txtViewTarteeb,txtViewName);
        }
        tools.ChangeFont("cairo.ttf", txt_main, txtViewName, txtViewCountry, txtViewTarteeb);
        tools.ChangeFont("AdobeArabic.otf", textViewtarteeb, txt_name, txt_tarteeb, txt_country);
        tools.ChangeFont("segoeui.ttf", txt_count);

        String UID = state.GetUID();
        if (isNetworkAvailable()) {
            GetData(UID);
            GetRank(UID);
            //tools.Message(UID);
            layout.setClickable(true);
            layout.setEnabled(true);
        } else {
            if (state.GetSebhaDialog()) {
                CreateDialog("تطبيق دليل الإيمان","عذرًا لا يوجد اتصال بالانترنت يمكنك التسبيح ولكن لن يُحتسب ذلك التسبيح للمسابقة",
                        isDark);
            }
            String Data = connection.GetData(UID);
            String[] arr = Data.split("\n");
            txt_name.setText(arr[0]);
            txt_country.setText(arr[1]);
            txt_tarteeb.setVisibility(View.INVISIBLE);
            txtViewTarteeb.setVisibility(View.INVISIBLE);
            layout.setClickable(false);
            layout.setEnabled(false);
        }

        img_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mcount = txt_count.getText().toString();
                int count = Integer.parseInt(mcount);
                count++;
                txt_count.setText(String.valueOf(count));
                if (isNetworkAvailable() && img_count.isEnabled() && img_count.isClickable()) {
                    UpdateData(UID);
                    img_count.setEnabled(false);
                    img_count.setClickable(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            img_count.setClickable(true);
                            img_count.setEnabled(true);
                        }
                    }, 500);
                }
            }
        });

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(Sebha.this, Tarteeb.class);
                    startActivity(intent);
                    finish();
                } else {
                    tools.Message("عذرًا لا يمكنك معرفة الترتيبات إلا في وجود اتصال بالانترنت");
                }
            }
        });
    }
    
    private void GetRank(String uid) {
        Map<String, Integer> map = new LinkedHashMap<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Database").child("Users");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Contest contest = ds.getValue(Contest.class);
                        if (contest != null) {
                            if (!(contest.getUserid().trim().equals(""))) { /*Add dev id here*/
                                map.put(contest.getName(), contest.getCount());
                            }
                        }
                    }
                    comparator = new ValueComparator(map);
                    Map<String,Integer> map2 = new TreeMap<>(comparator);
                    map2.putAll(map);
                    System.out.println("Map " + map2);
                    String username = connection.GetUserName(uid);
                    int Rank = tools.GetRank(map2, username);
                    txt_tarteeb.setText(String.valueOf(Rank));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void GetData(String uid) {
        tools.Message("برجاء الانتظار إلى ان يتم تحميل جميع بيانات حسابك");
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Database").child("Users");
        Query query = reference.orderByChild("userid").equalTo(uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Contest contest = ds.getValue(Contest.class);
                        if (contest != null) {
                            String Name = contest.getName();
                            String country = contest.getCountry();
                            txt_name.setText(Name);
                            txt_country.setText(country);
                        }
                    }
                } else {
                    tools.Message("عذرًا ... لم نتمكن من الوصول إلى بيانات المستخدم حاول التواصل مع المطورين لحل المشكلة");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void UpdateData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Database").child("Users");

        Query query = reference.orderByChild("userid").equalTo(uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Contest contest = ds.getValue(Contest.class);
                        if (contest != null) {
                            OldCount = contest.getCount();
                            NewCount = OldCount + 1;
                        }
                    }
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("count", NewCount);
                    reference.child(uid).updateChildren(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Inflates() {
        //main text view
        txt_main = findViewById(R.id.textViewMain);
        //comp. layout
        layout = findViewById(R.id.layout_tar);
        //img_cup = findViewById(R.id.img_cup);
        textViewtarteeb = findViewById(R.id.textViewTarteeb);
        //count text view
        txt_count = findViewById(R.id.txt_count);
        //data layouts
        txt_name = findViewById(R.id.txt_name);
        txt_country = findViewById(R.id.txt_country);
        txt_tarteeb = findViewById(R.id.txt_tarteeb);
        txtViewName = findViewById(R.id.textView6);
        txtViewCountry = findViewById(R.id.textView8);
        txtViewTarteeb = findViewById(R.id.textView10);
        //count button
        img_count = findViewById(R.id.img_count);
    }

    public void CreateDialog(String title,String message,boolean isDark) {
        Dialog dialog = new Dialog(Sebha.this);
        if (isDark) {
            dialog.setContentView(R.layout.dilaog_dark);
        } else {
            dialog.setContentView(R.layout.dilaog_light);
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        TextView txt_title = dialog.findViewById(R.id.txt_title);
        TextView txt_message = dialog.findViewById(R.id.txt_msg);
        CheckBox box = dialog.findViewById(R.id.checkBox);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        final boolean[] isChecked = new boolean[1];

        txt_title.setText(title);
        txt_message.setText(message);

        tools.ChangeFont("AdobeArabic.otf",txt_message,txt_title);
        tools.ChangeButtonFont("AdobeArabic.otf",btn_ok);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked[0] = box.isChecked();
                state.ShowSebhaDialog(!isChecked[0]);
                dialog.cancel();
            }
        });
        dialog.show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, Home_Activity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}