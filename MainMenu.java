package zhaoli.xshiki.com.atmospherik;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainMenu extends AppCompatActivity {

    private SharedPreferences sp;
    private String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setWindowAnimations(android.R.style.Animation_Activity);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent start=getIntent();
        Bundle bundle=start.getExtras();

        if(bundle!=null)
        {
            //noinspection ConstantConditions
            zipcode=bundle.get("zipcode").toString();
        }

        sp=getSharedPreferences("weatherdata",MODE_PRIVATE);

        GregorianCalendar gc=new GregorianCalendar();
        int nowtime=gc.get(Calendar.MINUTE);

        /*
        if(sp.getString("time",null)!=null)
        {
            String timestring=sp.getString("time",null);
            int time=Integer.valueOf(timestring);

            if((time+10)>nowtime||(time+10)>=59)
            {
                new GetWeather().execute("");
            }
            else
            {

            }
        }
        else
            new GetWeather().execute("");
            */
        new GetWeather().execute("");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem menu)
    {
        switch(menu.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(menu);
    }

    private class GetWeather extends AsyncTask<String, String, String>
    {
        //String link="http://api.openweathermap.org/data/2.5/forecast/city?id=5194369&APPID=c3be6d396225953fff0e6f75dff51058";

        String cityname="";
        String countryname="";
        String lat="";
        String lon="";
        String degrees="";
        String stuffinsky="";
        String description="";
        String iconcode="";

        TextView cityview=(TextView)findViewById(R.id.cityname);
        TextView countryview=(TextView)findViewById(R.id.countryname);
        TextView lonview=(TextView)findViewById(R.id.lon);
        TextView latview=(TextView)findViewById(R.id.lat);
        TextView skyview=(TextView)findViewById(R.id.sky);
        TextView descriptionview=(TextView)findViewById(R.id.description);
        TextView degreesview=(TextView)findViewById(R.id.degrees);

        ImageView image=(ImageView)findViewById(R.id.weatherpicture);
        Bitmap map;

        Exception ex;

        @Override
        protected void onPreExecute()
        {
            Toast.makeText(getApplicationContext(), "Loading...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String link="http://api.openweathermap.org/data/2.5/weather?zip="+zipcode+",us&APPID=c3be6d396225953fff0e6f75dff51058";
            try
            {
                URI uri=URI.create(link);
                String webs=uri.toASCIIString();
                URL url=new URL(webs);

                HttpURLConnection connection=(HttpURLConnection)url.openConnection();
                JsonParser parser=new JsonParser();
                JsonElement element=parser.parse(new InputStreamReader((InputStream)connection.getContent()));

                JsonObject json=element.getAsJsonObject();
                JsonObject coord=json.getAsJsonObject("coord");
                JsonObject sys=json.getAsJsonObject("sys");
                JsonObject main=json.getAsJsonObject("main");
                JsonObject clouds=json.getAsJsonObject("clouds");
                JsonObject wind=json.getAsJsonObject("wind");

                JsonArray array=json.getAsJsonArray("weather");

                lon=lon.concat(coord.get("lon").getAsString());
                lat=lat.concat(coord.get("lat").getAsString());
                cityname=cityname.concat(json.get("name").getAsString());
                countryname=countryname.concat(sys.get("country").getAsString());
                degrees=degrees.concat(main.get("temp").getAsString());

                for(int i=0; i<array.size(); i++)
                {
                    JsonObject weatherarray=array.get(i).getAsJsonObject();
                    stuffinsky=stuffinsky.concat(weatherarray.get("main").getAsString());
                    description=description.concat(weatherarray.get("description").getAsString());
                    iconcode=iconcode.concat(weatherarray.get("icon").getAsString());
                }

                URL urlpic=new URL("http://openweathermap.org/img/w/"+iconcode+".png");
                HttpURLConnection picture=(HttpURLConnection)urlpic.openConnection();
                InputStream is=picture.getInputStream();
                map=BitmapFactory.decodeStream(is);

            }
            catch(Exception e)
            {
                e.printStackTrace();
                ex=e;
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String string)
        {
            String symbol=Character.toString((char)176);
            cityview.setText(cityname);
            countryview.setText(countryname);
            lonview.setText("Longitude: "+lon);
            latview.setText("Latitude: "+lat);
            skyview.setText(stuffinsky);
            descriptionview.setText(description);
            String done=toFah(degrees)+symbol+"F";
            degreesview.setText(done);

            image.setImageBitmap(map);

            if(ex!=null)
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();

            GregorianCalendar gc=new GregorianCalendar();
            int minutes=gc.get(Calendar.MINUTE);
            String min=String.valueOf(minutes);

            SharedPreferences.Editor editor=getSharedPreferences("weatherdata",MODE_PRIVATE).edit();
            editor.putString("cityname",cityname);
            editor.putString("countryname",countryname);
            editor.putString("lon",lon);
            editor.putString("lat",lat);
            editor.putString("stuffinsky",stuffinsky);
            editor.putString("description",description);
            editor.putString("degrees",done);
            editor.putString("time",min);
            editor.apply();
        }

        private String toFah(String kelvin)
        {
            Double kelv=Double.valueOf(kelvin);
            Double fah=((kelv-273.15)*1.8)+32.0;
            Long done=Math.round(fah);
            return done.toString();
        }
    }
}
