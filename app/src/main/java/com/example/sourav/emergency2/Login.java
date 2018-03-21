package com.example.sourav.emergency2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText userid, pwd;
    Float lat,lng;
    Button submit;
    TextView txtresult, txtRegister;
    static final String MY_PREFERENCES = "MyPrefs";
    final String UserName = "KeyName";
    final String Lat = "KeyLat";
    final String Lng = "KeyLng";
    final String Uid = "KeyUid";
    final String LoginStatus = "KeyLoginStatus";
    SharedPreferences sharedPreferences;
    boolean check = false;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkForLogin();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //      ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
//        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        userid = (EditText) findViewById(R.id.uname);
        pwd = (EditText) findViewById(R.id.pwd);
        submit = (Button) findViewById(R.id.btnsubmit);
        submit.setOnClickListener(this);
        txtresult = (TextView) findViewById(R.id.textView);
        txtRegister = (TextView) findViewById(R.id.textView2);
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerRequest = new Intent(getBaseContext(), SignUp.class);
                startActivity(registerRequest);
            }
        });
    }


    String userId, password;
    @Override
    public void onClick(View v) {
        userId = userid.getText().toString();
        password = pwd.getText().toString();
//        try {
//            Long.parseLong(userId);
//                loginType = "true";
//        }
//        catch (NumberFormatException e) {
//            loginType = "false";
//        }
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(getBaseContext(), "Enter your E-mail or Phone No.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getBaseContext(), "Enter your Password", Toast.LENGTH_LONG).show();
            return;
        }


        AsyncLogin login = new AsyncLogin();
        login.execute(userId.trim(), password);
        //Toast.makeText(getApplicationContext(), "Function exit", Toast.LENGTH_LONG).show();

    }
    private class AsyncLogin extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Signing in...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String path = "http://192.168.137.1/login.php";
            String data;
            JSONObject jsonObject;
            try {
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(8000);
                httpURLConnection.setReadTimeout(8000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                data = URLEncoder.encode("usernamelogin", "UTF-8") + "=" + URLEncoder.encode(params[0], "UTF-8") +
                        "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8");
                //+
                //     "&" + URLEncoder.encode("loginType", "UTF-8") + "=" + URLEncoder.encode(params[2], "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.flush();
                outputStreamWriter.close();
                outputStream.close();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader is = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(is);
//                if (bufferedReader != null) {
//                    while ((readline = bufferedReader.readLine()) != null) {
//                        stringBuilder.append(readline + "\n");
//                    }
//                }
//                String result = stringBuilder.toString();
                String result = bufferedReader.readLine();
                jsonObject = new JSONObject(result);
                return jsonObject;
//                int id = jsonObject.getInt("code_auth");
//                if (id == 1) {
//                    Log.d("test", "correct");
//                    return id;
//                } else {
//                    Log.d("test", "incorrect");
//                    return id;
//                }
            } catch (Exception e) {
                Log.d("Error: ", "Connection Failed : " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
//            if(jsonObject == 1) {
//                txtresult.setText("Success");
//            }
//        }
            progressDialog.dismiss();
            if(jsonObject == null) {
                Toast.makeText(getBaseContext(), "Unable to login. Please try again later.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    if (jsonObject.getInt("flag") == 1) {
                        txtresult.setText(jsonObject.getString("message"));
                        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(UserName, jsonObject.getString("uname"));
                        editor.putString(Uid, jsonObject.getString("uid"));
                        editor.putFloat(Lat,Float.valueOf(jsonObject.getString("lat")));
                        editor.putFloat(Lng,Float.valueOf(jsonObject.getString("lng")));
                        lat=Float.valueOf(jsonObject.getString("lat"));
                        lng=Float.valueOf(jsonObject.getString("lng"));
                        Toast.makeText(Login.this, "lat:"+lat+"lng"+lng, Toast.LENGTH_SHORT).show();
                        // editor.putLong(Phone, jsonObject.getLong("phone"));
                        // editor.putString(Email, jsonObject.getString("email"));
                        editor.putBoolean(LoginStatus, true);
                        editor.apply();
                        //Log.d("test", "log2" + sharedPreferences.getBoolean(LoginStatus, false));
                        login(true, jsonObject.getString("message"));
                        //Long nam = sharedPreferences.getLong(Phone, 0L);
                        //txtresult.setText(jsonObject.getString("message") + " " + nam);
                    } else if (jsonObject.getInt("flag") == 0) {
                        login(false, jsonObject.getString("message"));
//                    if (jsonObject.getInt("db") == 1) {
//                        txtresult.setText(txtresult.getText() + " User Found " + jsonObject.getInt("db") + " ");
//                    }
//                    else if (jsonObject.getInt("db") == 0) {
//                        txtresult.setText(" User not found " + jsonObject.getInt("db") + " ");
//                        if (jsonObject.getInt("conn") == 0) {
//                            txtresult.setText(txtresult.getText() + "Connection Error" + jsonObject.getInt("code_conn") + " ");
//                        }
//                        else {
//                            txtresult.setText(txtresult.getText() + "Unknown Error" + jsonObject.getInt("code_conn") + " ");
//                        }
//                    }
                    }
                } catch (Exception e) {
                    Log.d("Error: ", "Unable to login. " + e);
                }
            }
//           try {
//                txtresult.setText("Hurr" + jsonObject.getInt("code_conn"));
//                if (jsonObject.getInt("code_conn") == 1) {
//                    txtresult.setText("Connection " + jsonObject.getInt("code_conn") + " ");
//                    if (jsonObject.getInt("db") == 1) {
//                        txtresult.setText(txtresult.getText() + "User Exists" + jsonObject.getInt("db") + " ");
//                        if (jsonObject.getInt("auth") == 1) {
//                            txtresult.setText(txtresult.getText() + "Successful" + jsonObject.getInt("auth") + " ");
//                        }
//                    }
//                }
//                else {
//                    txtresult.setText("Connection " + jsonObject.getInt("code_conn") + " ");
//                    if (jsonObject.getInt("db") == 1) {
//                        txtresult.setText(txtresult.getText() + "User Exists" + jsonObject.getInt("db") + " ");
//                        if (jsonObject.getInt("auth") == 1) {
//                            txtresult.setText(txtresult.getText() + "Successful" + jsonObject.getInt("auth") + " ");
//                        }
//                    }
//                }
//            }
//            catch(Exception e) {
//                Log.d("test", "" + e);
//            }
//        }
        }
    }

    private void login(boolean check, String message) {
        if(check) {
            checkForLogin();
        }

        else {
            txtresult.setText(message);
        }
    }

    public void checkForLogin() {
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        check = sharedPreferences.getBoolean(LoginStatus, false);
        if (check) {
            Intent loginNotRequired = new Intent(this, MapsActivity.class);
            startActivity(loginNotRequired);
            finish();
        }
    }
}
