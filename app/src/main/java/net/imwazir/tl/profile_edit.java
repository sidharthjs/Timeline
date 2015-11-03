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
import android.widget.EditText;
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

public class profile_edit extends Activity {

    public String sid, name, email, url, sdescrpt;

    public EditText et_name, et_email, et_url, et_sdescrpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        Intent intent = getIntent();
        sid = intent.getStringExtra("sid");
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        url = intent.getStringExtra("url");
        sdescrpt = intent.getStringExtra("sdescrpt");

        if(sid.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Something went wrong..", Toast.LENGTH_LONG).show();
        }

        else {
            et_name = (EditText) findViewById(R.id.et_name);
            et_email = (EditText) findViewById(R.id.et_email);
            et_url = (EditText) findViewById(R.id.et_url);
            et_sdescrpt = (EditText) findViewById(R.id.et_sdescrpt);

            et_name.setText(name);
            et_email.setText(email);
            et_url.setText(url);
            et_sdescrpt.setText(sdescrpt);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile_save:

                profile_save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void profile_save() {

        class profileSaveAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(profile_edit.this, "Please wait", "Saving details..");
            }

            @Override
            protected String doInBackground(String... params) {

                String sid = params[0];
                String profile_data = params[1];

                try {

                    String link = "http://tl.xtnote.com/profile.php";
                    String postData = "sid=" + URLEncoder.encode(sid, "UTF-8") + "&q=" + URLEncoder.encode("0", "UTF-8") + "&data=" + URLEncoder.encode(profile_data, "UTF-8");

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

                if(result.equals("1"))
                {
                    Toast.makeText(getApplicationContext(),"Profile saved!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(profile_edit.this, profile.class);
                    intent.putExtra("sid", sid);
                    startActivity(intent);
                }

                else {
                    Toast.makeText(getApplicationContext(),"Something went wrong.. Go back and Try again.." + result, Toast.LENGTH_LONG).show();
                }
            }
        }

        String u_name = et_name.getText().toString();
        String u_email = et_email.getText().toString();
        String u_url = et_url.getText().toString();
        String u_sdescrpt = et_sdescrpt.getText().toString();

        JSONObject json = new JSONObject();
        try {
            json.put("name", u_name);
            json.put("email", u_email);
            json.put("url", u_url);
            json.put("sdescrpt", u_sdescrpt);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Error: " + e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
        String profile_data = json.toString();

        new profileSaveAsync().execute(sid,profile_data);

    }

}
