//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AddCustomerUI {

    public JFrame view;

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");

    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtAddress = new JTextField(20);
    public JTextField txtPhone = new JTextField(20);
    public JTextField txtPaymentInfo = new JTextField(20);


    public AddCustomerUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Add Customer");
        view.setSize(400, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        String[] labels = {"Customer ID ", "Name ", "Address ", "Phone Number ", "Payment Info "};
        JTextField[] textFields = {txtCustomerID, txtName, txtAddress, txtPhone, txtPaymentInfo};

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JTextField field = textFields[i];
            l.setLabelFor(field);
            p.add(field);
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

    }

    public void run() {
        view.setVisible(true);
    }

    class AddButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CustomerModel customer = new CustomerModel();

            String id = txtCustomerID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "Customer ID cannot be null!");
                return;
            }

            try {
                customer.mCustomerID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Customer ID is invalid!");
                return;
            }

            String name = txtName.getText();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(null, "Customer name cannot be empty!");
                return;
            }

            customer.mName = name;

            String address = txtAddress.getText();
            if (address.length() > 0) {
                customer.mAddress = address;
            }
            else {
                customer.mAddress = "";
            }

            String phone = txtPhone.getText();
            if (phone.length() > 0) {
                customer.mPhone = phone;
            }
            else {
                customer.mPhone = "";
            }

            String payInfo = txtPaymentInfo.getText();
            if (payInfo.length() > 0) {
                customer.mPayInfo = payInfo;
            }
            else {
                customer.mPayInfo = "";
            }

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.ADD_CUSTOMER);
                output.println(customer.mCustomerID);
                output.println(customer.mName);
                output.println(customer.mAddress);
                output.println(customer.mPhone);
                output.println(customer.mPayInfo);

                int result = input.nextInt();
                if (result == MessageModel.OPERATION_OK) {
                    JOptionPane.showMessageDialog(null, "Customer saved successfully!");
                    view.dispose();
                }
                else if (result == MessageModel.DUPLICATE_CUSTOMER) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to add customer! Duplicate customer ID!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to add customer!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}