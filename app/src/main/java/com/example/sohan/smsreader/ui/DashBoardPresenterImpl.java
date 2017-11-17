package com.example.sohan.smsreader.ui;

import android.app.Activity;
import android.content.Context;

/**
 * Created by sohan on 17/11/17.
 */

public class DashBoardPresenterImpl implements AsynchTaskHelper.AsynchTaskCallBack {
    private final DashBoardView mView;
    private final DashBoardInteractorImpl mInteractor;

    public DashBoardPresenterImpl(DashBoardView view, DashBoardInteractorImpl dashBoardInteractor){
        mView = view;
        mInteractor = dashBoardInteractor;
    }

    public void fetchAllSms(Activity activity){
        mInteractor.fetchAllSms(this, activity);
    }

    @Override
    public void onSuccess() {
        mView.onSuccess();

    }

    @Override
    public void onFailure() {
        mView.onFailure();
    }

    @Override
    public void onProgessUpdate(int value) {
        mView.onProgessUpdate(value);
    }
}
