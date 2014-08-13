package ng.transfer.support.net;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import ng.transfer.support.info.Defines;
import ng.transfer.support.info.Transfer;
import ng.transfer.support.listener.TransferListeners;


/**
 * Created by JoeWoo on 13-11-23.
 */
public class HttpUtility {

    public static final int CONNECT_TIMEOUT = 10 * 1000;
    public static final int READ_TIMEOUT = 60 * 1000;

    public static final int UPLOAD_CONNECT_TIMEOUT = 10 * 1000;
    public static final int UPLOAD_READ_TIMEOUT = 60 * 1000;

    public static String doUploadFiles(List<Map<String, String>> imagesList, List<Map<String, String>> videosList, final TransferListeners.UploadProgressListener listener) throws Exception {
        StringBuilder allReturnedData = new StringBuilder("");
        int index = 0;
        String fileName;
        String fileType;

        HashMap<String, String> param = new HashMap<String, String>();
        param.put(Defines.PARAM_UUID, Transfer.getUUID());
        param.put(Defines.PARAM_FILES_COUNT, String.valueOf(imagesList.size() + videosList.size()));

        if (imagesList.size() > 0 && Transfer.getUploadParams().get(Defines.UPLOAD_IMAGES)) {
            listener.fileType(Defines.PARAM_IMAGE);
            for (Map<String, String> map : imagesList) {
                index++;
                File file = new File(map.get(Defines.PARAM_FILE_PATH));

                fileName = map.get(Defines.PARAM_FILE_NAME);
                fileType = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());

                param.remove(Defines.PARAM_FILE_TYPE);
                param.put(Defines.PARAM_FILE_TYPE, fileType);
                param.remove(Defines.PARAM_FILE_SIZE);
                param.put(Defines.PARAM_FILE_SIZE, map.get(Defines.PARAM_FILE_SIZE));
                param.remove(Defines.PARAM_FILE_NAME);
                param.put(Defines.PARAM_FILE_NAME, fileName);
                param.remove(Defines.PARAM_FILE_PATH);
                param.put(Defines.PARAM_FILE_PATH, map.get(Defines.PARAM_FILE_PATH));
                param.remove(Defines.PARAM_CURRENT_FILE_NUM);
                param.put(Defines.PARAM_CURRENT_FILE_NUM, String.valueOf(index));
                param.remove(Defines.PARAM_FILE_MODIFIED_DATE);
                param.put(Defines.PARAM_FILE_MODIFIED_DATE, map.get(Defines.PARAM_FILE_MODIFIED_DATE));

                Log.e(Defines.TAG, "No. " + param.get(Defines.PARAM_CURRENT_FILE_NUM));
                Log.e(Defines.TAG, param.get(Defines.PARAM_FILE_PATH));
                Log.e(Defines.TAG, param.get(Defines.PARAM_FILE_SIZE));

                try {
                    allReturnedData.append(sendSingleFile(file, index, imagesList.size(), param, Defines.PARAM_IMAGE, "image/*", listener));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    allReturnedData.append(index);
                    allReturnedData.append(Defines.PARAM_IMAGE);
                    allReturnedData.append("FAIL");
                    allReturnedData.append(',');
                }
            }
        }

        index = 0;
        if (videosList.size() > 0 && Transfer.getUploadParams().get(Defines.UPLOAD_VIDEOS)) {
            listener.fileType(Defines.PARAM_VIDEO);
            for (Map<String, String> map : videosList) {
                index++;
                File file = new File(map.get(Defines.PARAM_FILE_PATH));

                fileName = map.get(Defines.PARAM_FILE_NAME);
                fileType = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());

                param.remove(Defines.PARAM_FILE_TYPE);
                param.put(Defines.PARAM_FILE_TYPE, fileType);
                param.remove(Defines.PARAM_FILE_SIZE);
                param.put(Defines.PARAM_FILE_SIZE, map.get(Defines.PARAM_FILE_SIZE));
                param.remove(Defines.PARAM_FILE_NAME);
                param.put(Defines.PARAM_FILE_NAME, fileName);
                param.remove(Defines.PARAM_FILE_PATH);
                param.put(Defines.PARAM_FILE_PATH, map.get(Defines.PARAM_FILE_PATH));
                param.remove(Defines.PARAM_CURRENT_FILE_NUM);
                param.put(Defines.PARAM_CURRENT_FILE_NUM, String.valueOf(index));
                param.remove(Defines.PARAM_FILE_MODIFIED_DATE);
                param.put(Defines.PARAM_FILE_MODIFIED_DATE, map.get(Defines.PARAM_FILE_MODIFIED_DATE));


                Log.e(Defines.TAG, "No. " + param.get(Defines.PARAM_CURRENT_FILE_NUM));
                Log.e(Defines.TAG, param.get(Defines.PARAM_FILE_PATH));
                Log.e(Defines.TAG, param.get(Defines.PARAM_FILE_SIZE));

                    try {
                        allReturnedData.append(sendSingleFile(file, index, videosList.size(), param, Defines.PARAM_VIDEO, "video/*", listener));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.e("EXCEPTION", "FileNotFound2");
                    } catch (Exception ex) {
                        allReturnedData.append(index);
                        allReturnedData.append(Defines.PARAM_VIDEO);
                        allReturnedData.append("FAIL");
                        allReturnedData.append(',');
                        Log.e("EXCEPTION", "Exception2");
                    }
                }

        }

        return allReturnedData.toString();
    }

    public static String doPost(String urlStr, Map<String, String> param) throws Exception {
        try {
            Log.e(Defines.TAG, "URL to POST: " + urlStr);
            Log.e(Defines.TAG, "POST Param: " + encodeParamFromMap(param));

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate");

            conn.connect();

            DataOutputStream out = new DataOutputStream(conn.getOutputStream());
            out.write(encodeParamFromMap(param).getBytes());
            out.flush();
            out.close();

            return handleResponse(conn);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static StringBuilder sendSingleFile(File targetFile, long index, long allFilesCount, Map<String, String> param, String fileField, String fileTypeInBon, TransferListeners.UploadProgressListener listener) throws Exception {
        long transferred = 0;
        long fileSize = targetFile.length();
        StringBuilder returnData = new StringBuilder("");
        URL url = new URL(Transfer.getUrlToUploadFiles());
        String BOUNDARYSTR = getBoundry();
        byte[] barry = null;
        int contentLength = 0;
        String sendStr = "";
        try {
            barry = ("--" + BOUNDARYSTR + "--\r\n").getBytes("UTF-8");

            sendStr = getBoundaryMessage(BOUNDARYSTR, param, fileField, targetFile.getName(), fileTypeInBon);
            contentLength = sendStr.getBytes("UTF-8").length + (int) targetFile.length() + 2 * barry.length;
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpURLConnection conn;
        BufferedOutputStream out = null;
        FileInputStream fis = null;
        try {

            conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(UPLOAD_CONNECT_TIMEOUT);
            conn.setReadTimeout(UPLOAD_READ_TIMEOUT);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-type", "multipart/form-data;boundary=" + BOUNDARYSTR);
            conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
            conn.setFixedLengthStreamingMode(contentLength);

            conn.connect();

            out = new BufferedOutputStream(conn.getOutputStream());
            out.write(sendStr.getBytes("UTF-8"));
            fis = new FileInputStream(targetFile);

            int bytesRead;
            int bytesAvailable;
            int bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024;
            long output = 0;
            bytesAvailable = fis.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fis.read(buffer, 0, bufferSize);
            final Thread thread = Thread.currentThread();
            while (bytesRead > 0) {
                if (thread.isInterrupted()) {
                    throw new Exception();
                }

                out.write(buffer, 0, bufferSize);
                transferred += bufferSize;
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
                output += bytesRead;
                if (output % 50 == 0)
                    out.flush();
                if (listener != null) {
                    listener.progressString(index, allFilesCount);
                    listener.progress(transferred, fileSize);
                }

            }

            out.write(barry);
            out.write(barry);
            out.flush();
            out.close();

            returnData.append(index);
            returnData.append(fileField);
            returnData.append(handleResponse(conn));
            returnData.append(',');

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("EXCEPTION", "FileNotFound1");
            throw new FileNotFoundException();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.e("EXCEPTION", "Exception1");
            throw new Exception();
        } finally {
            try {
                if (out != null)
                    out.close();
                if (fis != null)
                    fis.close();
            } catch (Exception ignored) {}
        }
        return returnData;
    }

    private static String handleResponse(HttpURLConnection conn) throws Exception {
        InputStream is = null;
        BufferedReader br = null;

        try {

            Log.e(Defines.TAG, "http code: " + String.valueOf(conn.getResponseCode()));

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
                is = conn.getInputStream();
            else
                is = conn.getErrorStream();


            String content_encode = conn.getContentEncoding();

            if (content_encode != null && !content_encode.isEmpty() && content_encode.equals("gzip")) {
                is = new GZIPInputStream(is);
            }

            br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder("");
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            Log.e(Defines.TAG, "Http result: " + sb);

            return sb.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (is != null)
                is.close();
            if (br != null)
                br.close();
        }


    }

    private static String encodeParamFromMap(Map<String, String> param) {

        if (param == null)
            return "";

        StringBuilder sb = new StringBuilder("");

        boolean first = true;

        for (Map.Entry<String, String> stringStringEntry : param.entrySet()) {
            String value = stringStringEntry.getValue();
            if (first)
                first = false;
            else
                sb.append('&');

            try {
                sb.append(URLEncoder.encode(stringStringEntry.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(value, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return sb.toString();
    }

    private static String getBoundry() {
        StringBuilder _sb = new StringBuilder("");
        for (int t = 1; t < 12; t++) {
            long time = System.currentTimeMillis() + t;
            if (time % 3 == 0) {
                _sb.append((char) time % 9);
            } else if (time % 3 == 1) {
                _sb.append((char) (65 + time % 26));
            } else {
                _sb.append((char) (97 + time % 26));
            }
        }
        return _sb.toString();
    }

    private static String getBoundaryMessage(String boundary, Map params, String fileField, String fileName, String fileType) {
        StringBuilder res = new StringBuilder("--").append(boundary).append("\r\n");

        for (Object o : params.keySet()) {
            String key = (String) o;
            String value = (String) params.get(key);
            res.append("Content-Disposition: form-data; name=\"")
                    .append(key).append("\"\r\n").append("\r\n")
                    .append(value).append("\r\n").append("--")
                    .append(boundary).append("\r\n");
        }
        res.append("Content-Disposition: form-data; name=\"").append(fileField)
                .append("\"; filename=\"").append(fileName)
                .append("\"\r\n").append("Content-Type: ")
                .append(fileType).append("\r\n\r\n");

        return res.toString();
    }

}

