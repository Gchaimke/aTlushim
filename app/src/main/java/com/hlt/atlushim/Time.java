package com.hlt.atlushim;

import java.math.BigDecimal;
import java.math.RoundingMode;

class Time {



    static double dFromS(String str){
        String [] strToD=str.split(":");
        try {
            double hour = Double.parseDouble(strToD[0]);
            double minute = Double.parseDouble(strToD[1]);
            minute = ((100.0 / 60) * minute) / 100;
            BigDecimal bd = new BigDecimal(minute).setScale(2, RoundingMode.HALF_UP);
            return hour+bd.doubleValue();
        }catch (Exception ignored){

        }
        return 0;
    }

    static String sFromD(Double time){
        int h =time.intValue();
        int m = (int) (time * 100 % 100);
        int minute = Math.abs((int)(0.6*m));
        if(minute<10){
            return h+":0" + minute;
        }
        return h+":" + minute;
    }

    static String addTime(String time1, String time2){
        Double summ = dFromS(time1)+dFromS(time2);
        return sFromD(summ);
    }

    static String subTime(String time1, String time2){
        Double summ = dFromS(time1)-dFromS(time2);
        return sFromD(summ);
    }
}
