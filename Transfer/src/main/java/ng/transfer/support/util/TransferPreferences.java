package ng.transfer.support.util;

import android.content.SharedPreferences;

import ng.transfer.support.info.Transfer;

/**
 * Created by Joe on 2014/6/16.
 */
public class TransferPreferences {

    public static final String PREFERENCE_NAME = "transfer";
    public static final String KEY_IP = "ip";
    public static final String KEY_UUID = "uuid";

    public static void savePreference(String key, String value) {
        SharedPreferences.Editor editor = Transfer.getGlobalContext().getSharedPreferences(PREFERENCE_NAME, 0).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getPreference(String key) {
        SharedPreferences preferences = Transfer.getGlobalContext().getSharedPreferences(PREFERENCE_NAME, 0);
        return preferences.getString(key, "");
    }

}
