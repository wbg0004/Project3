//package edu.auburn;

public class ProductModel {
    public int mProductID;
    public String mName, mVendor, mDescription;
    public double mPrice, mTaxRate, mQuantity;

    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(mProductID).append(",");
        sb.append("\"").append(mName).append("\"").append(",");
        sb.append(mPrice).append(",");
        sb.append(mTaxRate).append(",");
        sb.append(mQuantity).append(",");
        sb.append("\"").append(mVendor).append("\"").append(",");
        sb.append("\"").append(mDescription).append("\"").append(")");
        return sb.toString();
    }
}
