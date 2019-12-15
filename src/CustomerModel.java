//package edu.auburn;

public class CustomerModel {
    public int mCustomerID;
    public String mName, mAddress, mPhone, mPayInfo;

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(mCustomerID).append(",");
        sb.append("\"").append(mName).append("\"").append(",");
        sb.append("\"").append(mAddress).append("\"").append(",");
        sb.append("\"").append(mPhone).append("\"").append(",");
        sb.append("\"").append(mPayInfo).append("\"").append(")");
        return sb.toString();
    }
}
