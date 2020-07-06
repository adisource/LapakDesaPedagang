package id.lombokit.LapakDesaPedagang.SessionManager;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    public  static  final String SP_WALI_APP="spwaliapp";
    public  static  final String SP_NIKWALI = "spnikwali";
    public  static  final String SP_IDUSER= "spiduser";
    public  static  final String SP_IDDESA = "spiddesa";
    public  static  final String SP_NOINDUK = "spnomerinduk";
    public  static  final String SP_LOGINED = "splogined";
    public  static  final String SP_USERNAME="spusername";

    SharedPreferences sp;
    SharedPreferences.Editor speditor;

    public SessionManager(Context context) {
        sp = context.getSharedPreferences(SP_WALI_APP, Context.MODE_PRIVATE);
        speditor = sp.edit();
    }
    public void saveString(String keySp, String value){
        speditor.putString(keySp,value);
        speditor.commit();
    }
    public  void saveInt(String keySp, int value){
        speditor.putInt(keySp,value);
        speditor.commit();
    }
    public  void saveBoolean(String keySp, boolean value){
        speditor.putBoolean(keySp,value);
        speditor.commit();
    }
    public String getSpIduser() {
        return sp.getString(SP_IDUSER,"");
    }

    public String getSpUsername()
    {
        return sp.getString(SP_USERNAME,"");
    }

    public String getSpNikwali() {
        return sp.getString(SP_NIKWALI,"");
    }

    public String getSpNoinduk() {
        return sp.getString(SP_NOINDUK, "");
    }

    public int getSpIddesa() {
        return sp.getInt(SP_IDDESA,0);
    }

    public Boolean getSpLogined() {
        return sp.getBoolean(SP_LOGINED,false);
    }
}
