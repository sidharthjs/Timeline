package net.imwazir.tl;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class signup extends Activity {

    private EditText et_signup_email, et_signup_usr, et_signup_pass;

    public TextView tv_signup_error;
    public Button bt_signup_next;
    public String signup_usr, signup_pass, signup_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        et_signup_email = (EditText) findViewById(R.id.et_signup_email);
        et_signup_usr = (EditText) findViewById(R.id.et_signup_usr);
        et_signup_pass = (EditText) findViewById(R.id.et_signup_pass);

        tv_signup_error = (TextView) findViewById(R.id.tv_signup_error);

        bt_signup_next = (Button) findViewById(R.id.bt_signup_next);
        bt_signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup_next();
            }
        });


    }

    public void signup_next() {

        signup_usr = et_signup_usr.getText().toString();
        signup_email = et_signup_email.getText().toString();
        signup_pass = et_signup_pass.getText().toString();


        class signupNextAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(signup.this, "Please wait", "Validating the given details..");
            }

            @Override
            protected String doInBackground(String... params) {

                try {

                    String link = "http://tl.xtnote.com/signup.php";
                    String postData = "q=" + URLEncoder.encode("1", "UTF-8") + "&data=" + URLEncoder.encode(params[0], "UTF-8");

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
                    Intent intent = new Intent(signup.this, profile_setup.class);
                    intent.putExtra("email", signup_email);
                    intent.putExtra("usr",signup_usr);
                    intent.putExtra("pass",signup_pass);
                    startActivity(intent);
                }
                else if(result.equals("2"))
                    tv_signup_error.setText("Email address is already registered..");
                else if(result.equals("3"))
                    tv_signup_error.setText("Username already exist..");
                else if(result.equals(""))
                    tv_signup_error.setText("Something went wrong. Try again..");

                else {

                }

            }
        }

        JSONObject json = new JSONObject();
        try {
            json.put("usr", signup_usr);
            json.put("email", signup_email);
            json.put("pass", signup_pass);

        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            finish();
        }
        String usr_data = json.toString();

        new signupNextAsync().execute(usr_data);

    }


}
