package com.example.sourav.emergency2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SignUp extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {
    GoogleApiClient googleApiClient;
    int auto_complete;
    EditText txtname, txtuser,txtpassword, txtconfirm_pass,location;
    Button btnregister;
    String name, uname, password, confirm_pass;
    int errorCode;
    JSONObject jsonObject;
    ProgressDialog progressDialog;
    LatLng finalLatLng;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Api Connection Failed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class Request extends AsyncTask<Object, Object, Integer> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Registering...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer success_flag) {
            progressDialog.dismiss();
            if (success_flag == 11) {
                Toast.makeText(getBaseContext(), "Email & Phone No. already exist! Try forget password.", Toast.LENGTH_SHORT).show();
            } else if (success_flag == 10) {
                Toast.makeText(getBaseContext(), "Phone No. already exist!", Toast.LENGTH_SHORT).show();
            } else if (success_flag == 1) {
                Toast.makeText(getBaseContext(), "Entry not done", Toast.LENGTH_SHORT).show();
            } else if (success_flag == 0) {
                Intent intent = new Intent(getBaseContext(), Login.class);
                // intent.putExtra("name", name);
                //intent.putExtra("uname",uname);
                Toast.makeText(getBaseContext(),"Entry successful",Toast.LENGTH_LONG).show();
                //startActivity(intent);
                finish();
            } else
                Toast.makeText(getBaseContext(), "UserName already exist.Try a different one.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                String path = "http://192.168.137.1/emcare.php";
                String data;
                URL url = new URL(path);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(6000);
                httpURLConnection.setReadTimeout(6000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") +
                        "&" + URLEncoder.encode("uname", "UTF-8") + "=" + URLEncoder.encode(uname, "UTF-8")+
                        "&" + URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(Double.toString(finalLatLng.latitude), "UTF-8")+
                        "&" + URLEncoder.encode("lng", "UTF-8") + "=" + URLEncoder.encode(Double.toString(finalLatLng.longitude), "UTF-8")+
                        "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
                bufferedWriter.write(data);
                bufferedWriter.close();
                outputStreamWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//               while ((readline = br.readLine()) != null) {
//                    stringBuilder.append(readline).append("\n");
//                }
                String result = bufferedReader.readLine();
                jsonObject = new JSONObject(result);
                errorCode = jsonObject.getInt("return_code");
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (Exception e) {
                Log.d("Error: ", "Connection Failed : " + e);
            }
            return errorCode;
        }
    }

    // Handler h=new Handler(){
    //  @Override
    //   public void handleMessage(Message msg) {

    //    Toast.makeText(getBaseContext(),""+bit1,Toast.LENGTH_LONG).show();
            /*if(bit1==1)
            {
                Intent intent=new Intent(getBaseContext(),Otp.class);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone1);
                intent.putExtra("email",email);
                intent.putExtra("password",password);
                startActivity(intent);
            }
            else if(bit1==0)
            {
                Toast.makeText(getBaseContext(),"User Already Exist!", Toast.LENGTH_SHORT).show();
                //Intent in1=new Intent(Login.class);
                //startActivity(in1);
            }
            else return;*/
    //}
    //};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        StrictMode.ThreadPolicy thread = new StrictMode.ThreadPolicy.Builder().build();
        StrictMode.setThreadPolicy(thread);
        findViewById(R.id.signup).requestFocus();
        googleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(SignUp.this)
                .addOnConnectionFailedListener(SignUp.this)
                .build();
        //    ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#000000"));
        auto_complete = 1;
//        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        location= (EditText) findViewById(R.id.location);
        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(SignUp.this);
                        startActivityForResult(intent, auto_complete);
                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        Toast.makeText(SignUp.this, "i was in catch", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.signup).requestFocus();
                        // TODO: Handle the error.
                    }
                }
            }
        });
        txtname = (EditText) findViewById(R.id.name);
        txtuser = (EditText) findViewById(R.id.username);
        txtpassword = (EditText) findViewById(R.id.password);
        txtconfirm_pass = (EditText) findViewById(R.id.confirm_password);
        btnregister = (Button) findViewById(R.id.signup1);
        btnregister.setOnClickListener(this);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == auto_complete) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getBaseContext(), data);
                // Log.i(TAG, "Place: " + place.getName());
                finalLatLng = place.getLatLng();
                Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            finalLatLng.latitude, finalLatLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i <50 ; i++) {
                            sb.append(address.getAddressLine(i));
                        }
                        //sb.append(address.)
                        //sb.append(address.getLocality()).append("\n");
                        //sb.append(address.getPostalCode()).append("\n");
                        //sb.append(address.getCountryName());
                        result = sb.toString();
                        location.setText(result);
                    }
                } catch (IOException e) {
                    System.out.print(e);
                }
                // endplace = (String) place.getName();
                // txtloc.setText(endplace);
                findViewById(R.id.signup).requestFocus();
            }   } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            //Status status = PlaceAutocomplete.getStatus(getContext(), data);
            Toast.makeText(this, "Result error", Toast.LENGTH_LONG).show();
            findViewById(R.id.signup).requestFocus();
            // TODO: Handle the error.
            //Log.i(TAG, status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Result cancelled", Toast.LENGTH_LONG).show();
            findViewById(R.id.signup).requestFocus();
            // The user canceled the operation.
        } else {
            Toast.makeText(this, "Some other error.", Toast.LENGTH_LONG).show();
            findViewById(R.id.signup).requestFocus();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.signup1) {
            name = txtname.getText().toString();
            uname = txtuser.getText().toString();
            //email = txtemail.getText().toString();
            password = txtpassword.getText().toString();
            confirm_pass = txtconfirm_pass.getText().toString();
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(uname)|| TextUtils.isEmpty(password)||
                    TextUtils.isEmpty(confirm_pass)) {
                Toast.makeText(getBaseContext(), "Please enter all the details!", Toast.LENGTH_LONG).show();
                return;
            }

            if (!Objects.equals(password, confirm_pass)) {
                Toast.makeText(getBaseContext(), "Re-enter same password", Toast.LENGTH_LONG).show();
                return;
            }

            new Request().execute();
        }
    }
}

                /*Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        synchronized (this)
                        { try {
                            URL url = new URL(path);
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setRequestMethod("POST");
                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
                            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                            data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") +
                                    "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone1, "UTF-8") +
                                    "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");*/
                        /*    bufferedWriter.write(data);
                            bufferedWriter.close();
                            outputStreamWriter.close();
                            outputStream.close();
                            InputStream in = httpURLConnection.getInputStream();
                            InputStreamReader inr = new InputStreamReader(in);
                            BufferedReader br = new BufferedReader(inr);
                            while ((readline = br.readLine()) != null) {
                                stringBuilder.append(readline).append("\n");
                            }
                            bit = stringBuilder.toString();
                            JSONObject json = new JSONObject(bit);
                            bit1 = json.getInt("code");
                            br.close();
                            inr.close();
                            in.close();
                            httpURLConnection.disconnect();
                        } catch (Exception e) {
                        }
                   h.sendEmptyMessage(0);
                    }}
                };
                Thread t = new Thread(r);
                t.start();
            }}}*/
               /* try {
                    URL url=new URL(path);
                    HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    OutputStream outputStream=httpURLConnection.getOutputStream();
                    OutputStreamWriter outputStreamWriter=new OutputStreamWriter(outputStream,"UTF-8");
                    BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
                    data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8") +
                            "&" + URLEncoder.encode("phone", "UTF-8") + "=" + URLEncoder.encode(phone1, "UTF-8") +
                            "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
                    bufferedWriter.write(data);
                    bufferedWriter.close();
                    outputStreamWriter.close();
                    outputStream.close();
                    InputStream in=httpURLConnection.getInputStream();
                    InputStreamReader inr=new InputStreamReader(in);
                    BufferedReader br=new BufferedReader(inr);
                    while ((readline=br.readLine())!=null)
                    {
                    stringBuilder.append(readline+"\n");
                    }
                    bit=stringBuilder.toString();
                    JSONObject json=new JSONObject(bit);
                    bit1= json.getInt("code");
                    br.close();
                    inr.close();
                    in.close();
                    httpURLConnection.disconnect();
                    if(bit1==1)
                    {
                        Intent intent=new Intent(this,Otp.class);
                        intent.putExtra("name",name);
                        intent.putExtra("phone",phone1);
                        intent.putExtra("email",email);
                        intent.putExtra("password",password);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(getBaseContext(),"User Already Exist!", Toast.LENGTH_SHORT).show();
                        //Intent in1=new Intent(Login.class);
                        //startActivity(in1);
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getBaseContext(),""+e,Toast.LENGTH_LONG).show();
                }
            }
        }*/

