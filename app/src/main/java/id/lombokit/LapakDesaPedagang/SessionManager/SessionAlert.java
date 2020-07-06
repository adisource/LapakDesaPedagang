package id.lombokit.LapakDesaPedagang.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionAlert {
    public  static  final String SP_ALERT_APP="spalert";
    public  static  final String SP_TRUE= "sptrue";

    SharedPreferences sp;
    SharedPreferences.Editor speditor;
    public SessionAlert(Context context) {
        sp = context.getSharedPreferences(SP_ALERT_APP, Context.MODE_PRIVATE);
        speditor = sp.edit();
    }
    public  void saveBoolean(String keySp, boolean value){
        speditor.putBoolean(keySp,value);
        speditor.commit();
    }
    public Boolean getSpLogined()
    {
        return sp.getBoolean(SP_TRUE,false);
    }


}
