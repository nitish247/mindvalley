package ntsh.tech.photolibrary.util;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by lenovo on 12-Apr-18.
 */

public interface DownloaderTasks<T> {
    Context getContext();
    void beforeProcess();
    void afterProcess(T result);
    void updateProgress(Integer progress);
}
