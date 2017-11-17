package com.example.sohan.smsreader.ui;

import android.app.Activity;

/**
 * Created by sohan on 17/11/17.
 */

public class DashBoardInteractorImpl {

    public void fetchAllSms(AsynchTaskHelper.AsynchTaskCallBack callBack, Activity context){
        AsynchTaskHelper asynchTaskHelper = new AsynchTaskHelper(callBack, context);
        asynchTaskHelper.execute();

    }
}
