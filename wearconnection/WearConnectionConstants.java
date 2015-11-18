package com.prokarma.wearpoc.connection;

/**
 * This class contains all the constants used in the Library.
 */
public interface WearConnectionConstants {


    public static class  RequestCode {
        int CONNECT_TO_WEAR = 1000;

    }

    class KEY {
        public static String SEND_DATA = "Send_Data";
        public static String CURRENT_TIME = "Current_Time";
        public static String USER_ACTION = "User_Action";
    }

    public static class TAG {
        String GOOGLE_PLAY_SERVICE_ERROR_DIALOG = "google_play_service_error_dialog";

    }

    public static final String START_WEAR_APPLICATION = "/start/wear/";
    public static final String START_MOBILE_APPLICATION = "/start/mobile/";

    public static final String PATH_WEAR_DATA = "/data/wear/";
    public static final String PATH_MOBILE_DATA = "/data/mobile/";

    public static final String PATH_WEAR_MESSAGE = "/message/wear/";
    public static final String PATH_MOBILE_MESSAGE = "/message/mobile/";

    class Path {
        public static final String PATH_USER_ACTION_DATA = PATH_MOBILE_DATA + "userAction";
    }

}
