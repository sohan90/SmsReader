package com.example.sohan.smsreader.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.example.sohan.smsreader.database.SmsReaderDbContract;
import com.example.sohan.smsreader.database.SmsReaderHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sohan on 17/11/17.
 */

public class AsynchTaskHelper extends AsyncTask<String, Integer, String> {
    private static final String TAG = AsynchTaskHelper.class.getSimpleName();
    private final AsynchTaskCallBack mCallback;


    public interface AsynchTaskCallBack {
        void onSuccess();

        void onFailure();

        void onProgessUpdate(int value);
    }

    private final Activity mContext;

    public AsynchTaskHelper(AsynchTaskCallBack callBack, Activity context) {
        mContext = context;
        mCallback = callBack;
    }


    @Override
    protected String doInBackground(String... strings) {
        String status = getAllSms("inbox");
        return status;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mCallback.onProgessUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mCallback.onSuccess();
        Log.d(TAG, s);
    }

    private String getAllSms(String folderName) {
        String status = null;
        Uri message = Uri.parse("content://sms/" + folderName);
        ContentResolver cr = mContext.getContentResolver();
        Cursor c = cr.query(message, null, null, null, null);

        if (c != null) {
            int totalSMS = c.getCount();
            try {
                String smsJson = loadJSONFromAsset();
                Gson gson = new Gson();
                RegexModel regxRegexModel = gson.fromJson(smsJson, RegexModel.class);
                if (c.moveToFirst()) {
                    for (int i = 0; i < totalSMS; i++) {
                        SmsReaderModel model = new SmsReaderModel();
                        String body = (c.getString(c.getColumnIndexOrThrow("body")));
                        String time = (c.getString(c.getColumnIndexOrThrow("date")));
                        String patternStr = "(.*credit card.*|.*debit card.* |.*debited.*)";
                        Pattern merchantName = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                        Matcher matcher = merchantName.matcher(body);
                        String type;
                        if (matcher.find()) {
                            type = SmsReaderDbContract.TYPE_BANK;
                            insertIntoBankDetailTable(regxRegexModel, body, time);
                        } else {
                            type = SmsReaderDbContract.TYPE_OTHERS;
                        }

                        Log.i(TAG, " Type " + type);
                        model.setBody(body);
                        model.setTime(time);
                        model.setType(type);
                        SmsReaderHelper dbHelper = SmsReaderHelper.getInstance(mContext);
                        dbHelper.insertIntoSmsTable(model);
                        c.moveToNext();
                    }
                    status = "SUCCESS";
                }
            } catch (Exception e) {
                e.printStackTrace();
                status = "FAILURE";
            } finally {
                c.close();
            }
        }

        return status;
    }

    private void insertIntoBankDetailTable(RegexModel model1, String body, String smsTime) {
        try {
            for (RegexModel.RulesModel rulesModel : model1.getRulesModelList()) {
                List<RegexModel.Patterns> patternsList = rulesModel.getPatterList();
                for (RegexModel.Patterns patterns : patternsList) {
                    String transType = patterns.getDataFieldsModel().getmTransactionType();
                    if (patterns.getSmsType().equalsIgnoreCase("transaction") &&
                            !TextUtils.isEmpty(transType) && (transType.equalsIgnoreCase("credit_card") ||
                            transType.equalsIgnoreCase("debit_card"))) {
                        String regex = patterns.getRegex();
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(body);
                        if (matcher.find()) {
                            DataFieldsModel.Amount amount = patterns.getDataFieldsModel().getAmount();
                            DataFieldsModel.Currency currency = patterns.getDataFieldsModel().getCurrency();
                            DataFieldsModel.Pan pan = patterns.getDataFieldsModel().getPan();
                            DataFieldsModel.Pos pos = patterns.getDataFieldsModel().getPos();
                            DataFieldsModel.DateFormat dateFormat = patterns.getDataFieldsModel().getDateFormat();
                            Log.i(TAG, "Patter msg " + matcher.group(0));
                            String amtStr = null;
                            String currcyStr = null;
                            String panStr = null;
                            String posStr = null;
                            String format = null;

                            if (amount != null) {
                                amtStr = matcher.group(amount.getGroupId());
                            }

                            if (currency != null) {
                                currcyStr = matcher.group(currency.getGroupId());
                            }

                            if (pan != null) {
                                panStr = matcher.group(pan.getGroupId());
                            }

                            if (pos != null) {
                                posStr = matcher.group(pos.getGroupId());
                            }

                            if (dateFormat != null) {
                                format = dateFormat.getDateFormat();
                            }

                            Log.i(TAG, "amount  " + amtStr + " Currency " + currcyStr
                                    + " PanStr " + panStr + " posStr " + posStr +
                                    " DateFormat " + format);
                            // inserting into table
                            SmsReaderHelper dbHelper = SmsReaderHelper.getInstance(mContext);
                            dbHelper.insertIntoBankCardTable("", panStr, amtStr, format, smsTime);

                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = mContext.getAssets().open("rules.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
