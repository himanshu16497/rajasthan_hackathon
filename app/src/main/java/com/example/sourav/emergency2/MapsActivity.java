package com.example.sourav.emergency2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.sourav.emergency2.Login.MY_PREFERENCES;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    JSONObject jsonObject1;
    Bundle extras;
    RelativeLayout.LayoutParams lp;
    float zoom,lat,lng;
    Polyline poly = null;
    SharedPreferences sharedPreferences;
    final String Lat = "KeyLat";
    final String Lng = "KeyLng";
    private final String Uid = "KeyUid";
    GoogleApiClient googleApiClient;
    Timer timer;
    TimerTask timerTask;
    int new1,old;
    String my_uid;
    RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
       // extras=getIntent().getExtras();
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        final Button btn = new Button(MapsActivity.this);
        googleApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(MapsActivity.this)
                .addOnConnectionFailedListener(MapsActivity.this)
                .build();
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        zoom= (float) 15.0;
        lat=sharedPreferences.getFloat(Lat,(float) 0.0);
        lng=sharedPreferences.getFloat(Lng,(float) 0.0);
        my_uid=sharedPreferences.getString(Uid,null);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

                StringBuilder stringBuilder=new StringBuilder();
                String data="";
                String readline="";
                String path="http://192.168.137.1/getdata1.php";
                try
                {
                    URL url = new URL(path);//java .net package
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    Toast.makeText(getBaseContext(), "Connection Established", Toast.LENGTH_LONG).show();
                    InputStream inputStream= httpURLConnection.getInputStream();
                    InputStreamReader din = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader=new BufferedReader(din);

                    while((readline=bufferedReader.readLine())!=null)
                    {
                        stringBuilder.append(readline+"\n");
                    }

                    data=stringBuilder.toString();
                   // Toast.makeText(getBaseContext(),""+data,Toast.LENGTH_LONG).show();
                    JSONArray jsonArray=new JSONArray(data);
                    old=jsonArray.length();
                    Toast.makeText(this, "this :"+old, Toast.LENGTH_LONG).show();
;                    JSONObject jsonObject=null;
                  /*  uid=new String[jsonArray.length()];
                    uname=new String[jsonArray.length()];
                    phone=new String[jsonArray.length()];
                    email=new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        jsonObject= jsonArray.getJSONObject(i);
                        uid[i]=jsonObject.getString("uid");
                        uname[i]=jsonObject.getString("uname");
                        phone[i]=jsonObject.getString("phone");
                        email[i]=jsonObject.getString("email");
                        Toast.makeText(getBaseContext(),"uid"+uid[i]+"uname"+uname[i]+"phone"+phone[i]+"email"+email[i],Toast.LENGTH_LONG).show();
                    }*/

                }
                catch(Exception e)
                {
                    Toast.makeText(getBaseContext(),""+e,Toast.LENGTH_LONG).show();
                }




        timer=new Timer();
        final Handler handler = new Handler();
        timerTask=new TimerTask() {
            @Override
            public void run() {
                Boolean post=handler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.print(old);
                        ReadTask readTask=new ReadTask();
                        readTask.execute();
                        try {
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 30000);

    }

    private class ReadTask extends AsyncTask<Object,Object,JSONObject>
    {

        @Override
        protected void onPostExecute(JSONObject o) {
            //Toast.makeText(MapsActivity.this, "the value of old is:"+old, Toast.LENGTH_LONG).show();
            Toast.makeText(MapsActivity.this, "the value of  old and new is:"+old+","+new1, Toast.LENGTH_LONG).show();

            if(old==0)
            {
                old=new1;
            }
            if(old!=new1)
            {

                try {
                    Toast.makeText(MapsActivity.this, "the value is NOT EQUAL:"+o.getString("UID"), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CompareTask ct=new CompareTask();
                ct.execute(o);
            }
            //super.onPostExecute(o);
        }

        @Override
        protected JSONObject doInBackground(Object... params) {
            StringBuilder stringBuilder=new StringBuilder();
            String data="";
            String readline="";
            String path="http://192.168.137.1/getdata1.php";
            try
            {
                URL url = new URL(path);//java .net package
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                Toast.makeText(getBaseContext(), "Connection Established", Toast.LENGTH_LONG).show();
                InputStream inputStream= httpURLConnection.getInputStream();
                InputStreamReader din = new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(din);

                while((readline=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(readline+"\n");
                }

                data=stringBuilder.toString();
                Toast.makeText(getBaseContext(),""+data,Toast.LENGTH_LONG).show();
                JSONArray jsonArray=new JSONArray(data);
                new1=jsonArray.length();
                jsonObject1=jsonArray.getJSONObject(new1-1);
                Log.d("NEW","abc:"+new1);
                  /*  uid=new String[jsonArray.length()];
                    uname=new String[jsonArray.length()];
                    phone=new String[jsonArray.length()];
                    email=new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        jsonObject= jsonArray.getJSONObject(i);
                        uid[i]=jsonObject.getString("uid");
                        uname[i]=jsonObject.getString("uname");
                        phone[i]=jsonObject.getString("phone");
                        email[i]=jsonObject.getString("email");
                        Toast.makeText(getBaseContext(),"uid"+uid[i]+"uname"+uname[i]+"phone"+phone[i]+"email"+email[i],Toast.LENGTH_LONG).show();
                    }*/

            }
            catch(Exception e)
            {
                Toast.makeText(getBaseContext(),""+e,Toast.LENGTH_LONG).show();
            }

            return jsonObject1;
        }
    }
    public String generateUrl(Float lat,Float lng,Float des1,Float des2) {
//


        String str_origin = "origin=" + lat  + "," + lng;
        //String str_origin = "origin="+30+","+76;


        // Destination of route
        //String str_dest = "destination="+30.5+","+76.5;
        String str_dest = "destination=" + des1 + "," + des2;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
//

        // Building the parameters to the web service
        String params = str_origin + "&" + str_dest + "&" + mode;

        // String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params + "&" + "key=AIzaSyARBGbhMSOZN44ckKkcnZJ922QlzgOTuRQ";
        return url;
    }

    private class CompareTask extends AsyncTask<JSONObject,Object,Boolean>
    {
        JSONObject jsObject;
        Float lat[];
        Float lng[];
        Float acclat,acclng,dest1,dest2;
        Button btn=new Button(MapsActivity.this);

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean)
            {
                Toast.makeText(MapsActivity.this, "I am nearest", Toast.LENGTH_LONG).show();
                try {

                    Ringtone defaultRingtone = RingtoneManager.getRingtone(getBaseContext(),
                            Settings.System.DEFAULT_RINGTONE_URI);
                    //fetch current Ringtone
                    Uri currentRintoneUri = RingtoneManager.getActualDefaultRingtoneUri(getBaseContext()
                            .getApplicationContext(), RingtoneManager.TYPE_RINGTONE);
                    Ringtone currentRingtone = RingtoneManager.getRingtone(getBaseContext(), currentRintoneUri);
                    //display Ringtone title
                    //play current Ringtone
                    currentRingtone.play();
                    lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    btn.setId(R.id.nameof);
                    relativeLayout.addView(btn, lp);
                    btn.setText("Himanshu Bansal");
                    btn.setBackground(getDrawable(R.drawable.buttonshape));
                    btn.setShadowLayer(5,0,0,141414);
                    btn.setTextColor(Color.parseColor("#FFFFFF"));
                    btn.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);

                    /////////////////////////////////////////////////////
                    String url=generateUrl(acclat,acclng,dest1,dest2);
                    FreeReadTask downloadTask = new FreeReadTask();
                    downloadTask.execute(url);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected Boolean doInBackground(JSONObject... params) {

            jsObject=jsonObject1;
            try {
                acclat=Float.valueOf(jsObject.getString("LAT"));
                acclng=Float.valueOf(jsObject.getString("LNG"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            StringBuilder stringBuilder=new StringBuilder();
            String data="";
            String readline="";
            String path="http://192.168.137.1/select.php";
            try
            {
                URL url = new URL(path);//java .net package
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                Toast.makeText(getBaseContext(), "Connection Established", Toast.LENGTH_LONG).show();
                InputStream inputStream= httpURLConnection.getInputStream();
                InputStreamReader din = new InputStreamReader(inputStream);
                BufferedReader bufferedReader=new BufferedReader(din);

                while((readline=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(readline+"\n");
                }

                data=stringBuilder.toString();
                Toast.makeText(getBaseContext(),""+data,Toast.LENGTH_LONG).show();
                JSONArray jsonArray=new JSONArray(data);
                //new1=jsonArray.length();
                JSONObject jsonObject=null;
                 lat=new Float[jsonArray.length()];
                    lng=new Float[jsonArray.length()];
                   // phone=new String[jsonArray.length()];
                    //email=new String[jsonArray.length()];

                    for(int i=0;i<jsonArray.length();i++)
                    {
                        jsonObject= jsonArray.getJSONObject(i);
                        lat[i]=Float.valueOf(jsonObject.getString("LAT"));
                        lng[i]=Float.valueOf(jsonObject.getString("LNG"));

                        //Toast.makeText(getBaseContext(),"uid"+uid[i]+"uname"+uname[i]+"phone"+phone[i]+"email"+email[i],Toast.LENGTH_LONG).show();
                    }
                    double min=9999999999.9999;
                    int index=0;

                    Location dest=new Location("Destination");
                    dest.setLatitude(acclat);
                    dest.setLongitude(acclng);
                    for(int i=0;i<jsonArray.length();i++)
                    {
                        Location source=new Location("Source");
                        source.setLatitude(acclat);
                        source.setLongitude(acclng);
                        double distance=source.distanceTo(dest);
                        if(distance<min)
                        {
                            min=distance;
                            index=i;
                        }

                    }
                    jsonObject=jsonArray.getJSONObject(index);
                    dest1= Float.valueOf(jsonObject.getString("LAT"));
                    dest2=Float.valueOf(jsonObject.getString("LNG"));
                    System.out.print("index is"+index);
                    if(jsonObject.getString("UID").equals(my_uid))
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
            }
            catch(Exception e)
            {
                Toast.makeText(getBaseContext(),""+e,Toast.LENGTH_LONG).show();
            }

            return null;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private class FreeReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url1 = new URL(url[0]);
                urlConnection = (HttpURLConnection) url1.openConnection();
                urlConnection.connect();
                iStream = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        iStream));
                StringBuffer sb = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                data = sb.toString();
                br.close();
                iStream.close();
                urlConnection.disconnect();

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
            new FreeParserTask().execute(result);
        }
    }
    private class FreeParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }


        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(5);
                polyLineOptions.color(Color.BLACK);
            }
            // if(poly!=null)
            //{
            //  poly.remove();
            //}
            poly = mMap.addPolyline(polyLineOptions);
            //float bg = BigDecimal.valueOf(Distance).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
            //txtDist = (TextView) fragmentView.findViewById(R.id.displace);
            //txtDist.setText(Float.toString(bg));

        }
    }
        @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng pos = new LatLng(lat,lng);
        mMap.addMarker(new MarkerOptions().position(pos).title(""));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(this, "Connection Established.", Toast.LENGTH_SHORT).show();
        LatLng pos = new LatLng(lat,lng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
