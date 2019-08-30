package com.pixelstudio.listings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String BASE_URL = "https://www.googleapis.com";

    EditText mQueryEditText;
    TextView textNoDataFound;
    ListView listView;
    boolean isConnected;
    ProgressBar mProgressBar;

    BooksAdapter mBooksAdapter;
    String queryEntered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mQueryEditText = findViewById(R.id.query_et);
        textNoDataFound = findViewById(R.id.text_no_data_found);
        mBooksAdapter = new BooksAdapter(this, 0);
        listView = findViewById(R.id.listView);
        listView.setAdapter(mBooksAdapter);
        isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            isConnected = true;
        }
        else
            // Internet connection not available
            isConnected = false;
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemThatWasClickedid = item.getItemId();
        if (itemThatWasClickedid == R.id.action_search) {
            queryEntered = mQueryEditText.getText().toString();
            if (queryEntered.matches("")) {
                Toast.makeText(this, "No query entered. Please enter a query", Toast.LENGTH_SHORT).show();
            }
            else {
                if (isConnected) {
                    makeBooksSearchQuery(queryEntered);
                } else {
                    Toast.makeText(this, "No Internet. Please check your connection", Toast.LENGTH_SHORT).show();
                }
            }
                return true;
            }
        return super.onOptionsItemSelected(item);
    }


    public static URL buildUrl(String queryEntered) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath("books")
                .appendPath("v1")
                .appendPath("volumes")
                .appendQueryParameter("q", queryEntered)
                .appendQueryParameter("maxResults", "20")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    private void makeBooksSearchQuery(String queryEntered) {
        URL booksSearchUrl = buildUrl(queryEntered);
        new BooksQueryTask().execute(booksSearchUrl);
    }

    public class BooksQueryTask extends AsyncTask<URL, Void, List<BooksData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<BooksData> doInBackground(URL... urls) {
            URL url = urls[0];
            List<BooksData> booksSearchResult = QueryUtils.fetchBooksData(url);
            return booksSearchResult;
        }

        @Override
        protected void onPostExecute(List<BooksData> booksData) {
            if (booksData == null) {
                return;
            }
            updateUi(booksData);
        }

        private void updateUi(List<BooksData> booksData) {
            if (booksData.isEmpty()){
                // if no books found, show a message
                textNoDataFound.setVisibility(View.VISIBLE);
            } else {
                textNoDataFound.setVisibility(View.GONE);
            }
            mBooksAdapter.clear();
            mBooksAdapter.addAll(booksData);
        }
    }

}
