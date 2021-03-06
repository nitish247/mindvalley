package ntsh.tech.photolibrary.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by lenovo on 12-Apr-18.
 */

public class ResourceLoader {

    //Single instance of the ResourceLoader to be used across the application (uses Singleton Pattern)
    private static ResourceLoader loader = new ResourceLoader();

    private ResourceLoader() {

    }

    public static ResourceLoader getInstance() {

        return loader;
    }


    //For abstract loading of images directly on ImageView with ID
    public void loadImage(final Context context, final String url, final int idImageView, final ImageLoaderListener responseListener) {
        ImageDownloader downloadTask = new ImageDownloader(new DownloaderTasks<Bitmap>() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public void beforeProcess() {

            }

            @Override
            public void afterProcess(Bitmap result) {
                if(result!=null) {
                    ImageView imageView = ((Activity) context).findViewById(idImageView);
                    imageView.setImageBitmap(result);
                }
                else {
                    responseListener.onFailure(idImageView, 0x0001);
                }
            }

            @Override
            public void updateProgress(Integer progress) {

            }
        }, url);
        downloadTask.fetch();
    }

    //For abstract loading of images directly on ImageView using the reference
    public void loadImage(final Context context, final String url, final ImageView imageView, final ImageLoaderListener responseListener) {
        ImageDownloader downloadTask = new ImageDownloader(new DownloaderTasks<Bitmap>() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public void beforeProcess() {

            }

            @Override
            public void afterProcess(Bitmap result) {
                if(result!=null) {
                    imageView.setImageBitmap(result);
                }
                else {
                    responseListener.onFailure(0, 0x0001);
                }
            }

            @Override
            public void updateProgress(Integer progress) {

            }
        }, url);
        downloadTask.fetch();
    }

    //For abstract loading of JSON
    public void loadJSON(final Context context, final String url, final LoaderResponseListener<JSONObject> responseListener) {
        JSONDownloader jsonDownloader = new JSONDownloader(new DownloaderTasks<JSONObject>() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public void beforeProcess() {

            }

            @Override
            public void afterProcess(JSONObject result) {
                if(result!=null)
                    responseListener.onResponse(result);
                else
                    responseListener.onFailure(1);
            }

            @Override
            public void updateProgress(Integer progress) {

            }
        }, url);
        jsonDownloader.fetch();
    }

    //For abstract loading of plain text (XML, HTML etc.)
    public void loadPlainText(final Context context, final String url, final LoaderResponseListener<String> responseListener) {
        PlainTextDownloader textDownloader = new PlainTextDownloader (new DownloaderTasks<String>() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public void beforeProcess() {

            }

            @Override
            public void afterProcess(String result) {
                if(result!=null)
                    responseListener.onResponse(result);
                else
                    responseListener.onFailure(1);
            }

            @Override
            public void updateProgress(Integer progress) {

            }
        }, url);
        textDownloader.fetch();
    }

    //For abstract downloading of files (need to provide URL and fileName on internal storage)
    public void downloadFile(final Context context, final String url, final String fileName, final LoaderResponseListener<File> responseListener) {
        FileDownloader fileDownloader = new FileDownloader(new DownloaderTasks<File>() {
            @Override
            public Context getContext() {
                return context;
            }

            @Override
            public void beforeProcess() {
                updateProgress(0);
            }

            @Override
            public void afterProcess(File downloadedFile) {
                responseListener.onResponse(downloadedFile);
            }

            @Override
            public void updateProgress(Integer progress) {
                responseListener.updateProgress(progress);
            }
        }, url, fileName);

        fileDownloader.download();
    }
}