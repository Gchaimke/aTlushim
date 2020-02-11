package com.hlt.atlushim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    PackageInfo pInfo;
    String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "V"+pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView appVersionInfo = findViewById(R.id.appVersion);
        appVersionInfo.setText(version);
    }
}
