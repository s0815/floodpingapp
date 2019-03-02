package com.example.floodpingapp;

import android.os.AsyncTask;
import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class PingAsyncTask extends AsyncTask<String, Void, Void> {

    private String pingDestIP;

    private ArrayList<Float> pingTimesList = new ArrayList<>();

    private String pingString = "";

    private MainActivity mainActivity = MainActivity.getInstance();

    public PingAsyncTask(String ip){
        //this.pingText=pingText;
        this.pingDestIP=ip;
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
        PingResult pingResult=null;

        int textCounter=0;
        long pingCounter=0;
        long lossCounter=0;
        double lossPercent=0;
        double pingMax=0;
        double pingMin=1000;
        float avgSum=0;

        while(! isCancelled() ) {
            pingCounter++;
            textCounter++;
            if (textCounter > 800) {
                textCounter = 0;
                pingString = "";
            }
            pingResult = pingAddress(pingDestIP);

            if (pingResult.isReachable) {
                pingString += ".";
                pingTimesList.add(pingResult.timeTaken);
                if (pingResult.timeTaken<pingMin) {
                    pingMin=pingResult.timeTaken;
                }
                if (pingResult.timeTaken>pingMax) {
                    pingMax=pingResult.timeTaken;
                }

                avgSum += pingResult.timeTaken;
            }

            if (pingResult.hasError()) {
                pingString += "X";
                lossCounter += 1;
            }

            //paintDia();

            mainActivity.updateTextPingResult(pingString);
            lossPercent = lossCounter * 100.0 / pingCounter;
            String strPingTimes = "Loss: "
                    + String.valueOf(lossCounter)
                    + " " + String.format("%.2f", lossPercent)
                    + "%\n"
                    + "Pings: " + pingCounter
                    + " Avg Time: "
                    + String.format("%.3f", avgSum / pingCounter)
                    + "\n"
                    + "Max: " + String.format("%.3f", pingMax)
                    + " Min: " + String.format("%.3f", pingMin);

            mainActivity.updateTextPingTimes(strPingTimes);
        }
        return null;
    }
}
