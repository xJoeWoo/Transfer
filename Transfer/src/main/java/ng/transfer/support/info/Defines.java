package ng.transfer.support.info;

/**
 * Created by Joe on 2014/6/10.
 */
public class Defines {

    public static final int MSG_UPLOAD_PROGRESS = 0;
    public static final int MSG_UPLOAD_PROGRESS_STRING = 2;
    public static final int MSG_UPLOAD_TYPE = 3;
    public static final int MSG_IMAGES_LIST = 1;

    public static final int UPLOAD_CALL_RECORD = 0;
    public static final int UPLOAD_CONTACTS = 1;
    public static final int UPLOAD_SMS = 2;
    public static final int UPLOAD_IMAGES = 3;
    public static final int UPLOAD_VIDEOS = 4;

    public static final String TAG = "Transfer";

    public static final float STATUS_FINISHED = 200;
    public static final float STATUS_FAILED = -4;
    public static final float STATUS_RESET = -2;
    public static final float STATUS_PAUSE = -1;
    public static final float STATUS_READY = -3;
    public static final float STATUS_SCAN_IMAGES = -5;
    public static final float STATUS_SCAN_VIDEOS = -6;
    public static final float STATUS_UPLOADING_IMAGES = -7;
    public static final float STATUS_UPLOADING_VIDEOS = -8;

    public static final String PARAM_JSON = "json";
    public static final String PARAM_IMAGE = "image";
    public static final String PARAM_VIDEO = "video";
    public static final String PARAM_UUID = "uuid";
    public static final String PARAM_BRAND = "brand";
    public static final String PARAM_MODEL = "model";
    public static final String PARAM_FILES_COUNT = "filesCount";
    public static final String PARAM_FILE_NAME = "fileName";
    public static final String PARAM_FILE_PATH = "filePath";
    public static final String PARAM_FILE_MODIFIED_DATE = "fileModifiedDate";
    public static final String PARAM_CURRENT_FILE_NUM = "currentFileNum";
    public static final String PARAM_FILE_TYPE = "fileType";
    public static final String PARAM_FILE_SIZE = "fileSize";
    public static final String PARAM_VIDEO_LENGTH = "videoLength";

    public static final String RETURN_SUCCESS = "SUCCESS";
    public static final String RETURN_FAIL = "FAIL";

}
