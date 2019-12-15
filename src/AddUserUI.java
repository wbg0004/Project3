//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AddUserUI {

    public JFrame view;

    public JButton btnAdd = new JButton("Add");
    public JButton btnCancel = new JButton("Cancel");

    public JTextField txtUserName = new JTextField(20);
    public JTextField txtPassword = new JPasswordField(20);
    public JTextField txtFullName = new JTextField(20);
    public JTextField txtCustomerID = new JTextField(20);

    String[] userTypes = {"Admin","Manager", "Cashier","Customer"};
    final JComboBox<String> dropUserTypes = new JComboBox<String>(userTypes);


    public AddUserUI()   {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Add User");
        view.setSize(350, 400);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        String[] labels = {"Username ", "Password ", "Full Name ", "Customer ID "};
        JTextField[] textFields = {txtUserName, txtPassword, txtFullName, txtCustomerID};

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JTextField field = textFields[i];
            l.setLabelFor(field);
            p.add(field);
            view.getContentPane().add(p);
        }
        JLabel l = new JLabel("User Type ");
        JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
        p.add(l);
        l.setLabelFor(dropUserTypes);
        p.add(dropUserTypes);
        view.getContentPane().add(p);


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
            String username = txtUserName.getText();

            if (username.length() == 0) {
                JOptionPane.showMessageDialog(null, "Username cannot be null!");
                return;
            }

            String password = txtPassword.getText();
            if (password.length() == 0) {
                JOptionPane.showMessageDialog(null, "Password cannot be null!");
                return;
            }

            String fullName = txtFullName.getText();
            if (fullName.length() == 0) {
                JOptionPane.showMessageDialog(null, "Name cannot be null!");
                return;
            }

            int userType = dropUserTypes.getSelectedIndex();

            String customerID = txtCustomerID.getText();
            int cId;

            if (customerID.length() == 0 && userType == 3) {
                JOptionPane.showMessageDialog(null, "Customer ID cannot be null for a Customer!");
                return;
            }
            else if (customerID.length() > 0) {
                JOptionPane.showMessageDialog(null, "Customer ID must be null for a non-Customer!");
                return;
            }

            if (userType != 3) {
                cId = 0;
            }
            else {
                cId = Integer.parseInt(customerID);
            }

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.ADD_USER);
                output.println(username);
                output.println(password);
                output.println(fullName);
                output.println(userType);
                output.println(cId);

                int result = input.nextInt();
                if (result == (MessageModel.OPERATION_OK)) {
                    JOptionPane.showMessageDialog(null, "User added successfully!");
                    view.dispose();
                }
                else if (result == MessageModel.DUPLICATE_USER) {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to add user! Duplicate user ID!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to add user!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}