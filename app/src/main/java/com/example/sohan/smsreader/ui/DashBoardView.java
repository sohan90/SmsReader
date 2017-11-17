package com.example.sohan.smsreader.ui;

/**
 * Created by sohan on 17/11/17.
 */

public interface DashBoardView {
    void onSuccess();

    void onFailure();

    void onProgessUpdate(int value);
}
