package com.example.sohan.smsreader.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.sohan.smsreader.R;

public class DashBoardActivity extends AppCompatActivity implements DashBoardView {

    private DashBoardPresenterImpl mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPresenter();
        fetchSms();
        findViewById(R.id.but).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dbmanager = new Intent(DashBoardActivity.this, AndroidDatabaseManager.class);
                startActivity(dbmanager);
            }
        });


    }

    private void fetchSms() {
        mPresenter.fetchAllSms(this);
    }

    private void setPresenter() {
        DashBoardInteractorImpl dashBoardInteractor = new DashBoardInteractorImpl();
        mPresenter = new DashBoardPresenterImpl(this, dashBoardInteractor);
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure() {

    }

    @Override
    public void onProgessUpdate(int value) {

    }
}
