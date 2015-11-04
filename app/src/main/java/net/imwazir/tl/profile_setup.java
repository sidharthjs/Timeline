package net.imwazir.tl;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
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

public class profile_setup extends Activity {

    private EditText et_signup_name, et_signup_url, et_signup_sdescrpt;

    String signup_email,signup_usr,signup_pass,signup_name,signup_url,signup_sdescrpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        Intent intent = getIntent();
        signup_email = intent.getStringExtra("email");
        signup_usr = intent.getStringExtra("usr");
        signup_pass = intent.getStringExtra("pass");

        et_signup_name = (EditText) findViewById(R.id.et_signup_name);
        et_signup_url = (EditText) findViewById(R.id.et_signup_url);
        et_signup_sdescrpt = (EditText) findViewById(R.id.et_signup_sdescrpt);


    }

    public void signup_create() {

        signup_name = et_signup_name.getText().toString();
        signup_url = et_signup_url.getText().toString();
        signup_sdescrpt = et_signup_sdescrpt.getText().toString();


        class signupCreateAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(profile_setup.this, "Please wait", "Creating your account..");
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String link = "http://tl.xtnote.com/signup.php";
                    String postData = "q=" + URLEncoder.encode("0", "UTF-8") + "&data=" + URLEncoder.encode(params[0], "UTF-8");

                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setDoOutput(true);

                    DataOutputStream contentWriter = new DataOutputStream(con.getOutputStream());
                    contentWriter.writeBytes(postData);
                    contentWriter.flush();
                    contentWriter.close();

                    BufferedReader contentReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String itr=contentReader.readLine();
                    return itr;
                }

                catch(IOException e) {
                    return new String("");
                }

            }

            @Override
            protected void onPostExecute(String result) {

                loadingDialog.dismiss();
                if(result.equals("1")) {
                    Toast.makeText(getApplicationContext(),"Account has been created..!",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(profile_setup.this, MainActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(getApplicationContext(),"Something went wrong. Try agian..!",Toast.LENGTH_LONG).show();

            }
        }

        JSONObject json = new JSONObject();
        try {
            json.put("usr", signup_usr);
            json.put("email", signup_email);
            json.put("pass", signup_pass);
            json.put("name", signup_name);
            json.put("url", signup_url);
            json.put("sdescrpt", signup_sdescrpt);


        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
        String usr_data = json.toString();

        new signupCreateAsync().execute(usr_data);

    }

}
