package search.abhishek.com.imagesearch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class SearchActivity extends Activity {
    private Handler handler = new Handler();
    private String keyword = "";
    private ArrayList<String> urls;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EditText searchField = (EditText) findViewById(R.id.searchField);
        list = (ListView) findViewById(R.id.list);
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
            getSearchResults(keyword);
        }
    };

    public void getSearchResults(String keyword) {

        String url = "https://en.wikipedia.org/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail&pithumbsize=50&pilimit=50&generator=prefixsearch&gpssearch=" + keyword;
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient(false, 80, 443);
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        if (response == null) {
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
                                JSONObject jsonObject1 = (JSONObject) pages.get(page);
                                if (jsonObject1.has("thumbnail")) {
                                    JSONObject thumb = jsonObject1.getJSONObject("thumbnail");
                                    String url = thumb.getString("source");
                                    urls.add(url);
                                } else {
                                    urls.add(String.valueOf(R.mipmap.ic_launcher));
                                }
                            }

                            if (urls != null && urls.size() > 0) {
                                ImageListView imageListView = new ImageListView(SearchActivity.this,urls);
                                list.setAdapter(imageListView);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable
                            error) {
                    }
                }

        );
    }

}
