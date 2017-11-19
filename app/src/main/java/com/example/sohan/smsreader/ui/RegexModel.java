package com.example.sohan.smsreader.ui;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sohan on 18/11/17.
 */

public class RegexModel {
    @SerializedName("rules")
    private List<RulesModel> mRulesModelList;

    public List<RulesModel> getRulesModelList() {
        return mRulesModelList;
    }

    public class RulesModel {
        @SerializedName("patterns")
        private List<Patterns> mPatterList;

        public List<Patterns> getPatterList() {
            return mPatterList;
        }
    }

    public class Patterns {
        public String getRegex() {
            return mRegex;
        }

        @SerializedName("regex")
        private String mRegex;

        @SerializedName("account_type")
        private String mAccountType;

        @SerializedName("sms_type")
        private String smsType;
        @SerializedName("data_fields")
        private DataFieldsModel dataFieldsModel;

        public DataFieldsModel getDataFieldsModel() {
            return dataFieldsModel;
        }

        public String getSmsType() {
            return smsType;
        }

        public String getmAccountType() {
            return mAccountType;
        }
    }
}
