import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Socket;
import java.util.Calendar;
import java.util.Objects;
import java.util.Scanner;

public class UpdatePurchaseUI {

    public JFrame view;

    public JButton btnLoad = new JButton("Load Purchase");
    public JButton btnSave = new JButton("Save Purchase");

    public JTextField txtPurchaseID = new JTextField(20);
    public JTextField txtProductID = new JTextField(20);
    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtQuantity = new JTextField(20);

    public JLabel labPrice = new JLabel("Product Price: ");
    public JLabel labDate = new JLabel("Date of Purchase: ");

    public JLabel labCustomerName = new JLabel("Customer Name: ");
    public JLabel labProductName = new JLabel("Product Name: ");

    public JLabel labCost = new JLabel("Cost: $0.00");
    public JLabel labTax = new JLabel("Tax: $0.00");
    public JLabel labTotalCost = new JLabel("Total Cost: $0.00");

    ProductModel product = new ProductModel();
    CustomerModel customer = new CustomerModel();
    PurchaseModel purchase = new PurchaseModel();

    public UpdatePurchaseUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Update Purchase Information");
        view.setSize(400, 450);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnLoad);
        panelButtons.add(btnSave);
        view.getContentPane().add(panelButtons);

        String[] labels = {"Purchase ID ", "Product ID ", "Customer ID ", "Quantity "};
        JTextField[] textFields = {txtPurchaseID, txtProductID, txtCustomerID, txtQuantity};
        JLabel[] jLabels = {labPrice, labDate, labCustomerName, labProductName, labCost, labTax, labTotalCost};

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JTextField field = textFields[i];
            l.setLabelFor(field);
            p.add(field);
            view.getContentPane().add(p);
        }

        for (int i = 0; i < jLabels.length; i++) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            JLabel label = jLabels[i];
            p.add(label);
            view.getContentPane().add(p);
        }

        btnLoad.addActionListener(new LoadButtonListener());

        btnSave.addActionListener(new SaveButtonListener());

        txtProductID.addFocusListener(new ProductIDFocusListener());

        txtCustomerID.addFocusListener(new CustomerIDFocusListener());

        txtQuantity.getDocument().addDocumentListener(new QuantityChangeListener());

    }

    public void run() {
        view.setVisible(true);
    }

    class LoadButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String id = txtPurchaseID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "PurchaseID cannot be null!");
                return;
            }

            try {
                purchase.mPurchaseID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "PurchaseID is invalid!");
                return;
            }

            // do client/server

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.GET_PURCHASE);
                output.println(purchase.mPurchaseID);

                String productID = input.nextLine();

                if (productID.equals("null")) {
                    JOptionPane.showMessageDialog(null, "Purchase does NOT exist!");
                    return;
                }

                purchase.mProductID = Integer.parseInt(productID);
                txtProductID.setText(productID);

                purchase.mCustomerID = Integer.parseInt(input.nextLine());
                txtCustomerID.setText(Integer.toString(purchase.mCustomerID));

                purchase.mQuantity = Double.parseDouble(input.nextLine());

                purchase.mCost = Double.parseDouble(input.nextLine());
                labCost.setText("Cost: $" + Double.toString(purchase.mCost));

                purchase.mTax = Double.parseDouble(input.nextLine());
                labTax.setText("Tax: $" + Double.toString(purchase.mTax));

                purchase.mTotal = Double.parseDouble(input.nextLine());
                labTotalCost.setText("Total Cost: " + Double.toString(purchase.mTotal));

                purchase.mDate = input.nextLine();
                labDate.setText("Date of purchase: " + purchase.mDate);

                String customerName = input.nextLine();
                if (!customerName.equals("null")) {
                    customer.mName = customerName;
                    labCustomerName.setText("Customer Name: " + customer.mName);
                    customer.mAddress = input.nextLine();
                    customer.mPhone = input.nextLine();
                    customer.mPayInfo = input.nextLine();
                }
                else {
                    labCustomerName.setText("Customer Name: ");
                    customer.mName = "";
                    customer.mAddress = "";
                    customer.mPhone = "";
                    customer.mPayInfo = "";
                }

                String productName = input.nextLine();
                if (!productName.equals("null")) {
                    product.mName = productName;
                    labProductName.setText("Product Name: " + product.mName);

                    String price = input.nextLine();
                    if (!price.equals("")) {
                        product.mPrice = Double.parseDouble(price);
                        purchase.mPrice = product.mPrice;
                        labPrice.setText("Product Price: " + product.mPrice);
                    }
                    else {
                        labPrice.setText("Product Price: ");
                    }
                    String quantity = input.nextLine();
                    if (!quantity.equals("")) {
                        product.mQuantity = Double.parseDouble(quantity);
                    }
                    String taxRate = input.nextLine();
                    if (!taxRate.equals("")) {
                        product.mTaxRate = Double.parseDouble(taxRate);
                    }
                    product.mVendor = input.nextLine();
                    product.mDescription = input.nextLine();
                }
                else {
                    product.mName = "";
                    product.mPrice = 0;
                    product.mQuantity = 0;
                    product.mTaxRate = 0;
                    product.mVendor = "";
                    product.mDescription = "";
                    labProductName.setText("Product Name: ");
                    labPrice.setText("Product Price: ");
                }

                txtQuantity.setText(Double.toString(purchase.mQuantity));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String purchaseID = txtPurchaseID.getText();

            purchase.mDate = Calendar.getInstance().getTime().toString();
            labDate.setText("Date of purchase: " + purchase.mDate);

            if (purchaseID.length() == 0) {
                JOptionPane.showMessageDialog(null, "PurchaseID cannot be null!");
                return;
            }

            try {
                purchase.mPurchaseID = Integer.parseInt(purchaseID);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "PurchaseID is invalid!");
                return;
            }

            String productID = txtProductID.getText();

            if (productID.length() == 0) {
                JOptionPane.showMessageDialog(null, "ProductID cannot be null!");
                return;
            }

            try {
                purchase.mProductID = Integer.parseInt(productID);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ProductID is invalid!");
                return;
            }

            if (Objects.equals(product.mName, null)) {
                JOptionPane.showMessageDialog(null,
                        "Error: No product with id = " + purchase.mProductID + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            String customerID = txtCustomerID.getText();

            if (customerID.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }

            try {
                purchase.mCustomerID = Integer.parseInt(customerID);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "CustomerID is invalid!");
                return;
            }

            if (Objects.equals(customer.mName, null)) {
                JOptionPane.showMessageDialog(null,
                        "Error: No customer with id = " + purchase.mCustomerID + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);

                return;
            }

            String quant = txtQuantity.getText();
            try {
                purchase.mQuantity = Double.parseDouble(quant);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantity is invalid!");
                return;
            }

            if (purchase.mQuantity > product.mQuantity) {
                JOptionPane.showMessageDialog(null,
                        "Not enough available products!", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            if (purchase.mQuantity <= 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: quantity must be greater than 0!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // all product info is ready! Send to Server!

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.PUT_PURCHASE);
                output.println(purchase.mPurchaseID);
                output.println(purchase.mProductID);
                output.println(purchase.mCustomerID);
                output.println(purchase.mQuantity);
                output.println(purchase.mCost);
                output.println(purchase.mTax);
                output.println(purchase.mTotal);
                output.println(purchase.mDate);

                TXTReceiptBuilder receipt = new TXTReceiptBuilder(purchase, product, customer);

                if (input.nextInt() == MessageModel.OPERATION_OK) {
                    JOptionPane.showMessageDialog(null, "Purchase saved successfully!");
                    JOptionPane.showMessageDialog(null, receipt.toString());
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to save purchase!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class ProductIDFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent focusEvent) {

        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            process();
        }

        private void process() {
            String s = txtProductID.getText();

            if (s.length() == 0) {
                labProductName.setText("Product Name: [not specified!]");
                return;
            }

            System.out.println("ProductID = " + s);

            try {
                purchase.mProductID = Integer.parseInt(s);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error: Invalid ProductID", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                product = new ProductModel();
                return;
            }

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.GET_PRODUCT);
                output.println(purchase.mProductID);

                String productName = input.nextLine();
                if (!productName.equals("null")) {
                    product.mProductID = purchase.mProductID;
                    product.mName = productName;
                    product.mPrice = Double.parseDouble(input.nextLine());
                    product.mTaxRate = Double.parseDouble(input.nextLine());
                    product.mQuantity = Double.parseDouble(input.nextLine());
                    product.mVendor = input.nextLine();
                    product.mDescription = input.nextLine();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (product.mProductID != purchase.mProductID) {
                JOptionPane.showMessageDialog(null,
                        "Error: No product with id = " + purchase.mProductID + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                labProductName.setText("Product Name: ");
                product = new ProductModel();

                return;
            }

            labProductName.setText("Product Name: " + product.mName);
            purchase.mPrice = product.mPrice;
            labPrice.setText("Product Price: " + product.mPrice);

            purchase.mCost = purchase.mQuantity * product.mPrice;

            BigDecimal costBD = BigDecimal.valueOf(purchase.mCost);
            costBD = costBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mCost = costBD.doubleValue();

            purchase.mTax = purchase.mCost * product.mTaxRate;

            BigDecimal taxBD = BigDecimal.valueOf(purchase.mTax);
            taxBD = taxBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mTax = taxBD.doubleValue();

            purchase.mTotal = purchase.mCost + purchase.mTax;

            BigDecimal totalBD = BigDecimal.valueOf(purchase.mTotal);
            totalBD = totalBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mTotal = totalBD.doubleValue();

            labCost.setText("Cost: $" + String.format("%8.2f", purchase.mCost).trim());
            labTax.setText("Tax: $" + String.format("%8.2f", purchase.mTax).trim());
            labTotalCost.setText("Total Cost: $" + String.format("%8.2f", purchase.mTotal).trim());

        }

    }

    private class CustomerIDFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent focusEvent) {

        }

        @Override
        public void focusLost(FocusEvent focusEvent) {
            process();
        }

        private void process() {
            String s = txtCustomerID.getText();

            if (s.length() == 0) {
                labCustomerName.setText("Customer Name: [not specified!]");
                return;
            }

            System.out.println("CustomerID = " + s);

            try {
                purchase.mCustomerID = Integer.parseInt(s);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error: Invalid CustomerID", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                customer = new CustomerModel();
                return;
            }

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.GET_CUSTOMER);
                output.println(purchase.mCustomerID);

                String customerName = input.nextLine();
                if (!customerName.equals("null")) {
                    customer.mCustomerID = purchase.mCustomerID;
                    customer.mName = customerName;
                    customer.mAddress = input.nextLine();
                    customer.mPhone = input.nextLine();
                    customer.mPayInfo = input.nextLine();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            if (purchase.mCustomerID != customer.mCustomerID) {
                JOptionPane.showMessageDialog(null,
                        "Error: No customer with id = " + purchase.mCustomerID + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                labCustomerName.setText("Customer Name: ");
                customer = new CustomerModel();

                return;
            }

            labCustomerName.setText("Customer Name: " + customer.mName);

        }

    }

    private class QuantityChangeListener implements DocumentListener {
        public void changedUpdate(DocumentEvent e) {
            process();
        }

        public void removeUpdate(DocumentEvent e) {
            process();
        }

        public void insertUpdate(DocumentEvent e) {
            process();
        }

        private void process() {
            String s = txtQuantity.getText();

            if (s.length() == 0) {

                return;
            }

            System.out.println("Quantity = " + s);

            try {
                purchase.mQuantity = Double.parseDouble(s);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error: Please enter a valid quantity", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (purchase.mQuantity < 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: Please enter a valid quantity", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            purchase.mCost = purchase.mQuantity * product.mPrice;

            BigDecimal costBD = BigDecimal.valueOf(purchase.mCost);
            costBD = costBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mCost = costBD.doubleValue();

            purchase.mTax = purchase.mCost * product.mTaxRate;

            BigDecimal taxBD = BigDecimal.valueOf(purchase.mTax);
            taxBD = taxBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mTax = taxBD.doubleValue();

            purchase.mTotal = purchase.mCost + purchase.mTax;

            BigDecimal totalBD = BigDecimal.valueOf(purchase.mTotal);
            totalBD = totalBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mTotal = totalBD.doubleValue();

            labCost.setText("Cost: $" + String.format("%8.2f", purchase.mCost).trim());
            labTax.setText("Tax: $" + String.format("%8.2f", purchase.mTax).trim());
            labTotalCost.setText("Total Cost: $" + String.format("%8.2f", purchase.mTotal).trim());

        }
    }
}
