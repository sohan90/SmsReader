package com.example.sohan.smsreader.database;

import android.provider.BaseColumns;

/**
 * Created by sohan on 17/11/17.
 */

public class SmsReaderContract {

    private SmsReaderContract() {
        // empty constructor
    }

    public abstract class SmsReaderEntry implements BaseColumns {
        public final static String TABLE_NAME = "credit_card";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ADDRESS = "address";
        public final static String COLUMN_BODY= "body";
        public final static String COLUMN_READ = "amount";
        public final static String COLUMN_TIME = "time";
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_PERSON = "person";

    }


}
