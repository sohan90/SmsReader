package com.example.sohan.smsreader.database;

import android.provider.BaseColumns;

/**
 * Created by sohan on 17/11/17.
 */

public class SmsReaderDbContract {

    private SmsReaderDbContract() {
        // empty constructor
    }

    public abstract class SmsReaderEntry implements BaseColumns {
        public final static String TABLE_NAME = "sms";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_ADDRESS = "address";
        public final static String COLUMN_BODY= "body";
        public final static String COLUMN_READ = "amount";
        public final static String COLUMN_TIME = "time";
        public final static String COLUMN_TYPE = "type";
        public final static String COLUMN_PERSON = "person";

    }


    public abstract class CardDetailEntry implements BaseColumns{
        public final static String TABLE_NAME = "card";
        public final static String _ID = BaseColumns._ID;
        public final static String BANK_NAME = "bank_name";
        public final static String CARD_NO = "card_no";
        public final static String TRANSACTION = "card_transaction";
        public final static String TRANSACTION_TIME = "transaction_time";
        public final static String SMS_TIME = "sms_time";

    }

    public static final String TYPE_BANK = "bank";
    public static final String TYPE_OTHERS = "others";


}
