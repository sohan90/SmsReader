package com.example.sohan.smsreader.database;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by sohan on 17/11/17.
 */

public class SmsReaderHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "reader.db";
    private static final int DATABASE_VERSION = 1;
    private static SmsReaderHelper mInstance;

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
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + SmsReaderContract.SmsReaderEntry.TABLE_NAME + " ("
                + SmsReaderContract.SmsReaderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + SmsReaderContract.SmsReaderEntry.COLUMN_BODY + " TEXT NOT NULL, "
                + SmsReaderContract.SmsReaderEntry.COLUMN_ADDRESS + " TEXT, "
                + SmsReaderContract.SmsReaderEntry.COLUMN_READ + " TEXT ,"
                + SmsReaderContract.SmsReaderEntry.COLUMN_TIME + " TEXT, "
                + SmsReaderContract.SmsReaderEntry.COLUMN_PERSON + " TEXT, "
                + SmsReaderContract.SmsReaderEntry.COLUMN_TYPE + " TEXT);";

        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}
