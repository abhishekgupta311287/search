package search.abhishek.com.imagesearch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageListView extends ArrayAdapter{
    private final Activity context;
    private ArrayList<String> urls;

    public ImageListView(Activity context,
                      ArrayList<String> urls) {
        super(context, R.layout.list, urls);
        this.context = context;
        this.urls = urls;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list, null, true);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imagelist);
        String url = urls.get(position);
        if (url.contains("http")) {
            Glide.with(context)
                    .load(url) // Uri of the picture
                    .into(imageView);
        } else {
            imageView.setImageResource(Integer.valueOf(url));
        }

        return rowView;
    }
}
