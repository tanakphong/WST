package com.wf.demo.wfv30;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.Locale;

public class ServiceForm extends AppCompatActivity {

    private TextView IPAddress;
    private TextView Port;
    private TextView Password;
    private TextView MyIP;
    private CheckBox DedDisPlay;
    private CheckBox SFLoftMode;
    private CheckBox SPriceMode;
    Activity activity;
    Locale myLocale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new Prefs.Builder()
                .setContext(getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        setLocal(Prefs.getString("Language","th"));
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.setTheme(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.service_form);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        View view = getSupportActionBar().getCustomView();
        View Mview = getWindow().getDecorView().findViewById(android.R.id.content);
        activity = this;
        setTouchSound(view);
        setTouchSound(Mview);
        TextView titleBar = (TextView) view.findViewById(R.id.TitleBar);
        titleBar.setText(R.string.ServiceMenu);
        MyIP = (TextView) findViewById(R.id.MyIP);
        IPAddress = (TextView) findViewById(R.id.IPAddress);
        Port = (TextView) findViewById(R.id.Port);
        Password = (TextView) findViewById(R.id.Password);
        SPriceMode = (CheckBox) findViewById(R.id.SPriceMode);
        DedDisPlay = (CheckBox) findViewById(R.id.DedDisPlay);
        SFLoftMode = (CheckBox) findViewById(R.id.SFLoftMode);
        MyIP.setText(Utils.getIP(ServiceForm.this));

        if(Prefs.getString("Language",null)!=null && Prefs.getString("Language",null).equals("en")){
            RadioButton engMenu = (RadioButton) findViewById(R.id.EngMenu);
            engMenu.setChecked(true);
        }else{
            RadioButton thaiMenu = (RadioButton) findViewById(R.id.ThaiMenu);
            thaiMenu.setChecked(true);
        }
        Button btnSubmmit = (Button) findViewById(R.id.Submit);
        btnSubmmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Prefs.putString("IPAddress",IPAddress.getText().toString());
                Prefs.putString("Port",Port.getText().toString());
                Prefs.putString("Password",Password.getText().toString());
                Prefs.putBoolean("SShowMonitor",SPriceMode.isChecked());
                Prefs.putBoolean("DedDisPlay",DedDisPlay.isChecked());
                Prefs.putBoolean("SFLoftMode",SFLoftMode.isChecked());
                if (!Prefs.getString("PriceType","").equals("")) {
                    Intent intent = new Intent(ServiceForm.this, TerminalForm.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(ServiceForm.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        setDataForm();
    }

    public void selectLanguage(View view){
        switch (view.getId()){
            case R.id.ThaiMenu:
                Prefs.putString("Language","th");
                break;
            case R.id.EngMenu:
                Prefs.putString("Language","en");
                break;
            default:
                Prefs.putString("Language","th");
                break;
        }
        Intent intent = new Intent(this,ServiceForm.class);
        startActivity(intent);
        finish();
    }

    private void setDataForm(){
        IPAddress.setText(Prefs.getString("IPAddress",""));
        Port.setText(Prefs.getString("Port",""));
        Password.setText(Prefs.getString("Password",""));
        SPriceMode.setChecked(Prefs.getBoolean("SShowMonitor",false));
        DedDisPlay.setChecked(Prefs.getBoolean("DedDisPlay",false));
        SFLoftMode.setChecked(Prefs.getBoolean("SFLoftMode",false));
    }

    private void setLocal(String language){
        myLocale = new Locale(language);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf,dm);
        getApplication().createConfigurationContext(conf);
    }

    private void setTouchSound(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.addSound(activity);
                return false;
            }
        });
    }
}
