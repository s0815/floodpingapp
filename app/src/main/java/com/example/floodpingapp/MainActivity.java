package com.example.floodpingapp;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
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

    private DhcpInfo get_dhcp() {
        final WifiManager manager = (WifiManager) super.getApplicationContext().getSystemService(WIFI_SERVICE);
        return manager.getDhcpInfo();
    }

    private String get_ip() {
        return Formatter.formatIpAddress(get_dhcp().ipAddress);
    }

    private String get_gw_ip() {
        return Formatter.formatIpAddress(get_dhcp().gateway);
    }

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

    public static MainActivity getInstance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        default_gw_editText = (EditText) findViewById(R.id.editText);
        TextView ip_address_editText = (TextView) findViewById(R.id.editText2);
        pingDestIP = get_gw_ip();
        default_gw_editText.setText(pingDestIP);
        ip_address_editText.setText(get_ip());

        createIpSpinner(pingDestIP);

    }

    /**
     * Called when the user taps the Send button
     */
    public void sendMessage(View view) {
        // Do something in response to button
//        Intent intent = new Intent(this, DisplayMessageActivity.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
//        startActivity(intent);
        btnClick = (Button) findViewById(view.getId());
        pingDestIP = default_gw_editText.getText().toString();


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


    }


}
