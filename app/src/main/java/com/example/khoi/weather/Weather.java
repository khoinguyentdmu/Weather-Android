package com.example.khoi.weather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.lang.annotation.Documented;
import java.text.SimpleDateFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class Weather extends AppCompatActivity implements View.OnClickListener {

    private Button btnSearch;
    private TextView txtCityName;

    private TextView lblCityName;
    private TextView lblTimeSunRise;
    private TextView lblTimeSunSet;
    private TextView lblCurrentCelsius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        addControls();

        callOpenWeatherMap();
    }

    private void callOpenWeatherMap(){
        String URL = "http://api.openweathermap.org/data/2.5/weather?q="+ txtCityName.getText() +"&appid=df655911f6c0e9bdcf614ad78e2e1cc4&mode=xml";
        RequestQueue mRequestQueue;

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Respone", response);
                        try{
                            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                            Document doc = dBuilder.parse(new InputSource(new StringReader(response)));
                            doc.getDocumentElement().normalize();
                            Log.e("Root element", doc.getDocumentElement().getNodeName());

                            Element elementCity = (Element) doc.getElementsByTagName("city").item(0);
                            String cityName = elementCity.getAttribute("name");
                            Log.e("City", cityName);
                            lblCityName.setText(cityName);

                            String timeSunRise = ((Element) elementCity.getChildNodes().item(2)).getAttribute("rise");
                            timeSunRise = timeSunRise.substring(timeSunRise.length()-8);
                            Log.e("Sun rise", timeSunRise);
                            lblTimeSunRise.setText(timeSunRise);

                            String timeSunSet = ((Element) elementCity.getChildNodes().item(2)).getAttribute("set");
                            timeSunSet = timeSunSet.substring(timeSunSet.length()-8);
                            Log.e("Sun set", timeSunSet);
                            lblTimeSunSet.setText(timeSunSet);

                            String currentCelsius = ((Element) doc.getElementsByTagName("temperature").item(0)).getAttribute("value");
                            currentCelsius = String.valueOf(Double.parseDouble(currentCelsius)-273.15);
                            Log.e("Current celsius", currentCelsius);
                            lblCurrentCelsius.setText(currentCelsius);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Respone", error.toString());
                    }
                });

        mRequestQueue.add(stringRequest);
    }

    private void addControls() {
        this.btnSearch = findViewById(R.id.btnSearch);
        this.btnSearch.setOnClickListener(this);
        this.txtCityName = findViewById(R.id.txtCityName);
        txtCityName.setText("Hanoi");
        this.lblTimeSunRise = findViewById(R.id.lblTimeSunRise);
        this.lblTimeSunSet = findViewById(R.id.lblTimeSunSet);
        this.lblCurrentCelsius = findViewById(R.id.lblCurrentCelsius);
        this.lblCityName = findViewById(R.id.lblCityName);
    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {
            callOpenWeatherMap();
        }
    }
}
