package com.mkandeel.dalelelemanupdate;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.mkandeel.dalelelemanupdate.HelperClasses.State;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;

public class Tarteel extends AppCompatActivity {

    private Tools tools;
    private State state;
    private boolean isDark;
    private int index;
    private BottomNavigationView nav;
    private String URL;
    private Bundle bundle;
    private final int REQUEST_CODE = 306;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tarteel);
        tools = Tools.getInstance(this);
        state = State.getInstance(this);
        isDark = state.getState();

        nav = findViewById(R.id.bottom_nav_view);
        if (isDark) {
            nav.inflateMenu(R.menu.dark_menu);
            nav.setBackgroundColor(getResources().getColor(R.color.dark_mode));
            nav.setItemTextColor(getColorStateList(R.color.white));
        } else {
            nav.inflateMenu(R.menu.menu);
            nav.setBackgroundColor(getResources().getColor(R.color.white));
            nav.setItemTextColor(getColorStateList(R.color.black));
        }

        bundle = new Bundle();

        if (ActivityCompat.checkSelfPermission(Tarteel.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(Tarteel.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Tarteel.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            Granted();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Granted();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
            return;
        }
    }

    NavigationBarView.OnItemSelectedListener btm_nav =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment;
                    switch (item.getItemId()) {
                        case R.id.list:
                            fragment = new List_Fragment();
                            fragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
                            break;
                        case R.id.downloads:
                            fragment = new Downloads_Fragment();
                            fragment.setArguments(bundle);
                            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, fragment).commit();
                            break;
                    }
                    return true;
                }
            };

    private void Granted() {
        final String[] items = {"عبد الباسط عبد الصمد", "مشاري راشد العفاسي"};
        index = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(Tarteel.this);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                index = which;
            }
        });
        builder.setTitle("اختر القارئ");
        builder.setPositiveButton("اختيار", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (index == 0) {
                    URL = "https://server7.mp3quran.net/basit/Almusshaf-Al-Mojawwad";
                } else if (index == 1) {
                    URL = "https://server8.mp3quran.net/afs";
                }
                bundle.putString("URL", URL);

                List_Fragment lfragment = new List_Fragment();
                lfragment.setArguments(bundle);

                nav.setSelectedItemId(R.id.list);

                getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, lfragment)
                        .commit();
                nav.setOnItemSelectedListener(btm_nav);

                dialog.dismiss();

            }
        });
        builder.show();
    }
}