package com.mkandeel.dalelelemanupdate.HelperClasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import static android.content.Context.POWER_SERVICE;

import com.mkandeel.dalelelemanupdate.praying_time;

public class AlarmReceiver extends BroadcastReceiver {
    private Tools tools;

    @Override
    public void onReceive(Context context, Intent intent) {
        tools = Tools.getInstance(context);
        String Notif_title = "حان الآن موعد الصلاة";
        String Notif_message = "حي على الصلاة";
        if (intent.getExtras() != null) {
            int index = intent.getExtras().getInt("index");
            switch (index) {
                case 0:
                    Notif_title = "حان الآن موعد آذان الفجر";
                    Notif_message = "الصلاة خير من النوم";
                    break;
                case 1:
                    Notif_title = "حان الآن موعد آذان الظهر";
                    break;
                case 2:
                    Notif_title = "حان الآن موعد آذان العصر";
                    break;
                case 3:
                    Notif_title = "حان الآن موعد آذان المغرب";
                    break;
                case 4:
                    Notif_title = "حان الآن موعد آذان العشاء";
                    break;
            }
        }
        tools.ShowNotification(Notif_title, Notif_message, praying_time.class);
        //wakeLock.release();
    }
}
