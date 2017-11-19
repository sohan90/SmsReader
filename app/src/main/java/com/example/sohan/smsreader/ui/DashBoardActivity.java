package com.example.sohan.smsreader.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.sohan.smsreader.R;

public class DashBoardActivity extends BaseActivity implements DashBoardView, BaseActivity.RequestPermissionAction {

    private DashBoardPresenterImpl mPresenter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPresenter();

        findViewById(R.id.but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbmanager = new Intent(DashBoardActivity.this, AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });


    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getReadSMSPermission(this);
    }

    private void fetchSms() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
        mPresenter.fetchAllSms(this);
    }

    private void setPresenter() {
        DashBoardInteractorImpl dashBoardInteractor = new DashBoardInteractorImpl();
        mPresenter = new DashBoardPresenterImpl(this, dashBoardInteractor);
    }

    @Override
    public void onSuccess() {
        mProgressDialog.dismiss();

    }

    @Override
    public void onFailure() {
        mProgressDialog.dismiss();
    }

    @Override
    public void onProgessUpdate(int value) {

    }

    @Override
    public void permissionDenied() {

    }

    @Override
    public void permissionGranted() {
        fetchSms();
    }
}
