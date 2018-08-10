package com.wf.demo.wfv30;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Intent intent;
    private EditText ShopCard;
    private AlertDialog loadingDialog;
    SoapConnect.AsyncResponse validTerminalResponse;
    SoapConnect.AsyncResponse cardToshopResponse;
    SoapConnect.AsyncResponse numTerminalResponse;
    LineConnect.AsyncResponse lineResponse;
    Locale myLocale;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Build Prefs
        new Prefs.Builder()
                .setContext(getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Prefs.remove("PriceType");
        Prefs.putString("Language",Prefs.getString("Language","th"));
        setLocal(Prefs.getString("Language","th"));
        super.onCreate(savedInstanceState);
        Utils.setTheme(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // Set Nav Bar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_bar);
        View view = getSupportActionBar().getCustomView();
        View Mview = getWindow().getDecorView().findViewById(android.R.id.content);
        activity = this;
        setTouchSound(view);
        setTouchSound(Mview);
        TextView titleBar = (TextView) view.findViewById(R.id.TitleBar);
        titleBar.setText(R.string.LogOnMenu);
        ImageButton iconBar = (ImageButton) view.findViewById(R.id.IconRightBar);
        iconBar.setImageResource(R.drawable.gear_white);
        iconBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                intent = new Intent(MainActivity.this, ServiceForm.class);
                startActivity(intent);
            }
        });

        setLoadingDialog(); // Set Default Loading Dialog
        ShopCard = (EditText) findViewById(R.id.ShopCard);
        final Button btnSubmit = (Button) findViewById(R.id.Submit);

        validTerminalResponse = new SoapConnect.AsyncResponse(){
            @Override
            public void processFinish(JSONObject result) {
                if(result.has("Table1")){
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject data = jsonArray.getJSONObject(i);
                            Utils.setDataJson(MainActivity.this,data);
                        }
                        if (!Prefs.getString("SwapShop",null).equals("Y")
                                && !Prefs.getString("SlogOn",null).equals("Y")){
                            ShopCard.setVisibility(View.INVISIBLE);
                            btnSubmit.callOnClick();
                        }else{
                            ShopCard.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                }else{
                    btnSubmit.setVisibility(View.INVISIBLE);
                    ShopCard.setVisibility(View.INVISIBLE);
                }
            }
        };
        if(Prefs.getString("IPAddress",null)!=null){
            Model.ValidTerminalJS(loadingDialog,MainActivity.this,validTerminalResponse);
        }

        // On click Button
        ShopCard.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    btnSubmit.callOnClick();
                    return true;
                }
                return false;
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("TermCode",null)==null) {
                    AlertDialog.Builder mError = new AlertDialog.Builder(MainActivity.this);
                    View view = View.inflate(MainActivity.this, R.layout.error_message, null);
                    mError.setView(view);
                    mError.setCancelable(true);
                    TextView err = view.findViewById(R.id.ErrorMessage);
                    err.setText(R.string.PMissingConfig);
                    AlertDialog error = mError.create();
                    mError.show();
                }else {
                    numTerminalResponse = new SoapConnect.AsyncResponse() {
                        @Override
                        public void processFinish(JSONObject result) {
                            if (result.has("Table1")) {
                                try {
                                    JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject data = jsonArray.getJSONObject(i);
                                        Utils.setDataJson(MainActivity.this, data);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if(ShopCard.getVisibility() == View.VISIBLE){
                                TextView shopCard = (TextView) findViewById(R.id.ShopCard);
                                if(shopCard.getText().toString().equals("")){
                                    AlertDialog.Builder mError = new AlertDialog.Builder(MainActivity.this);
                                    View view = View.inflate(MainActivity.this, R.layout.error_message, null);
                                    mError.setView(view);
                                    mError.setCancelable(true);
                                    TextView err = (TextView) view.findViewById(R.id.ErrorMessage);
                                    err.setText(R.string.PMissingData);
                                    mError.show();
                                }else {
                                    cardToshopResponse = new SoapConnect.AsyncResponse() {
                                        @Override
                                        public void processFinish(JSONObject result) {
                                            if (result.has("dtShop")) {
                                                try {
                                                    JSONArray jsonArray = new JSONArray(result.getString("dtShop"));
                                                    for (int i = 0; i < jsonArray.length(); i++) {
                                                        JSONObject data = jsonArray.getJSONObject(i);
                                                        Utils.setDataJson(MainActivity.this, data);
                                                    }
                                                    intent = new Intent(MainActivity.this, TerminalForm.class);
                                                    startActivity(intent);
                                                    finish();
                                                    loadingDialog.dismiss();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    };
                                    TextView CardNo = findViewById(R.id.ShopCard);
                                    Model.CardToShopJS(loadingDialog,MainActivity.this,cardToshopResponse,CardNo.getText().toString());
                                }
                            }else{
                                intent = new Intent(MainActivity.this, TerminalForm.class);
                                startActivity(intent);
                                finish();
                                loadingDialog.dismiss();
                            }
                        }
                    };
                    Model.NumTerminalByCategoryJS(loadingDialog,MainActivity.this,numTerminalResponse);
                    ShopCard.requestFocus();
                    ShopCard.selectAll();
                }
            }
        });
        /*lineResponse = new LineConnect.AsyncResponse(){
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("info")){
                    try {
                        String res = result.getString("info");
                        JSONObject data = new JSONObject(res);
                        if (data.has("paymentUrl")){
                            String url = data.getString("paymentUrl");
                            JSONObject payment = new JSONObject(url);
                            ImageView imgView = (ImageView) findViewById(R.id.imageView);
                            Utils.showQR(payment.getString("app"),imgView); //have 2 link for payment (web,app)
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        TestLine();
        TesPromptPay();
        String text = Promptpay.text_qr("0874432803",true,"TH","100.50","THB");
        ImageView imgView = (ImageView) findViewById(R.id.imageView);
        Utils.showQR(text,imgView); */

    }

    public void TestLine(){
        try {
            LineConnect line = new LineConnect(MainActivity.this,lineResponse);
            JSONObject params = new JSONObject();
            String url = "https://sandbox-api-pay.line.me/v2/payments/request";
            params.put("productName","test_product");
            params.put("amount",1);
            params.put("currency","THB");
            params.put("confirmUrl","http://google.co.th");
            params.put("orderId","17091908300251");
            line.execute(url,params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setLoadingDialog(){
        AlertDialog.Builder mAlert = new AlertDialog.Builder(MainActivity.this);
        View Vloading = View.inflate(MainActivity.this,R.layout.loading,null);
        ProgressBar pgb = (ProgressBar) Vloading.findViewById(R.id.progressBarLoading);
        pgb.setVisibility(View.GONE);
        pgb.setVisibility(View.VISIBLE);
        mAlert.setView(Vloading);
        mAlert.setCancelable(false);
        loadingDialog = mAlert.create();
        Utils.hideKeyboardAlert(loadingDialog);
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