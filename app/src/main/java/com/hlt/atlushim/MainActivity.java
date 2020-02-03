package com.hlt.atlushim;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        Intent intent = getIntent();
        getResult(intent.getStringExtra("result"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()){
            case "תאריך":
                Calendar date = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("YYYY_MM", Locale.getDefault());
                date.add(Calendar.MONTH,-1);
                Toast.makeText(this, dateFormat.format(date.getTime()), Toast.LENGTH_SHORT).show();
                break;
            case "הגדרות":
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
            case "אודות":
                Intent intent = new Intent(this,AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        String name = data.getStringExtra("name");
        //tvName.setText("Your name is " + name);
    }

    public static String [][] to2dim (String source, String outerdelim, String innerdelim) {
        // outerdelim may be a group of characters
        String [] sOuter = source.split ("[" + outerdelim + "]");
        int size = sOuter.length;
        // one dimension of the array has to be known on declaration:
        String [][] result = new String [size][];
        int count = 0;
        for (String line : sOuter)
        {
            result [count] = line.split (innerdelim);
            ++count;
        }
        return result;
    }

    void getResult(String result) {
        String sDate, sMore, sLess, sTotal;
        int[] colors = new int[2];
        colors[0] = Color.parseColor("#FFCEF7FF");
        colors[1] = Color.parseColor("#FFDADCFF");

        String[][] rows = to2dim(result, "\n", ",");

        double mHours = 0;
        double lHours = 0;
        if(!rows[0][0].equals("error")) {
            setContentView(R.layout.activity_main);
            LinearLayout parent = findViewById(R.id.linLayout);
            LayoutInflater ltInflater = getLayoutInflater();

            String[] strHeader=rows[0][0].split(" ");
            Objects.requireNonNull(getSupportActionBar()).setTitle("דוח נוכחות של "+strHeader[1]+" "+strHeader[0]);
            getSupportActionBar().setSubtitle("נחון לתאריך "+strHeader[7]);

            for (int i = 2; i < rows.length-3; i++) {
                View item = ltInflater.inflate(R.layout.item, parent, false);
                TextView date = item.findViewById(R.id.taarih);
                TextView in = item.findViewById(R.id.in);
                TextView out = item.findViewById(R.id.out);
                TextView total = item.findViewById(R.id.total);
                if (rows[i].length>60){
                    if (rows[i][7].equals("רגיל")) {
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][4]);
                        out.setText(rows[i][5]);
                        total.setText(rows[i][6]);
                        if (!rows[2][1].contains("אי השלמת תקן")) {
                            double totalDouble = Double.parseDouble(rows[i][rows[i].length - 4]);
                            if (totalDouble < 8.40)
                                lHours += 8.40 - totalDouble;
                        }else{
                            double totalDouble = Double.parseDouble(rows[i][rows[i].length - 5]);
                            if (totalDouble < 8.40)
                                lHours += 8.40 - totalDouble;
                        }
                        if (rows[2][29].contains("125") && !rows[i][64].isEmpty())
                            mHours += Double.parseDouble(rows[i][64]);
                        if (rows[2][30].contains("150") && !rows[i][65].isEmpty())
                            mHours += Double.parseDouble(rows[i][65]);

                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    } else if (rows[i][7].equals("חופשה")) {
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][7]);
                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    } else if (rows[i][7].contains("מחלה")) {
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText(rows[i][7]);
                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
                    } else if ( rows[i][1].contains("חג")) {
                        sDate = rows[i][2] + "\n" + rows[i][3];
                        date.setText(sDate);
                        in.setText("חג");
                        item.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                        item.setBackgroundColor(colors[i % 2]);
                        parent.addView(item);
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
                sTotal = getString(R.string.youLess) + " " + Time.sFromD(sum) +" "+ getString(R.string.hours);
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
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            finish();
                            break;
                    }
                    // commonVariable.setSteps(0);
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("לצאת מהתוכנה?")
                    .setPositiveButton("כן", dialogInterface)
                    .setNegativeButton("לא", dialogInterface).show();
        }
    }



}

