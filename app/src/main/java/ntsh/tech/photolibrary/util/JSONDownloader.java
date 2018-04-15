package ntsh.tech.photolibrary.util;

import android.content.SyncStatusObserver;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by lenovo on 12-Apr-18.
 */

public class JSONDownloader extends AsyncTask<String, Void, JSONObject> {

    private String mUrl;
    private DownloaderTasks<JSONObject> mDownloaderTasks;

    public JSONDownloader(DownloaderTasks downloaderTasks, String url) {
        this.mDownloaderTasks = downloaderTasks;
        this.mUrl = url;
    }

    public void fetch() {
        JSONObject jsonFromCache = CacheManager.getInstance().checkJsonInCache(mUrl);
        if(jsonFromCache==null){
            this.execute(mUrl);
        }
        else {
            this.onPostExecute(jsonFromCache);
            Log.d("Cache", "Found json in cache");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDownloaderTasks.beforeProcess();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        String strUrl = strings[0];
        HttpURLConnection urlConnection = null;
        URL url;
        try {
            url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            byte[] contents = new byte[1024];

            int bytesRead;
            StringBuilder jsonBuilder = new StringBuilder();
            while((bytesRead = in.read(contents)) != -1) {
                jsonBuilder.append(new String(contents, 0, bytesRead));
            }
            String strJson = jsonBuilder.toString();
            if(strJson.substring(0,1).equals("[")){
                strJson = "{\"series\":" + strJson + "}";
            }
            JSONObject jsonObject = new JSONObject(strJson);
            return jsonObject;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {

            e.printStackTrace();
        } finally {
            if(urlConnection!=null)
                urlConnection.disconnect();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        JSONObject jsonFromCache = CacheManager.getInstance().checkJsonInCache(mUrl);
        if(jsonFromCache==null){
            CacheManager.getInstance().addJsonToCache(mUrl, result);
        }
        mDownloaderTasks.afterProcess(result);
    }
}
