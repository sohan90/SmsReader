package com.example.sohan.smsreader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.sohan.smsreader.ui.SmsReaderModel;

import java.util.ArrayList;

/**
 * Created by sohan on 17/11/17.
 */

public class SmsReaderHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reader.db";
    private static final int DATABASE_VERSION = 1;
    private static SmsReaderHelper mInstance;
    private static final String CREATE_SMS_TABLE = "CREATE TABLE " + SmsReaderDbContract.SmsReaderEntry.TABLE_NAME + " ("
            + SmsReaderDbContract.SmsReaderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SmsReaderDbContract.SmsReaderEntry.COLUMN_BODY + " TEXT NOT NULL, "
            + SmsReaderDbContract.SmsReaderEntry.COLUMN_ADDRESS + " TEXT, "
            + SmsReaderDbContract.SmsReaderEntry.COLUMN_READ + " TEXT,"
            + SmsReaderDbContract.SmsReaderEntry.COLUMN_TIME + " TEXT, "
            + SmsReaderDbContract.SmsReaderEntry.COLUMN_PERSON + " TEXT, "
            + SmsReaderDbContract.SmsReaderEntry.COLUMN_TYPE + " TEXT);";

    private static final String CREATE_CARD_TABLE = "CREATE TABLE " + SmsReaderDbContract.CardDetailEntry.TABLE_NAME + " ("
            + SmsReaderDbContract.CardDetailEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + SmsReaderDbContract.CardDetailEntry.BANK_NAME + " TEXT, "
            + SmsReaderDbContract.CardDetailEntry.CARD_NO + " TEXT, "
            + SmsReaderDbContract.CardDetailEntry.TRANSACTION + " TEXT, "
            + SmsReaderDbContract.CardDetailEntry.TRANSACTION_TIME + " TEXT, "
            + SmsReaderDbContract.CardDetailEntry.SMS_TIME + " TEXT);";

    public static SmsReaderHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SmsReaderHelper(context);
        }
        return mInstance;
    }

    private SmsReaderHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SMS_TABLE);
        db.execSQL(CREATE_CARD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_CARD_TABLE);
    }

    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (SQLException sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        } catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }

    public void insertIntoBankCardTable(String bankName, String cardNo, String transactionAmount,

                                        String transactionTime, String smsTime) {

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SmsReaderDbContract.CardDetailEntry.BANK_NAME, bankName);
        contentValues.put(SmsReaderDbContract.CardDetailEntry.CARD_NO, cardNo);
        contentValues.put(SmsReaderDbContract.CardDetailEntry.TRANSACTION, transactionAmount);
        contentValues.put(SmsReaderDbContract.CardDetailEntry.TRANSACTION_TIME, transactionTime);
        contentValues.put(SmsReaderDbContract.CardDetailEntry.SMS_TIME, smsTime);
        database.insert(SmsReaderDbContract.CardDetailEntry.TABLE_NAME, null, contentValues);
    }

    public void insertIntoSmsTable(SmsReaderModel model) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SmsReaderDbContract.SmsReaderEntry.COLUMN_ADDRESS, model.getAddress());
        contentValues.put(SmsReaderDbContract.SmsReaderEntry.COLUMN_BODY, model.getBody());
        contentValues.put(SmsReaderDbContract.SmsReaderEntry.COLUMN_READ, model.getRead());
        contentValues.put(SmsReaderDbContract.SmsReaderEntry.COLUMN_TIME, model.getTime());
        contentValues.put(SmsReaderDbContract.SmsReaderEntry.COLUMN_TYPE, model.getType());
        contentValues.put(SmsReaderDbContract.SmsReaderEntry.COLUMN_PERSON, model.getPerson());
        database.insert(SmsReaderDbContract.SmsReaderEntry.TABLE_NAME, null, contentValues);
    }
}
