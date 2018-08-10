package com.wf.demo.wfv30;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;

import static android.content.ContentValues.TAG;

// #################################################################################################
// #                                        This Class HttpsUtil                                   #
// #################################################################################################

class HttpsUtil {

    private static TrustManager[] trustManagers;

    private static class _FakeX509TrustManager implements javax.net.ssl.X509TrustManager {
        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return (_AcceptedIssuers);
        }
    }

    static void allowAllSSL() {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        javax.net.ssl.SSLContext context = null;

        if (trustManagers == null) {
            trustManagers = new TrustManager[] { new _FakeX509TrustManager() };
        }

        try {
            context = javax.net.ssl.SSLContext.getInstance("TLS");
            context.init(null, trustManagers, new SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Log.e("allowAllSSL", e.toString());
        }
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
    }
}
// #################################################################################################
// #                                        This Class Promptpay                                   #
// #################################################################################################

class Promptpay {

    static String text_qr(String account,Boolean one_time,String country,String money,String currency){
        String oneTime=null;
        String Version = "0002"+"01";
        if (one_time){
            oneTime = "010212";
        }else{
            oneTime = "010211";
        }
        String merchant_account_information="2937"; // Detail Saler
        merchant_account_information += "0016"+"A000000677010111"; // Number of PromptPay
        if (account.length() != 13){
            merchant_account_information+="011300";
            if (country.equals("TH")) {
                merchant_account_information += "66"; // Code Country
            }
            account = account.substring(1);
            merchant_account_information+=account;
        }else{
            merchant_account_information+="02"+account; // Account ID
        }
        country="5802"+country;
        if (currency.equals("THB")) {
            currency = "5303" + "764"; // "764" is money Thai Bath
        }
        if (!money.equals("")) {
            String[] check_money = money.split("0");
            int length = String.valueOf(Float.valueOf(money)).length();
            String fmoney = String.valueOf(Float.valueOf(money));
            if (check_money.length == 1 || check_money[1].length() == 1) {
                money = "54" + "0" + String.valueOf(length+1)+(fmoney+"0");
            }
            else {
                money = "54" + "0" + String.valueOf(length)+fmoney;
            }
        }
        String check_sum=Version+oneTime+merchant_account_information+country+currency+money+"6304";
        String res = Integer.toHexString(Promptpay.crc16xmodem(check_sum.toUpperCase(),0xffff)).replace("0x","");
        String result = null;
        if(res.length()<4){
            for (int i=0;i<(4-res.length());i++){
                if(i==0){
                    result = "0";
                }
                else{
                    result +="0";
                }
            }
            result += res;
        }else{
            result = res;
        }
        result = (check_sum+result).toUpperCase();
        return result;
    }

    static int[] crc16Table = {
            0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
            0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef,
            0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6,
            0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
            0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485,
            0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
            0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4,
            0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc,
            0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823,
            0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b,
            0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12,
            0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a,
            0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
            0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49,
            0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70,
            0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78,
            0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f,
            0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004, 0x4025, 0x7046, 0x6067,
            0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
            0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256,
            0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d,
            0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
            0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c,
            0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634,
            0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab,
            0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3,
            0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a,
            0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92,
            0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9,
            0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1,
            0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
            0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0,
    };
    static int _crc16(String data, int crc, int [] table){
        for(int i=0;i<data.length();i++) {
            crc = ((crc << 8) & 0xff00) ^ table[((crc >> 8) & 0xff) ^ (data.charAt(i))];
        }
        return crc & 0xffff;
    }
    static int crc16xmodem(String data,int crc){
        return _crc16(data,crc,crc16Table);
    }

}
// #################################################################################################
// #                                        This Class Utils                                       #
// #################################################################################################

class Utils {

    private static MediaPlayer mp;

    static void showAllPref(Activity activity){
        new Prefs.Builder()
                .setContext(activity.getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(activity.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        Map<String, ?> prefsMap = Prefs.getAll();
        for (Map.Entry<String, ?> entry: prefsMap.entrySet()) {
            Log.v("SharedPreferences", entry.getKey() + ":" + entry.getValue().toString());
        }
    }

    static void addSound(final Activity activity){
        new Prefs.Builder()
                .setContext(activity.getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(activity.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        String sound = Prefs.getString("ThemeSound","Default");
        AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        int volume = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
            switch (sound){
                case "Crystal":
                    mp = MediaPlayer.create(activity,R.raw.crystal);
                    break;
                case "MouseClick":
                    mp = MediaPlayer.create(activity,R.raw.mouseclick);
                    break;
                case "Bell":
                    mp = MediaPlayer.create(activity,R.raw.bell);
                    break;
                case "Beep":
                    mp = MediaPlayer.create(activity,R.raw.beep);
                    break;
                case "Swish":
                    mp = MediaPlayer.create(activity,R.raw.swish);
                    break;
                default:
                    mp = MediaPlayer.create(activity,R.raw.effect);
                    break;
            }
            mp.setVolume(volume,volume);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp.isPlaying()){
                        mp.stop();
                        mp.release();
                    }
                }
            });
        }
    }

    static void addSoundWithKey(final Activity activity,String sound){
        new Prefs.Builder()
                .setContext(activity.getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(activity.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        AudioManager am = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        int volume = am.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        if (volume != 0) {
            switch (sound){
                case "Crystal":
                    mp = MediaPlayer.create(activity,R.raw.crystal);
                    break;
                case "MouseClick":
                    mp = MediaPlayer.create(activity,R.raw.mouseclick);
                    break;
                case "Bell":
                    mp = MediaPlayer.create(activity,R.raw.bell);
                    break;
                case "Beep":
                    mp = MediaPlayer.create(activity,R.raw.beep);
                    break;
                case "Swish":
                    mp = MediaPlayer.create(activity,R.raw.swish);
                    break;
                default:
                    mp = MediaPlayer.create(activity,R.raw.effect);
                    break;
            }
            mp.setVolume(volume,volume);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mp.isPlaying()){
                        mp.stop();
                        mp.release();
                    }
                }
            });
        }
    }

    static void setTheme(Activity activity){
        new Prefs.Builder()
                .setContext(activity.getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(activity.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        int color;
        Window window = activity.getWindow();
        String ThemeColor = Prefs.getString("ThemeColor","Default");
        if (ThemeColor.equals("Blue")) {
            activity.setTheme(R.style.Theme_Blue);
            color = R.color.colorBlue;
        }else if (ThemeColor.equals("Red")) {
            activity.setTheme(R.style.Theme_Red);
            color = R.color.colorRed;
        }else if (ThemeColor.equals("Black")) {
            activity.setTheme(R.style.Theme_Black);
            color = R.color.colorBlack2;
        }else if (ThemeColor.equals("Green")) {
            activity.setTheme(R.style.Theme_Green);
            color = R.color.colorGreen;
        }else if (ThemeColor.equals("Pink")) {
            activity.setTheme(R.style.Theme_Pink);
            color = R.color.colorPink;
        }else{
            activity.setTheme(R.style.Theme_Default);
            color = R.color.colorBlack;
        }
        Prefs.putInt("ThemeVisible",R.color.colorBGVisible);
        Prefs.putInt("ThemeVisibleText",R.color.colorTextVisible);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(activity,color));
        }
    }

    static String setamt(String amt){
        float val = 0;
        if (amt != null && !amt.equals("")){
            val = Float.valueOf(amt);
        }
        return String.format("%.2f",val);
    }

    static String setamtF(String amt){
        DecimalFormat formatter = new DecimalFormat("#,###,##0.00");
        float val = 0;
        if (amt != null && !amt.equals("")){
            val = Float.valueOf(amt);
        }
        return formatter.format(val);
    }

    static void setDataJson(Context context, JSONObject data){
        new Prefs.Builder()
                .setContext(context.getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(context.getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Iterator<String> keys = data.keys();
        while(keys.hasNext()){
            String key = keys.next();
            String val;
            if (!key.equals("method") && !key.equals("getProp")) {
                try {
                    val = data.getString(key);
                    Prefs.putString(key,val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void hideKeyboardAlert(AlertDialog alert){
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    static void setWHAlertDialog(AlertDialog alert){
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        //int width = alert.getContext().getResources().getInteger(R.integer.WidthAlert);
        alert.getWindow().setLayout((int) (width*0.8),alert.getWindow().getAttributes().height);
    }

    static void setDelayDismiss(final AlertDialog alert){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alert.dismiss();
            }
        }, alert.getContext().getResources().getInteger(R.integer.DelayDismiss));
    }

    static void showQR(String content, ImageView imgView){
        QRCodeWriter writer = new QRCodeWriter();
        try {
            int width = 500;
            int height = 500;
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK: Color.WHITE);
                }
            }
            imgView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    static String getIP(Context context) {
        //WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = "0.0.0.0";
        /*if (!isHotspot(wifiManager)) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = (ipAddress & 0xFF) + "." +
                    ((ipAddress >> 8) & 0xFF) + "." +
                    ((ipAddress >> 16) & 0xFF) + "." +
                    ((ipAddress >> 24) & 0xFF);
        }*/
        ArrayList<String> tmpNetwork = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    Log.i(TAG, "getIP: "+String.valueOf(inetAddress));
                    if (!inetAddress.isLoopbackAddress()) {
                        inetAddress.getAddress();
                        if (!inetAddress.getHostAddress().toString().startsWith("fe80")) {
                            tmpNetwork.add(inetAddress.getHostAddress().toString());
                        }
                    }
                }
            }
            return tmpNetwork.get(0);
        } catch (SocketException ex) {
            Log.i("dlg", "getIPAddress : " + ex.getMessage());
            return ip;
        }
    }

    private static boolean isHotspot(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getDeclaredMethod("getWifiApState");
            method.setAccessible(true);
            int state = (Integer) method.invoke(wifiManager, (Object[]) null);
            int AP_STATE_ENABLED = 13;
            if (state == AP_STATE_ENABLED) {
                return true;
            }
        } catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}

// #################################################################################################
// #                                        This Class Soap Connect                                #
// #################################################################################################

class SoapConnect extends AsyncTask<String,Void,JSONObject> {
    private TextView err;
    private AlertDialog error;
    private AlertDialog dialog;

    interface AsyncResponse {
        void processFinish(JSONObject result);
    }

    private AsyncResponse delegate = null;

    SoapConnect(Context context, AsyncResponse delegate,AlertDialog loadingDialog)
    {
        this.delegate = delegate;
        dialog = loadingDialog;
        AlertDialog.Builder mError = new AlertDialog.Builder(context);
        View Verror = View.inflate(context,R.layout.error_message,null);
        mError.setView(Verror);
        mError.setCancelable(true);
        error = mError.create();
        Utils.hideKeyboardAlert(error);
        err = (TextView) Verror.findViewById(R.id.ErrorMessage);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result = null;
        String IP = params[0];
        String Port = params[1];
        String jstring = params[2];
        String[] sort = params[3].split(",");
        Log.i("doInBackground", jstring);
        try {
            result = CallWebService.GetDataService("http://"+IP+":"+Port+"/WFservice30?wsdl",new JSONObject(jstring),sort);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        super.onPostExecute(s);
        Log.i("onPostExecute",String.valueOf(s));
        try {
            if(s != null && s.has("ErrorDesc")){
                dialog.dismiss();
                err.setText(s.getString("ErrorDesc"));
                error.show();
                Utils.setWHAlertDialog(error);
                Utils.setDelayDismiss(error);
            }
            if(this.delegate != null){
                this.delegate.processFinish(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

// #################################################################################################
// #                                        This Class Line Connect                                #
// #################################################################################################

class LineConnect extends AsyncTask<String,Void,JSONObject> {
    private TextView err;
    private AlertDialog.Builder mError;
    private AlertDialog dialog;

    interface AsyncResponse {
        void processFinish(JSONObject result) throws JSONException;
    }

    private AsyncResponse delegate = null;

    LineConnect(Context context, AsyncResponse delegate)
    {
        this.delegate = delegate;
        AlertDialog.Builder mAlert = new AlertDialog.Builder(context);
        View Vloading = View.inflate(context,R.layout.loading,null);
        ProgressBar pgb = (ProgressBar) Vloading.findViewById(R.id.progressBarLoading);
        pgb.setVisibility(View.GONE);
        pgb.setVisibility(View.VISIBLE);
        mAlert.setView(Vloading);
        mAlert.setCancelable(false);
        dialog = mAlert.create();

        mError = new AlertDialog.Builder(context);
        View Verror = View.inflate(context,R.layout.error_message,null);
        mError.setView(Verror);
        mError.setCancelable(true);
        err = (TextView) Verror.findViewById(R.id.ErrorMessage);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject result = null;
        String URLS = params[0];
        String jstring = params[1];

        Log.i("doInBackground", jstring);
        try {
            JSONObject data = new JSONObject(jstring);
            URL url = new URL(URLS); //Enter URL here
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
            httpURLConnection.setRequestProperty("X-LINE-ChannelId","1535970895");
            httpURLConnection.setRequestProperty("X-LINE-ChannelSecret","d572892a297f1e52eb7d1949baa0245f");
            httpURLConnection.connect();

            JSONObject jsonObject = new JSONObject();

            Iterator<String> iter = data.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = data.getString(key);
                jsonObject.put(key,value);
            }

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonObject.toString());
            wr.flush();
            wr.close();

            BufferedReader cgiOutput = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = cgiOutput.readLine();
            Log.i("Checkdata From Line", line);
            return new JSONObject(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(JSONObject s) {
        super.onPostExecute(s);
        dialog.dismiss();
        Log.i("onPostExecute",String.valueOf(s));
        try {
            if (s != null && s.has("returnCode") && !s.getString("returnCode").equals("0000")) {
                err.setText(s.getString("returnMessage"));
                mError.show();
            } else if (this.delegate != null) {
                this.delegate.processFinish(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

// #################################################################################################
// #                                        This Class TCP Client                                  #
// #################################################################################################

class TCPClient {
    @SuppressLint("NewApi")
    static void send(String message, String ip, int port) {
        new TCPSend(message, ip, port).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }
    @SuppressLint("NewApi")
    private static class TCPSend extends AsyncTask<Void, Void, Void> {
        private SendCallback callback = null;
        private String message = null;
        private String ip = null;
        private String tag = null;
        private int port;

        TCPSend(String message, String ip, int port) {
            this.message = message;
            this.ip = ip;
            this.port = port;
        }
        protected Void doInBackground(Void... params) {
            try {
                Socket s = new Socket(ip, port);
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                String outgoingMsg = message + System.getProperty("line.separator");
                out.write(outgoingMsg);
                out.flush();
                if(callback != null) {
                    s.setSoTimeout(5000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String inMessage = in.readLine();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            if(inMessage.contains("%OK%")) {
                                callback.onSuccess(tag);
                            } else {
                                callback.onFailed(tag);
                            }
                        }
                    });
                }
                s.close();
            } catch (IOException e) {
                if(callback != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            callback.onFailed(tag);
                        }
                    });
                }
            }
            return null;
        }
    }

    interface SendCallback {
        void onSuccess(String tag);
        void onFailed(String tag);
    }
}

// #################################################################################################
// #                                        This Class Call Line Api                               #
// #################################################################################################

// #################################################################################################
// #                                        This Class Call WCF                                    #
// #################################################################################################

class CallWebService {
    private static final String NAMESPACE = "http://tempuri.org/";
    private static final int TIMEOUT = 10000;

    static JSONObject GetDataService(String url, JSONObject jobj, String[] sort) throws JSONException {
        String SOAP_ACTION = null;
        String OPERATION_NAME = null;
        try{
            OPERATION_NAME = String.valueOf(jobj.getString("method"));
            SOAP_ACTION = "http://tempuri.org/IWFServiceV30/"+OPERATION_NAME;
        }catch (Exception e){
            e.printStackTrace();
        }
        SoapObject request = new SoapObject(NAMESPACE, OPERATION_NAME);
        for (String aSort : sort) {
            request.addProperty(aSort, jobj.getString(aSort));
        }
        try {
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.bodyOut = request;
            HttpsUtil.allowAllSSL();
            HttpTransportSE androidHttpTransport = new HttpTransportSE(url, TIMEOUT);
            androidHttpTransport.debug = true;
            androidHttpTransport.reset();
            androidHttpTransport.call(SOAP_ACTION, envelope);
            SoapObject result=(SoapObject)envelope.bodyIn;
            int getErr = 1;
            if (jobj.has("getError")){
                getErr = Integer.valueOf(jobj.getString("getError"));
            }
            if (result.getPropertyCount() <= 1 || jobj.has("ReturnNow")){
                return null;
            }
            try {
                Log.i("Check Data", String.valueOf(result));
                if ((Boolean) result.getProperty(0)){
                    Log.i("Get result by prop",String.valueOf(result));
                    String obj = String.valueOf(result.getProperty(Integer.valueOf(jobj.getString("getProp"))));
                    return new JSONObject(obj);
                }else{
                    JSONObject err = new JSONObject();
                    err.put("Error","Have error");
                    err.put("ErrorDesc",result.getProperty(getErr));
                    err.put("ErrorType","Manual");
                    return err;
                }
            }catch (Exception e){
                e.printStackTrace();
                JSONObject err = new JSONObject();
                err.put("Error","Don't have data");
                err.put("ErrorDesc",result.getProperty(1));
                err.put("ErrorType","Manual");
                return null;
            }
        } catch (Exception e){
            e.printStackTrace();
            JSONObject err = new JSONObject();
            err.put("Error","Exception error");
            err.put("ErrorDesc",e.getMessage());
            err.put("ErrorType","Exception");
            return err;
        }
    }
}