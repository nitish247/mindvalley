package ntsh.tech.photolibrary;

/**
 * Created by lenovo on 15-Apr-18.
 */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ntsh.tech.photolibrary.util.ImageLoaderListener;
import ntsh.tech.photolibrary.util.ResourceLoader;

public class CustomGrid extends BaseAdapter{
    private Context mContext;
    private final List<String> imageUrls;
    private final List<String> largeUrls;

    public CustomGrid(Context c, List<String> imageUrls, List<String> largeUrls) {
        mContext = c;
        this.imageUrls = imageUrls;
        this.largeUrls = largeUrls;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = inflater.inflate(R.layout.grid_item, null);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);

            grid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ShowImageActivity.class);
                    intent.putExtra(ShowImageActivity.LARGE_URL, largeUrls.get(position));
                    mContext.startActivity(intent);
                }
            });

            ResourceLoader.getInstance().loadImage(mContext, imageUrls.get(position), imageView, new ImageLoaderListener() {
                @Override
                public void onFailure(int imageView, int failureCode) {

                }
            });
        } else {
            grid = (View) convertView;
        }

        return grid;
    }
}