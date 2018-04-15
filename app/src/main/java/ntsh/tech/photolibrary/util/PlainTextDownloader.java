package ntsh.tech.photolibrary.util;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lenovo on 12-Apr-18.
 */

public class PlainTextDownloader extends AsyncTask<String, Void, String> {

    private String mUrl;
    private DownloaderTasks<String> mDownloaderTasks;

    public PlainTextDownloader(DownloaderTasks downloaderTasks, String url) {
        this.mDownloaderTasks = downloaderTasks;
        this.mUrl = url;
    }

    public void fetch() {
        String textFromCache = CacheManager.getInstance().checkTextInCache(mUrl);
        if(textFromCache==null){
            this.execute(mUrl);
        }
        else {
            this.onPostExecute(textFromCache);
            Log.d("Cache", "Found text in cache");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDownloaderTasks.beforeProcess();
    }

    @Override
    protected String doInBackground(String... strings) {
        String strUrl = strings[0];
        HttpURLConnection urlConnection = null;
        URL url = null;
        try {
            url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringWriter writer = new StringWriter();
            IOUtils.copy(in, writer);
            String text = writer.toString();
            return text;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        String textFromCache = CacheManager.getInstance().checkTextInCache(mUrl);
        if(textFromCache==null){
            CacheManager.getInstance().addTextToCache(mUrl, result);
        }
        mDownloaderTasks.afterProcess(result);
    }
}
