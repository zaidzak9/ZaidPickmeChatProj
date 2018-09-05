package com.zaid.zaidpickmeproj.helper;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ValidationHelper {

    public static boolean isValidEmail(String email) {
        boolean result = false;

        if (!TextUtils.isEmpty(email)) {
            result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }

        return result;
    }



}
