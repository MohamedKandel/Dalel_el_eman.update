package com.mkandeel.dalelelemanupdate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.mkandeel.dalelelemanupdate.Adapters.Adapter;
import com.mkandeel.dalelelemanupdate.HelperClasses.State;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;

import java.util.ArrayList;
import java.util.Objects;


public class List_Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private int index;
    private String URL;
    private boolean isGranted = false;
    private LinearLayout layout;
    private TextView txt_fehres,txt_qar;
    private ListView lv;
    private Tools tools;
    private State state;
    private String qare2;

    public List_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            URL = getArguments().getString("URL");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list_, container, false);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txt_fehres = view.findViewById(R.id.txt_fehres);
        txt_qar = view.findViewById(R.id.txt_Qar);

        layout = view.findViewById(R.id.layout);
        lv = view.findViewById(R.id.lv);
        tools = Tools.getInstance(getContext());
        state = State.getInstance(getContext());

        tools.ChangeFont("cairo.ttf", txt_fehres);
        tools.ChangeFont("AdobeArabic.otf",txt_qar);
        boolean isDark = state.getState();
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            tools.Change_Color("white",txt_fehres,txt_qar);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            tools.Change_Color("green",txt_fehres,txt_qar);
        }

        if (state.GetTarteelDialog()) {
            CreateDialog("تطبيق دليل الإيمان","يمكنك تحميل السور عن طريق ضغطة مطولة على السورة",isDark);
        }

        if (URL.contains("afs")) {
            qare2 = "afs";
            txt_qar.setText("مشاري راشد العفاسي");
        } else {
            qare2 = "abd";
            txt_qar.setText("عبد الباسط عبد الصمد");
        }

        ArrayList<String> list_names = tools.Suraah_name();

        ArrayList<String> list_urls = tools.Generate_URLs(URL.trim());

        Adapter adapter = new Adapter(getContext(), list_names,isDark);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //tools.Message(list_names.get(position)+"\n"+list_urls.get(position));
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(getActivity(), Play.class);
                    intent.putExtra("position", position);
                    intent.putStringArrayListExtra("names", list_names);
                    intent.putExtra("Qar", qare2);
                    intent.putExtra("type", "online");
                    intent.putStringArrayListExtra("URLs", list_urls);
                    startActivity(intent);
                } else {
                    tools.Message("عذرًا لا يوجد اتصال بالانترنت تأكد من اتصال هاتفك بالانترنت لتتمكن من سماع السور عبر الانترنت\n" +
                            "يمكنك سماع السور المحملة بدون انترنت");
                }
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu menu = new PopupMenu(getActivity(), view);
                menu.getMenuInflater().inflate(R.menu.pop_up, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.down:
                                String fileName = list_urls.get(position)
                                        .substring(list_urls.get(position).length() - 7);
                                Download(list_urls.get(position), fileName);
                                tools.Message(fileName);
                                break;
                        }
                        return true;
                    }
                });
                menu.show();
                return true;
            }
        });
    }


    private void Download(String url, String name) {
        DownloadManager manager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalFilesDir(getActivity(), Environment.DIRECTORY_DOWNLOADS,
                qare2+"/"+name);
        manager.enqueue(request);
        //tools.Message(Environment.DIRECTORY_DOWNLOADS + "/" + name);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().registerReceiver(onComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"Download Completed",Toast.LENGTH_SHORT).show();
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public void CreateDialog(String title,String message,boolean isDark) {
        Dialog dialog = new Dialog(requireContext());
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
                state.ShowTarteelDialog(!isChecked[0]);
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        requireActivity().unregisterReceiver(onComplete);
    }
}