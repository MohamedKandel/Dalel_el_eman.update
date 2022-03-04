package com.mkandeel.dalelelemanupdate.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mkandeel.dalelelemanupdate.HelperClasses.Home_Module;
import com.mkandeel.dalelelemanupdate.R;
import com.mkandeel.dalelelemanupdate.HelperClasses.Tools;
import com.mkandeel.dalelelemanupdate.HelperClasses.onItemClickListener;

import java.util.ArrayList;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.RV_Holder> {

    private ArrayList<Home_Module> list;
    private Context context;
    private onItemClickListener itemClickListener;
    private Tools tools;
    private boolean isDark;

    public Home_Adapter(ArrayList<Home_Module> list, Context context,boolean isDark) {
        this.context = context;
        this.list = list;
        tools = Tools.getInstance(context);
        this.isDark = isDark;
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public RV_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.rv_row, parent, false);
        return new RV_Holder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RV_Holder holder, int position) {
        Home_Module module = list.get(position);
        holder.txt_ar.setText(module.getTxt_ar());
        holder.txt_en.setText(module.getTxt_en());
        holder.img.setImageResource(module.getImg());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class RV_Holder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView txt_ar, txt_en;

        public RV_Holder(@NonNull View itemView, final onItemClickListener listener) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            txt_en = itemView.findViewById(R.id.txt_en);
            txt_ar = itemView.findViewById(R.id.txt_ar);
            tools.ChangeFont("calibri.ttf", txt_ar, txt_en);
            if (isDark) {
                tools.Change_Color("white",txt_en,txt_ar);
            } else {
                tools.Change_Color("black",txt_en,txt_ar);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
