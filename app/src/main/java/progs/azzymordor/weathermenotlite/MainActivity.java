package progs.azzymordor.weathermenotlite;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.simple.JSONObject;

import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.parser.*;
import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //Neel Driving
    String init_location = "42.3601,-71.0589";
    double lat = 42.3601;
    double longe = -71.0589;
    Marker currentM;
    SearchView searchView;
    RequestQueue geoRequestQueue;
    StringRequest geoStringRequest;
    SupportMapFragment mapFragment;
    GoogleMap map;
    ImageView imageView;

    //RequestQueue initialized
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        imageView = findViewById(R.id.imageView2);
        darkSkyToDisplay(findViewById(android.R.id.content));
        searchView = (SearchView) findViewById(R.id.searchView);
        geoRequestQueue = Volley.newRequestQueue(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String url =  "https://maps.googleapis.com/maps/api/geocode/json?address="+ query +",+CA&key="+"AIzaSyAzC8p8YQBXWQIAX5oxZDGL_TgKCo0Xn40";

                //String Request initialized
                geoStringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.v("searchView","Trying geocode json request");
                            Object object = new JSONParser().parse(response);
                            JSONObject jsonobj = (JSONObject) object;
                            //Grabs the currently list from inside the JSON.
                            JSONArray results = (JSONArray) jsonobj.get("results");
                            JSONObject geometry = (JSONObject) ((JSONObject) results.get(0)).get("geometry");
                            JSONObject location =  (JSONObject) geometry.get("location");
                            //Gets each of the 4 vars that we need
                            lat = Double.valueOf(location.get("lat").toString());
                            longe = Double.valueOf(location.get("lng").toString());
                            LatLng loc = new LatLng(lat, longe);
                            darkSkyToDisplay(findViewById(android.R.id.content));
                            currentM.remove();
                            currentM = map.addMarker(new MarkerOptions().position(loc)
                                    .title("Marker in location"));
                            map.moveCamera(CameraUpdateFactory.newLatLng(loc));
                        } catch (ParseException e) {
                            Toast.makeText(getApplicationContext(),"Parsing Exception, Bad Response",Toast.LENGTH_SHORT);
                        }
//                    Object object = new JSONParser().parse(response);
//                    JSONObject jsonobj = (JSONObject) object;
//                    input = (String) jsonobj.get("timezone");
//                    Toast.makeText(getApplicationContext(),input,input.length()).show();


                    }
                }, new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"GET Failed",Toast.LENGTH_SHORT);
                    }
                });

                geoRequestQueue.add(geoStringRequest);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
    //Neel Driving

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(lat, longe);
        currentM = googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        map = googleMap;

    }
    //Eric Driving
    public void darkSkyToDisplay(View view) {
        RequestQueue mRequestQueue;
        StringRequest mStringRequest;
        String url = "https://api.darksky.net/forecast/56f947ebc9aa3e3e8dd69e37f36843e4/" + lat + "," + longe +"?exclude=minutely,hourly,daily,alerts,flags";
        final TextView textView = (TextView) findViewById(R.id.text);

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Object object = new JSONParser().parse(response);
                    JSONObject jsonobj = (JSONObject) object;
                    //Grabs the currently list from inside the JSON.
                    JSONObject currently = (JSONObject) jsonobj.get("currently");
                    //Gets each of the 4 vars that we need
                    TextView temp = (TextView) findViewById(R.id.temperature);
                    TextView humid = (TextView) findViewById(R.id.humidity);
                    TextView wind = (TextView) findViewById(R.id.windspeed);
                    TextView precip = (TextView) findViewById(R.id.precipitation);
                    temp.setText("Temperature: "+ currently.get("temperature"));
                    humid.setText("Humidity: "+ currently.get("humidity"));
                    wind.setText("Wind Speed: "+ currently.get("windSpeed"));
                    precip.setText("Precipitation: "+ currently.get("icon"));

                    String convertedicon = (String)currently.get("icon");
                    convertedicon = convertedicon.replaceAll("-","_");

                    imageView.setImageResource(getResources().getIdentifier(convertedicon, "drawable", getPackageName()));

                    //Displays the information on the screen
                    Toast.makeText(getApplicationContext(), "Timezone: "+ jsonobj.get("timezone"), Toast.LENGTH_LONG).show();
//                    Toast.makeText(getApplicationContext(), "Time :" + response.toString(), Toast.LENGTH_LONG).show();

                } catch (ParseException e) {
                    Toast.makeText(getApplicationContext(),"Parsing Exception, Bad Response",Toast.LENGTH_SHORT);
                }
//                    Object object = new JSONParser().parse(response);
//                    JSONObject jsonobj = (JSONObject) object;
//                    input = (String) jsonobj.get("timezone");
//                    Toast.makeText(getApplicationContext(),input,input.length()).show();


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"GET Failed",Toast.LENGTH_SHORT);
            }
        });

        mRequestQueue.add(mStringRequest);
    }
}
