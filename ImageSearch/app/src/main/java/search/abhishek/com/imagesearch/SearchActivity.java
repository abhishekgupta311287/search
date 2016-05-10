package search.abhishek.com.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchActivity extends Activity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private final String SEARCH_URL = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=50&pilimit=50&generator=prefixsearch&gpssearch=";
    private Handler handler = new Handler();
    private String keyword = "";
    private ArrayList<String> urls;
    private ListView list;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EditText searchField = (EditText) findViewById(R.id.searchField);
        list = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keyword = s.toString().trim();
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 1000);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (keyword != null && keyword.length() > 0) {
                displaySearchResults(keyword);
            }
        }
    };

    /**
     * Fetches and displays the search results for the passed keyword
     * @param keyword
     */
    public void displaySearchResults(String keyword) {
        progressBar.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
        String url = SEARCH_URL + keyword;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(false, 80, 443);
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        if (response == null) {
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.search_noresults), Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject query = (JSONObject) jsonObject.get("query");
                            JSONObject pages = query.getJSONObject("pages");
                            Iterator iterator = pages.keys();
                            urls = new ArrayList<String>();
                            while (iterator.hasNext()) {
                                String page = (String) iterator.next();
                                JSONObject pageObject = (JSONObject) pages.get(page);
                                if (pageObject.has("thumbnail")) {
                                    JSONObject thumb = pageObject.getJSONObject("thumbnail");
                                    String url = thumb.getString("source");
                                    urls.add(url);
                                } else {
                                    urls.add(String.valueOf(R.drawable.icon));
                                }
                            }

                            if (urls != null && urls.size() > 0) {
                                ImageListView imageListView = new ImageListView(SearchActivity.this, urls);
                                list.setAdapter(imageListView);
                                progressBar.setVisibility(View.GONE);
                                list.setVisibility(View.VISIBLE);
                                Animation animation = AnimationUtils.loadAnimation(SearchActivity.this, R.anim.up_bottom);
                                list.startAnimation(animation);
                            }

                        } catch (Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SearchActivity.this, getResources().getString(R.string.search_error), Toast.LENGTH_LONG).show();
                            Log.d(TAG, e.getMessage());
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, getResources().getString(R.string.search_error), Toast.LENGTH_LONG).show();
                        Log.d(TAG, error.getMessage());
                    }
                }

        );
    }

}
