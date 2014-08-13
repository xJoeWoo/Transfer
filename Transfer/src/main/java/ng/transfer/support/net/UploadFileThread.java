package ng.transfer.support.net;

import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;
import ng.transfer.support.listener.TransferListeners;
import ng.transfer.support.sql.SQLHelper;

/**
 * Created by Joe on 2014/6/16.
 */
public class UploadFileThread extends Thread implements TransferListeners.UploadProgressListener {

    private Handler handler;
    private long lastProgressUpdateTime = 0;
    private long lastProgressStringUpdateTime = 0;
    private int retryTimes = 0;
    private List<Map<String, String>> imagesList;
    private List<Map<String, String>> videosList;

    public UploadFileThread(Handler handler, List<Map<String, String>> imagesList, List<Map<String, String>> videosList) {
        this.handler = handler;
        this.imagesList = imagesList;
        this.videosList = videosList;
    }


    @Override
    public void run() {

        try {
            Transfer.setUploading(true);
            Log.e(Defines.TAG, "START UPLOAD FILES!");

            String returnString = HttpUtility.doUploadFiles(imagesList, videosList, this);
            List<Integer> imageFails = new ArrayList<Integer>();
            List<Integer> videoFails = new ArrayList<Integer>();

            if (returnString != null && !returnString.isEmpty()) {
                String[] returns = returnString.split(",");

                for (String str : returns) {
                    if (str.contains(Defines.RETURN_FAIL)) {
                        if (str.contains(Defines.PARAM_IMAGE)) {
                            imageFails.add(Integer.valueOf(str.substring(0, 1)) - 1);
                            Log.e(Defines.TAG, "IMAGE FAIL: " + String.valueOf(Integer.valueOf(str.substring(0, 1)) - 1));
                        } else if (str.contains(Defines.PARAM_VIDEO)) {
                            videoFails.add(Integer.valueOf(str.substring(0, 1)) - 1);
                            Log.e(Defines.TAG, "VIDEO FAIL: " + String.valueOf(Integer.valueOf(str.substring(0, 1)) - 1));
                        }
                    }
                }
            }

            if (imageFails.size() > 0 || videoFails.size() > 0) {
                reSendFile(imageFails, videoFails);
                handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_FAILED).sendToTarget();
            } else {
                SQLHelper.clear();
                handler.obtainMessage(Defines.MSG_UPLOAD_TYPE, 0, 0, Defines.STATUS_FINISHED).sendToTarget();
                handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_FINISHED).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Transfer.setUploading(false);
    }


    private void reSendFile(List<Integer> imageFails, List<Integer> videoFails) {

        if (retryTimes < 2) {

            List<Map<String, String>> toResendImagesList = new ArrayList<Map<String, String>>();
            List<Map<String, String>> toResendVideosList = new ArrayList<Map<String, String>>();
            List<Map<String, String>> tempList = new ArrayList<Map<String, String>>();

            for (int i : imageFails) {
                Map<String, String> map = imagesList.get(i);
                toResendImagesList.add(map);
                map.put(Defines.PARAM_FILE_TYPE, Defines.PARAM_IMAGE);
                tempList.add(map);
            }

            for (int i : videoFails) {
                Map<String, String> map = videosList.get(i);
                toResendVideosList.add(map);
                map.put(Defines.PARAM_FILE_TYPE, Defines.PARAM_VIDEO);
                tempList.add(map);
            }

            SQLHelper.savePath(tempList);

            try {

                String returnString = HttpUtility.doUploadFiles(toResendImagesList, toResendVideosList, this);

                retryTimes++;

                imageFails = new ArrayList<Integer>();
                videoFails = new ArrayList<Integer>();

                imagesList = toResendImagesList;
                videosList = toResendVideosList;

                if (returnString != null && !returnString.isEmpty()) {
                    String[] returns = returnString.split(",");

                    for (String str : returns) {
                        if (str.contains(Defines.RETURN_FAIL)) {
                            if (str.contains(Defines.PARAM_IMAGE)) {
                                imageFails.add(Integer.valueOf(str.substring(0, 1)) - 1);
                                Log.e(Defines.TAG, "IMAGE FAIL: " + String.valueOf(Integer.valueOf(str.substring(0, 1)) - 1));
                            } else if (str.contains(Defines.PARAM_VIDEO)) {
                                videoFails.add(Integer.valueOf(str.substring(0, 1)) - 1);
                                Log.e(Defines.TAG, "VIDEO FAIL: " + String.valueOf(Integer.valueOf(str.substring(0, 1)) - 1));
                            }
                        }
                    }

                    if (toResendImagesList.size() > 0 | toResendVideosList.size() > 0) {
                        reSendFile(imageFails, videoFails);
                        handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_FAILED).sendToTarget();
                    } else {
                        SQLHelper.clear();
                        handler.obtainMessage(Defines.MSG_UPLOAD_TYPE, 0, 0, Defines.STATUS_FINISHED).sendToTarget();
                        handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_FINISHED).sendToTarget();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void progress(long transferred, long fileSize) {
        if (System.currentTimeMillis() - lastProgressUpdateTime > 200) {
            handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, ((float) transferred / (float) fileSize) * 360).sendToTarget();
            lastProgressUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    public void progressString(long currentIndex, long allFilesCount) {
        if (System.currentTimeMillis() - lastProgressStringUpdateTime > 200) {
            handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS_STRING, 0, 0, String.valueOf(currentIndex) + '/' + String.valueOf(allFilesCount)).sendToTarget();
            lastProgressStringUpdateTime = System.currentTimeMillis();
        }
    }

    @Override
    public void fileType(String type) {
        if (type.equals(Defines.PARAM_IMAGE)) {
            handler.obtainMessage(Defines.MSG_UPLOAD_TYPE, 0, 0, Defines.STATUS_UPLOADING_IMAGES).sendToTarget();
        } else {
            handler.obtainMessage(Defines.MSG_UPLOAD_TYPE, 0, 0, Defines.STATUS_UPLOADING_VIDEOS).sendToTarget();
        }

    }
}
