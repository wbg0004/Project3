public class MessageModel {

    public static final int GET_PRODUCT = 100;
    public static final int PUT_PRODUCT = 101;
    public static final int ADD_PRODUCT = 102;
    public static final int DUPLICATE_PRODUCT = 103;

    public static final int GET_CUSTOMER = 200;
    public static final int PUT_CUSTOMER = 201;
    public static final int ADD_CUSTOMER = 202;
    public static final int DUPLICATE_CUSTOMER = 203;

    public static final int GET_PURCHASE = 300;
    public static final int PUT_PURCHASE = 301;
    public static final int ADD_PURCHASE = 302;
    public static final int DUPLICATE_PURCHASE = 303;

    public static final int GET_USER = 400;
    public static final int PUT_USER = 401;
    public static final int ADD_USER = 402;
    public static final int DUPLICATE_USER = 403;
    public static final int UPDATE_USER_WRONG_PASS = 405;

    public static final int PURCHASE_REPORT = 900;
    public static final int PURCHASE_HISTORY = 901;
    public static final int SEARCH_PRODUCT_PRICE = 910;
    public static final int SEARCH_PRODUCT_NAME = 911;

    public static final int OPERATION_OK = 1000; // server responses!
    public static final int OPERATION_FAILED = 1001;

    public static final int LOGIN = 3000;
    public static final int LOGIN_SUCCESS = 3001;
    public static final int LOGIN_WRONG_PASS = -3002;
    public static final int LOGIN_WRONG_USER = -3003;
    public static final int LOGOUT = 4000;
    public static final int LOGOUT_SUCCESS = 4001;

    public int code;
    public String data;

    public MessageModel() {
        code = 0;
        data = null;
    }

    public MessageModel(int code, String data) {
        this.code = code;
        this.data = data;
    }
}