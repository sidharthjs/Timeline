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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class profile extends Activity {

    public String sid, name, email, url, sdescrpt;

    public TextView tv_name_val, tv_email_val, tv_url_val, tv_sdescrpt_val;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        tv_name_val = (TextView) findViewById(R.id.tv_name_val);
        tv_email_val = (TextView) findViewById(R.id.tv_email_val);
        tv_url_val = (TextView) findViewById(R.id.tv_url_val);
        tv_sdescrpt_val = (TextView) findViewById(R.id.tv_sdescrpt_val);

        if(sid.isEmpty()) {
            finish();
        }

        else {

            class profileAsync extends AsyncTask<String, Void, String> {

                private Dialog loadingDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loadingDialog = ProgressDialog.show(profile.this, "Please wait", "loading..");
                }

                @Override
                protected String doInBackground(String... params) {

                    String sid = params[0];

                    try {

                        String link = "http://tl.xtnote.com/profile.php";
                        String postData = "sid=" + URLEncoder.encode(sid, "UTF-8") + "&q=" + URLEncoder.encode("1", "UTF-8");

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
                        return new String("Operation Failed:\n" + e);
                    }
                }

                @Override
                protected void onPostExecute(String result) {
                    loadingDialog.dismiss();

                    if(result.equals(""))
                    {
                        Toast.makeText(getApplicationContext(),"Couldn't able to load the profile..", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    else {
                        //code tho prase the result var and display them in textviews..
                        try {
                            JSONObject json = new JSONObject(result);
                            name = json.getString("name");
                            email = json.getString("email");
                            url = json.getString("url");
                            sdescrpt = json.getString("sdescrpt");

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                            finish();
                        }

                        tv_name_val.setText(name);
                        tv_email_val.setText(email);
                        tv_url_val.setText(url);
                        tv_sdescrpt_val.setText(sdescrpt);
                    }
                }
            }
            new profileAsync().execute(sid);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_edit:

                Intent intent = new Intent(profile.this, profile_edit.class);
                intent.putExtra("sid", sid);
                intent.putExtra("name",tv_name_val.getText().toString());
                intent.putExtra("email",tv_email_val.getText().toString());
                intent.putExtra("url",tv_url_val.getText().toString());
                intent.putExtra("sdescrpt",tv_sdescrpt_val.getText().toString());
                startActivity(intent);

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }


}
