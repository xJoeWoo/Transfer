package ng.transfer.support.info;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseBooleanArray;

import ng.transfer.support.sql.SQLHelper;
import ng.transfer.support.util.TransferPreferences;
import ng.transfer.support.util.TransferUtils;

/**
 * Created by Joe on 2014/6/11.
 */
public class Transfer extends Application {

    private static Context context;
    private static boolean isUploading;
    private static String UUID;
    private static SparseBooleanArray uploadParams;
    private static String ipSuffix;
    private static String ipPrefix;
    private static SQLiteDatabase sql;

    public static String getUrlToUploadText() {
        return "http://" + ipPrefix + '.' + ipSuffix + ":8080/TransferServer/upload";
    }

    public static String getUrlToUploadFiles() {
        return "http://" + ipPrefix + '.' + ipSuffix + ":8080/TransferServer/upload";
    }

    public static String getUUID() {
        return UUID;
    }

    public static SparseBooleanArray getUploadParams() {
        return uploadParams;
    }

    public static void setUploadParams(SparseBooleanArray uploadParams) {
        Transfer.uploadParams = uploadParams;
    }

    public static void setIpSuffix(String ipSuffix) {
        Transfer.ipSuffix = ipSuffix;
    }

    public static boolean isUploading() {
        return isUploading;
    }

    public static void setUploading(boolean isUploading) {
        Transfer.isUploading = isUploading;
    }

    public static Context getGlobalContext() {
        return context;
    }

    public static String getResString(int resId) {
        return context.getString(resId);
    }

    public static int getResColor(int resId) {
        return context.getResources().getColor(resId);
    }

    public static SQLiteDatabase getSQL() {
        return sql;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this.getApplicationContext();
//        IMEI = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        UUID = TransferPreferences.getPreference(TransferPreferences.KEY_UUID);
        if(UUID.isEmpty()) {
            UUID = java.util.UUID.randomUUID().toString();
            TransferPreferences.savePreference(TransferPreferences.KEY_UUID, UUID);
        }
        String localIp = TransferUtils.Network.getLocalIpAddress();
        Log.e("IP", TransferUtils.Network.getLocalIpAddress());
        ipPrefix = localIp.substring(0, localIp.lastIndexOf('.'));

        sql = new SQLHelper(context, SQLHelper.SQL_NAME, null, SQLHelper.SQL_VERSION).getWritableDatabase();
    }
}
