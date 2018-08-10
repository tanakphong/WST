package com.wf.demo.wfv30;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONObject;

class Model {

    static void NumTerminalByCategoryJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,TermCategory";
        try {
            params.put("method", "NumTerminalByCategoryJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("TermCategory", "T");
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ValidTerminalJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity,soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,TermDesc,TermType,TermCategory";
        try {
            params.put("method", "ValidTerminalJS");
            params.put("getProp","3");
            params.put("TermCategory","T");
            params.put("TermType","S");
            params.put("TermDesc",Utils.getIP(activity));
            params.put("encodePWD",Prefs.getString("Password",""));
            params.put("getError","2");
            soap.execute(Prefs.getString("IPAddress",""),Prefs.getString("Port",""),params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void CardToShopJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,CardNo";
        try {
            params.put("method", "CardToShopJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("CardNo", CardNo);
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""),params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void GetCardInfoJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,CardNo";
        try {
            params.put("method", "GetCardInfoJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("CardNo", CardNo);
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ToPrintSUM(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,ShopCode,TermCode";
        try {
            params.put("method", "ToPrintSUM");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("TermCode", Prefs.getString("TermCode",""));
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void SelectTerminalJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,Integer getProp){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,TermDesc,TermType,TermCategory,SwapShop,ShopCode";
        try {
            params.put("method", "SelectTerminalJS");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("TermDesc", Prefs.getString("TermDesc",""));
            params.put("TermType", Prefs.getString("TermType",""));
            params.put("TermCategory", Prefs.getString("TermCategory",""));
            params.put("SwapShop", Prefs.getString("SwapShop",""));
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("getProp",getProp);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void GetSysInfoJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,ForLocation";
        try {
            params.put("method", "GetSysInfoJS");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("ForLocation", Prefs.getString("TermType",""));
            params.put("getProp",2);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void VoidSaleAcceptJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo,String RunningNo){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,CardNo,ShopCode,TermCode,DedDisplay,SaleRunningNo";
        try {
            params.put("method", "VoidSaleAcceptJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("CardNo", CardNo);
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("TermCode", Prefs.getString("TermCode",""));
            params.put("DedDisplay", Prefs.getString("DedDisplay",""));
            params.put("SaleRunningNo", RunningNo);
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ValueTransferJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardFrom,String CardTo){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,TrnBy,TrnLocCode,CardNoSource,CardNoTarget,MyDesc";
        try {
            params.put("method", "ValueTransferJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("TrnBy", "S");
            params.put("TrnLocCode", Prefs.getString("ShopCode",""));
            params.put("CardNoSource", CardFrom);
            params.put("CardNoTarget", CardTo);
            params.put("MyDesc", activity.getText(R.string.DescTransfer));
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void GetCardRightJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,CardNo";
        try {
            params.put("method", "GetCardRightJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("CardNo", CardNo);
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void PriceTypeJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String PriceType){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,getORput,ShopCode,PutPriceType";
        try {
            params.put("method", "PriceTypeJS");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("getORput", "PUT");
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("PutPriceType", PriceType);
            params.put("getProp",2);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void SelectShopNetSaleJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,ShopCode";
        try {
            params.put("method", "SelectShopNetSaleJS");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("getProp",2);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void SaleVoidCardReadedJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,CardNo,ShopCode";
        try {
            params.put("method", "SaleVoidCardReadedJS");
            params.put("encodePWD", Prefs.getString("Password", ""));
            params.put("CardNo", CardNo);
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("getProp","2");
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void GetServerNowJS(AlertDialog loadingDialog, Activity activity){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, null,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD";
        try {
            params.put("method", "GetServerNowJS");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("ReturnNow",true);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ToPrintCardTrans(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,CardNo,TermCode";
        try {
            params.put("method", "ToPrintCardTrans");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("CardNo", CardNo);
            params.put("TermCode", Prefs.getString("TermCode",""));
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ToPrintSLIP(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,ShopCode,RunningNo,PrinterName";
        try {
            params.put("method", "ToPrintSLIP");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("RunningNo", "");
            params.put("PrinterName", Prefs.getString("PrinterName",""));
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void ToPrintABB(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,ShopCode,AbbTaxRunningNo,PrinterName";
        try {
            params.put("method", "ToPrintABB");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("AbbTaxRunningNo", "");
            params.put("PrinterName", Prefs.getString("PrinterName",""));
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void DispOnOff(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String MonitorMode){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,getORput,TermCode,PutMode";
        try {
            params.put("method", "DispOnOff");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("getORput", "PUT");
            params.put("TermCode", Prefs.getString("TermCode",""));
            params.put("PutMode", MonitorMode);
            params.put("ReturnNow",true);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void PrinterOnOff(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String PrintMode){
        loadingDialog.show();
        Utils.setWHAlertDialog(loadingDialog);
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,getORput,TermCode,PutMode";
        try {
            params.put("method", "PrinterOnOff");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("getORput", "PUT");
            params.put("TermCode", Prefs.getString("TermCode",""));
            params.put("PutMode", PrintMode);
            params.put("ReturnNow",true);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void SaleCardReadedJS(AlertDialog loadingDialog, Activity activity, SoapConnect.AsyncResponse soapasync,String CardNo,Float OrderAmount,String Prices,String PromPrices,String Codes,String Desc,String TrnType){
        SoapConnect soap = new SoapConnect(activity, soapasync,loadingDialog);
        JSONObject params = new JSONObject();
        String sort = "encodePWD,TrnType,CardNo,ShopCode,TermCode,OrderAmount,StmItemPnorm,StmItemPprom,StmItemCode,StmItemDesc,DedDisplay";
        try {
            params.put("method", "SaleCardReadedJS");
            params.put("encodePWD", Prefs.getString("Password",""));
            params.put("TrnType", TrnType);
            params.put("CardNo", CardNo);
            params.put("ShopCode", Prefs.getString("ShopCode",""));
            params.put("TermCode", Prefs.getString("TermCode",""));
            params.put("OrderAmount", OrderAmount);
            params.put("StmItemPnorm", Prices);
            params.put("StmItemPprom", PromPrices);
            params.put("StmItemCode", Codes);
            params.put("StmItemDesc", Desc);
            params.put("DedDisplay", Prefs.getString("DedDisplay",""));
            params.put("getProp",2);
            soap.execute(Prefs.getString("IPAddress", ""), Prefs.getString("Port", ""), params.toString(),sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}