package net.imwazir.tl;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class subscriber extends Activity {

    Button add, rmv;
    public String sid, MainResult;
    String elementName,values;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscriber);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");

        add = (Button)findViewById(R.id.button);
        rmv = (Button)findViewById(R.id.button2);
        lv =  (ListView)findViewById(R.id.listView);
        getList();

    }

    public void getList () {

        class subsAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;
            @Override
            protected void onPreExecute() {

                super.onPreExecute();
                loadingDialog = ProgressDialog.show(subscriber.this, "Please wait", "loading..");

            }

            @Override
            protected String doInBackground(String... params) {

                String sid = params[0];

                try {

                    String link = "http://tl.xtnote.com/subscriber.php";
                    String postData = "sid=" + URLEncoder.encode(sid, "UTF-8") + "&q=" + URLEncoder.encode("00", "UTF-8");

                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setDoOutput(true);

                    DataOutputStream contentWriter = new DataOutputStream(con.getOutputStream());
                    contentWriter.writeBytes(postData);
                    contentWriter.flush();
                    contentWriter.close();

                    BufferedReader contentReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String itr = contentReader.readLine();
                    return itr;
                }

                catch (IOException e) {
                    return e.toString();
                }
            }

            @Override
            protected void onPostExecute(String result) {

                loadingDialog.dismiss();
                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                MainResult = result;

                //Jsonarray to listview
                try {
                    JSONArray jArray = new JSONArray(MainResult);
                    JSONObject json_data;
                    ArrayList<String> items = new ArrayList<String>();
                    for(int i=0; i < jArray.length() ; i++) {
                        json_data = jArray.getJSONObject(i);
                        String name=json_data.getString("subscriber");
                        items.add(name);

                    }
                    //lv.add("new item");
                    //lv.remove("item");

                    ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(subscriber.this, android.R.layout.simple_list_item_single_choice, items);
                    lv.setAdapter(mArrayAdapter);
                }

                catch (Exception e) {

                }
            }
        }
        new subsAsync().execute(sid);
    }


}