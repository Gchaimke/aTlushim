package com.hlt.atlushim;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class LoginActivity extends AppCompatActivity {

    final String PASSWORD = "password";
    final String USERNAME = "username";
    ProgressBar pBar;
    MyAsyncTask mAsync;
    SharedPreferences sPref;
    EditText etUser;
    EditText etPass;
    String site = "https://www.tlushim.co.il/main.php?op=start";
    //String site = "https://www.tlushim.co.il/main.php?op=atnd&month=2020_01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        loadData();
    }

    public void loginButtonClick(View view) {
        if (!DetectConnection.checkInternetConnection(this)) {
            saveData();
            Snackbar.make(view, "אין אינטרנט", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else {
            Snackbar.make(view, "טוען נתונים מאתר", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            pBar = findViewById(R.id.progressBar);
            pBar.setVisibility(View.VISIBLE);
            saveData();
            startAsync(etUser.getText().toString(), etPass.getText().toString(),site);
        }
    }

    void startAsync(String user,String pass,String mySite){
        mAsync = new MyAsyncTask(this);
        mAsync.execute(user,pass,mySite);
    }

    void asyncResult(String result) {
        pBar.setVisibility(View.INVISIBLE);
        if(result.equals("error")){
            Toast.makeText(this, "שם משתמש או סיסמה לא נכונים", Toast.LENGTH_LONG).show();
        }else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);
        }
    }

    void saveData() {
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(PASSWORD, etPass.getText().toString());
        ed.putString(USERNAME, etUser.getText().toString());
        ed.apply();
        //Toast.makeText(this, "User data saved", Toast.LENGTH_SHORT).show();
    }

    void loadData() {
        sPref = getPreferences(MODE_PRIVATE);
        String savedText = sPref.getString(PASSWORD, "");
        etPass.setText(savedText);
        savedText = sPref.getString(USERNAME, "");
        etUser.setText(savedText);
        //Toast.makeText(this, "User data loaded", Toast.LENGTH_SHORT).show();
    }
}
