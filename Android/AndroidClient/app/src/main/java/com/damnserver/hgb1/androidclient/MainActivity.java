package com.damnserver.hgb1.androidclient;

import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText EditRed;
    EditText EditGreen;
    EditText EditBlue;
    EditText EditID;
    ToggleButton OnButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditRed = (EditText)findViewById(R.id.editTextRed);
        EditGreen = (EditText)findViewById(R.id.editTextGreen);
        EditBlue = (EditText)findViewById(R.id.editTextBlue);
        EditID = (EditText)findViewById(R.id.editTextID);
        OnButton = (ToggleButton)findViewById(R.id.OnButton);
    }

    public void sendColors(View view){
        ClientTask clientT = new ClientTask(this,"192.168.1.7",5045);

        JSONObject json = new JSONObject();

        try {
            OnButton.isChecked();

            JSONArray colors = new JSONArray();
            colors.put(Integer.parseInt(EditRed.getText().toString()));
            colors.put(Integer.parseInt(EditGreen.getText().toString()));
            colors.put(Integer.parseInt(EditBlue.getText().toString()));

            json.put("status",OnButton.isChecked() ? 1:0);
            json.put("id", Integer.parseInt(EditID.getText().toString()));
            json.put("change",1);
            json.put("color",colors);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i("Test",json.toString());
        clientT.execute(json.toString());
    }
}
