package com.hlt.atlushim;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    GetPrevAsyncTask mAsync;
    final String PASSWORD = "password";
    final String USERNAME = "username";

    SharedPreferences preferences;
    ProgressDialog pd;
    String user;
    String pass;
    String parentActivity="";
    boolean renew=false;
    Calendar date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Intent intent = getIntent();
        updateViewWithResult(intent.getStringExtra("result"));
        parentActivity = intent.getStringExtra("parentActivity");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        user = preferences.getString(USERNAME, "");
        pass = preferences.getString(PASSWORD, "");
        date = Calendar.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent intent = getIntent();
        parentActivity = intent.getStringExtra("parentActivity");
        assert parentActivity != null;
        if(parentActivity.equals("main")){
            getMenuInflater().inflate(R.menu.prev_month_menu, menu);
        }else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    void connectionDialog(Context context,String message){
        pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()){
            case "חודש לפני":
                if (!DetectConnection.checkInternetConnection(this)) {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }else {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy_MM", Locale.getDefault());
                    if(date.get(Calendar.DAY_OF_MONTH) > 25){
                        date.add(Calendar.MONTH, 0);
                    }else {
                        date.add(Calendar.MONTH, -1);
                    }
                    startAsync(user, pass, "https://www.tlushim.co.il/main.php?op=atnd&month=" + dateFormat.format(date.getTime()));
                    connectionDialog(this,getString(R.string.prev_month)+" "+dateFormat.format(date.getTime()));
                }
                break;
            case "אודות":
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
            case "לחדש":
                if (!DetectConnection.checkInternetConnection(this)) {
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                }else {
                    connectionDialog(this,getString(R.string.get_update));
                    renew = true;
                    startAsync(user, pass, "https://www.tlushim.co.il/main.php?op=start");
                }
                break;
            case "חזרה":
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void startAsync(String user,String pass,String mySite){
        mAsync = new GetPrevAsyncTask(this);
        mAsync.execute(user,pass,mySite);
    }

    void asyncResult(String result) {
        SharedPreferences.Editor editor = preferences.edit();
        pd.dismiss();
        if(result.equals("error")){
            Toast.makeText(this, getString(R.string.update_error), Toast.LENGTH_LONG).show();
        }else {
            if(renew){
                updateViewWithResult(result);
                editor.putString("data", result);
                editor.apply();
                Toast.makeText(this,getString(R.string.update_ok) , Toast.LENGTH_LONG).show();
                renew=false;
            }else {
                editor.putString("prevMonth",result);
                editor.apply();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("result", result);
                intent.putExtra("parentActivity", "main");
                startActivity(intent);
            }
        }
    }

    public static String [][] to2dim (String source, String outer_delimiter, String inner_delimiter) {
        // outer_delimiter may be a group of characters
        String [] sOuter = source.split ("[" + outer_delimiter + "]");
        int size = sOuter.length;
        // one dimension of the array has to be known on declaration:
        String [][] result = new String [size][];
        int count = 0;
        for (String line : sOuter)
        {
            result [count] = line.split (inner_delimiter);
            ++count;
        }
        return result;
    }

    void updateViewWithResult(String result) {
        String sDate, sMore, sLess, sTotal;
        int[] colors = new int[2];
        colors[0] = Color.parseColor("#FFCEF7FF");
        colors[1] = Color.parseColor("#FFDADCFF");
        String[][] rows = to2dim(result, "\n", ",");
        double mHours = 0;
        double lHours = 0;
        double tkn , totalDouble ;
        if(!rows[0][0].equals("error") && !rows[0][0].isEmpty()) {
            setContentView(R.layout.activity_main);
            LinearLayout parent = findViewById(R.id.linLayout);
            LayoutInflater ltInflater = getLayoutInflater();

            String[] strHeader=rows[0][0].split(" ");
            Objects.requireNonNull(getSupportActionBar()).setTitle("נוכחות "+strHeader[1]+" "+strHeader[0]);
            getSupportActionBar().setSubtitle("נכון לתאריך "+strHeader[7]);

            for (int i = 2; i < rows.length-3; i++) {
                View item = ltInflater.inflate(R.layout.item, parent, false);
                TextView date = item.findViewById(R.id.taarih);
                TextView in = item.findViewById(R.id.in);
                TextView out = item.findViewById(R.id.out);
                TextView total = item.findViewById(R.id.total);
                if (rows[i].length>60){
                    if (rows[i][rows[i].length - 3].equals("רגיל") || rows[i][rows[i].length - 3].contains("חוה")) {
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][4]);
                        out.setText(rows[i][5]);
                        total.setText(rows[i][6]);
                        tkn = Double.parseDouble(rows[i][rows[i].length - 2]);
                        if (rows[i][rows[i].length - 3].equals("חוה\"מ 1"))
                            tkn=8.5;

                        if (!rows[i][rows[i].length - 4].isEmpty() && !rows[2][1].contains("אי השלמת תקן")) {
                            totalDouble = Double.parseDouble(rows[i][rows[i].length - 4]);
                            if (totalDouble < tkn)
                                lHours += tkn - totalDouble;
                            if(totalDouble > tkn)
                                mHours+= totalDouble-tkn;
                        }else if(!rows[i][rows[i].length - 5].isEmpty()){
                            totalDouble = Double.parseDouble(rows[i][rows[i].length - 5]);
                            if (totalDouble < tkn)
                                lHours += tkn - totalDouble;
                            if(totalDouble > tkn)
                                mHours+= totalDouble-tkn;
                        }

                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    }else if (rows[i][7].equals("חופשה") ||
                            rows[i][7].contains("מילואים") ||
                            rows[i][7].contains("חול בתפקיד") ||
                            rows[i][7].contains("אבל") ||
                            rows[i][7].contains("מצב בטחוני")) {
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][7]);
                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    }else if(rows[i][rows[i].length - 3].contains("העדרות")||
                            rows[i][rows[i].length - 3].contains("חג")){
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][rows[i].length - 3]);
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    }else if(rows[i][rows[i].length - 3].contains("שישי")){
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][4]);
                        out.setText(rows[i][5]);
                        total.setText(rows[i][6]);
                        if (!rows[i][rows[i].length - 4].isEmpty() && !rows[2][1].contains("אי השלמת תקן")) {
                            totalDouble = Double.parseDouble(rows[i][rows[i].length - 4]);
                            mHours+= totalDouble;
                        }else if(!rows[i][rows[i].length - 5].isEmpty()){
                            totalDouble = Double.parseDouble(rows[i][rows[i].length - 5]);
                            mHours+= totalDouble;
                        }
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    }

                    if(rows[i][7].contains("מחלה")){
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        total.setText("");
                        in.setText("");
                        out.setText(rows[i][7]);
                    }


                }
            }

            TextView moreHours = findViewById(R.id.overHours);
            TextView lessHours = findViewById(R.id.lessHours);
            TextView whatYouNeed = findViewById(R.id.whatYouNeed);

            moreHours.setTextColor(Color.parseColor("#008577"));
            lessHours.setTextColor(Color.parseColor("#DF9797"));

            sMore = getString(R.string.overHours) + "\n" + Time.sFromD(mHours);
            sLess = getString(R.string.lessHours) + "\n" + Time.sFromD(lHours);
            moreHours.setText(sMore);
            lessHours.setText(sLess);

            double sum = mHours - lHours;
            if (sum < 0) {
                sTotal = getString(R.string.youLess) + " " + Time.sFromD(Math.abs(sum)) +" "+ getString(R.string.hours);
                whatYouNeed.setText(sTotal);
                whatYouNeed.setTextColor(Color.parseColor("#DF9797"));
            } else {
                sTotal = getString(R.string.youMore) + " " + Time.sFromD(sum) +" "+ getString(R.string.hours);
                whatYouNeed.setText(sTotal);
                whatYouNeed.setTextColor(Color.parseColor("#008577"));
            }

        }
    }


    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        builder.setTitle("לצאת מהתוכנה?");
        builder.setCancelable(false).setPositiveButton("כן", (dialogInterface, i) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }).setNegativeButton("לא", (dialogInterface, i) -> finish());
        Intent intent = getIntent();
        parentActivity = intent.getStringExtra("parentActivity");
        assert parentActivity != null;
        if(parentActivity.equals("login")){
            AlertDialog alert = builder.create();
            alert.show();
        }else {
            finish();
        }
    }
}