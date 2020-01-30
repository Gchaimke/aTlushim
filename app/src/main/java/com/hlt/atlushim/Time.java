package com.hlt.atlushim;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Time {



    public static double dFromS(String str){
        DecimalFormat df = new DecimalFormat("0.00");
        String [] strToD=str.split(":");
        try {
            double hour = Double.parseDouble(strToD[0]);
            double minute = Double.parseDouble(strToD[1]);
            minute = ((100.0 / 60) * minute) / 100;
            BigDecimal bd = new BigDecimal(minute).setScale(2, RoundingMode.HALF_UP);
            return hour+bd.doubleValue();
        }catch (Exception e){

        }
        return 0;
    }

    public static String sFromD(Double time){
        int h = (int) Math.floor(time);
        int m = (int) (time * 100 % 100);
        int minute = (int)(0.6*m);
        if(minute<10){
            return h+":0" + minute;
        }
        return h+":" + minute;
    }

    public static String addTime(String time1, String time2){
        String total="";
        Double summ = dFromS(time1)+dFromS(time2);
        total = sFromD(summ);
        return total;
    }

    public static String subTime(String time1, String time2){
        String total="";
        Double summ = dFromS(time1)-dFromS(time2);
        total = sFromD(summ);
        return total;
    }
}
