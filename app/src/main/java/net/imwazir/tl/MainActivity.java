package net.imwazir.tl;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends Activity {

    private EditText et_username, et_passwd;
    public TextView tv_result;
    String username, passwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = (EditText) findViewById(R.id.et_username);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        tv_result = (TextView) findViewById(R.id.tv_result);
    }

    public void login(View view) {

        username = et_username.getText().toString();
        passwd = et_passwd.getText().toString();

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Signing in..");
            }

            @Override
            protected String doInBackground(String... params) {

                String usr = params[0];
                String pass = params[1];

                try {

                    String link = "http://tl.xtnote.com/login.php";
                    String postData = "usr=" + URLEncoder.encode(usr, "UTF-8") + "&pass=" + URLEncoder.encode(pass, "UTF-8");

                    URL url = new URL(link);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("POST");
                    con.setDoOutput(true);

                    DataOutputStream contentWriter = new DataOutputStream(con.getOutputStream());
                    contentWriter.writeBytes(postData);
                    contentWriter.flush();
                    contentWriter.close();

                    BufferedReader contentReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String text = "";
                    String itr = "";
                    while(true) {
                        itr=contentReader.readLine();
                        if(itr==null)
                            break;
                        text +=itr;
                    }
                    return text;
                }

                catch(IOException e) {
                    return new String("Operation Failed:\n"+e);
                }

            }

            @Override
            protected void onPostExecute(String result) {

                loadingDialog.dismiss();
                if(result=="10")
                    tv_result.setText("Password doesn't match..");
                else if(result=="11")
                    tv_result.setText("Username doesn't exist..");
                else if(result=="")
                    tv_result.setText("Something went wrong. Try again..");
                else {
                    Intent intent = new Intent(MainActivity.this, newsfeed.class);
                    intent.putExtra("sid", result);
                    startActivity(intent);
                }

            }
        }

        new LoginAsync().execute(username, passwd);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
