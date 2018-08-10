package com.wf.demo.wfv30;

import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.wf.demo.wfv30.R.id.AmtFrom;
import static com.wf.demo.wfv30.R.id.CardNoFrom;
import static com.wf.demo.wfv30.R.id.CheckCard;
import static com.wf.demo.wfv30.R.id.ShowDesc;
import static com.wf.demo.wfv30.Utils.setDelayDismiss;
import static com.wf.demo.wfv30.Utils.setWHAlertDialog;
import static com.wf.demo.wfv30.Utils.setamt;

public class TerminalForm extends AppCompatActivity {

    private static final String TAG = "dlg";
    private Intent intent;

    private View tView;
    private View pView;
    private View cbView;
    private View themeView;
    private View showdataView;
    private View sShopView;
    private View sPrintView;
    private View printSumView;

    private AlertDialog dAlert;
    private AlertDialog shAlert;
    private AlertDialog lAlert;
    private AlertDialog cAlert;
    private AlertDialog pAlert;
    private AlertDialog csAlert;
    private AlertDialog tAlert;
    private AlertDialog cbAlert;
    private AlertDialog sAlert;
    private AlertDialog vAlert;
    private AlertDialog AlertError;
    private AlertDialog loadingDialog;
    private AlertDialog pSumAlert;
    private AlertDialog pSlipAlert;

    private AlertDialog shopSetup;
    private AlertDialog monitorSetup;
    private AlertDialog soundSetup;
    private AlertDialog printSetup;
    private AlertDialog themeSetup;
    private AlertDialog showdataSetup;

    private Button ABtnSubmit;
    private Button LBtnSubmit;
    private Button CBtnSubmit;
    private Button PBtnSubmit;
    private Button CSBtnSubmit;
    private Button TBtnSubmit;
    private Button CBBtnClear;

    private Button btnChangeShop;
    private Button btnCancel;
    private Button btnTransfer;
    private Button btnLock;
    private Button btnPrintSum;
    private Button printSum;
    private Button btnPrintSlip;
    private Button setupShop;
    private Button setupMonitor;
    private Button setupPrinter;
    private Button setupSound;
    private EditText Input;

    private TextView ATextLine;
    private TextView SHTextLine;
    private EditText CCardNo;
    private EditText CAmount;
    private TextView CSShopNo;
    private EditText PCardNo;
    private TextView CSShopName;
    private EditText CSCardNo;
    private ImageButton CSCheckCard;
    private TextView CBCardExp;
    private EditText CBCardNo;
    private TextView CBAmtRemain;
    private TextView errText;
    private EditText VCardNo;
    private ScrollView scroll;

    private TableLayout OrderListTable;

    JSONArray items;
    TableLayout dataTable;
    JSONArray CTSarray;
    JSONObject CTSobj;
    JSONObject Voidobj;
    JSONObject Card;
    JSONObject Payment;

    String VerifyCase;

    SoapConnect.AsyncResponse selectTerminalResponse;
    SoapConnect.AsyncResponse cardToshopResponse;
    SoapConnect.AsyncResponse getCardInfoResponse;
    SoapConnect.AsyncResponse getTerminalResponse;
    SoapConnect.AsyncResponse getShopResponse;
    SoapConnect.AsyncResponse getSystemResponse;
    SoapConnect.AsyncResponse printSummary;
    SoapConnect.AsyncResponse selectShopNetSale;
    SoapConnect.AsyncResponse checkCardTrans;
    SoapConnect.AsyncResponse printCardTrans;
    SoapConnect.AsyncResponse printCurrentSlip;
    SoapConnect.AsyncResponse printAbb;

    SoapConnect.AsyncResponse voidCard;
    SoapConnect.AsyncResponse voidCardInfo; //Add by nineNeung
    SoapConnect.AsyncResponse voidSale;
    SoapConnect.AsyncResponse checkCard;
    SoapConnect.AsyncResponse getCardRight;
    SoapConnect.AsyncResponse getCardFrom;
    SoapConnect.AsyncResponse getCardTo;
    SoapConnect.AsyncResponse getCardMulti;
    SoapConnect.AsyncResponse checkCardMulti;
    SoapConnect.AsyncResponse valueTransfer;
    SoapConnect.AsyncResponse valueTransferMulti;
    SoapConnect.AsyncResponse getCardPayment;
    SoapConnect.AsyncResponse paymentVerify;
    SoapConnect.AsyncResponse paymentAccept;
    SoapConnect.AsyncResponse changePriceType;
    SoapConnect.AsyncResponse putPrint;
    SoapConnect.AsyncResponse putMonitor;

    Locale myLocale;
    Activity activity;
    private TextView mTotal;
    private String code, type, detail, price, total;
    private Handler handler;
    private Runnable rCleasMonitorDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new Prefs.Builder()
                .setContext(getApplicationContext())
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        Prefs.putString("Language", Prefs.getString("Language", "th"));
        setLocal(Prefs.getString("Language", "th"));

        super.onCreate(savedInstanceState);
        Utils.setTheme(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.terminal_form);
        getSupportActionBar().hide();
        View Mview = getWindow().getDecorView().findViewById(android.R.id.content);
        activity = this;
        setTouchSound(Mview);
        setLoadingDialog(); // -- Set Default Loading Dialog

        // -- Create Scroll View --
        scroll = (ScrollView) findViewById(R.id.ScrollView);

        // -- Create DataTable --
        dataTable = (TableLayout) findViewById(R.id.DataTable);
        dataTable.setStretchAllColumns(true);

        // -- Create DataList --
        OrderListTable = (TableLayout) findViewById(R.id.OrderListTable);
        OrderListTable.setStretchAllColumns(true);
        TableRow row = new TableRow(this);
        View view = View.inflate(this, R.layout.order_list_view, null);
        TextView Desc = (TextView) view.findViewById(R.id.Description);
        TextView Price = (TextView) view.findViewById(R.id.Price);
        CheckBox Check = (CheckBox) view.findViewById(R.id.CheckBox);
        Check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                CheckBox all = (CheckBox) v;
                for (int j = 0; j < OrderListTable.getChildCount(); j++) {
                    View view = OrderListTable.getChildAt(j);
                    if (view instanceof TableRow) {
                        TableRow list = (TableRow) view;
                        CheckBox listCheck = (CheckBox) list.findViewById(R.id.CheckBox);
                        if (all.isChecked()) {
                            listCheck.setChecked(true);
                        } else {
                            listCheck.setChecked(false);
                        }
                    }
                }
            }
        });
        Desc.setText(R.string.ODescription);
        Price.setText(R.string.OPrice);
        row.addView(view);
        OrderListTable.addView(row);

        getTerminalResponse = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        TextView Desc = (TextView) findViewById(R.id.Desc);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Utils.setDataJson(TerminalForm.this, obj);
                            if (obj.has("ScrWelcome")) {
                                Desc.setText(obj.getString("ScrWelcome"));
                            }
                        }
                        Model.GetSysInfoJS(loadingDialog, TerminalForm.this, getSystemResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        getShopResponse = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Utils.setDataJson(TerminalForm.this, obj);
                        }
                        String PriceType = Prefs.getString("PriceType", "M");
                        Prefs.putString("PriceType", PriceType);
                        Integer getProp;
                        switch (PriceType) {
                            case "M":
                                getProp = 5;
                                break;
                            case "H":
                                getProp = 4;
                                break;
                            default:
                                getProp = 3;
                                break;
                        }
                        Model.SelectTerminalJS(loadingDialog, TerminalForm.this, selectTerminalResponse, getProp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        selectTerminalResponse = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result != null && result.has("Table1")) {
                    try {
                        items = new JSONArray(result.getString("Table1"));
                        UpdateItems(); // Set View Items
                        UpdateMonitor("Clear", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                setDefaultData(); //-- Set Default Data --
            }
        };
        getSystemResponse = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Utils.setDataJson(TerminalForm.this, obj);
                        }
                        Model.SelectTerminalJS(loadingDialog, TerminalForm.this, getShopResponse, 3);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                }
            }
        };
        Model.SelectTerminalJS(loadingDialog, TerminalForm.this, getTerminalResponse, 2);

        setAlertError(); // -- Set Default Alert Error --
        setShowAlertPopup(); // -- Set Default Show Alert --
        setVerifyPopup(); // -- Set Default Verify Popup --
        setAlertPopup(); // -- Set Default Alert Popup--
        setLockScreen(); // -- Set Default Lock Screen --
        setCancelPopup(); // -- Set Default Cancel Popup --
        setPaymentPopup(); // -- Set Default Payment Popup --
        setChangeShopPopup(); // -- Set Default Change Shop Popup --
        //setTransferMultiPopup(); // -- Set Default Transfer Multi Popup --
        setTransferPopup(); // -- Set Default Transfer Shop Popup --
        setCheckBalPopup(); // -- Set Default Check Balance Popup --
        setTerminalPopup(); // -- Set Default Terminal Setup Popup --
        setLogoutPopup(); // -- Set Default Logout Popup --
        setDeletePopup(); // -- Set Default Delete Popup --
        setClearPopup(); // -- Set Default Clear Popup --
        setPrintSumPopup(); // -- Set Default Print Summary Popup --
        setPrintSlip(); //-- Set Default Print Slip Popup --

        setPrinterSetup(); // -- Set Default Printer Setup --
        setMonitorSetup(); // -- Set Default Monitor Setup --
        setTerminalSetup(); // -- Set Default Terminal Setup --
        setThemeSetup(); // -- Set Default Theme Setup --
        setSoundSetup(); // -- Set Default Theme Sound --
        setShowDataSetup(); // -- Set Default ShowData Setup --

        setShowData();
        Input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (Prefs.getString("PriceType", "").equals("E")) {
                    Input.setSelection(Input.getText().toString().length());
                    UpdateTotal();
                } else {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        if (Input != null && !Input.getText().toString().equals("")) {
                            try {
                                setItem(Input.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Input.setText("");
                    }
                    Input.requestFocus();
                }
                return false;
            }
        });

        //Edit by nineNeung
        mTotal = (TextView) findViewById(R.id.Total);
        handler = new Handler();
    }

    private void setLoadingDialog() {
        AlertDialog.Builder mAlert = new AlertDialog.Builder(TerminalForm.this);
        View Vloading = View.inflate(TerminalForm.this, R.layout.loading, null);
        ProgressBar pgb = (ProgressBar) Vloading.findViewById(R.id.progressBarLoading);
        pgb.setVisibility(View.GONE);
        pgb.setVisibility(View.VISIBLE);
        mAlert.setView(Vloading);
        mAlert.setCancelable(false);
        loadingDialog = mAlert.create();
        Utils.hideKeyboardAlert(loadingDialog);
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
    }

    private void setItem(String code) throws JSONException {
        for (int i = 0; i < items.length(); i++) {
            JSONObject obj = items.getJSONObject(i);
            if (obj.getString("ItemCode").equals(code)) {
                UpdateList(i);
                break;
            }
        }
    }

    private void setDataToPayment(String TrnType) {
        EditText CardNo = (EditText) pView.findViewById(R.id.CardNo);
        EditText Total = (EditText) findViewById(R.id.Total);
        if (CardNo != null && !CardNo.getText().toString().equals("")) {
            if (Total != null && !Total.getText().toString().equals("")) {
                String cardNo = CardNo.getText().toString();
                Float total = Float.valueOf(Total.getText().toString());
                String prices = "";
                String promprices = "";
                String descs = "";
                String codes = "";
                for (int i = 1; i < OrderListTable.getChildCount(); i++) {
                    View view = OrderListTable.getChildAt(i);
                    if (view instanceof TableRow) {
                        TextView price = (TextView) view.findViewById(R.id.Price);
                        TextView promprice = (TextView) view.findViewById(R.id.PromPrice);
                        TextView desc = (TextView) view.findViewById(R.id.Description);
                        TextView code = (TextView) view.findViewById(R.id.Code);
                        String p = price.getText().toString();
                        String pp = promprice.getText().toString();
                        String d = desc.getText().toString();
                        String c = code.getText().toString();
                        if (Prefs.getString("PriceType", "M").equals("H")) {
                            c = p;
                        }
                        if (i != 1) {
                            prices += "|" + p;
                            promprices += "|" + pp;
                            descs += "|" + d;
                            codes += "|" + c;
                        } else {
                            prices = p;
                            promprices = pp;
                            descs = d;
                            codes = c;
                        }

                    }
                }
                if (Prefs.getString("PriceType", "M").equals("E")) {
                    prices = setamt(String.valueOf(total));
                    codes = prices;
                    promprices = prices;
                    descs = Prefs.getString("SaleDesc", null);
                }
                if (TrnType.equals("P")) {
                    Model.SaleCardReadedJS(loadingDialog, TerminalForm.this, paymentAccept, cardNo, total, prices, promprices, codes, descs, "P");
                } else {
                    Model.SaleCardReadedJS(loadingDialog, TerminalForm.this, paymentVerify, cardNo, total, prices, promprices, codes, descs, "V");
                }
            }
        }
    }

    private void setDefaultData() {

        // -- Set Title --
        TextView Title = (TextView) findViewById(R.id.Title);
        String shopName;
        if (Prefs.getString("Language", null).equals("th")) {
            shopName = Prefs.getString("ShopNameT", null);
        } else {
            shopName = Prefs.getString("ShopNameE", null);
        }
        Resources res = getResources();
        if (shopName.length() > res.getInteger(R.integer.MaxL)) {
            shopName = shopName.substring(0, res.getInteger(R.integer.MaxL)) + "...";
        }
        String title = getString(R.string.Shop) + " : " + Prefs.getString("ShopCode", null) + "," + shopName;
        if (Prefs.getString("PriceType", null) != null) {
            if (Prefs.getString("PriceType", "").equals("H")) {
                Title.setText(title + "," + getString(R.string.PriceTypeH));
            } else if (Prefs.getString("PriceType", "").equals("M")) {
                Title.setText(title + "," + getString(R.string.PriceTypeM));
            } else {
                Title.setText(title + "," + getString(R.string.PriceTypeE));
            }
        }

        // -- Set Total --
        TextView Total = (TextView) findViewById(R.id.Total);
        Total.setText(setamt(String.valueOf(Prefs.getDouble("Total", 0))));

        setTheme();
        setPermission();
    }

    private void setPermission() {
        Resources res = getResources();
        Integer BGVisible = res.getInteger(Prefs.getInt("ThemeVisible", Color.TRANSPARENT));
        Integer TextVisible = res.getInteger(Prefs.getInt("ThemeVisibleText", Color.TRANSPARENT));
        if (Prefs.getString("SwapShop", null).equals("N")) {
            btnChangeShop.setBackgroundColor(BGVisible);
            btnChangeShop.setTextColor(TextVisible);
            btnChangeShop.setOnClickListener(null);
        }
        if (Prefs.getString("Svoid", null).equals("D")) {
            btnCancel.setBackgroundColor(BGVisible);
            btnCancel.setTextColor(TextVisible);
            btnCancel.setOnClickListener(null);
        }
        if (Prefs.getString("PrinterUse", "").equals("N")) {
            btnPrintSlip.setBackgroundColor(BGVisible);
            btnPrintSlip.setTextColor(TextVisible);
            btnPrintSlip.setOnClickListener(null);
            printSum.setVisibility(View.GONE);
        }
        if (Prefs.getString("PriceType", "").equals("H")) {
            Input.setEnabled(false);
        }
        if (Prefs.getString("PriceType", "").equals("E")) {
            Input.setVisibility(View.GONE);
        }
        if (Prefs.getString("CardTrans", "").equals("N") || Prefs.getString("CardPolicy", "F").equals("C")) {
            btnTransfer.setBackgroundColor(BGVisible);
            btnTransfer.setTextColor(TextVisible);
            btnTransfer.setOnClickListener(null);
        }
        if (Prefs.getString("SlogOn", "").equals("N") && Prefs.getString("SwapShop", "").equals("N")) {
            btnLock.setBackgroundColor(BGVisible);
            btnLock.setTextColor(TextVisible);
            btnLock.setOnClickListener(null);
        }
        if (Prefs.getString("SnetSale", "").equals("D")) {
            btnPrintSum.setBackgroundColor(BGVisible);
            btnPrintSum.setTextColor(TextVisible);
            btnPrintSum.setOnClickListener(null);
        }
        if (Prefs.getString("ChangePriceMode", "").equals("N") || Prefs.getString("SpriceMode", "").equals("D")) {
            setupShop.setBackgroundColor(BGVisible);
            setupShop.setTextColor(TextVisible);
            setupShop.setOnClickListener(null);
        }
        if (Prefs.getString("Mdisplay", "").equals("D")) {
            setupMonitor.setBackgroundColor(BGVisible);
            setupMonitor.setTextColor(TextVisible);
            setupMonitor.setOnClickListener(null);
        }
        if (Prefs.getString("Mprinter", "").equals("D")) {
            setupPrinter.setBackgroundColor(BGVisible);
            setupPrinter.setTextColor(TextVisible);
            setupPrinter.setOnClickListener(null);
        }
    }

    private void setAlertError() {
        AlertDialog.Builder mError = new AlertDialog.Builder(TerminalForm.this);
        View errView = View.inflate(TerminalForm.this, R.layout.error_message, null);
        mError.setView(errView);
        mError.setCancelable(true);
        AlertError = mError.create();
        errText = (TextView) errView.findViewById(R.id.ErrorMessage);
    }

    private void setAlertPopup() {
        View aView = View.inflate(this, R.layout.validate_popup, null);
        ATextLine = (TextView) aView.findViewById(R.id.TextLine);
        ABtnSubmit = (Button) aView.findViewById(R.id.Submit);
        Button ABtnCancel = (Button) aView.findViewById(R.id.Cancel);
        AlertDialog.Builder mAlert = new AlertDialog.Builder(TerminalForm.this);
        mAlert.setView(aView);
        mAlert.setCancelable(false);
        dAlert = mAlert.create();
        Utils.hideKeyboardAlert(dAlert);
        ABtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                dAlert.dismiss();
            }
        });
    }

    private void setShowAlertPopup() {
        View view = View.inflate(this, R.layout.validate_popup, null);
        SHTextLine = (TextView) view.findViewById(R.id.TextLine);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.LFoot);
        ll.setVisibility(View.GONE);
        AlertDialog.Builder Alert = new AlertDialog.Builder(TerminalForm.this);
        Alert.setView(view);
        Alert.setCancelable(true);
        shAlert = Alert.create();
        Utils.hideKeyboardAlert(shAlert);
    }

    private void setVerifyPopup() {
        View view = View.inflate(this, R.layout.verify_popup, null);
        final Button BtnSubmit = (Button) view.findViewById(R.id.Submit);
        Button BtnCancel = (Button) view.findViewById(R.id.Cancel);
        VCardNo = (EditText) view.findViewById(R.id.CardNo);
        AlertDialog.Builder mAlert = new AlertDialog.Builder(TerminalForm.this);
        mAlert.setView(view);
        mAlert.setCancelable(false);
        vAlert = mAlert.create();
        Utils.hideKeyboardAlert(vAlert);
        getCardRight = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                boolean verify = false;
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        JSONObject obj = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);
                            Log.d("dlg", "processFinish: " + obj);
                        }
                        String RefShopCode = Prefs.getString("ShopCode", "");
                        String ObjShopCode = null;
                        String CardRoleType = null;
                        if (obj.has("CardRoleType")) {
                            CardRoleType = obj.getString("CardRoleType");
                        }
                        if (obj.has("ShopCode")) {
                            ObjShopCode = obj.getString("ShopCode");
                        }
                        switch (VerifyCase) {
                            case "Svoid":
                                if (obj.has("Mvoid") && obj.getString("Mvoid").equals("Y") && CardRoleType.equals("M")) {
                                    verify = true;
                                } else if (obj.has("Svoid") && obj.getString("Svoid").equals("Y")
                                        && CardRoleType.equals("S") && RefShopCode.equals(ObjShopCode)) {
                                    verify = true;
                                }
                                break;
                            case "SpriceMode":
                                if (obj.has("MpriceMode") && obj.getString("MpriceMode").equals("Y") && CardRoleType.equals("M")) {
                                    verify = true;
                                } else if (obj.has("SpriceMode") && obj.getString("SpriceMode").equals("Y")
                                        && CardRoleType.equals("S") && RefShopCode.equals(ObjShopCode)) {
                                    verify = true;
                                }
                                break;
                            case "PrintSumPopup":
                                if (obj.has("MnetSale") && obj.getString("MnetSale").equals("Y") && CardRoleType.equals("M")) {
                                    verify = true;
                                } else if (obj.has("SnetSale") && obj.getString("SnetSale").equals("Y")
                                        && CardRoleType.equals("S") && RefShopCode.equals(ObjShopCode)) {
                                    verify = true;
                                }
                                break;
                            case "Mprinter":
                                if (obj.has("Mprinter") && obj.getString("Mprinter").equals("Y") && CardRoleType.equals("M")) {
                                    verify = true;
                                }
                                break;
                            case "Mdisplay":
                                if (obj.has("Mdisplay") && obj.getString("Mdisplay").equals("Y") && CardRoleType.equals("M")) {
                                    verify = true;
                                }
                                break;
                        }

                        if (verify) {
                            vAlert.dismiss();
                            VCardNo.setText("");
                            if (VerifyCase.equals("Svoid")) {
                                cAlert.show();
                                setWHAlertDialog(cAlert);
                            } else if (VerifyCase.equals("SpriceMode")) {
                                shopSetup.show();
                                setWHAlertDialog(shopSetup);
                            } else if (VerifyCase.equals("PrintSumPopup")) {
                                pSumAlert.show();
                                setWHAlertDialog(pSumAlert);
                                SimpleDateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm:ss");
                                Date CurrentTime = Calendar.getInstance().getTime();
                                String Date = String.valueOf(DateFormat.format(CurrentTime));
                                String Time = String.valueOf(TimeFormat.format(CurrentTime));
                                TextView date = (TextView) printSumView.findViewById(R.id.CurrentDate);
                                date.setText(getText(R.string.Date) + " " + Date + " " + getText(R.string.Time) + " " + Time);
                                Model.SelectShopNetSaleJS(loadingDialog, TerminalForm.this, selectShopNetSale);
                                Model.GetServerNowJS(loadingDialog, TerminalForm.this);
                            } else if (VerifyCase.equals("Mprinter")) {
                                printSetup.show();
                                setWHAlertDialog(printSetup);
                            } else if (VerifyCase.equals("Mdisplay")) {
                                monitorSetup.show();
                                setWHAlertDialog(monitorSetup);
                            }
                        } else {
                            errText.setText(getText(R.string.NotGetVerify));
                            AlertError.show();
                            setWHAlertDialog(AlertError);
                            Utils.setDelayDismiss(AlertError);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        // On click Button
        VCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    BtnSubmit.callOnClick();
                    return true;
                }
                return false;
            }
        });
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.GetCardRightJS(loadingDialog, TerminalForm.this, getCardRight, VCardNo.getText().toString());
            }
        });
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                vAlert.dismiss();
                cAlert.dismiss();
                shopSetup.dismiss();
                VCardNo.setText("");
            }
        });
    }

    private void setLockScreen() {
        View lView = View.inflate(this, R.layout.lockscreen_popup, null);
        LBtnSubmit = (Button) lView.findViewById(R.id.Submit);
        final EditText LCardNo = (EditText) lView.findViewById(R.id.CardNo);
        AlertDialog.Builder mLock = new AlertDialog.Builder(TerminalForm.this);
        mLock.setView(lView);
        mLock.setCancelable(false);
        lAlert = mLock.create();
        Utils.hideKeyboardAlert(lAlert);
        btnLock = (Button) findViewById(R.id.Lock);
        btnLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                lAlert.show();
                setWHAlertDialog(lAlert);
            }
        });
        LBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.CardToShopJS(loadingDialog, TerminalForm.this, checkCard, LCardNo.getText().toString());
                LCardNo.setText("");
            }
        });
        LCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    LBtnSubmit.callOnClick();
                    return true;
                }
                return false;
            }
        });
        checkCard = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("dtShop")) {
                    try {
                        boolean match = false;
                        JSONArray jsonArray = new JSONArray(result.getString("dtShop"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            if (Prefs.getString("ShopCode", null).equals(obj.getString("ShopCode"))) {
                                match = true;
                            }
                        }
                        if (match) {
                            lAlert.dismiss();
                        } else {
                            errText.setText(R.string.CardNotMatch);
                            AlertError.show();
                            setWHAlertDialog(AlertError);
                            Utils.setDelayDismiss(AlertError);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                }
            }
        };
    }

    private void setCancelPopup() {
        View cView = View.inflate(this, R.layout.cancel_popup, null);
        CCardNo = (EditText) cView.findViewById(R.id.CardNo);
        final ImageButton CCheckCard = (ImageButton) cView.findViewById(R.id.CheckCard);
        CAmount = (EditText) cView.findViewById(R.id.Amount);
        CBtnSubmit = (Button) cView.findViewById(R.id.Submit);
        Button CBtnCancel = (Button) cView.findViewById(R.id.Cancel);
        AlertDialog.Builder mCancel = new AlertDialog.Builder(TerminalForm.this);
        mCancel.setView(cView);
        mCancel.setCancelable(false);
        cAlert = mCancel.create();
        Utils.hideKeyboardAlert(cAlert);
        btnCancel = (Button) findViewById(R.id.Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("Svoid", "").equals("Y")) {
                    VerifyCase = "Svoid";
                    vAlert.show();
                    setWHAlertDialog(vAlert);
                } else {
                    cAlert.show();
                    setWHAlertDialog(cAlert);
                }
                CBtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        ATextLine.setText(R.string.PCancel);
                        dAlert.show();
                        ABtnSubmit.requestFocus();
                        setWHAlertDialog(dAlert);
                        ABtnSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.addSound(activity);
                                if (Voidobj != null && !Voidobj.has("RunningNo")) {
                                    errText.setText(R.string.NotValidData);
                                    AlertError.show();
                                    setWHAlertDialog(AlertError);
                                    Utils.setDelayDismiss(AlertError);
                                }
                                try {
                                    Model.VoidSaleAcceptJS(loadingDialog, TerminalForm.this, voidSale, CCardNo.getText().toString(), Voidobj.getString("RunningNo"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
            }
        });
        CCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    CCheckCard.callOnClick();
                    return true;
                }
                return false;
            }
        });
        CCheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);

                Model.GetCardInfoJS(loadingDialog, TerminalForm.this, voidCardInfo, CCardNo.getText().toString());

            }
        });
        voidCardInfo = new SoapConnect.AsyncResponse() {

            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
//                    MemName,CardTypeDesc
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Card = jsonArray.getJSONObject(i);
                        }
//                        type = Card.getString("CardTypeDesc");
//                        detail = Card.getString("MemName");
//                        Log.d(TAG, "detail: " + detail);
//                        Log.d(TAG, "CardInfo: " + jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Model.SaleVoidCardReadedJS(loadingDialog, TerminalForm.this, voidCard, CCardNo.getText().toString());
                } else {
                    CCardNo.requestFocus();
                    CCardNo.selectAll();
                }
            }
        };

        voidCard = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Voidobj = jsonArray.getJSONObject(i);
                            CAmount.setText(setamt(Voidobj.getString("GrandTotal")));
                        }
                        code = CCardNo.getText().toString();
                        price = CAmount.getText().toString();
                        UpdateMonitor("CancelList", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    CCardNo.requestFocus();
                    CCardNo.selectAll();
                }
            }
        };
        voidSale = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    Voidobj = null;
                    cAlert.dismiss();
                    dAlert.dismiss();
                    String CardNo = CCardNo.getText().toString();
                    CAmount.setText("");
                    CCardNo.setText("");
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        JSONObject obj = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);
                        }
                        SHTextLine.setText(getText(R.string.PCancelAmt) + " : " + setamt(obj.getString("ApproveAmount"))
                                + Html.fromHtml("<br>" + getText(R.string.CardNo) + " : " + CardNo)
                                + " " + getText(R.string.RemainAmt) + " : " + setamt(obj.getString("RetRemain")));
                        shAlert.show();
                        code = CardNo;
                        price = setamt(obj.getString("RetRemain"));
                        UpdateMonitor("CancelledList", false);
                        setWHAlertDialog(shAlert);
                        setDelayDismiss(shAlert);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    CCardNo.requestFocus();
                    CCardNo.selectAll();
                }
            }
        };
        CBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                CCardNo.setText("");
                CAmount.setText("");
                cAlert.dismiss();
            }
        });

    }

    private void setPaymentPopup() {
        pView = View.inflate(this, R.layout.payment_popup, null);
        PCardNo = (EditText) pView.findViewById(R.id.CardNo);
        PBtnSubmit = (Button) pView.findViewById(R.id.Submit);
        Button PBtnCancel = (Button) pView.findViewById(R.id.Cancel);
        final AlertDialog.Builder mPayment = new AlertDialog.Builder(TerminalForm.this);
        mPayment.setView(pView);
        mPayment.setCancelable(false);
        pAlert = mPayment.create();
        Utils.hideKeyboardAlert(pAlert);
        Button btnPaid = (Button) findViewById(R.id.Pay);
        btnPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                TextView Total = (TextView) findViewById(R.id.Total);
                String total = Total.getText().toString();
                if (!total.equals("") && !total.equals("0.00")) {
                    UpdatePayment();
                    //UpdateMonitor("Payment", true);
                    pAlert.show();
                    PCardNo.requestFocus();
                    setWHAlertDialog(pAlert);
                    PBtnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.addSound(activity);
                            String TrnType = "P";
                            setDataToPayment(TrnType);
                        }
                    });
                }
            }
        });
        PCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Utils.hideKeyboardAlert(pAlert);
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardPayment, PCardNo.getText().toString());
                    return true;
                }
                return false;
            }
        });
        getCardPayment = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Card = jsonArray.getJSONObject(i);
                        }
                        String TrnType = "V";
                        setDataToPayment(TrnType);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    PCardNo.requestFocus();
                    PCardNo.selectAll();
                }
            }
        };
        paymentAccept = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        JSONObject obj = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);
                            Utils.setDataJson(TerminalForm.this, obj);
                        }
                        EditText CardNo = (EditText) pView.findViewById(R.id.CardNo);
                        String cardNo = CardNo.getText().toString();
                        UpdateMonitor("Payment", true);
                        ClearPayment(true);
                        pAlert.dismiss();
                        SHTextLine.setText(getText(R.string.CardNo) + " : " + cardNo + " "
                                + getText(R.string.RemainAmt) + " : " + setamt(obj.getString("RetRemain")));
                        shAlert.show();
                        setWHAlertDialog(shAlert);
                        setDelayDismiss(shAlert);
                        //UpdateMonitor("Clear", true);
                        ClearMonitorDelay();
                        TextView Text = (TextView) findViewById(R.id.Input);
                        Text.setText("");
                        UpdateTotal();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    PCardNo.requestFocus();
                    PCardNo.selectAll();
                }
            }
        };
        paymentVerify = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Payment = jsonArray.getJSONObject(i);
                        }
                        UpdatePayment();
                        UpdateMonitor("Payment", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    PCardNo.requestFocus();
                    PCardNo.selectAll();
                }
            }
        };
        PBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                ClearPayment(false);
                UpdateMonitor("Clear", true);
                pAlert.dismiss();
            }
        });
    }

    private void setChangeShopPopup() {
        View csView = View.inflate(this, R.layout.changeshop_popup, null);
        CSShopNo = (TextView) csView.findViewById(R.id.ShopNo);
        CSShopName = (TextView) csView.findViewById(R.id.ShopName);
        CSCardNo = (EditText) csView.findViewById(R.id.CardNo);
        CSCheckCard = (ImageButton) csView.findViewById(CheckCard);
        CSBtnSubmit = (Button) csView.findViewById(R.id.Submit);
        final Button CSBtnCancel = (Button) csView.findViewById(R.id.Cancel);
        AlertDialog.Builder mChangeShop = new AlertDialog.Builder(TerminalForm.this);
        mChangeShop.setView(csView);
        mChangeShop.setCancelable(false);
        csAlert = mChangeShop.create();
        Utils.hideKeyboardAlert(csAlert);
        btnChangeShop = (Button) findViewById(R.id.ChangeShop);
        btnChangeShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
//                if (Prefs.getString("SnetSale", "").equals("Y")) {
//                    VerifyCase = "PrintSumPopup";
//                    vAlert.show();
//                    setWHAlertDialog(vAlert);
//                } else {
                csAlert.show();
                setWHAlertDialog(csAlert);
//                }
            }
        });
        CSBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (CTSarray != null && CTSarray.length() > 0) {
                    csAlert.dismiss();
                    try {
                        for (int i = 0; i < CTSarray.length(); i++) {
                            JSONObject data = CTSarray.getJSONObject(i);
                            Utils.setDataJson(TerminalForm.this, data);
                        }
                        NewIntent();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    errText.setText(R.string.NotValidData);
                    AlertError.show();
                    setWHAlertDialog(AlertError);
                    Utils.setDelayDismiss(AlertError);
                }
            }
        });
        CSCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    CSCheckCard.callOnClick();
                    return true;
                }
                return false;
            }
        });
        CSCheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.CardToShopJS(loadingDialog, TerminalForm.this, cardToshopResponse, CSCardNo.getText().toString());
            }
        });
        cardToshopResponse = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("dtShop")) {
                    try {
                        CTSarray = new JSONArray(result.getString("dtShop"));
                        for (int i = 0; i < CTSarray.length(); i++) {
                            CTSobj = CTSarray.getJSONObject(i);
                            //Utils.setDataJson(TerminalForm.this, data);
                        }
                        CSShopNo.setText(CTSobj.getString("ShopCode"));
                        if (Prefs.getString("Language", null).equals("th")) {
                            CSShopName.setText(CTSobj.getString("ShopNameT"));
                        } else {
                            CSShopName.setText(CTSobj.getString("ShopNameE"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    CSCardNo.requestFocus();
                    CSCardNo.selectAll();
                }
            }
        };
        CSBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                CSShopName.setText("");
                CSCardNo.setText("");
                CSShopNo.setText("");
                csAlert.dismiss();
            }
        });
    }

    private void setTransferMultiPopup() {
        final View view = View.inflate(this, R.layout.transfer_multi_popup, null);
        AlertDialog.Builder mTransfer = new AlertDialog.Builder(TerminalForm.this);
        mTransfer.setView(view);
        mTransfer.setCancelable(false);
        tAlert = mTransfer.create();
        Utils.hideKeyboardAlert(tAlert);
        final EditText CFrom = (EditText) view.findViewById(R.id.CardNoFrom);
        final EditText CardNo = (EditText) view.findViewById(R.id.CardNo);
        final TextView Amt = (TextView) view.findViewById(R.id.AmtRemain);
        ImageButton CheckCard = (ImageButton) view.findViewById(R.id.CheckCard);
        btnTransfer = (Button) findViewById(R.id.Transfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                tAlert.show();
                setWHAlertDialog(tAlert);
                Button TMBtnCancel = (Button) view.findViewById(R.id.Cancel);
                TMBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        CFrom.setText("");
                        CardNo.setText("");
                        Amt.setText("");
                        Card = null;
                        tAlert.dismiss();
                    }
                });
            }
        });
        CardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardMulti, CardNo.getText().toString());
                    return true;
                }
                return false;
            }
        });
        CheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardMulti, CardNo.getText().toString());
            }
        });
        getCardMulti = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                Log.i("processFinish", "processFinish: " + CFrom.getText().toString());
                if (result.has("Table")) {
                    String CNo = CardNo.getText().toString();
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Card = jsonArray.getJSONObject(i);
                        }
                        if (!CFrom.getText().toString().equals("")) {
                            Model.ValueTransferJS(loadingDialog, TerminalForm.this, valueTransferMulti, CFrom.getText().toString(), CNo);
                        } else {
                            loadingDialog.dismiss();
                            Amt.setText(setamt(Card.getString("BalanceValue")));
                            UpdateMonitor("CheckBal", false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CFrom.setText(CNo);
                } else {
                    loadingDialog.dismiss();
                }
                CardNo.requestFocus();
                CardNo.selectAll();
            }
        };
        valueTransferMulti = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    Model.GetCardInfoJS(loadingDialog, TerminalForm.this, checkCardMulti, CardNo.getText().toString());
                } else {
                    loadingDialog.dismiss();
                }
                CardNo.requestFocus();
                CardNo.selectAll();
            }
        };
        checkCardMulti = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Card = jsonArray.getJSONObject(i);
                        }
                        Amt.setText(setamt(Card.getString("BalanceValue")));
                        UpdateMonitor("CheckBal", false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadingDialog.dismiss();
                CardNo.requestFocus();
                CardNo.selectAll();
            }
        };
    }

    private void setTransferPopup() {
        tView = View.inflate(this, R.layout.transfer_popup, null);
        TBtnSubmit = (Button) tView.findViewById(R.id.Submit);
        Button TBtnCancel = (Button) tView.findViewById(R.id.Cancel);
        AlertDialog.Builder mTransfer = new AlertDialog.Builder(TerminalForm.this);
        mTransfer.setView(tView);
        mTransfer.setCancelable(false);
        tAlert = mTransfer.create();
        Utils.hideKeyboardAlert(tAlert);
        final EditText CardFrom = (EditText) tView.findViewById(R.id.CardNoFrom);
        final EditText CardTo = (EditText) tView.findViewById(R.id.CardNoTo);
        final TextView AmtFrom = (TextView) tView.findViewById(R.id.AmtFrom);
        final TextView AmtTo = (TextView) tView.findViewById(R.id.AmtTo);
        final TextView AmtBal = (TextView) tView.findViewById(R.id.AmtRemain);
        btnTransfer = (Button) findViewById(R.id.Transfer);
        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                tAlert.show();
                setWHAlertDialog(tAlert);
                TBtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        Model.ValueTransferJS(loadingDialog, TerminalForm.this, valueTransfer, CardFrom.getText().toString(), CardTo.getText().toString());
                    }
                });
            }
        });
        ImageButton TranFrom = (ImageButton) tView.findViewById(R.id.CheckCardFrom);
        ImageButton TranTo = (ImageButton) tView.findViewById(R.id.CheckCardTo);
        CardFrom.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardFrom, CardFrom.getText().toString());
                    return true;
                }
                return false;
            }
        });
        CardTo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardTo, CardTo.getText().toString());
                    return true;
                }
                return false;
            }
        });
        TranFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardFrom, CardFrom.getText().toString());
            }
        });
        TranTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardTo, CardTo.getText().toString());
            }
        });
        getCardFrom = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            AmtFrom.setText(setamt(obj.getString("BalanceValue")));
                        }
                        UpdateBalanceTransfer(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    CardFrom.requestFocus();
                    CardFrom.selectAll();
                }
            }
        };
        getCardTo = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            AmtTo.setText(setamt(obj.getString("BalanceValue")));
                        }
                        UpdateBalanceTransfer(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    CardTo.requestFocus();
                    CardTo.selectAll();
                }
            }
        };
        valueTransfer = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    String amtTrans = AmtFrom.getText().toString();
                    String cardTo = CardTo.getText().toString();
                    TextView TamtBal = (TextView) tView.findViewById(R.id.AmtRemain);
                    TamtBal.setText("");
                    AmtFrom.setText("");
                    AmtTo.setText("");
                    CardFrom.setText("");
                    CardTo.setText("");
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table1"));
                        JSONObject obj = null;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);
                        }
                        tAlert.dismiss();
                        SHTextLine.setText(getText(R.string.AmtTransfer) + " : " + setamt(amtTrans)
                                + Html.fromHtml("<br>" + getText(R.string.CardNoTo) + " : " + cardTo)
                                + " " + getText(R.string.CardRemainTrans) + " : "
                                + setamt(obj.getString("BalanceValueNew")));
                        shAlert.show();
                        setWHAlertDialog(shAlert);
                        setDelayDismiss(shAlert);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                    ClearMonitorDelay();
                } else {
                    CardFrom.requestFocus();
                }
            }
        };
        TBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                CardFrom.setText("");
                CardTo.setText("");
                AmtFrom.setText("");
                AmtTo.setText("");
                AmtBal.setText("");
                tAlert.dismiss();
                UpdateMonitor("", false);
            }
        });
    }

    private void setCheckBalPopup() {
        cbView = View.inflate(this, R.layout.checkbal_popup, null);
        CBCardExp = (TextView) cbView.findViewById(R.id.CardExp);
        CBCardNo = (EditText) cbView.findViewById(R.id.CardNo);
        CBAmtRemain = (TextView) cbView.findViewById(R.id.AmtRemain);
        ImageButton CBCheckCard = (ImageButton) cbView.findViewById(CheckCard);
        Button CBBtnCancel = (Button) cbView.findViewById(R.id.Cancel);
        CBBtnClear = (Button) cbView.findViewById(R.id.Clear);
        AlertDialog.Builder mCheckBal = new AlertDialog.Builder(TerminalForm.this);
        mCheckBal.setView(cbView);
        mCheckBal.setCancelable(false);
        cbAlert = mCheckBal.create();
        Utils.hideKeyboardAlert(cbAlert);
        CBBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                CBCardNo.setText("");
                CBAmtRemain.setText("");
                CBCardExp.setText("-");
                Card = null;
                UpdateMonitor("Clear", true);
                cbAlert.dismiss();
            }
        });
        CBCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardInfoResponse, CBCardNo.getText().toString());
                    return true;
                }
                return false;
            }
        });
        CBCheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.GetCardInfoJS(loadingDialog, TerminalForm.this, getCardInfoResponse, CBCardNo.getText().toString());
            }
        });
        getCardInfoResponse = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table")) {
                    try {
                        JSONArray jsonArray = new JSONArray(result.getString("Table"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Card = jsonArray.getJSONObject(i);
                            String ExpDate = Card.getString("ExpDate").split(" ", -1)[0];
//                            if (Card.has("ExpDate") && !Card.getString("ExpDate").equals("")) {
//                                String[] DateTime = Card.getString("ExpDate").split(" ");
//                                String[] Date = DateTime[0].split("/");
//                                if (Date.length >= 3) {
//                                    ExpDate = Date[1] + "/" + Date[0] + "/" + String.valueOf(Integer.valueOf(Date[2]));
//                                }
//                            }
                            CBCardExp.setText(String.format(getText(R.string.CardExp) + " : %s", ExpDate));
                            float bal;
                            if (Prefs.getString("CardPolicy", "F").equals("F")) {
                                bal = Float.valueOf(setamt(Card.getString("BalanceValue")));
                                if (Prefs.getBoolean("DedDisPlay", false)) {
                                    bal -= Float.valueOf(setamt(Card.getString("ReserveValue")));
                                }
                            } else {
                                Log.d(TAG, "processFinish: 1");
                                if (Prefs.getBoolean("SFLoftMode", false)) {
                                    Log.d(TAG, "processFinish: SFLoftMode Use");
                                    bal = Float.valueOf(setamt(Card.getString("UsedValue")));
                                } else {
                                    Log.d(TAG, "processFinish: SFLoftMode Remain");
                                    bal = Float.valueOf(setamt(Card.getString("FoodLoftCreditLimit"))) - Float.valueOf(setamt(Card.getString("UsedValue")));
                                }
                            }
                            CBAmtRemain.setText(setamt(String.valueOf(bal)));
                        }
                        UpdateMonitor("CheckBal", true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    loadingDialog.dismiss();
                } else {
                    CBCardNo.requestFocus();
                    CBCardNo.selectAll();
                }
            }
        };
        Button btnCheckBal = (Button) findViewById(R.id.CheckBalance);
        btnCheckBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                cbAlert.show();
                CBCardNo.requestFocus();
                setWHAlertDialog(cbAlert);
                CBBtnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        CBAmtRemain.setText(null);
                        CBCardExp.setText("-");
                        CBCardNo.setText(null);
                    }
                });
            }
        });
    }

    private void setTerminalPopup() {
        Input = (EditText) findViewById(R.id.Input);
        final View sView = View.inflate(this, R.layout.setup_popup, null);
        Button SBtnCancel = (Button) sView.findViewById(R.id.Cancel);
        AlertDialog.Builder mSetup = new AlertDialog.Builder(TerminalForm.this);
        mSetup.setView(sView);
        mSetup.setCancelable(false);
        sAlert = mSetup.create();
        Utils.hideKeyboardAlert(sAlert);
        Button btnSetup = (Button) findViewById(R.id.Setup);
        setupShop = (Button) sView.findViewById(R.id.SetupShop);
        setupMonitor = (Button) sView.findViewById(R.id.SetupMonitor);
        setupPrinter = (Button) sView.findViewById(R.id.SetupPrinter);
        setupSound = (Button) sView.findViewById(R.id.SetupSound);
        Button setupTheme = (Button) sView.findViewById(R.id.SetupTheme);
        Button setupShowData = (Button) sView.findViewById(R.id.SetupShowData);
        Button setupService = (Button) sView.findViewById(R.id.SetupService);
        btnSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                sAlert.show();
                setWHAlertDialog(sAlert);
            }
        });
        setupTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                themeSetup.show();
                setWHAlertDialog(themeSetup);
            }
        });
        setupService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Intent intent = new Intent(TerminalForm.this, ServiceForm.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        setupShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("SpriceMode", "").equals("Y")) {
                    VerifyCase = "SpriceMode";
                    vAlert.show();
                    setWHAlertDialog(vAlert);
                } else {
                    shopSetup.show();
                    setWHAlertDialog(shopSetup);
                }
            }
        });
        setupShowData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                showdataSetup.show();
                setWHAlertDialog(showdataSetup);


            }
        });
        changePriceType = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    sAlert.dismiss();
                    shopSetup.dismiss();
                    loadingDialog.dismiss();
                    NewIntent();
                }
            }
        };
        SBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                sAlert.dismiss();
            }
        });
        setupPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("Mprinter", "").equals("Y")) {
                    VerifyCase = "Mprinter";
                    vAlert.show();
                    setWHAlertDialog(vAlert);
                } else {
                    printSetup.show();
                    setWHAlertDialog(printSetup);
                }
            }
        });
        setupMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("Mdisplay", "").equals("Y")) {
                    VerifyCase = "Mdisplay";
                    vAlert.show();
                    setWHAlertDialog(vAlert);
                } else {
                    monitorSetup.show();
                    setWHAlertDialog(monitorSetup);
                }
            }
        });
        setupSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                soundSetup.show();
                setWHAlertDialog(soundSetup);
            }
        });
    }

    private void setPrinterSetup() {
        sPrintView = View.inflate(this, R.layout.printer_setup, null);
        AlertDialog.Builder setShop = new AlertDialog.Builder(TerminalForm.this);
        setShop.setView(sPrintView);
        setShop.setCancelable(false);
        printSetup = setShop.create();

        final Button pTestPrint = (Button) sPrintView.findViewById(R.id.TestPrint);
        RadioButton pEnable = (RadioButton) sPrintView.findViewById(R.id.Enable);
        RadioButton pDisable = (RadioButton) sPrintView.findViewById(R.id.Disable);
        Button pSubmit = (Button) sPrintView.findViewById(R.id.Submit);
        Button pCancel = (Button) sPrintView.findViewById(R.id.Cancel);
        final RadioGroup groups = (RadioGroup) sPrintView.findViewById(R.id.PrintGroup);
        if (Prefs.getString("PrinterUse", "N").equals("Y")) {
            groups.check(sPrintView.findViewById(R.id.Enable).getId());
            pTestPrint.setVisibility(View.VISIBLE);
        } else {
            groups.check(sPrintView.findViewById(R.id.Disable).getId());
            pTestPrint.setVisibility(View.GONE);
        }
        pSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                int rid = groups.getCheckedRadioButtonId();
                String PrintMode = "";
                switch (rid) {
                    case R.id.Enable:
                        PrintMode = "Y";
                        Prefs.putString("PrinterUse", "Y");
                        break;
                    case R.id.Disable:
                        PrintMode = "N";
                        Prefs.putString("PrinterUse", "N");
                        break;
                }
                Model.PrinterOnOff(loadingDialog, TerminalForm.this, putPrint, PrintMode);
            }
        });
        pCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                printSetup.dismiss();
            }
        });
        putPrint = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                loadingDialog.dismiss();
                printSetup.dismiss();
                NewIntent();
            }
        };
        pEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                pTestPrint.setVisibility(View.VISIBLE);
            }
        });
        pDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                pTestPrint.setVisibility(View.GONE);
            }
        });
        pTestPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.ToPrintSLIP(loadingDialog, TerminalForm.this, printCurrentSlip);
            }
        });

    }

    private void setMonitorSetup() {
        View view = View.inflate(this, R.layout.monitor_setup, null);
        AlertDialog.Builder setShop = new AlertDialog.Builder(TerminalForm.this);
        setShop.setView(view);
        setShop.setCancelable(false);
        monitorSetup = setShop.create();

        final Button mTestMonitor = (Button) view.findViewById(R.id.TestMonitor);
        RadioButton mEnable = (RadioButton) view.findViewById(R.id.Enable);
        RadioButton mDisable = (RadioButton) view.findViewById(R.id.Disable);
        Button pSubmit = (Button) view.findViewById(R.id.Submit);
        Button pCancel = (Button) view.findViewById(R.id.Cancel);
        final RadioGroup groups = (RadioGroup) view.findViewById(R.id.MonitorGroup);
        if (Prefs.getString("DispUse", "N").equals("Y")) {
            groups.check(view.findViewById(R.id.Enable).getId());
            mTestMonitor.setVisibility(View.VISIBLE);
        } else {
            groups.check(view.findViewById(R.id.Disable).getId());
            mTestMonitor.setVisibility(View.GONE);
        }
        pSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                int rid = groups.getCheckedRadioButtonId();
                String MonitorMode = "";
                switch (rid) {
                    case R.id.Enable:
                        MonitorMode = "Y";
                        Prefs.putString("DispUse", "Y");
                        break;
                    case R.id.Disable:
                        MonitorMode = "N";
                        Prefs.putString("DispUse", "N");
                        break;
                }
                Model.DispOnOff(loadingDialog, TerminalForm.this, putMonitor, MonitorMode);
            }
        });
        pCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                monitorSetup.dismiss();
            }
        });
        putMonitor = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                loadingDialog.dismiss();
                monitorSetup.dismiss();
            }
        };
        mEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                mTestMonitor.setVisibility(View.VISIBLE);
            }
        });
        mDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                mTestMonitor.setVisibility(View.GONE);
            }
        });
        mTestMonitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                UpdateMonitor("TestDisplay", true);
            }
        });

    }

    private void setTerminalSetup() {
        sShopView = View.inflate(this, R.layout.shop_setup, null);
        AlertDialog.Builder setShop = new AlertDialog.Builder(TerminalForm.this);
        setShop.setView(sShopView);
        setShop.setCancelable(false);
        shopSetup = setShop.create();
        Button BtnSubmit = (Button) sShopView.findViewById(R.id.Submit);
        Button BtnCancel = (Button) sShopView.findViewById(R.id.Cancel);

        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                shopSetup.dismiss();
                setPriceCheck();
            }
        });
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                String priceType;
                RadioButton H = (RadioButton) sShopView.findViewById(R.id.HotPrice);
                RadioButton M = (RadioButton) sShopView.findViewById(R.id.Menu);
                if (H.isChecked()) {
                    priceType = "H";
                } else if (M.isChecked()) {
                    priceType = "M";
                } else {
                    priceType = "E";
                }
                Model.PriceTypeJS(loadingDialog, TerminalForm.this, changePriceType, priceType);
                Prefs.putString("PriceType", priceType);
            }
        });
        setPriceCheck();
    }

    private void setPriceCheck() {
        RadioButton H = (RadioButton) sShopView.findViewById(R.id.HotPrice);
        RadioButton M = (RadioButton) sShopView.findViewById(R.id.Menu);
        RadioButton E = (RadioButton) sShopView.findViewById(R.id.Manual);
        if (Prefs.getString("PriceType", "").equals("H")) {
            H.setChecked(true);
        } else if (Prefs.getString("PriceType", "").equals("M")) {
            M.setChecked(true);
        } else if (Prefs.getString("PriceType", "").equals("E")) {
            E.setChecked(true);
        }
    }

    private void setSoundSetup() {
        final View view = View.inflate(this, R.layout.sound_setup, null);
        AlertDialog.Builder setSound = new AlertDialog.Builder(TerminalForm.this);
        setSound.setView(view);
        setSound.setCancelable(false);
        soundSetup = setSound.create();
        Button BtnSubmit = (Button) view.findViewById(R.id.Submit);
        Button BtnCancel = (Button) view.findViewById(R.id.Cancel);
        String[] Sounds = {"Default", "MouseClick", "Crystal", "Bell", "Beep", "Swish"};
        RadioButton[] Rdos = new RadioButton[5];
        Integer round = 0;
        for (final String sound : Sounds) {
            int resId = getResources().getIdentifier(sound, "id", getPackageName());
            Rdos[round] = (RadioButton) view.findViewById(resId);
            Rdos[round].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.addSoundWithKey(activity, sound);
                }
            });
        }
        BtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioGroup rgroup = (RadioGroup) view.findViewById(R.id.Groups);
                int rid = rgroup.getCheckedRadioButtonId();
                switch (rid) {
                    case R.id.Default:
                        Prefs.putString("ThemeSound", "Default");
                        Utils.addSoundWithKey(activity, "Default");
                        break;
                    case R.id.Crystal:
                        Prefs.putString("ThemeSound", "Crystal");
                        Utils.addSoundWithKey(activity, "Crystal");
                        break;
                    case R.id.MouseClick:
                        Prefs.putString("ThemeSound", "MouseClick");
                        Utils.addSoundWithKey(activity, "MouseClick");
                        break;
                    case R.id.Bell:
                        Prefs.putString("ThemeSound", "Bell");
                        Utils.addSoundWithKey(activity, "Bell");
                        break;
                    case R.id.Beep:
                        Prefs.putString("ThemeSound", "Beep");
                        Utils.addSoundWithKey(activity, "Beep");
                        break;
                    case R.id.Swish:
                        Prefs.putString("ThemeSound", "Swish");
                        Utils.addSoundWithKey(activity, "Swish");
                        break;
                }
                soundSetup.dismiss();
            }
        });
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                soundSetup.dismiss();
                setSound(view);
            }
        });
        setSound(view);
    }

    private void setSound(View view) {
        String theme = Prefs.getString("ThemeSound", "Default");
        RadioGroup groups = (RadioGroup) view.findViewById(R.id.Groups);
        if (theme.equals("Crystal")) {
            groups.check(view.findViewById(R.id.Crystal).getId());
        } else if (theme.equals("MouseClick")) {
            groups.check(view.findViewById(R.id.MouseClick).getId());
        } else if (theme.equals("Bell")) {
            groups.check(view.findViewById(R.id.Bell).getId());
        } else if (theme.equals("Beep")) {
            groups.check(view.findViewById(R.id.Beep).getId());
        } else if (theme.equals("Swish")) {
            groups.check(view.findViewById(R.id.Swish).getId());
        } else {
            groups.check(view.findViewById(R.id.Default).getId());
        }
    }

    private void setThemeSetup() {
        themeView = View.inflate(this, R.layout.theme_setup, null);
        AlertDialog.Builder setTheme = new AlertDialog.Builder(TerminalForm.this);
        setTheme.setView(themeView);
        setTheme.setCancelable(false);
        themeSetup = setTheme.create();
        Button sSubmit = (Button) themeView.findViewById(R.id.Submit);
        Button sCancel = (Button) themeView.findViewById(R.id.Cancel);
        sCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                themeSetup.dismiss();
            }
        });
        sSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                RadioGroup rgroup = (RadioGroup) themeView.findViewById(R.id.Groups);
                int rid = rgroup.getCheckedRadioButtonId();
                switch (rid) {
                    case R.id.Default:
                        Prefs.putString("ThemeColor", "Default");
                        break;
                    case R.id.Blue:
                        Prefs.putString("ThemeColor", "Blue");
                        break;
                    case R.id.Red:
                        Prefs.putString("ThemeColor", "Red");
                        break;
                    case R.id.Black:
                        Prefs.putString("ThemeColor", "Black");
                        break;
                    case R.id.Pink:
                        Prefs.putString("ThemeColor", "Pink");
                        break;
                    case R.id.Green:
                        Prefs.putString("ThemeColor", "Green");
                        break;
                }
                themeSetup.dismiss();
                NewIntent();
            }
        });
    }

    private void setShowDataSetup() {
        showdataView = View.inflate(this, R.layout.showdata_setup, null);
        AlertDialog.Builder setdata = new AlertDialog.Builder(TerminalForm.this);
        setdata.setView(showdataView);
        setdata.setCancelable(false);
        showdataSetup = setdata.create();
        Button sSubmit = (Button) showdataView.findViewById(R.id.Submit);
        Button sCancel = (Button) showdataView.findViewById(R.id.Cancel);
        sCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                showdataSetup.dismiss();
            }
        });
        sSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                RadioGroup groups = (RadioGroup) showdataView.findViewById(R.id.SizeGroup);
                CheckBox showCode = (CheckBox) showdataView.findViewById(R.id.ShowCode);
                CheckBox showDesc = (CheckBox) showdataView.findViewById(ShowDesc);
                CheckBox showImage = (CheckBox) showdataView.findViewById(R.id.ShowImage);
                int rid = groups.getCheckedRadioButtonId();
                switch (rid) {
                    case R.id.S:
                        Prefs.putString("ThemeSize", "S");
                        break;
                    case R.id.M:
                        Prefs.putString("ThemeSize", "M");
                        break;
                    case R.id.L:
                        Prefs.putString("ThemeSize", "L");
                        break;
                }
                if (showCode.isChecked()) {
                    Prefs.putBoolean("ShowCode", true);
                } else {
                    Prefs.putBoolean("ShowCode", false);
                }
                if (showDesc.isChecked()) {
                    Prefs.putBoolean("ShowDesc", true);
                } else {
                    Prefs.putBoolean("ShowDesc", false);
                }
                if (showImage.isChecked()) {
                    Prefs.putBoolean("ShowImage", true);
                } else {
                    Prefs.putBoolean("ShowImage", false);
                }
                try {
                    UpdateItems();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showdataSetup.dismiss();
            }
        });
    }

    private void setShowData() {
        Prefs.putString("ThemeSize", Prefs.getString("ThemeSize", "M"));
        Prefs.putBoolean("ShowCode", Prefs.getBoolean("ShowCode", true));
        Prefs.putBoolean("ShowDesc", Prefs.getBoolean("ShowDesc", true));
        Prefs.putBoolean("ShowImage", Prefs.getBoolean("ShowImage", true));
        String size = Prefs.getString("ThemeSize", "M");
        RadioGroup groups = (RadioGroup) showdataView.findViewById(R.id.SizeGroup);
        CheckBox showCode = (CheckBox) showdataView.findViewById(R.id.ShowCode);
        CheckBox showDesc = (CheckBox) showdataView.findViewById(ShowDesc);
        CheckBox showImage = (CheckBox) showdataView.findViewById(R.id.ShowImage);
        switch (size) {
            case "S":
                groups.check(showdataView.findViewById(R.id.S).getId());
                break;
            case "L":
                groups.check(showdataView.findViewById(R.id.L).getId());
                break;
            default:
                groups.check(showdataView.findViewById(R.id.M).getId());
                break;
        }
        showCode.setChecked(Prefs.getBoolean("ShowCode", true));
        showDesc.setChecked(Prefs.getBoolean("ShowDesc", true));
        showImage.setChecked(Prefs.getBoolean("ShowImage", true));
    }

    private void setPrintSumPopup() {
        printSumView = View.inflate(this, R.layout.printsum_popup, null);
        AlertDialog.Builder mSetup = new AlertDialog.Builder(TerminalForm.this);
        mSetup.setView(printSumView);
        mSetup.setCancelable(false);
        pSumAlert = mSetup.create();
        Utils.hideKeyboardAlert(pSumAlert);
        btnPrintSum = (Button) findViewById(R.id.PrintSummary);
        final EditText ShopNo = (EditText) printSumView.findViewById(R.id.ShopNo);
        final EditText ShopName = (EditText) printSumView.findViewById(R.id.ShopName);
        final EditText TotalSale = (EditText) printSumView.findViewById(R.id.TotalSale);
        final EditText TotalVoid = (EditText) printSumView.findViewById(R.id.TotalVoid);
        final EditText NetSale = (EditText) printSumView.findViewById(R.id.NetSale);
        final EditText NumCard = (EditText) printSumView.findViewById(R.id.NumCardsAtShop);
        final EditText BillSale = (EditText) printSumView.findViewById(R.id.BillSale);
        final EditText BillVoid = (EditText) printSumView.findViewById(R.id.BillVoid);
        final EditText NetBill = (EditText) printSumView.findViewById(R.id.NetBill);
        printSum = (Button) printSumView.findViewById(R.id.Print);
        btnCancel = (Button) printSumView.findViewById(R.id.Cancel);
        btnPrintSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("SnetSale", "").equals("Y")) {
                    VerifyCase = "PrintSumPopup";
                    vAlert.show();
                    setWHAlertDialog(vAlert);
                } else {
                    pSumAlert.show();
                    setWHAlertDialog(pSumAlert);
                    SimpleDateFormat DateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm:ss");
                    Date CurrentTime = Calendar.getInstance().getTime();
                    String Date = String.valueOf(DateFormat.format(CurrentTime));
                    String Time = String.valueOf(TimeFormat.format(CurrentTime));
                    TextView date = (TextView) printSumView.findViewById(R.id.CurrentDate);
                    date.setText(getText(R.string.Date) + " " + Date + " " + getText(R.string.Time) + " " + Time);
                    Model.SelectShopNetSaleJS(loadingDialog, TerminalForm.this, selectShopNetSale);
                    Model.GetServerNowJS(loadingDialog, TerminalForm.this);
                }
            }
        });
        printSummary = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                loadingDialog.dismiss();
                pSumAlert.dismiss();
            }
        };
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                pSumAlert.dismiss();
            }
        });
        printSum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                Model.ToPrintSUM(loadingDialog, TerminalForm.this, printSummary);
            }
        });
        selectShopNetSale = new SoapConnect.AsyncResponse() {
            @Override
            public void processFinish(JSONObject result) {
                if (result.has("Table1")) {
                    JSONArray jsonArray = null;
                    JSONObject obj = null;
                    try {
                        jsonArray = new JSONArray(result.getString("Table1"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            obj = jsonArray.getJSONObject(i);
                        }

                        ShopNo.setText(Prefs.getString("ShopCode", ""));
                        if (Prefs.getString("Language", "th").equals("th")) {
                            ShopName.setText(Prefs.getString("ShopNameT", ""));
                        } else {
                            ShopName.setText(Prefs.getString("ShopNameE", ""));
                        }
                        TotalSale.setText(setamt(obj.getString("TotalSale")));
                        TotalVoid.setText(setamt(obj.getString("TotalVoid")));
                        NetSale.setText(setamt(obj.getString("NetSale")));
                        NumCard.setText(obj.getString("NumCardsAtShop"));
                        BillSale.setText(obj.getString("BillSale"));
                        BillVoid.setText(obj.getString("BillVoid"));
                        NetBill.setText(obj.getString("NetBill"));
                        pSumAlert.show();
                        setWHAlertDialog(pSumAlert);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                loadingDialog.dismiss();
            }
        };
    }

    private void setPrintSlip() {
        final View view = View.inflate(this, R.layout.printslip_popup, null);
        AlertDialog.Builder pslip = new AlertDialog.Builder(TerminalForm.this);
        pslip.setView(view);
        pslip.setCancelable(false);
        pSlipAlert = pslip.create();
        Utils.hideKeyboardAlert(pSlipAlert);
        final View cardView = View.inflate(this, R.layout.print_cardtrans, null);
        AlertDialog.Builder pCardTrans = new AlertDialog.Builder(TerminalForm.this);
        pCardTrans.setView(cardView);
        pCardTrans.setCancelable(false);
        final AlertDialog pCardAlert = pCardTrans.create();
        Utils.hideKeyboardAlert(pCardAlert);
        btnPrintSlip = (Button) findViewById(R.id.PrintSlip);
        btnPrintSlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                pSlipAlert.show();
                setWHAlertDialog(pSlipAlert);
                Button btnCancel = (Button) view.findViewById(R.id.Cancel);
                Button btnCardTrans = (Button) view.findViewById(R.id.CardTrans);
                Button btnCurrentSlip = (Button) view.findViewById(R.id.CurrentSlip);
                printCurrentSlip = new SoapConnect.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject result) {
                        Model.ToPrintABB(loadingDialog, TerminalForm.this, printAbb);

                    }
                };
                printAbb = new SoapConnect.AsyncResponse() {
                    @Override
                    public void processFinish(JSONObject result) {
                        pSlipAlert.dismiss();
                    }
                };
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        pSlipAlert.dismiss();
                    }
                });
                btnCardTrans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        pCardAlert.show();
                        setWHAlertDialog(pCardAlert);
                        Button btnCardCancel = (Button) cardView.findViewById(R.id.Cancel);
                        final Button btnCardPrint = (Button) cardView.findViewById(R.id.Print);
                        final EditText CardNo = (EditText) cardView.findViewById(R.id.CardNo);
                        checkCardTrans = new SoapConnect.AsyncResponse() {

                            @Override
                            public void processFinish(JSONObject result) {
                                if (result.has("Table")) {
                                    Model.ToPrintCardTrans(loadingDialog, TerminalForm.this, printCardTrans, CardNo.getText().toString());
                                } else {
                                    CardNo.selectAll();
                                    CardNo.requestFocus();
                                }
                            }
                        };
                        printCardTrans = new SoapConnect.AsyncResponse() {
                            @Override
                            public void processFinish(JSONObject result) {
                                loadingDialog.dismiss();
                                pCardAlert.dismiss();
                                pSlipAlert.dismiss();
                            }
                        };
                        btnCardCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.addSound(activity);
                                pCardAlert.dismiss();
                            }
                        });
                        btnCardPrint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.addSound(activity);
                                Model.GetCardInfoJS(loadingDialog, TerminalForm.this, checkCardTrans, CardNo.getText().toString());
                            }
                        });
                        CardNo.setOnKeyListener(new View.OnKeyListener() {

                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                                    btnCardPrint.callOnClick();
                                }
                                return false;
                            }
                        });
                    }
                });
                btnCurrentSlip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        Model.ToPrintSLIP(loadingDialog, TerminalForm.this, printCurrentSlip);
                    }
                });
            }
        });
    }

    private void NewIntent() {
        intent = new Intent(TerminalForm.this, TerminalForm.class);
        startActivity(intent);
        finish();
    }

    private void setLogoutPopup() {
        Button btnLogOut = (Button) findViewById(R.id.Logout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                ATextLine.setText(R.string.PLogOut);
                dAlert.show();
                ABtnSubmit.requestFocus();
                setWHAlertDialog(dAlert);
                ABtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        dAlert.dismiss();
                        finish();
                    }
                });
            }
        });
    }

    private void setDeletePopup() {
        Button btnDelete = (Button) findViewById(R.id.Delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                if (Prefs.getString("PriceType", null).equals("E")) {
                    String value = Input.getText().toString();
                    if (value != null && value.length() > 0) {
                        String data = value.substring(0, value.length() - 1);
                        Input.setText(data);
                        UpdateTotal();
                    }
                } else {
                    ATextLine.setText(R.string.PDelete);
                    dAlert.show();
                    ABtnSubmit.requestFocus();
                    setWHAlertDialog(dAlert);
                    ABtnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Utils.addSound(activity);
                            StringBuilder stringBuilder = new StringBuilder();
                            Double pp = 0.0;
                            for (int i = OrderListTable.getChildCount(); i > 0; i--) {
                                View view = OrderListTable.getChildAt(i);
                                if (view instanceof TableRow) {
                                    CheckBox cb = (CheckBox) view.findViewById(R.id.CheckBox);
                                    TextView tv = (TextView) view.findViewById(R.id.Description);
                                    TextView p = (TextView) view.findViewById(R.id.Price);
                                    if (cb.isChecked()) {
                                        stringBuilder.append(String.format(", %s", tv.getText().toString()));
                                        pp += Double.valueOf(p.getText().toString());
                                        OrderListTable.removeViewAt(i);
                                    }
                                }
                            }
                            detail = stringBuilder.toString().substring(2);
                            price = pp.toString();
                            UpdateTotal();
                            dAlert.dismiss();
                            UpdateCheckList();
                            UpdateMonitor("DeleteItem", false);
                        }
                    });
                }
            }
        });
    }

    private void setClearPopup() {
        Button btnClear = (Button) findViewById(R.id.Clear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.addSound(activity);
                ATextLine.setText(R.string.PClear);
                dAlert.show();
                ABtnSubmit.requestFocus();
                setWHAlertDialog(dAlert);
                ABtnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addSound(activity);
                        int childCount = OrderListTable.getChildCount();
                        if (childCount > 1) {
                            OrderListTable.removeViews(1, childCount - 1);
                        }
                        TextView Text = (TextView) findViewById(R.id.Input);
                        Text.setText("");
                        UpdateTotal();
                        dAlert.dismiss();
                        UpdateCheckList();
                        UpdateMonitor("", false);
                    }
                });
            }
        });
    }

    private void setTheme() {
        String theme = Prefs.getString("ThemeColor", "Default");
        RadioGroup groups = (RadioGroup) themeView.findViewById(R.id.Groups);
        int color;
        if (theme.equals("Blue")) {
            groups.check(themeView.findViewById(R.id.Blue).getId());
            color = R.color.colorBlue;
        } else if (theme.equals("Red")) {
            groups.check(themeView.findViewById(R.id.Red).getId());
            color = R.color.colorRed;
        } else if (theme.equals("Black")) {
            groups.check(themeView.findViewById(R.id.Black).getId());
            color = R.color.colorBlack2;
        } else if (theme.equals("Green")) {
            groups.check(themeView.findViewById(R.id.Green).getId());
            color = R.color.colorGreen;
        } else if (theme.equals("Pink")) {
            groups.check(themeView.findViewById(R.id.Pink).getId());
            color = R.color.colorPink;
        } else {
            groups.check(themeView.findViewById(R.id.Default).getId());
            color = R.color.colorBlack;
        }
        Resources r = getResources();
        String name = getPackageName();
        for (int i = 1; i <= 8; i++) {
            int layout_id = r.getIdentifier("LL" + i, "id", name);
            LinearLayout ll = (LinearLayout) findViewById(layout_id);
            GradientDrawable bg = (GradientDrawable) ll.getBackground();
            bg.setStroke(2, ContextCompat.getColor(this, color));
        }
    }

    private void UpdateMonitor(String MType, Boolean ShowDed) {
        handler.removeCallbacks(rCleasMonitorDelay);
        EditText cardNo = (EditText) pView.findViewById(R.id.CardNo);
        EditText dueAmt = (EditText) pView.findViewById(R.id.DueAmount);
        EditText remainAmt = (EditText) pView.findViewById(R.id.RemainAmt);
        EditText CcardNo = (EditText) cbView.findViewById(R.id.CardNo);

        String ShopName = " ";
        if (Prefs.getString("Language", "th").equals("th")) {
            ShopName = Prefs.getString("ShopNameT", " ");
        } else {
            ShopName = Prefs.getString("ShopNameE", " ");
        }
        String Detail = " ";
        String CardType = " ";
        String CompanyName = Prefs.getString("CompanyName", " ");
        String ScrWelcome = Prefs.getString("ScrWelcome", " ");
        if (ScrWelcome.equals("")) {
            ScrWelcome = " ";
        }
        String CardNo = " ";
        String L1a = " ";
        String L1b = " ";
        String L2a = " ";
        String L2b = " ";
        try {
            if (MType.equals("Payment")) {
                CardNo = cardNo.getText().toString();
                L1a = (String) getText(R.string.MAmount);
                L1b = Utils.setamtF(dueAmt.getText().toString());
                L2a = (String) getText(R.string.MRemain);
                L2b = Utils.setamtF(remainAmt.getText().toString());
            } else if (MType.equals("CancelList")) {
                CardNo = code;
                L1a = (String) getText(R.string.MCancelTotal);
                L1b = Utils.setamtF(price);
            } else if (MType.equals("CancelledList")) {
                CardNo = code;
                CardType = type;
                Detail = detail;
                L1a = (String) getText(R.string.MRemain);
                L1b = Utils.setamtF(price);
            } else if (MType.equals("DeleteItem")) {
                CardNo = (String) getText(R.string.MDeleteList);
                CardType = detail;
                L1a = (String) getText(R.string.MDeleteTotal);
                L1b = Utils.setamtF(price);
                L2a = (String) getText(R.string.MTotal);
                L2b = Utils.setamtF(mTotal.getText().toString());
            } else if (MType.equals("SelectItem")) {
                //CardNo = code;
                CardType = detail;
                L1a = (String) getText(R.string.MAmount);
                L1b = Utils.setamtF(price);
                L2a = (String) getText(R.string.MTotal);
                L2b = Utils.setamtF(mTotal.getText().toString());
            } else if (MType.equals("TestDisplay")) {
                ScrWelcome = getResources().getString(R.string.DisplayTestScr);
                CardNo = getResources().getString(R.string.DisplayTestCardNo);
                CardType = getResources().getString(R.string.DisplayTestCardType);
                Detail = getResources().getString(R.string.DisplayTestDetail);
                L1a = getResources().getString(R.string.DisplayTestL1a);
                L1b = getResources().getString(R.string.DisplayTestL1b);
                L2a = getResources().getString(R.string.DisplayTestL2a);
                L2b = getResources().getString(R.string.DisplayTestL2b);

            } else if (MType.equals("Transfer")) {
                CardNo = code;
                CardType = detail;
                L1a = (String) getText(R.string.MRemain);
                L1b = Utils.setamtF(price);
                L2a = (String) getText(R.string.MTotal);
                L2b = Utils.setamtF(total);
            } else if (MType.equals("CheckBal")) {
                L1a = (String) getText(R.string.CardExp);
                if (Card.has("ExpDate") && !Card.getString("ExpDate").equals("")) {
                    L1b = Card.getString("ExpDate");
//                    String[] DateTime = Card.getString("ExpDate").split(" ");
//                    String[] Date = DateTime[0].split("/");
//                    if (Date.length >= 3) {
//                        L1b = Date[1] + "/" + Date[0] + "/" + String.valueOf(Integer.valueOf(Date[2]));
//                    }
                }
                L2a = (String) getText(R.string.MRemain);
                float bal = 0;
                if (Prefs.getString("CardPolicy", "F").equals("F")) {
                    bal = Float.valueOf(setamt(Card.getString("BalanceValue")));
                    if (Prefs.getBoolean("DedDisPlay", false) && ShowDed) {

                        bal -= Float.valueOf(setamt(Card.getString("ReserveValue")));
                    }
                } else {
                    if (Prefs.getBoolean("SFLoftMode", false)) {
                        bal = Float.valueOf(setamt(Card.getString("FoodLoftCreditLimit"))) - Float.valueOf(setamt(Card.getString("UsedValue")));
                    } else {
                        bal = Float.valueOf(setamt(Card.getString("UsedValue")));
                    }
                }
                L2b = Utils.setamtF(String.valueOf(bal));
                CardNo = CcardNo.getText().toString();
            }
            if (Card != null) {
                if (Card.has("MemName") && !Card.getString("MemName").equals("")) {
                    Detail = Card.getString("MemName") + " ";
                }
                if (Card.has("CardTypeDesc")) {
                    CardType = Card.getString("CardTypeDesc");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Prefs.getString("DispUse", "N").equals("Y")) {
            TCPClient.send(
                    CompanyName + "|"
                            + ShopName + "|"
                            + ScrWelcome + "|"
                            + CardNo + "|"
                            + CardType + "|"
                            + Detail + "|"
                            + L1a + "|"
                            + L1b + "|"
                            + L2a + "|"
                            + L2b
                    , Prefs.getString("DispIP", null), Integer.valueOf(Prefs.getString("SmartDisplayTCPport", "0")));
        }

    }

    private void ClearMonitorDelay() {
        final String ShopName;
        if (Prefs.getString("Language", "th").equals("th")) {
            ShopName = Prefs.getString("ShopNameT", "");
        } else {
            ShopName = Prefs.getString("ShopNameE", "");
        }
        final String CompanyName = Prefs.getString("CompanyName", "");
        final String ScrWelcome = Prefs.getString("ScrWelcome", "");
        rCleasMonitorDelay = new Runnable() {
            @Override
            public void run() {
                TCPClient.send(CompanyName + "|"
                                + ShopName + "|"
                                + ScrWelcome + "|"
                                + "|"
                                + "|"
                                + "|"
                                + "|"
                                + "|"
                                + "|"
                                + ""
                        , Prefs.getString("DispIP", null), Integer.valueOf(Prefs.getString("SmartDisplayTCPport", "0")));
                handler.removeCallbacks(rCleasMonitorDelay);
            }
        };
        handler.postDelayed(rCleasMonitorDelay, getResources().getInteger(R.integer.Delay10s));
    }

    private void UpdateItems() throws JSONException {
        int count = items.length();
        int round = 0;
        int xsize = 1;
        dataTable.removeAllViews();
        Resources res = getResources();
        String Tsize = Prefs.getString("ThemeSize", "M");
        if (Prefs.getString("PriceType", "").equals("H") || Prefs.getString("PriceType", "").equals("M")) {
            while (round < count) {
                TableRow row = new TableRow(this);
                boolean showImage = false;
                for (int i = 0; i < 4; i++) {
                    if (round >= count) {
                        round += 1;
                        break;
                    } else {
                        JSONObject obj = items.getJSONObject(round);
                        View tx = View.inflate(this, R.layout.data_view, null);
                        TextView RText = (TextView) tx.findViewById(R.id.Text);
                        TextView ItemID = (TextView) tx.findViewById(R.id.ItemID);
                        LinearLayout Layout = (LinearLayout) tx.findViewById(R.id.Layout);
                        if (Prefs.getString("PriceType", "").equals("H")) {
                            RText.setText(setamt(obj.getString("ItemPrice")));
                            xsize = 2;
                        } else if (Prefs.getString("PriceType", "").equals("M")) {
                            String desc = "";
                            if (Prefs.getBoolean("ShowCode", false)) {
                                desc += "[" + obj.getString("ItemCode") + "]\n";
                            }
                            if (Prefs.getBoolean("ShowDesc", false)) {
                                desc += obj.getString("ItemDesc");
                            }
                            if (Prefs.getBoolean("ShowImage", false)) {
                                showImage = true;
                            }
                            RText.setText(desc);
                            if (!Prefs.getBoolean("ShowCode", false) && !Prefs.getBoolean("ShowDesc", false)) {
                                RText.setVisibility(View.GONE);
                            }
                        }
                        switch (Tsize) {
                            case "M":
                                Layout.setMinimumHeight(res.getInteger(R.integer.SizeM));
                                RText.setTextSize(res.getInteger(R.integer.TextM) * xsize);
                                break;
                            case "L":
                                Layout.setMinimumHeight(res.getInteger(R.integer.SizeL));
                                RText.setTextSize(res.getInteger(R.integer.TextL) * xsize);
                                break;
                            case "S":
                                Layout.setMinimumHeight(res.getInteger(R.integer.SizeS));
                                RText.setTextSize(res.getInteger(R.integer.TextS) * xsize);
                                break;
                        }
                        ItemID.setText(String.valueOf(round));
                        ImageView Image = (ImageView) tx.findViewById(R.id.Image);
                        if (obj.has("ItemImage") && !obj.getString("ItemImage").equals("") && showImage) {
                            byte[] blob = Base64.decode(obj.getString("ItemImage"), Base64.DEFAULT);
                            Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                            Image.setImageBitmap(bmp);
                        } else {
                            Image.setVisibility(View.GONE);
                        }
                        row.addView(tx);
                        tx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.addSound(activity);
                                TextView Text = (TextView) v.findViewById(R.id.ItemID);
                                try {
                                    UpdateList(Integer.valueOf(Text.getText().toString()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        round += 1;
                    }
                }
                dataTable.addView(row);
            }
        } else {
            final String[] items = {"7", "8", "9", "00", "4", "5", "6", "0", "1", "2", "3", "."};
            count = items.length;
            round = 0;
            xsize = 2;
            while (round < count) {
                TableRow row = new TableRow(this);
                for (int i = 0; i < 4; i++) {
                    if (round >= count) {
                        round += 1;
                        break;
                    } else {
                        View tx = View.inflate(this, R.layout.data_view, null);
                        TextView RText = (TextView) tx.findViewById(R.id.Text);
                        ImageView Image = (ImageView) tx.findViewById(R.id.Image);
                        Image.setVisibility(View.GONE);
                        Image.requestLayout();
                        RText.setText(items[round]);
                        LinearLayout Layout = (LinearLayout) tx.findViewById(R.id.Layout);
                        switch (Tsize) {
                            case "M":
                                Layout.setMinimumHeight(res.getInteger(R.integer.SizeM));
                                RText.setTextSize(res.getInteger(R.integer.TextM) * xsize);
                                break;
                            case "L":
                                Layout.setMinimumHeight(res.getInteger(R.integer.SizeL));
                                RText.setTextSize(res.getInteger(R.integer.TextL) * xsize);
                                break;
                            case "S":
                                Layout.setMinimumHeight(res.getInteger(R.integer.SizeS));
                                RText.setTextSize(res.getInteger(R.integer.TextS) * xsize);
                                break;
                        }
                        row.addView(tx);
                        final int finalRound = round;
                        tx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Utils.addSound(activity);
                                TextView Text = (TextView) findViewById(R.id.Input);
                                String value = Text.getText().toString();
                                value += items[finalRound];
                                Text.setText(value);
                                UpdateTotal();
                            }
                        });
                        round += 1;
                    }
                }
                dataTable.addView(row);
            }
        }
    }

    private void UpdateList(Integer i) throws JSONException {
        JSONObject obj = items.getJSONObject(i);
        TableRow row = new TableRow(this);
        View view = View.inflate(this, R.layout.order_list_view, null);
        TextView Desc = (TextView) view.findViewById(R.id.Description);
        TextView Price = (TextView) view.findViewById(R.id.Price);
        TextView Code = (TextView) view.findViewById(R.id.Code);
        TextView PromPrice = (TextView) view.findViewById(R.id.PromPrice);
        detail = obj.getString("ItemDesc");
        code = obj.getString("ItemCode");
        price = setamt(obj.getString("ItemPrice"));
        Desc.setText(detail);
        Code.setText(code);
        Price.setText(price);
        PromPrice.setText(setamt(obj.getString("ItemPriceProm")));
        row.addView(view);
        OrderListTable.addView(row);
        scroll.fullScroll(View.FOCUS_DOWN);
        UpdateTotal();
        UpdateCheckList();
        UpdateMonitor("SelectItem", false);
    }

    private void UpdateCheckList() {
        for (int i = OrderListTable.getChildCount(); i >= 0; i--) {
            View view = OrderListTable.getChildAt(i);
            if (view instanceof TableRow) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.CheckBox);
                if (i == (OrderListTable.getChildCount() - 1) && i > 0) {
                    cb.setChecked(true);
                } else {
                    cb.setChecked(false);
                }
            }
        }
    }

    private void UpdateBalanceTransfer(boolean isForm) {
        TextView TamtBal = (TextView) tView.findViewById(R.id.AmtRemain);
        TextView TcardNoFrom = (TextView) tView.findViewById(CardNoFrom);
        TextView TamtFrom = (TextView) tView.findViewById(AmtFrom);
        TextView TcardNoTo = (TextView) tView.findViewById(R.id.CardNoTo);
        TextView TamtTo = (TextView) tView.findViewById(R.id.AmtTo);
        float amtFrom = 0;
        float amtTo = 0;
        float amtBal;
        if (!TamtFrom.getText().toString().equals("")) {
            amtFrom = Float.valueOf(TamtFrom.getText().toString());
        }
        if (!TamtTo.getText().toString().equals("")) {
            amtTo = Float.valueOf(TamtTo.getText().toString());
        }
        amtBal = amtFrom + amtTo;
        TamtBal.setText(setamt(String.valueOf(amtBal)));
        if (isForm) {
            code = TcardNoFrom.getText().toString();
            detail = getResources().getString(R.string.form);
            price = TamtFrom.getText().toString();
        } else {
            code = TcardNoTo.getText().toString();
            detail = getResources().getString(R.string.to);
            price = TamtTo.getText().toString();
        }
        total = setamt(String.valueOf(amtBal));
        UpdateMonitor("Transfer", false);
    }

    private void UpdateTotal() {
        float total = 0;
        if (Prefs.getString("PriceType", "").equals("E")) {
            TextView Input = (TextView) findViewById(R.id.Input);
            mTotal.setText(setamt(Input.getText().toString()));
        } else {
            for (int i = 1; i < OrderListTable.getChildCount(); i++) {
                View view = OrderListTable.getChildAt(i);
                if (view instanceof TableRow) {
                    TextView price = (TextView) view.findViewById(R.id.Price);
                    float p = Float.valueOf(price.getText().toString());
                    total += p;
                }
            }
            mTotal.setText(setamt(String.valueOf(total)));
        }
    }

    private void UpdatePayment() {
        EditText PCardType = (EditText) pView.findViewById(R.id.CardType);
        EditText PMember = (EditText) pView.findViewById(R.id.Member);
        EditText PSubTotal = (EditText) pView.findViewById(R.id.SubTotal);
        EditText PPromDiscount = (EditText) pView.findViewById(R.id.PromDiscount);
        EditText PDiscount = (EditText) pView.findViewById(R.id.Discount);
        EditText PDueAmount = (EditText) pView.findViewById(R.id.DueAmount);
        EditText PTotal = (EditText) pView.findViewById(R.id.Total);
        EditText PRemainAmt = (EditText) pView.findViewById(R.id.RemainAmt);
        String CardType = null;
        String MemName = null;
        float total = 0;
        float bal = 0;
        float discount_two = 0;
        float discount_one = 0;
        float discount;
        float dueamount;
        try {
            if (Payment != null) {
                if (Payment.has("DiscAmount") && !Payment.getString("DiscAmount").equals("")) {
                    discount_two = Float.valueOf(Payment.getString("DiscAmount"));
                }
                if (Payment.has("DiscValue") && !Payment.getString("DiscValue").equals("")) {
                    discount_one = Float.valueOf(Payment.getString("DiscValue"));
                }
            }
            if (Card != null) {
                if (Card.has("CardTypeDesc")) {
                    CardType = Card.getString("CardTypeDesc");
                }
                if (Card.has("BalanceValue") && !Card.getString("BalanceValue").equals("")) {
                    if (Prefs.getString("CardPolicy", "F").equals("F")) {
                        if (Prefs.getBoolean("DedDisPlay", false)) {
                            bal = Float.valueOf(setamt(Card.getString("BalanceValue"))) - Float.valueOf(setamt(Card.getString("ReserveValue")));
                        } else {
                            bal = Float.valueOf(Card.getString("BalanceValue"));
                        }
                    } else {
                        bal = Float.valueOf(setamt(Card.getString("FoodLoftCreditLimit"))) - Float.valueOf(setamt(Card.getString("UsedValue")));
                    }
                }
                if (Card.has("MemName")) {
                    MemName = Card.getString("MemName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PCardType.setText(CardType);
        PTotal.setText(setamt(String.valueOf(bal)));
        PMember.setText(MemName);
        TextView Total = (TextView) findViewById(R.id.Total);
        if (Total != null && !Total.getText().toString().equals("")) {
            total = Float.valueOf(Total.getText().toString());
        }
        discount = discount_one + discount_two;
        dueamount = total - discount;
        String disc_o = String.valueOf(discount_one);
        String disc_t = String.valueOf(discount_two);
        String dueamt = String.valueOf(dueamount);
        String remain = String.valueOf(bal - dueamount);
        PSubTotal.setText(setamt(Total.getText().toString()));
        if (Card != null) {
            PPromDiscount.setText(setamt(disc_o));
            PDiscount.setText(setamt(disc_t));
            PDueAmount.setText(setamt(dueamt));
            PRemainAmt.setText(setamt(remain));
        } else {
            PPromDiscount.setText(setamt("0"));
            PDiscount.setText(setamt("0"));
            PDueAmount.setText(setamt("0"));
            PRemainAmt.setText(setamt("0"));
        }

    }

    private void ClearPayment(Boolean Paid) {
        EditText PCardNo = (EditText) pView.findViewById(R.id.CardNo);
        PCardNo.setText("");
        Card = null;
        Payment = null;
        if (Paid) {
            int childCount = OrderListTable.getChildCount();
            if (childCount > 1) {
                OrderListTable.removeViews(1, childCount - 1);
            }
            UpdateTotal();
        }
    }

    private void setLocal(String language) {
        myLocale = new Locale(language);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);
        getApplication().createConfigurationContext(conf);
    }

    private void setTouchSound(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Utils.addSound(activity);
                return false;
            }
        });
    }
}