//package edu.auburn;

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
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Scanner;

public class AddPurchaseUI {

    public JFrame view;

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");

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

    public AddPurchaseUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Add Purchase");
        view.setSize(400, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

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

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnAdd);
        panelButtons.add(btnCancel);
        view.getContentPane().add(panelButtons);

        btnAdd.addActionListener(new AddButtonListener());

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                view.dispose();
            }
        });

        txtProductID.addFocusListener(new ProductIDFocusListener());

        txtCustomerID.addFocusListener(new CustomerIDFocusListener());

        txtQuantity.getDocument().addDocumentListener(new QuantityChangeListener());

    }
    //////////////////////////////////////////////////////////////////////////////////////////
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

            if (product.mProductID == 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: No product with id = " + purchase.mProductID + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                labProductName.setText("Product Name: ");

                return;
            }

            labProductName.setText("Product Name: " + product.mName);
            purchase.mPrice = product.mPrice;
            labPrice.setText("Product Price: " + product.mPrice);

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

            if (customer.mCustomerID == 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: No customer with id = " + purchase.mCustomerID + " in store!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                labCustomerName.setText("Customer Name: ");

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
                //labCustomerName.setText("Customer Name: [not specified!]");
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

            if (purchase.mQuantity <= 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: Please enter a valid quantity", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (purchase.mQuantity > product.mQuantity) {
                JOptionPane.showMessageDialog(null,
                        "Not enough available products!", "Information",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            purchase.mCost = purchase.mQuantity * product.mPrice;

            BigDecimal costBD = BigDecimal.valueOf(purchase.mCost);
            costBD = costBD.setScale(2, RoundingMode.HALF_UP);
            purchase.mCost = costBD.doubleValue();

            purchase.mTax = purchase.mCost * 0.09;

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
    //////////////////////////////////////////////////////////////////////////////////////////
    public void run() {
        purchase = new PurchaseModel();
        purchase.mDate = Calendar.getInstance().getTime().toString();
        labDate.setText("Date of purchase: " + purchase.mDate);
        view.setVisible(true);
    }

    class AddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {

            String id = txtPurchaseID.getText();
            String productID = txtProductID.getText();
            String customerID = txtCustomerID.getText();
            String quantity = txtQuantity.getText();
            double quan;

            if (productID.length() == 0) {
                JOptionPane.showMessageDialog(null, "Product ID cannot be null!");
                return;
            }

            if (product.mProductID == 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: Invalid Product ID!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (customerID.length() == 0) {
                JOptionPane.showMessageDialog(null, "Customer ID cannot be null!");
                return;
            }

            if (customer.mCustomerID == 0) {
                JOptionPane.showMessageDialog(null,
                        "Error: Invalid Customer ID!", "Error Message",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (quantity.length() == 0) {
                JOptionPane.showMessageDialog(null, "Quantity cannot be null!");
                return;
            }

            try {
                quan = Double.parseDouble(quantity);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantity is invalid!");
                return;
            }
            if (quan <= 0) {
                JOptionPane.showMessageDialog(null, "Quantity is invalid!");
                return;
            }

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "Purchase ID cannot be null!");
                return;
            }

            try {
                purchase.mPurchaseID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Purchase ID is invalid!");
                return;
            }

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.ADD_PURCHASE);
                output.println(purchase.mPurchaseID);
                output.println(purchase.mProductID);
                output.println(purchase.mCustomerID);
                output.println(purchase.mQuantity);
                output.println(purchase.mCost);
                output.println(purchase.mTax);
                output.println(purchase.mTotal);
                output.println(purchase.mDate);

                TXTReceiptBuilder receipt = new TXTReceiptBuilder(purchase, product, customer);

                int result = input.nextInt();
                if (result == MessageModel.OPERATION_OK) {
                    JOptionPane.showMessageDialog(null, "Purchase saved successfully!");
                    JOptionPane.showMessageDialog(null, receipt.toString());
                    view.dispose();
                }
                else if (result == MessageModel.DUPLICATE_PURCHASE) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to add purchase! Duplicate purchase ID!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to add purchase!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}