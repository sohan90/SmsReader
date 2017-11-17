package com.example.sohan.smsreader.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.sohan.smsreader.database.SmsReaderContract;
import com.example.sohan.smsreader.database.SmsReaderHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
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
                if (c.moveToFirst()) {
                    for (int i = 0; i < totalSMS; i++) {
                        SmsReaderModel model = new SmsReaderModel();
                        String id = (c.getString(c.getColumnIndexOrThrow("_id")));
                        String address = (c.getString(c.getColumnIndexOrThrow("address")));
                        String body = (c.getString(c.getColumnIndexOrThrow("body")));
                        String read = (c.getString(c.getColumnIndex("read")));
                        String time = (c.getString(c.getColumnIndexOrThrow("date")));
                        String type = (c.getString(c.getColumnIndexOrThrow("type")));
                        String person = (c.getString(c.getColumnIndexOrThrow("person")));

                        model.setId(id);
                        model.setAddress(address);
                        model.setBody(body);
                        model.setRead(read);
                        model.setTime(time);
                        model.setType(type);
                        model.setPerson(person);
                        //insertIntoDatabase(model);

                       /* String rulesJsonFile = loadJSONFromAsset();
                        JSONObject rulesJsonObj = new JSONObject(rulesJsonFile);
                        if (!rulesJsonObj.isNull("rules")) {
                            JSONArray jsonArray = rulesJsonObj.getJSONArray("rules");
                            for (int j = 0 ; j < jsonArray.length(); j++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (!jsonObject.isNull("name")) {
                                }
                                if (!jsonObject.isNull("patterns")) {
                                    JSONArray patterArray = jsonObject.getJSONArray("patterns");
                                    for (int k = 0 ; k < patterArray.length(); k ++) {
                                        JSONObject patternOBj = patterArray.getJSONObject(0);
                                        if (!patternOBj.isNull("regex")) {
                                            String regex = patternOBj.getString("regex");
                                            Pattern pattern = Pattern.compile(regex);
                                            Matcher matcher = pattern.matcher(body);
                                            if (matcher.find()) {
                                                Log.i(TAG, " Pattern " +  matcher.group(0));
                                            }
                                        }
                                    }
                                }
                            }
                        }*/

                        //normalRegx(id, address, body, read, time, type, person);
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

    private void normalRegx(String id, String address, String body, String read, String time, String type, String person) {
        Pattern regEx = Pattern.compile("(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)");
        Pattern merchantName = Pattern.compile("(?i)(?:\\sat\\s|in\\*)([A-Za-z0-9]*\\s?-?\\s?[A-Za-z0-9]*\\s?-?\\.?)");
        Pattern cardName = Pattern.compile("(?i)(?:\\smade on|ur|made a\\s|in\\*)([A-Za-z]*\\s?-?\\s[A-Za-z]*\\s?-?\\s[A-Za-z]*\\s?-?)");
        Pattern msg = Pattern.compile("[a-zA-Z0-9]{2}-[a-zA-Z0-9]{6}");

        Matcher matcher = regEx.matcher(body);
        Matcher merchantMatcher = merchantName.matcher(address);
        Matcher cardMatcher = cardName.matcher(body);
        Matcher msgMatcher = msg.matcher(address);

        String cardNameStr = null;
        String str = null;
        String merchant = null;
        String bankName = null;


        if (matcher.find()) {
            str = matcher.group(0);
        }

        if (merchantMatcher.find()) {
            merchant = merchantMatcher.group(0);
        }

        if (cardMatcher.find()) {
            cardNameStr = cardMatcher.group(0);
        }

        if (msgMatcher.find()){
            bankName =   msgMatcher.group();
        }

        Log.i(TAG, "Id " + id + " Address " + address + " Body " + body + " read "
                + read + " Time " + time + " Type " + type + " Person " + person + " MESSAGE " + str
                + " MERCHANT NAME " + merchant + " CARD NAME " + cardNameStr +  " Bank Name " + bankName);
    }

    private void insertIntoDatabase(SmsReaderModel model) {
        SmsReaderHelper dbHelpler = SmsReaderHelper.getInstance(mContext);
        SQLiteDatabase database = dbHelpler.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SmsReaderContract.SmsReaderEntry.COLUMN_ADDRESS, model.getAddress());
        contentValues.put(SmsReaderContract.SmsReaderEntry.COLUMN_BODY, model.getBody());
        contentValues.put(SmsReaderContract.SmsReaderEntry.COLUMN_READ, model.getRead());
        contentValues.put(SmsReaderContract.SmsReaderEntry.COLUMN_TIME, model.getTime());
        contentValues.put(SmsReaderContract.SmsReaderEntry.COLUMN_TYPE, model.getType());
        contentValues.put(SmsReaderContract.SmsReaderEntry.COLUMN_PERSON, model.getPerson());
        database.insert(SmsReaderContract.SmsReaderEntry.TABLE_NAME, null, contentValues);
    }

    public String loadJSONFromAsset() {
        String json = null;
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
