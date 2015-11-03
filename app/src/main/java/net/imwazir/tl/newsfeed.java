package net.imwazir.tl;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class newsfeed extends Activity {

    private EditText et_post;

    public String sid, post_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);

        et_post = (EditText) findViewById(R.id.et_post);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_newsfeed, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void post(View view) {


        post_data = et_post.getText().toString();

        if(sid.isEmpty()) {
            finish();
        }

        else if(post_data.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Type something..", Toast.LENGTH_SHORT).show();
        }

        else {

            class PostAsync extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    Toast.makeText(getApplicationContext(), "Posting data..", Toast.LENGTH_SHORT).show();
                }

                @Override
                protected String doInBackground(String... params) {

                    String sid = params[0];
                    String post_data = params[1];

                    try {

                        String link = "http://tl.xtnote.com/post.php";
                        String postData = "sid=" + URLEncoder.encode(sid, "UTF-8") + "&data=" + URLEncoder.encode(post_data, "UTF-8");

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
                        return new String("");
                    }
                }

                @Override
                protected void onPostExecute(String result) {

                    if(result.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    }
                    else if(result.equals("")) {
                        Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            new PostAsync().execute(sid, post_data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:

                Intent intent = new Intent(newsfeed.this, profile.class);
                intent.putExtra("sid", sid);
                startActivity(intent);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
