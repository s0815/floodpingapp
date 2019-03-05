package com.example.floodpingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static MainActivity instance;

    private static boolean pingThreadRunning = false;

    private PingAsyncTask pingAt;

    private Button btnClick;
    private Spinner ipSpinner;
    private EditText default_gw_editText;
    private String pingDestIP;

    private NetworkHelper networkHelper = new NetworkHelper(this);

    public void updateTextPingResult(String str) {
        TextView txt = (TextView) findViewById(R.id.textPingResult);
        txt.setText(str);
    }

    public void updateTextPingTimes(String str) {
        TextView txt = (TextView) findViewById(R.id.textPingTimes);
        txt.setText(str);
    }

    public void updateTextDefaultGw(String str) {
        default_gw_editText.setText(str);
    }

    private void createIpSpinner(String ip) {
        ipSpinner = (Spinner) findViewById(R.id.ipSpinner);
        Context context = getApplicationContext();
        ArrayList<String> ipList = new ArrayList<>();
        ipList.add(ip);
        String[] resIpArray = context.getResources().getStringArray(R.array.ip_array);
        ipList.addAll(Arrays.asList(resIpArray));
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, ipList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ipSpinner.setAdapter(adapter);
        SpinnerActivity spiAct = new SpinnerActivity();
        ipSpinner.setOnItemSelectedListener(spiAct);
    }

    private void dialogIpInvalid(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Destination IP is invalid");
        builder.setMessage("Please check and change your IP address.");
        builder.setPositiveButton("OK", null);
        builder.show();

    }

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String networkState="";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        default_gw_editText = (EditText) findViewById(R.id.editText);
        TextView ip_address_editText = (TextView) findViewById(R.id.editText2);

        networkState=networkHelper.getConnectionType();


        pingDestIP = networkHelper.getGwIp();
        default_gw_editText.setText(pingDestIP);
        ip_address_editText.setText(networkHelper.getIp());

        createIpSpinner(pingDestIP);



    }

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        boolean ipValid;
        btnClick = (Button) findViewById(view.getId());
        pingDestIP = default_gw_editText.getText().toString();
        ipValid= networkHelper.checkIpIsValid(pingDestIP);
        if (ipValid) {
            if (pingThreadRunning == false) {
                pingAt = new PingAsyncTask(pingDestIP);
                pingAt.execute(pingDestIP);
                btnClick.setText("Stop");
                pingThreadRunning = true;
            } else {
                pingAt.cancel(true);
                btnClick.setText("Start");
                pingThreadRunning = false;
            }

        } else {
            dialogIpInvalid();
        }


    }


}
