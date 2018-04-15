package ntsh.tech.photolibrary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ntsh.tech.photolibrary.util.CacheManager;
import ntsh.tech.photolibrary.util.ImageLoaderListener;
import ntsh.tech.photolibrary.util.LoaderResponseListener;
import ntsh.tech.photolibrary.util.ResourceLoader;

public class MainActivity extends AppCompatActivity {

    private final String source = "http://pastebin.com/raw/wgkJgazE";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<String> urlsList = new ArrayList<>();
    private List<String> largeUrlsList = new ArrayList<>();
    private CustomGrid gridAdapter;
    private TextView txtDownloadProgress;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        loadDataFromServer();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
        txtDownloadProgress = (TextView) findViewById(R.id.downloadProgress);
        txtDownloadProgress.setText("");
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                urlsList.clear();
                largeUrlsList.clear();
                if(gridAdapter!=null)
                    gridAdapter.notifyDataSetChanged();
                CacheManager.getInstance().clearCache();
                loadDataFromServer();

            }
        });
    }

    public static boolean checkStoragePermission(Context context){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    private void downloadFile() {
        //Asks for the Storage permission if not present
        if(!checkStoragePermission(MainActivity.this)){
            List<String> permissionsList=new ArrayList<>();
            permissionsList.add("android.permission.READ_EXTERNAL_STORAGE");
            permissionsList.add("android.permission.WRITE_EXTERNAL_STORAGE");
            String[] permissionsArray = new String[permissionsList.size()];
            ActivityCompat.requestPermissions(MainActivity.this, permissionsList.toArray(permissionsArray), 100);
            return;
        }

        final String urlDownload = "http://www.undergraduatelibrary.org/system/files/1109.pdf";
        final String folderName = Environment.getExternalStorageDirectory().getPath() + "/PhotoLibrary";
        final String fileName = folderName + "/1109.pdf";

        File folder = new File(folderName);
        if(!folder.exists())
            folder.mkdir();

        Toast.makeText(getApplicationContext(), "The file will be downloaded!", Toast.LENGTH_SHORT).show();
        ResourceLoader.getInstance().downloadFile(MainActivity.this, urlDownload, fileName, new LoaderResponseListener<File>() {
            @Override
            public void onResponse(File response) {
                Toast.makeText(getApplicationContext(), "File downloaded successfully! " + response.getPath(), Toast.LENGTH_LONG).show();
                txtDownloadProgress.setText("");

            }

            @Override
            public void onFailure(int failureCode) {
                Toast.makeText(getApplicationContext(), "File downloaded failed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateProgress(Integer progress) {
                txtDownloadProgress.setText(String.valueOf(progress)+"% downloaded");
            }
        });
    }

    //Loads data from the URL in JSON format
    private void loadDataFromServer() {
        ResourceLoader.getInstance().loadJSON(MainActivity.this, source, new LoaderResponseListener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    initialize(response);
                    if(mSwipeRefreshLayout!=null)
                        mSwipeRefreshLayout.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int failureCode) {
                Toast.makeText(MainActivity.this, "Error loading data from the server", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateProgress(Integer progress) {

            }
        });
    }

    //Initializes content using data in JSON from the server
    private void initialize(JSONObject jsonResponse) throws JSONException {
        JSONArray usersArray = jsonResponse.getJSONArray("series");
        JSONObject firstUser = (JSONObject) usersArray.get(0);
        JSONObject firstUserData = (JSONObject) firstUser.get("user");
        JSONObject firstProfileImages = (JSONObject) firstUserData.get("profile_image");   //gets profile image of the first user
        String userName = firstUserData.getString("name");

        ((TextView)findViewById(R.id.userProfileName)).setText(userName);

        //Abstract image loader
        ResourceLoader.getInstance().loadImage(MainActivity.this,
                firstProfileImages.getString("medium"),
                R.id.userProfileImage,
                new ImageLoaderListener() {
                    @Override
                    public void onFailure(int idImageView, int failureCode) {
                        Log.d("ImageLoad", "Profile image load failed!");
                    }
                });

        Log.d("array", usersArray.toString());

        //Store URL one image from each user in an ArrayList
        for(int i=0;i<usersArray.length();i++){
            JSONObject userData = (JSONObject) usersArray.get(i);
            JSONObject urlsData = (JSONObject) userData.get("urls");
            urlsList.add(urlsData.getString("thumb"));  //URLs for thumbnails
            largeUrlsList.add(urlsData.getString("regular"));   //URL for loading image when the user clicks on any thumbnail
        }

        gridAdapter = new CustomGrid(MainActivity.this, urlsList, largeUrlsList);   //Initialize gridAdapter
        GridView grid=(GridView)findViewById(R.id.grid);
        grid.setAdapter(gridAdapter);
    }
}
