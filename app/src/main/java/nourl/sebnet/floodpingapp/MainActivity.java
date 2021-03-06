package nourl.sebnet.floodpingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import nourl.sebnet.floodpingapp.R;

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

    private void dialogNoNetwork(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Error");
        builder.setMessage("There is no mobile or wifi network. Check airplane mode is off.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }
    private void dialogMobileGatewayNotPingable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mobile Network Gateway Not Pingable");
        builder.setMessage("Your providers next router is not pingable. Destination will switch to 1.1.1.1. If you only want to test your wifi, please turn it on");
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
        if (networkState.equals("NO_NETWORK")) {
            dialogNoNetwork();
        } else if (networkState.equals("MOBILE")) {
            dialogMobileGatewayNotPingable();
            pingDestIP = "1.1.1.1";

        } else {
            pingDestIP = networkHelper.getGwIp();
        }
        default_gw_editText.setText(pingDestIP);
        ip_address_editText.setText(networkHelper.getIp());
        createIpSpinner(pingDestIP);

    }


    public void startButton(View view) {
        btnClick = (Button) findViewById(view.getId());
        pingDestIP = default_gw_editText.getText().toString();
        boolean ipValid= networkHelper.checkIpIsValid(pingDestIP);
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
