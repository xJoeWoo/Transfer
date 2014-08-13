package ng.transfer.support.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Joe on 2014/6/16.
 */
public class FormFile {

    /* 上传文件的数据 */
    private byte[] data;
    private InputStream inStream;
    private File file;
    /* 文件名称 */
    private String filname;
    /* 请求参数名称*/
    private String parameterName;
    /* 内容类型 */
    private String contentType = "application/octet-stream";

    public FormFile(String targetFileName, byte[] data, String paramName) {
        this.data = data;
        this.filname = targetFileName;
        this.parameterName = paramName;
    }

    public FormFile(String targetFileName, File file, String paramName) {
        this.filname = targetFileName;
        this.parameterName = paramName;
        this.file = file;
        try {
            this.inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public InputStream getInStream() {
        return inStream;
    }

    public byte[] getData() {
        return data;
    }

    public String getFilname() {
        return filname;
    }

    public void setFilname(String filname) {
        this.filname = filname;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}