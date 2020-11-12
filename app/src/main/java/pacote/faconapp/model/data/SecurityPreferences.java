package pacote.faconapp.model.data;

import android.content.Context;
import android.content.SharedPreferences;

public class SecurityPreferences {
    //classe para armazenar infos na memoria cache

    private SharedPreferences mSharedPreferences;

    public SecurityPreferences (Context mContext) {
        this.mSharedPreferences = mContext.getSharedPreferences("Login", Context.MODE_PRIVATE);
    }

    public void storeString(String key, String value){
        this.mSharedPreferences.edit().putString(key, value).apply();
    }


    public String getStoredSting(String key){
        return this.mSharedPreferences.getString(key, "");
    }
}
