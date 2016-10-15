package com.j13.zed.api;


public class ApiConstant {

    public static final String APP_ID = "3";
    public static final String SECRET_KEY = "jXJhmCwAcHJnLTyQuKrLLUGLZexjZbqI";

    public static final String KEY_METHOD = "method";
    public static final String KEY_HTTP_METHOD = "httpMethod";

    private static String BASE_API_URL = null;
    private static String BASE_MCP_API_URL = null;

    public static final String JAX_RELEASE_URL="http://123.56.86.200:8080/api";

    // 测试
    private static final String API_TEST_URL = "http://192.168.111.103:8080/";
    private static final String NEW_API_TEST_URL = "https://test.api.bchbc.com/";
    // 预发布
    private static final String API_PRE_URL = "http://pre.api.xlmc.sandai.net/";
    // 线上
    private static final String API_RELEASE_URL = "http://api.ra2.xlmc.sec.miui.com/";
    private static final String NEW_API_RELEASE_URL = "http://api.xlmc.sec.miui.com/";

    public static final String SERVER_TYPE_TEST = "test";
    public static final String SERVER_TYPE_PRE = "pre";
    public static final String SERVER_TYPE_RELEASE = "release";

    public static String getBaseApiUrl() {
//        if (BASE_API_URL == null) {
//            if (SERVER_TYPE_RELEASE.equals(Config.SERVER_TYPE)) {
//                BASE_API_URL = API_RELEASE_URL;
//            } else {
//                BASE_API_URL = API_TEST_URL;
//            }
//        }
        return API_TEST_URL;
    }

//    public static String getBaseMcpApiUrl() {
//        if (BASE_MCP_API_URL == null) {
//            if (SERVER_TYPE_RELEASE.equals(Config.SERVER_TYPE)) {
//                BASE_MCP_API_URL = NEW_API_RELEASE_URL;
//            } else if (SERVER_TYPE_PRE.equals(Config.SERVER_TYPE)) {
//                BASE_MCP_API_URL = API_PRE_URL;
//            } else if (SERVER_TYPE_TEST.equals(Config.SERVER_TYPE)) {
//                BASE_MCP_API_URL = NEW_API_TEST_URL;
//            } else {
//                BASE_MCP_API_URL = NEW_API_TEST_URL;
//            }
//        }
//        return BASE_MCP_API_URL;
//    }
//
//    // miui base api
//    private static String MIUI_BASE_API_URL = null;
//    private static final String MIUI_API_TEST_HOST = "test.ad.xiaomi.com";
//    private static final String MIUI_API_RELEASE_HOST = "api.ad.xiaomi.com";
//
//    public static String getMiuiApiHost() {
//        if (MIUI_BASE_API_URL == null) {
//            if (SERVER_TYPE_TEST.equals(Config.SERVER_TYPE)) {
//                MIUI_BASE_API_URL = MIUI_API_TEST_HOST;
//            } else {
//                MIUI_BASE_API_URL = MIUI_API_RELEASE_HOST;
//            }
//        }
//        return MIUI_BASE_API_URL;
//    }
//
//    public static String getMiuiBaseApiUrl() {
//        return "http://" + getMiuiApiHost() + "/";
//    }

}
