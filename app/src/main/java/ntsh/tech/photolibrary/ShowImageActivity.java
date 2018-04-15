package ntsh.tech.photolibrary;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import ntsh.tech.photolibrary.util.ImageLoaderListener;
import ntsh.tech.photolibrary.util.ResourceLoader;

/**
 * Created by lenovo on 15-Apr-18.
 */

public class ShowImageActivity extends AppCompatActivity {

    String imageUrl="";
    public static final String LARGE_URL = "LARGE_URL";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        if(getIntent().hasExtra(LARGE_URL)){
            this.imageUrl = getIntent().getExtras().getString(LARGE_URL);
            ResourceLoader.getInstance().loadImage(ShowImageActivity.this, imageUrl, R.id.imgLarge, new ImageLoaderListener() {
                @Override
                public void onFailure(int imageView, int failureCode) {
                    Toast.makeText(ShowImageActivity.this, "Image loading failed!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
