package com.mkandeel.dalelelemanupdate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mkandeel.dalelelemanupdate.Adapters.Adapter;
import com.mkandeel.dalelelemanupdate.HelperClasses.State;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Downloads_Fragment extends Fragment {

    private int index;
    private String URL;
    private boolean isGranted = false;
    private LinearLayout layout;
    private TextView txt_downloads, txt_qar;
    private ListView lv;
    private Tools tools;
    private State state;
    private String qare2,path;

    public Downloads_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {
            URL = getArguments().getString("URL");
            //Toast.makeText(getActivity(),getArguments().getString("URL"),Toast.LENGTH_SHORT).show();
        }

        return inflater.inflate(R.layout.fragment_downloads_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tools = Tools.getInstance(getContext());
        state = State.getInstance(getContext());

        txt_qar = view.findViewById(R.id.txt_Qar);
        txt_downloads = view.findViewById(R.id.txt_downloads);
        lv = view.findViewById(R.id.lv);

        tools.ChangeFont("cairo.ttf", txt_downloads);
        tools.ChangeFont("AdobeArabic.otf", txt_qar);

        boolean isDark = state.getState();
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            tools.Change_Color("white",txt_downloads,txt_qar);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            tools.Change_Color("green",txt_downloads,txt_qar);
        }

        if (URL.contains("afs")) {
            qare2 = "afs";
            txt_qar.setText("مشاري راشد العفاسي");
        } else {
            qare2 = "abd";
            txt_qar.setText("عبد الباسط عبد الصمد");
        }

        path = Objects.requireNonNull(
                requireContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS + "/" + qare2))
                .getAbsolutePath();


        // contain surah number i.e. 001.mp3 , 003.mp3 , 114.mp3 , ...
        ArrayList<String> list_files = tools.ListFiles(path);
        // contain surah name i.e. الفاتحة , آل عمران , الناس
        ArrayList<String> temp_list = new ArrayList<>();
        Map<String,String> map = tools.GetName();
        if (list_files!=null) {
            for (String str : list_files) {
                String name = map.get(str);
                temp_list.add(name);
            }
        }


        if (temp_list.size()>0) {
            Adapter adapter = new Adapter(getContext(), temp_list,isDark);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String surah_path = path + "/"+list_files.get(position);
                    tools.Message(surah_path);
                    Intent intent = new Intent(getActivity(),Play.class);
                    intent.putExtra("URLs",list_files);
                    intent.putExtra("names",temp_list);
                    intent.putExtra("position",position);
                    intent.putExtra("Qar",qare2);
                    intent.putExtra("type","offline");
                    startActivity(intent);
                }
            });
        } else {
            tools.Message("لا يوجد أية سور مُحملة للقارئ الشيخ\n "+txt_qar.getText().toString());
        }
    }
}