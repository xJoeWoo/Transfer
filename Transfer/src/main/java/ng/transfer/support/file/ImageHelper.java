package ng.transfer.support.file;

import android.database.Cursor;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;

/**
 * Created by Joe on 2014/6/24.
 */
public class ImageHelper extends Thread {

    private Handler handler;

    public ImageHelper(Handler handler) {
        this.handler = handler;
    }

    private static List<Map<String, String>> scanImages() {

        Log.e("TAG", "Scan Images");

        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.SIZE, MediaStore.Images.Media.DATE_MODIFIED};

        Cursor cursor = Transfer.getGlobalContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED);

        List<Map<String, String>> imagesPathList = new ArrayList<Map<String, String>>();

        if (cursor != null && cursor.getCount() > 0) {
            Log.e("TAG", String.valueOf(cursor.getCount()));

            //末尾开始
            cursor.moveToLast();

            Map<String, String> map = new HashMap<String, String>();

            map.put(Defines.PARAM_FILE_PATH, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            map.put(Defines.PARAM_FILE_MODIFIED_DATE, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
            map.put(Defines.PARAM_FILE_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
            map.put(Defines.PARAM_FILE_SIZE, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));

            imagesPathList.add(map);


            while (cursor.moveToPrevious()) {

                map = new HashMap<String, String>();

                map.put(Defines.PARAM_FILE_PATH, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                map.put(Defines.PARAM_FILE_MODIFIED_DATE, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
                map.put(Defines.PARAM_FILE_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                map.put(Defines.PARAM_FILE_SIZE, cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));

                imagesPathList.add(map);

            }
            cursor.close();
        } else {
            Log.e("TAG", "No Cursor");
        }
        return imagesPathList;

    }

    public void run() {
        handler.obtainMessage(Defines.MSG_IMAGES_LIST, 0, 0, scanImages()).sendToTarget();
    }

}
