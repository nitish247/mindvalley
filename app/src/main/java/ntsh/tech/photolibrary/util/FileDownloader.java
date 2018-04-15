package ntsh.tech.photolibrary.util;

import android.os.AsyncTask;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lenovo on 12-Apr-18.
 */

public class FileDownloader extends AsyncTask<String, Integer, File> {

    private String mUrl;
    private String mFileName;
    private DownloaderTasks<File> mDownloaderTasks;

    public FileDownloader(DownloaderTasks downloaderTasks, String url, String fileName) {
        this.mDownloaderTasks = downloaderTasks;
        this.mFileName = fileName;
        this.mUrl = url;
    }

    public void download() {
        this.execute(mUrl);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mDownloaderTasks.updateProgress(values[0]);     //pass the value the view

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDownloaderTasks.beforeProcess();
    }

    @Override
    protected File doInBackground(String... urls) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        File downloadedFileRef = null;
        try {
            URL url = new URL(urls[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(mFileName);

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {

                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (output != null) {
                    output.close();
                    downloadedFileRef = new File(mFileName);    //get the File reference to the downloaded file
                }
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }

        return downloadedFileRef;
    }

    @Override
    protected void onPostExecute(File downloadedFile) {
        mDownloaderTasks.afterProcess(downloadedFile);      //pass the downloaded File reference to the view
    }
}
