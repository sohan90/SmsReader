package com.example.sohan.smsreader.ui;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sohan on 18/11/17.
 */

public class PatterModel {
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

    }
}
