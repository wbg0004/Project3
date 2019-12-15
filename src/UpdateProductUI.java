import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UpdateProductUI {

    public JFrame view;

    public JButton btnLoad = new JButton("Load Product");
    public JButton btnSave = new JButton("Save Product");

    public JTextField txtProductID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtPrice = new JTextField(20);
    public JTextField txtTaxRate = new JTextField(20);
    public JTextField txtQuantity = new JTextField(20);
    public JTextField txtVendor = new JTextField(20);
    public JTextField txtDescription = new JTextField(20);


    public UpdateProductUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Update Product Information");
        view.setSize(400, 450);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnLoad);
        panelButtons.add(btnSave);
        view.getContentPane().add(panelButtons);

        String[] labels = {"Product ID ", "Name ", "Price ", "Tax Rate", "Quantity ", "Vendor ", "Description "};
        JTextField[] textFields = {txtProductID, txtName, txtPrice, txtTaxRate, txtQuantity, txtVendor, txtDescription};

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JTextField field = textFields[i];
            l.setLabelFor(field);
            p.add(field);
            view.getContentPane().add(p);
        }


        btnLoad.addActionListener(new LoadButtonListener());

        btnSave.addActionListener(new SaveButtonListener());

    }

    public void run() {
        view.setVisible(true);
    }

    class LoadButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ProductModel product = new ProductModel();
            String id = txtProductID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "ProductID cannot be null!");
                return;
            }

            try {
                product.mProductID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ProductID is invalid!");
                return;
            }

            // do client/server

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.GET_PRODUCT);
                output.println(product.mProductID);

                product.mName = input.nextLine();

                if (product.mName.equals("null")) {
                    JOptionPane.showMessageDialog(null, "Product does NOT exist!");
                    return;
                }

                txtName.setText(product.mName);

                product.mPrice = input.nextDouble();
                txtPrice.setText(Double.toString(product.mPrice));

                product.mTaxRate = input.nextDouble();
                txtTaxRate.setText(Double.toString(product.mTaxRate));

                product.mQuantity = input.nextDouble();
                txtQuantity.setText(Double.toString(product.mQuantity));
                input.nextLine();

                product.mVendor = input.nextLine();
                txtVendor.setText(product.mVendor);

                product.mDescription = input.nextLine();
                txtDescription.setText(product.mDescription);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            ProductModel product = new ProductModel();
            String id = txtProductID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "ProductID cannot be null!");
                return;
            }

            try {
                product.mProductID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ProductID is invalid!");
                return;
            }

            String name = txtName.getText();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(null, "Product name cannot be empty!");
                return;
            }

            product.mName = name;

            String price = txtPrice.getText();
            try {
                product.mPrice = Double.parseDouble(price);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Price is invalid!");
                return;
            }

            String taxRate = txtTaxRate.getText();
            try {
                product.mTaxRate = Double.parseDouble(taxRate);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Tax Rate is invalid!");
                return;
            }

            String quant = txtQuantity.getText();
            try {
                product.mQuantity = Double.parseDouble(quant);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Quantity is invalid!");
                return;
            }

            String vendor = txtVendor.getText();
            product.mVendor = vendor;

            String description = txtDescription.getText();
            product.mDescription = description;

            // all product info is ready! Send to Server!

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.PUT_PRODUCT);
                output.println(product.mProductID);
                output.println(product.mName);
                output.println(product.mPrice);
                output.println(product.mTaxRate);
                output.println(product.mQuantity);
                output.println(product.mVendor);
                output.println(product.mDescription);

                if (input.nextInt() == (MessageModel.OPERATION_OK)) {
                    JOptionPane.showMessageDialog(null, "Product saved successfully!");
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to save product!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
