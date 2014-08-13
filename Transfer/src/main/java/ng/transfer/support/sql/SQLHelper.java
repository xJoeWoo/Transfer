package ng.transfer.support.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;
import java.util.Map;

import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;


public class SQLHelper extends SQLiteOpenHelper {

    public final static int SQL_VERSION = 1;
    public static final String FAILED_FILES_TABLE = "failed_files_table";
    public final static String COLUMN_ID = "id";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_PATH = "path";
    public final static String COLUMN_TYPE = "type";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + FAILED_FILES_TABLE
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_NAME + " VARCHAR,"
            + COLUMN_PATH + " VARCHAR NOT NULL UNIQUE,"
            + COLUMN_TYPE + " VARCHAR NOT NULL);";
    public final static String TAG_SQL = "SQL";
    public final static String SQL_NAME = "Transfer.db";

    public SQLHelper(Context context, String name, CursorFactory factory,
                     int version) {
        super(context, name, factory, version);
    }

    public static boolean savePath(List<Map<String, String>> failedFilesList) {
        if (failedFilesList != null && failedFilesList.size() > 0) {
            if (Transfer.getSQL() != null) {
                ContentValues cv = new ContentValues();
                int savedRows = 0;
                for (Map<String, String> map : failedFilesList) {
                    cv.clear();
                    cv.put(SQLHelper.COLUMN_NAME, map.get(Defines.PARAM_FILE_NAME));
                    cv.put(SQLHelper.COLUMN_PATH, map.get(Defines.PARAM_FILE_PATH));
                    cv.put(SQLHelper.COLUMN_TYPE, map.get(Defines.PARAM_FILE_TYPE));
                    try {
                        if (Transfer.getSQL().insertWithOnConflict(FAILED_FILES_TABLE, null, cv, SQLiteDatabase.CONFLICT_IGNORE) > 0) {
                            savedRows++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e(TAG_SQL, String.valueOf(savedRows));
                return savedRows == failedFilesList.size();
            } else
                return false;
        } else
            return false;
    }

    public static boolean hasFails() {
        if (Transfer.getSQL() != null) {
            Cursor cursor = Transfer.getSQL().query(FAILED_FILES_TABLE, new String[]{COLUMN_PATH}, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    Log.e(TAG_SQL, String.valueOf(cursor.getCount()));
                    cursor.close();
                    return true;
                } else {
                    cursor.close();
                    return false;
                }
            } else {
                return false;
            }
        } else
            return false;
    }

    public static Cursor getFailedFilesPath() {
        if (Transfer.getSQL() != null) {
            return Transfer.getSQL().query(FAILED_FILES_TABLE, new String[]{COLUMN_PATH, COLUMN_TYPE}, null, null, null, null, null);
        } else
            return null;
    }

    public static boolean clear() {
        return Transfer.getSQL() != null && Transfer.getSQL().delete(FAILED_FILES_TABLE, null, null) > 0;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
