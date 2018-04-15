package ntsh.tech.photolibrary.util;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONObject;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by lenovo on 12-Apr-18.
 */

public class CacheManager {

    //Static instance of CacheManager to be used across the application (uses Singleton Pattern)
    private static CacheManager cacheManager = new CacheManager();

    //Using thread-safe implementation of dequeue
    private ConcurrentLinkedDeque<String> imageUsageQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<String> jsonUsageQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<String> textUsageQueue = new ConcurrentLinkedDeque<>();

    private long cacheSize = 0;
    public static final long MAX_CACHE = 5*1024*1024; //5 MB max size
    private Hashtable<String, Bitmap> imageCache;
    private Hashtable<String, JSONObject> jsonCache;
    private Hashtable<String, String> textCache;

    private CacheManager() {
        imageCache = new Hashtable<>();
        jsonCache = new Hashtable<>();
        textCache = new Hashtable<>();
    }

    public long getCacheSize() {
        return this.cacheSize;
    }

    public void clearCache() {
        imageCache.clear();
        jsonCache.clear();
        textCache.clear();
        cacheSize = 0;
    }

    public static CacheManager getInstance() {
        return cacheManager;
    }

    private long getAvailableCacheSize() {
        return MAX_CACHE-cacheSize;
    }

    public boolean addImageToCache(String url, Bitmap bitmap) {
        if(bitmap==null)
            return false;
        long size = bitmap.getByteCount();
        if(size>MAX_CACHE)      //if the image is larger than cache size
            return false;

        if(size>getAvailableCacheSize()) {      //if the image size is larger than available cache size
            while(size>getAvailableCacheSize()){
                removeLeastUsedImageFromCache();    //free some cache space by removing least used images
            }
        }

        imageUsageQueue.addLast(url);
        imageCache.put(url, bitmap);
        cacheSize += size;
        Log.d("CACHE_SIZE", String.valueOf(cacheSize/1024) + " KB");
        return true;
    }

    public boolean addJsonToCache(String url, JSONObject json) {
        if(json==null)
            return false;
        long size = json.toString().getBytes().length;
        if(size>MAX_CACHE)
            return false;

        if(size>getAvailableCacheSize()) {
            while(size>getAvailableCacheSize()){
                removeLeastUsedJsonFromCache();
            }
        }

        jsonUsageQueue.addLast(url);
        jsonCache.put(url, json);
        cacheSize += size;
        Log.d("CACHE_SIZE", String.valueOf(cacheSize/1024) + " KB");
        return true;
    }

    public boolean addTextToCache(String url, String text) {
        if(text==null)
            return false;
        long size = text.getBytes().length;
        if(size>MAX_CACHE)
            return false;

        if(size>getAvailableCacheSize()) {
            while(size>getAvailableCacheSize()){
                removeLeastUsedTextFromCache();
            }
        }

        textUsageQueue.addLast(url);
        textCache.put(url, text);
        cacheSize += size;
        Log.d("CACHE_SIZE", String.valueOf(cacheSize/1024) + " KB");
        return true;
    }

    public Bitmap checkImageInCache(String url) {
        Bitmap bitmap = imageCache.get(url);
        if(bitmap!=null) {
            imageUsageQueue.remove(url);
            imageUsageQueue.addFirst(url);
        }
        return bitmap;
    }

    public JSONObject checkJsonInCache(String url) {
        JSONObject json = jsonCache.get(url);
        if(json!=null) {
            jsonUsageQueue.remove(url);
            jsonUsageQueue.addFirst(url);
        }
        return json;
    }

    public String checkTextInCache(String url) {
        String text = textCache.get(url);
        if(text!=null) {
            textUsageQueue.remove(url);
            textUsageQueue.addFirst(url);
        }
        return text;
    }

    public void removeLeastUsedImageFromCache() {
        synchronized (this) {
            Log.d("CACHE_MAX", "Cache max size exceeded");
            String leastUrl = imageUsageQueue.getLast();
            imageUsageQueue.removeLast();       //remove least used element from the queue
            Bitmap leastUsedBitmap = imageCache.get(leastUrl);
            long size = leastUsedBitmap.getByteCount();     //remove the bitmap from the cache
            cacheSize -= size;
            imageCache.remove(leastUrl);
        }

    }

    public void removeLeastUsedJsonFromCache() {
        synchronized (this) {
            Log.d("CACHE_MAX", "Cache max size exceeded");
            String leastUrl = jsonUsageQueue.getLast();
            jsonUsageQueue.removeLast();

            JSONObject leastUsedJson = jsonCache.get(leastUrl);
            long size = leastUsedJson.toString().getBytes().length;
            cacheSize -= size;

            jsonCache.remove(leastUrl);
        }
    }

    public void removeLeastUsedTextFromCache() {
        synchronized (this) {
            Log.d("CACHE_MAX", "Cache max size exceeded");
            String leastUrl = textUsageQueue.getLast();
            textUsageQueue.removeLast();

            String leastUsedText = textCache.get(leastUrl);
            long size = leastUsedText.getBytes().length;
            cacheSize -= size;

            textCache.remove(leastUrl);
        }
    }

}
