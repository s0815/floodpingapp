package com.example.floodpingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.stealthcopter.networktools.IPTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class NetworkHelper {
    private Context context;

    public NetworkHelper(Context context){
        this.context=context;
    }

    public String getIp() {
        String [] cmdResult = getRouteFromShell();

        for (String i : cmdResult) {
            System.out.println(i);
        }
        //return Formatter.formatIpAddress(get_dhcp().ipAddress);
        return cmdResult[8];
    }

    public String getGwIp() {
        String [] cmdResult = getRouteFromShell();

        //return Formatter.formatIpAddress(get_dhcp().gateway);
        return cmdResult[2];
    }

    public boolean checkIpIsValid(String ip){
        return IPTools.isIPv4Address(ip);

    }

    public String getConnectionType(){
        String netState="";
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connMan.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if (!isConnected){
            netState="NO_NETWORK";
        } else {
            netState=activeNetwork.getTypeName();
        }
        System.out.println(netState);
        return netState;

    }

    private String[] getRouteFromShell(){
        String command = "ip route get 1.1.1.1";
        Process process=null;
        String line="";
        try {
            process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            line=reader.readLine();
            System.out.println("CMD: " + line);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return line.split(" ");

    }



}
