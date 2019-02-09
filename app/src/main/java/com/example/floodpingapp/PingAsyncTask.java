package com.example.floodpingapp;

import android.os.AsyncTask;
import android.widget.TextView;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class PingAsyncTask extends AsyncTask<String, Void, Void> {

    public AtomicBoolean isStopped=new AtomicBoolean(false);

    public PingAsyncTask instance;

    private TextView pingText;
    private String pingDestIP;

    private ArrayList<Float> pingTimesList = new ArrayList<>();

    private String pingString = "";

    private MainActivity mainActivity = MainActivity.getInstance();

    public PingAsyncTask(String ip){
        //this.pingText=pingText;
        this.pingDestIP=ip;
    }

    public PingAsyncTask getInstance(){
        return instance;
    }

    private String calcAvgPingTime(ArrayList<PingResult> pingArray){
        float addresult=0;
        for (PingResult pr : pingArray){
            addresult+=pr.getTimeTaken();
        }
        return String.valueOf(addresult/pingArray.size());
    }

    private PingResult pingAddress(String ip){
        PingResult pingRes=null;
        try {
            pingRes = Ping.onAddress(pingDestIP).setTimeOutMillis(1000).doPing();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return pingRes;
    }

    @Override
    protected Void doInBackground(String... strings) {
        //this.pingDestIP=strings[0];

        instance=this;
        System.out.println("PingThread instance: " + instance);
        PingResult pingResult=null;

        int textCounter=0;
        long pingCounter=0;
        long lossCounter=0;
        float avgSum=0;
        while(! isCancelled() ){
            pingCounter++;
            textCounter++;
            if(textCounter>640){
                textCounter=0;
                pingString="";
            }
            pingResult=pingAddress(pingDestIP);

            if (pingResult.isReachable){
                pingString+="O";
                pingTimesList.add(pingResult.timeTaken);
                avgSum+=pingResult.timeTaken;
            }

            if (pingResult.hasError()) {
                pingString+="X";
                lossCounter+=1;
            }

            mainActivity.updateTextPingResult(pingString);
            String strPingTimes = "Loss: "
                    + String.valueOf(lossCounter)
                    + " " + String.valueOf(lossCounter/pingCounter*100)
                    + "%\n"
                    + "Pings: " + pingCounter
                    + " Avg Time: "
                    + String.valueOf(avgSum/pingCounter);

            mainActivity.updateTextPingTimes(strPingTimes);
        }
        return null;
    }



}
