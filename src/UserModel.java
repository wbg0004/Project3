public class UserModel {
    public static final int CUSTOMER = 3;
    public static final int CASHIER = 2;
    public static final int MANAGER = 1;
    public static final int ADMIN = 0;


    public String mUsername, mPassword, mFullname;
    public int mUserType;
    public int mCustomerID; // if usertype is CUSTOMER

    public UserModel(String username, String password, String fullname, int userType, int customerID) {
        mUsername = username;
        mPassword = password;
        mFullname = fullname;
        mUserType = userType;
        mCustomerID = customerID;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append("\"").append(mUsername).append("\"").append(",");
        sb.append("\"").append(mPassword).append("\"").append(",");
        sb.append("\"").append(mFullname).append("\"").append(",");
        sb.append(mUserType).append(")");
        return sb.toString();
    }

}