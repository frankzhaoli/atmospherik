package zhaoli.xshiki.com.atmospherik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.List;

public class Start extends AppCompatActivity {

    private String list[]={"United States"};
    private SharedPreferences sp;
    private EditText zipcodeedittext;
    private String zipcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        sp=getSharedPreferences("city",MODE_PRIVATE);
        zipcodeedittext=(EditText)findViewById(R.id.zipcodeedittext);

        if(sp.getString("zip",null)!=null)
        {
            String zip=sp.getString("zip",null);
            zipcodeedittext.setText(zip);
        }
        /*
        Spinner spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_item,list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        */

    }

    public String getJson()
    {
        String json=null;
        try
        {
            AssetManager am=getApplicationContext().getAssets();
            InputStream is=am.open("uscities.json");
            byte[] buff=new byte[is.available()];
            is.read(buff);
            is.close();
            json=new String(buff,"UTF-8");
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        return json;
    }

    public void enter(View view)
    {
        zipcode=zipcodeedittext.getText().toString();
        Intent mainmenu=new Intent(this,MainMenu.class);
        mainmenu.putExtra("zipcode",zipcode);
        putIntoShared(zipcode);
        startActivity(mainmenu);
    }

    public void putIntoShared(String zip)
    {
        SharedPreferences.Editor sp=getSharedPreferences("city",MODE_PRIVATE).edit();
        sp.putString("zip",zip);
        sp.apply();
    }
}
