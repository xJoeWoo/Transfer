package ng.transfer.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ng.transfer.R;
import ng.transfer.support.file.VideoHelper;
import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;
import ng.transfer.support.net.UploadFileThread;
import ng.transfer.support.net.UploadTextThread;
import ng.transfer.support.sql.SQLHelper;
import ng.transfer.support.util.TransferPreferences;
import ng.transfer.support.util.TransferUtils;
import ng.transfer.support.view.UploadButton;


public class MainActivity extends ActionBarActivity {

    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ly_act_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Ng");

        wakeLock.acquire();
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//
//        menu.clear();
//        menu.add(0, 0, 0, "CLEAR");
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case 0: {
//                SQLHelper.clear();
//                break;
//            }
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeLock.release();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private SparseBooleanArray chooses = new SparseBooleanArray();

        private UploadTextThread uploadTextThread;

        private UploadButton btnUpload;

        private Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Defines.MSG_UPLOAD_PROGRESS: {
                        btnUpload.setProgress((Float) msg.obj);
                        break;
                    }
                    case Defines.MSG_UPLOAD_PROGRESS_STRING: {
                        btnUpload.setProgressString((String) msg.obj);
                        break;
                    }
                    case Defines.MSG_UPLOAD_TYPE: {
                        btnUpload.setUploadType((Float) msg.obj);
                        break;
                    }
                    case Defines.MSG_IMAGES_LIST: {
                        btnUpload.setProgress(Defines.STATUS_SCAN_VIDEOS);
                        List<Map<String, String>> imagesPathList = (List<Map<String, String>>) msg.obj;
                        new VideoHelper(handler, imagesPathList).start();
                        break;
                    }
                }
            }

        };


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.ly_fg_main, container, false);

            final EditText et_ip = (EditText) rootView.findViewById(R.id.et_ip);
            if (!TransferPreferences.getPreference(TransferPreferences.KEY_IP).isEmpty()) {
                et_ip.setText(TransferPreferences.getPreference(TransferPreferences.KEY_IP));
            }

            final ToggleButton btn_tg_images = (ToggleButton) rootView.findViewById(R.id.btn_tg_images);
            final ToggleButton btn_tg_videos = (ToggleButton) rootView.findViewById(R.id.btn_tg_videos);

            final boolean isMountedSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

            if (!isMountedSD) {
                btn_tg_images.setChecked(false);
                btn_tg_videos.setChecked(false);
            }

            btn_tg_images.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isMountedSD) {
                        Toast.makeText(Transfer.getGlobalContext(), R.string.toast_insert_sd, Toast.LENGTH_LONG).show();
                        btn_tg_images.setChecked(false);
                    }
                }
            });

            btn_tg_videos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isMountedSD) {
                        Toast.makeText(Transfer.getGlobalContext(), R.string.toast_insert_sd, Toast.LENGTH_LONG).show();
                        btn_tg_videos.setChecked(false);
                    }
                }
            });

            btnUpload = (UploadButton) rootView.findViewById(R.id.btn_start_upload);
            btnUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    chooses.put(Defines.UPLOAD_CALL_RECORD, ((ToggleButton) rootView.findViewById(R.id.btn_tg_call_record)).isChecked());
                    chooses.put(Defines.UPLOAD_CONTACTS, ((ToggleButton) rootView.findViewById(R.id.btn_tg_contacts)).isChecked());
                    chooses.put(Defines.UPLOAD_SMS, ((ToggleButton) rootView.findViewById(R.id.btn_tg_sms)).isChecked());
                    chooses.put(Defines.UPLOAD_IMAGES, (btn_tg_images.isChecked()));
                    chooses.put(Defines.UPLOAD_VIDEOS, (btn_tg_videos.isChecked()));

                    if (!Transfer.isUploading()) {//是否正在上传
                        if (et_ip.getText().toString() != null && !et_ip.getText().toString().trim().isEmpty()) {//是否输入地址
                            if (TransferUtils.Network.isConnectedWifi()) {//是否连接WIFI
                                TransferPreferences.savePreference(TransferPreferences.KEY_IP, et_ip.getText().toString().trim());
                                Transfer.setUploadParams(chooses);
                                Transfer.setIpSuffix(et_ip.getText().toString().trim());
                                if (!SQLHelper.hasFails()) {//是否有上传失败记录
                                    startUpload();
                                } else {
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                                    dialogBuilder.setMessage(R.string.dialog_has_fails);
                                    dialogBuilder.setTitle(R.string.dialog_has_fails_title);
                                    dialogBuilder.setPositiveButton(R.string.dialog_upload_failed, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Cursor cursor = SQLHelper.getFailedFilesPath();
                                            List<Map<String, String>> imagesList = new ArrayList<Map<String, String>>();
                                            List<Map<String, String>> videosList = new ArrayList<Map<String, String>>();
                                            while (cursor.moveToNext()) {
                                                String type = cursor.getString(cursor.getColumnIndex(SQLHelper.COLUMN_TYPE));
                                                HashMap<String, String> map = new HashMap<String, String>();
                                                map.put(Defines.PARAM_FILE_PATH, cursor.getString(cursor.getColumnIndex(SQLHelper.COLUMN_PATH)));
                                                if (type.equals(Defines.PARAM_IMAGE)) {
                                                    imagesList.add(map);
                                                } else if (type.equals((Defines.PARAM_VIDEO))) {
                                                    videosList.add(map);
                                                }
                                            }
                                            cursor.close();
                                            new UploadFileThread(handler, imagesList, videosList).start();
                                        }
                                    });
                                    dialogBuilder.setNegativeButton(R.string.dialog_upload_all, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startUpload();
                                        }
                                    });
                                    dialogBuilder.create().show();
                                }
                            } else {
                                Toast.makeText(Transfer.getGlobalContext(), R.string.toast_connect_wifi, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(Transfer.getGlobalContext(), R.string.toast_type_ip, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Transfer.getGlobalContext(), R.string.toast_is_uploading, Toast.LENGTH_LONG).show();
                    }
                }
            });

            return rootView;
        }

        private void startUpload() {
            SQLHelper.clear();
            uploadTextThread = new UploadTextThread(handler);
            uploadTextThread.start();
            Transfer.setUploading(true);
        }
    }


}
