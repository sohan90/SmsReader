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

                        /*InputStream stream = mContext.getAssets().open("rules.json");
                        try {
                            JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
                            Gson gson = new GsonBuilder().create();
                            reader.beginObject();
                            while (reader.hasNext()) {
                                PatterModel patterModel = gson.fromJson(reader.toString(), PatterModel.class);
                                PatterModel.RulesModel rulesModelList = patterModel.getRulesModelList().get(0);
                                List<PatterModel.Patterns> patternsList = rulesModelList.getPatterList();
                                for (PatterModel.Patterns patterns : patternsList) {
                                    String regex = patterns.getRegex();
                                    Pattern pattern = Pattern.compile(regex);
                                    Matcher matcher = pattern.matcher(body);
                                    if (matcher.find()) {
                                        Log.i(TAG, "Patter " + matcher.group(0));
                                    }
                                }

                                break;
                            }
                            reader.close();
                        } catch (UnsupportedEncodingException ex) {

                        } catch (IOException ex) {

                        }*/
                        normalRegx(id, address, body, read, time, type, person);
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
        Pattern creditPattern = Pattern.compile("(?i)(?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2}) debited to Ac x+(\\\\d{4}).*-(.*charges.*) tot.*(?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2})");


        Pattern creditPattern2 = Pattern.compile("(?i)Transaction of (INR|Rs|[a-z]{3})[\\\\.\\\\s]*([\\\\d,]*\\\\.?\\\\d{1,2}).*Credit Card XX(\\\\d{4}) at (.*) on (.*).");
        Pattern creditPattern6 = Pattern.compile("(?i) purchase of (?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2}) has been .* credit card\\\\s*\\\\d*x*\\\\d*(\\\\d{4}) at (.*) on.* balance is (?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2})");
        Pattern creditPattern3 = Pattern.compile("(?i)(?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2}) has been spent .* credit card.* ending with (\\\\d{4}) at (.*) on ([\\\\d\\\\/]* at [\\\\d\\\\:]*) IST. avl bal (?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2}).");
        Pattern creditPattern4 = Pattern.compile(" (?i)(?:INR|Rs)[\\\\.:,\\\\s]*([\\\\d,]+\\\\.?\\\\d{0,2}) .* centralbank (credit card) ending ([\\\\d]+) on ([\\\\d\\\\:\\\\-]*)at (.*)avl bal - (?:INR|Rs)[\\\\.:,\\\\s]*([\\\\d,]+\\\\.?\\\\d+).curr o\\\\/s - (?:INR|Rs)[\\\\.:,\\\\s]*([\\\\d,]+\\\\.?\\\\d+).");
        Pattern creditPattern5 = Pattern.compile("(?i)(?:INR|RS)[\\\\.:,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2}) has.* credit card.* x+(\\\\d{4}) .* at (.*) avl bal. is [\\\\.:,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2})");
        Pattern creditPattern7 = Pattern.compile("(?i)credit card no ending (\\\\d{4}) for (?:INR|Rs)[\\\\.,\\\\s]*([\\\\d,]*\\\\.?\\\\d{0,2}) on.* at (.*) contact");
        Pattern creditPattern8 = Pattern.compile("(?i)(INR|Rs|[a-z]{3} )[\\\\.:,\\\\s]*([\\\\d,]+\\\\.?\\\\d{0,2}).*spent on your Credit Card X+([\\\\d]{4}) on (.*) at (.*)\\\\. Avbl.*?(?:INR|Rs)[\\\\.:,\\\\s]*(-?[\\\\d,]+\\\\.?\\\\d{0,2})");



        Matcher matcher = regEx.matcher(body);
        Matcher merchantMatcher = merchantName.matcher(address);
        Matcher cardMatcher = cardName.matcher(body);
        Matcher msgMatcher = msg.matcher(address);
        Matcher creditMatcher = creditPattern.matcher(body);
        Matcher creditMatcher2 = creditPattern2.matcher("Transaction of Rs.1,078.98 made on SBI Credit Card XX3450 at PAYTM on 9 Jun 15.");

        String cardNameStr = null;
        String str = null;
        String merchant = null;
        String bankName = null;
        String creditCardStr = null;


        if (matcher.find()) {
            str = matcher.group(0);
        }

        if (merchantMatcher.find()) {
            merchant = merchantMatcher.group(0);
        }

        if (cardMatcher.find()) {
            cardNameStr = cardMatcher.group(0);
        }

        if (msgMatcher.find()) {
            bankName = msgMatcher.group();
        }

        if (creditMatcher.find()){
            creditCardStr = creditMatcher.group();
        }

        if (creditMatcher2.find()){
            creditCardStr = creditMatcher2.group(0);
        }

        Log.e(TAG, "CardPattern " +  creditCardStr);
        Log.i(TAG, "Id " + id + " Address " + address + " Body " + body + " read "
                + read + " Time " + time + " Type " + type + " Person " + person + " MESSAGE " + str
                + " MERCHANT NAME " + merchant + " CARD NAME " + cardNameStr + " Bank Name " + bankName);
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

    public InputStream loadJSONFromAsset() {
        //String json = null;
        InputStream stream = null;
        try {
            InputStream is = mContext.getAssets().open("rules.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            //is.close();
            stream = is;
            //json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return stream;
    }


}
