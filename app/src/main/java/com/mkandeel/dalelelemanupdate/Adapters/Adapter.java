package com.mkandeel.dalelelemanupdate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mkandeel.dalelelemanupdate.R;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;

import java.util.ArrayList;

public class Adapter extends ArrayAdapter<String> {
    private Context context;
    private ArrayList<String> list;
    private Tools tools;
    private boolean isDark;

    public Adapter(Context context, ArrayList<String> list,boolean isDark) {
        super(context, R.layout.list_row, R.id.surah_name, list);
        this.context = context;
        this.list = list;
        tools = Tools.getInstance(context);
        this.isDark = isDark;
    }

    @NonNull
    @Override
    public View getView(int position, @NonNull View coverView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row, parent, false);
        TextView txt = view.findViewById(R.id.surah_name);
        txt.setText(list.get(position));
        tools.ChangeFont("AdobeArabic.otf", txt);
        if (isDark) {
            tools.Change_Color("white",txt);
        } else {
            tools.Change_Color("green",txt);
        }
        return view;
    }
}
