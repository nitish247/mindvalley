package ntsh.tech.photolibrary.util;

import org.json.JSONObject;

/**
 * Created by lenovo on 12-Apr-18.
 */

public interface LoaderResponseListener<T> {

    void onResponse(T response);
    void onFailure(int failureCode);
    void updateProgress(Integer progress);
}
