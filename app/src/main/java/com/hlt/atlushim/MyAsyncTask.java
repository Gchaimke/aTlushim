package com.hlt.atlushim;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

class MyAsyncTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final LoginActivity lClass;

    MyAsyncTask(LoginActivity lClass) {
        this.lClass = lClass;
    }

    @Override
    protected void onPreExecute() {
        //showProgressDialog
        System.out.println("Connecting to tlushim.co.il from login Activity");
    }

    @Override
    protected String  doInBackground(String... loginData) {
        String str="...";
        HtmlParser htmlParser = new HtmlParser();
        try {
            str = htmlParser.connectToSite(loginData[0], loginData[1], loginData[2]);

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return str;
    }

    @Override
    protected void onPostExecute(String result) {
        lClass.asyncResult(result);

    }

}

class GetPrevAsyncTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final MainActivity mClass;

    GetPrevAsyncTask(MainActivity mClass) {
        this.mClass = mClass;
    }

    @Override
    protected void onPreExecute() {
        //showProgressDialog
        System.out.println("Connecting to tlushim.co.il from Main Activity");
    }

    @Override
    protected String  doInBackground(String... loginData) {
        String str="...";
        HtmlParser htmlParser = new HtmlParser();
        try {
            str = htmlParser.connectToSite(loginData[0], loginData[1], loginData[2]);

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return str;
    }

    @Override
    protected void onPostExecute(String result) {
        mClass.asyncResult(result);

    }

}
