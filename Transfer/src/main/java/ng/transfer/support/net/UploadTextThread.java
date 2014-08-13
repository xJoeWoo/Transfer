package ng.transfer.support.net;


import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.google.gson.Gson;

import java.security.cert.TrustAnchor;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import ng.transfer.support.bean.InfoBean;
import ng.transfer.support.file.ImageHelper;
import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;
import ng.transfer.support.listener.TransferListeners;
import ng.transfer.support.util.TransferUtils;

/**
 * Created by Joe on 2014/6/10.
 */
public class UploadTextThread extends Thread {

    private Handler handler;

    public UploadTextThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {

        handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_READY).sendToTarget();

        List jsons[] = TransferUtils.BuildInfoJSONs.getJSONLists();

        HashMap<String, String> param = new HashMap<String, String>();

        Log.e(Defines.TAG, Build.BRAND);
        Log.e(Defines.TAG, Build.MODEL);

        InfoBean infoBean = new InfoBean(Transfer.getUUID(), Build.BRAND, Build.MODEL);

        if (jsons[TransferUtils.BuildInfoJSONs.POS_CALL_RECORD] != null && !jsons[TransferUtils.BuildInfoJSONs.POS_CALL_RECORD].isEmpty())
            infoBean.setCallRecord(jsons[TransferUtils.BuildInfoJSONs.POS_CALL_RECORD]);
        if (jsons[TransferUtils.BuildInfoJSONs.POS_CONTACTS] != null && !jsons[TransferUtils.BuildInfoJSONs.POS_CONTACTS].isEmpty())
            infoBean.setContacts(jsons[TransferUtils.BuildInfoJSONs.POS_CONTACTS]);
        if (jsons[TransferUtils.BuildInfoJSONs.POS_SMS] != null && !jsons[TransferUtils.BuildInfoJSONs.POS_SMS].isEmpty())
            infoBean.setSms(jsons[TransferUtils.BuildInfoJSONs.POS_SMS]);

            param.put(Defines.PARAM_JSON, new Gson().toJson(infoBean));

        try {

            HttpUtility.doPost(Transfer.getUrlToUploadText(), param);
            new ImageHelper(handler).start();
            handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_SCAN_IMAGES).sendToTarget();

        } catch (Exception e) {
            e.printStackTrace();
            handler.obtainMessage(Defines.MSG_UPLOAD_PROGRESS, 0, 0, Defines.STATUS_FAILED).sendToTarget();
        }

        Transfer.setUploading(false);

//        new UploadFileThread(handler).start();
    }
}
