package ntsh.tech.photolibrary.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.constraint.solver.Cache;
import android.util.Log;
import android.widget.Toast;

import java.io.InputStream;

public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

    private String mUrl;
    private DownloaderTasks<Bitmap> mDownloaderTasks;

    public ImageDownloader(DownloaderTasks downloaderTasks, String url){
        this.mDownloaderTasks = downloaderTasks;
        this.mUrl = url;
    }

    public void fetch() {
        Bitmap bitmapFromCache = CacheManager.getInstance().checkImageInCache(mUrl);
        if(bitmapFromCache==null){
            this.execute(mUrl);
        }
        else {
            this.onPostExecute(bitmapFromCache);
            Log.d("Cache", "Found image in cache");
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDownloaderTasks.beforeProcess();
    }

    @Override
    protected Bitmap doInBackground(String... URL) {

        String imageURL = URL[0];

        Bitmap bitmap = null;
        try {
            InputStream input = new java.net.URL(imageURL).openStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        Bitmap bitmapFromCache = CacheManager.getInstance().checkImageInCache(mUrl);
        if(bitmapFromCache==null){
            CacheManager.getInstance().addImageToCache(mUrl, result);
        }
        mDownloaderTasks.afterProcess(result);
    }
}