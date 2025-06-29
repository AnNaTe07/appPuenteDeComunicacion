package com.softannate.apppuentedecomunicacion.data.local;

import android.content.Context;
import com.softannate.apppuentedecomunicacion.utils.DateUtils;

public class TokenExpirationChecker {

    public static boolean isTokenExpired(Context context) {
        String exp = SpManager.getExpiracionToken(context);
        return DateUtils.isBeforeNow(exp);
    }

    public static boolean isRefreshTokenExpired(Context context) {
        String exp = SpManager.getExpiracionRefreshoken(context);
        return DateUtils.isBeforeNow(exp);
    }
}

