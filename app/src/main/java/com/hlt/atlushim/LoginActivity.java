package com.hlt.atlushim;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    final String PASSWORD = "password";
    final String USERNAME = "username";
    final String DATA = "data";
    ProgressBar pBar;
    MyAsyncTask lAsync;
    EditText etUser;
    EditText etPass;
    String site = "https://www.tlushim.co.il/main.php?op=start";
    String strData;
    //String site = "https://www.tlushim.co.il/main.php?op=atnd&month=2020_01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        getDefaults(this);

        if(!strData.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", strData);
            intent.putExtra("parentActivity", "login");
            startActivity(intent);
        }
    }

    public void loginButtonClick(View view) {
        if (!DetectConnection.checkInternetConnection(this)) {
            setDefaults(this);
            Snackbar.make(view, "אין אינטרנט", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            Snackbar.make(view, "טוען נתונים מאתר", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            pBar = findViewById(R.id.progressBar);
            pBar.setVisibility(View.VISIBLE);
            setDefaults(this);
            startAsync(etUser.getText().toString(), etPass.getText().toString(),site);
        }
    }

    void startAsync(String user,String pass,String mySite){
        lAsync = new MyAsyncTask(this);
        lAsync.execute(user,pass,mySite);
    }

    void asyncResult(String result) {
        pBar.setVisibility(View.INVISIBLE);
        if(result.equals("error")){
            Toast.makeText(this, "שם משתמש או סיסמה לא נכונים", Toast.LENGTH_LONG).show();
        }else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("data", result);
            editor.apply();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("result", result);
            intent.putExtra("parentActivity", "login");
            startActivity(intent);
        }
    }

    void setDefaults(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASSWORD, etPass.getText().toString());
        editor.putString(USERNAME, etUser.getText().toString());
        editor.apply();
    }

    void getDefaults(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String savedText = preferences.getString(PASSWORD, "");
        etPass.setText(savedText);
        savedText = preferences.getString(USERNAME, "");
        etUser.setText(savedText);
        strData = preferences.getString(DATA,"");
    }
}
