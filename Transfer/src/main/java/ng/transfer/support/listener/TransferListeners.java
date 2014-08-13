package ng.transfer.support.listener;

/**
 * Created by Joe on 2014/6/11.
 */
public class TransferListeners {

    public interface UploadProgressListener {
        public void progress(long transferred, long fileSize);
        public void progressString(long currentIndex, long allFilesCount);
        public void fileType(String type);
    }

}
