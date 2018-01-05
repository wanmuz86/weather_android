package com.itrainasia.weatherapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RequestQueue queue = Volley.newRequestQueue(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        final CustomAdapter adapter = new CustomAdapter(getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        String url ="https://api.openweathermap.org/data/2.5/forecast/daily?lat=35&lon=139&appid=9fd7a449d055dba26a982a3220f32aa2";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String city = jsonObject.getJSONObject("city").getString("name");
                            JSONArray weatherArray = jsonObject.getJSONArray("list");
                            Log.d("debug", "City is "+city);
                            Toast.makeText(getApplicationContext(), "city is "+city, Toast.LENGTH_LONG).show();
                            for (int i=0; i< weatherArray.length(); i++){
                                adapter.addWeather(weatherArray.getJSONObject(i));
                            }
                            adapter.notifyDataSetChanged();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error",error.toString());
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView weatherTextView;
        TextView temperatureTextView;
        TextView dateTextView;


        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.custom_row,  parent, false));

            imageView = itemView.findViewById(R.id.imageView);
            weatherTextView = itemView.findViewById(R.id.weatherText);
            temperatureTextView = itemView.findViewById(R.id.tempText);
            dateTextView = itemView.findViewById(R.id.dateText);

        }
    }

    public static class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>{

        public List<JSONObject> weatherList = new ArrayList<>();


        public Context context;
        //This is the constructor/initializer
        //In this case we pass the contet
        //Context will be used for creating new intent and Show the image

        public CustomAdapter(Context context){
            this.context = context;

        }
        //Which ViewHolder will it use
        //Code always the same
        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext()),parent);
        }
        //What each row will show
        @Override
        public void onBindViewHolder(CustomViewHolder holder, int position) {
            JSONObject currentObj = weatherList.get(position);


            try {
                Date date = new Date(currentObj.getLong("dt")*1000L);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
// give a timezone reference for formatting (see comment at the bottom)
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                String formattedDate = sdf.format(date);

                holder.dateTextView.setText(formattedDate);



                holder.temperatureTextView.setText((currentObj.getJSONObject("temp")
                        .getDouble("day")-273.15 )+" C");
                holder.weatherTextView.setText(currentObj.getJSONArray("weather")
                        .getJSONObject(0).getString("main"));
                String icon = currentObj.getJSONArray("weather")
                        .getJSONObject(0).getString("icon");
                String url = "https://openweathermap.org/img/w/"+icon+".png";
                Picasso.with(context).load(url).into(holder.imageView);
            } catch (JSONException e) {
                e.printStackTrace();
            }
// the format of your date

        }

        //How many rows in the recyclerview
        @Override
        public int getItemCount() {
            return weatherList.size();
        }

        public void addWeather(JSONObject weather){
            weatherList.add(weather);
        }
    }
}
