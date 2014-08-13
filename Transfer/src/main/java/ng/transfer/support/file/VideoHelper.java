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
import ng.transfer.support.net.UploadFileThread;

/**
 * Created by Joe on 2014/6/24.
 */
public class VideoHelper extends Thread {

    private Handler handler;
    private List<Map<String, String>> imagesList;

    public VideoHelper(Handler handler, List<Map<String, String>> imagesList) {
        this.handler = handler;
        this.imagesList = imagesList;
    }

    private static List<Map<String, String>> scanVideos() {

        Log.e("TAG", "Scan Videos");

        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATE_MODIFIED};

        Cursor cursor = Transfer.getGlobalContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Video.Media.DATE_MODIFIED);

        List<Map<String, String>> videosList = new ArrayList<Map<String, String>>();

        if (cursor != null && cursor.getCount() > 0) {
            Log.e("TAG", String.valueOf(cursor.getCount()));

            //末尾开始
            cursor.moveToLast();

            Map<String, String> map = new HashMap<String, String>();

            map.put(Defines.PARAM_FILE_PATH, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            map.put(Defines.PARAM_FILE_MODIFIED_DATE, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)));
            map.put(Defines.PARAM_FILE_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
            map.put(Defines.PARAM_FILE_SIZE, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));

            videosList.add(map);

            while (cursor.moveToPrevious()) {

                map = new HashMap<String, String>();

                map.put(Defines.PARAM_FILE_PATH, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
                map.put(Defines.PARAM_FILE_MODIFIED_DATE, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)));
                map.put(Defines.PARAM_FILE_NAME, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
                map.put(Defines.PARAM_FILE_SIZE, cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));

                videosList.add(map);
            }
            cursor.close();
        } else {
            Log.e("TAG", "No Cursor");
        }
        return videosList;

    }

    public void run() {
        List<Map<String, String>> videosList = scanVideos();
        handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_READY).sendToTarget();
        new UploadFileThread(handler, imagesList, videosList).start();
    }

}
