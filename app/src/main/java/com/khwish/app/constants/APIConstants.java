package com.khwish.app.constants;

import com.khwish.app.BuildConfig;

public class APIConstants {

//    private static final String STG_BASE_URL="http://192.168.0.106:8080";
    private static final String STG_BASE_URL = "http://64.225.84.49";
    private static final String PROD_BASE_URL = "http://68.183.245.105";

    private static final String ENV_PROD = "production";

    public static String getBaseUrl() {
        if (ENV_PROD.equals(BuildConfig.BUILD_ENV)) {
            return PROD_BASE_URL;
        } else {
            return STG_BASE_URL;
        }
    }
}