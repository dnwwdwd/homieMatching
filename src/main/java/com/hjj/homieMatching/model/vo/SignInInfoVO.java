package com.hjj.homieMatching.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class SignInInfoVO implements Serializable {

    private List<Date> signedInDates;

    private int signedInDayNum; // 已签到天数

    private boolean isSignedIn;

    public boolean getIsSignedIn() {
        return isSignedIn;
    }

    public void setIsSignedIn(boolean signedIn) {
        isSignedIn = signedIn;
    }
}
