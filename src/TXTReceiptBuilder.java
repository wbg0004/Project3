public class TXTReceiptBuilder implements IReceiptBuilder {

    StringBuilder sb = new StringBuilder();

    public TXTReceiptBuilder(PurchaseModel purchase, ProductModel product, CustomerModel customer) {
        appendHeader("Mike's Fruits and Veggies Shop");
        appendCustomer(customer);
        appendProduct(product);
        appendPurchase(purchase);
        appendFooter("Have a great day!");
    }

    @Override
    public void appendHeader(String header) {
        sb.append(header).append("\n");
    }

    @Override
    public void appendCustomer(CustomerModel customer) {
        sb.append("Customer ID: ").append(customer.mCustomerID).append("\n");
        sb.append("Customer Name: ").append(customer.mName).append("\n");
    }

    @Override
    public void appendProduct(ProductModel product) {
        sb.append("Product ID: ").append(product.mProductID).append("\n");
        sb.append("Product Name: ").append(product.mName).append("\n");
        sb.append("Price: ").append(product.mPrice).append("\n");
        sb.append("Tax Rate: ").append(product.mTaxRate).append("\n");
    }

    @Override
    public void appendPurchase(PurchaseModel purchase) {
        sb.append("Purchase ID: ").append(purchase.mPurchaseID).append("\n");
        sb.append("Quantity: ").append(purchase.mQuantity).append("\n");
        sb.append("Cost: ").append(purchase.mCost).append("\n");
        sb.append("Tax Cost: ").append(purchase.mTax).append("\n");
        sb.append("Total Cost: ").append(purchase.mTotal).append("\n");
        sb.append("Date: ").append(purchase.mDate).append("\n");
    }

    @Override
    public void appendFooter(String footer) {
        sb.append(footer).append("\n");
    }

    public String toString() {
        return sb.toString();
    }
}
