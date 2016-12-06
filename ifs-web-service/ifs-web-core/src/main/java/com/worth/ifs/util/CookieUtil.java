package com.worth.ifs.util;

public final class CookieUtil {
    private static CookieUtilHelper cookieUtilHelper;

    public static CookieUtilHelper getInstance() {
        if(null ==  cookieUtilHelper) cookieUtilHelper = new CookieUtilHelper();

        return cookieUtilHelper;
    }
}
