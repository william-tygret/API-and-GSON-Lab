package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //publickey 5338ba0531e07df1f4b4281a123d6b84
    //privatekey c88085d59c3b842dc535b9b5b4537333b1bc56f5
    //md5hash e117b7ced336aead75dcd57f5d392fe5

    private MarvelAsyncTask mTask;
    ArrayAdapter<MarvelCharacter> mAdapter;
    ArrayList<MarvelCharacter> dataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ListView listView = (ListView)findViewById(R.id.listView);
        dataArray = new ArrayList<>();
        mAdapter = new ArrayAdapter<MarvelCharacter>(this,android.R.layout.simple_list_item_1,android.R.id.text1,dataArray );
        listView.setAdapter(mAdapter);

        final EditText editText = (EditText)findViewById(R.id.editText);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    if (mTask != null && (mTask.getStatus() != AsyncTask.Status.FINISHED)) {
                        mTask.cancel(true);
                    }
                    mTask = new MarvelAsyncTask();
                    String string = editText.getText().toString();
                    final String url = "http://gateway.marvel.com/v1/public/characters?nameStartsWith="+string+"&apikey=fa443fc4b33f7237765dc9a31c13aa7c&ts=12345&hash=bb42fc23b33291300948ce0bfa64a6b3";
                    mTask.execute(url);
                } else {
                    Toast.makeText(MainActivity.this, "No network connection detected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MarvelAsyncTask extends AsyncTask<String, Void, MarvelCharacterSearchResult> {

        @Override
        protected MarvelCharacterSearchResult doInBackground(String... params) {
            String data ="";
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream inStream = connection.getInputStream();
                data = getInputData(inStream);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            MarvelCharacterSearchResult result = gson.fromJson(data,MarvelCharacterSearchResult.class);

            return result;
        }

        @Override
        protected void onPostExecute(MarvelCharacterSearchResult result) {
            super.onPostExecute(result);
            MarvelCharacterSearchData data = result.getData();
            ListView listView = (ListView) findViewById(R.id.listView);
            mAdapter.clear();
            dataArray.addAll(data.getResults());

            mAdapter.notifyDataSetChanged();


        }
    }

    private String getInputData(InputStream inStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));

        String data = null;

        while ((data = reader.readLine()) != null){
            builder.append(data);
        }

        reader.close();

        return builder.toString();
    }
}
