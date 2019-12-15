//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CashierUI {
    public JFrame view;

    public JButton btnAddCustomer = new JButton("Add Customer");
    public JButton btnUpdateCustomer = new JButton("Update Customer");
    public JButton btnAddPurchase = new JButton("Add Purchase");
    public JButton btnUpdatePurchase = new JButton("Update Purchase");

    public JButton btnManageAccount = new JButton("Manage Account");
    public JButton btnLogout = new JButton("Logout");

    public JLabel loginLabel = new JLabel("Logged in as - Cashier");

    Socket link;
    Scanner input;
    PrintWriter output;
    int mAccessToken;
    UserModel mUser;

    public CashierUI(int accessToken, UserModel user) {
        this.view = new JFrame();
        this.mAccessToken = accessToken;
        this.mUser = user;

        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setTitle("Store Management System");
        view.setSize(1000, 600);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout());
        JLabel title = new JLabel("Store Management System");

        title.setFont (title.getFont ().deriveFont (24.0f));
        titlePanel.add(title);

        view.getContentPane().add(titlePanel);

        JPanel logPanel = new JPanel(new FlowLayout());
        logPanel.add(loginLabel);
        view.add(logPanel);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnAddCustomer);
        panelButtons.add(btnUpdateCustomer);
        panelButtons.add(btnAddPurchase);
        panelButtons.add(btnUpdatePurchase);

        JPanel accPanel = new JPanel(new FlowLayout());
        accPanel.add(btnManageAccount);
        accPanel.add(btnLogout);

        view.getContentPane().add(panelButtons);

        view.getContentPane().add(accPanel);

        btnUpdateCustomer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateCustomerUI updateCustomer = new UpdateCustomerUI();
                updateCustomer.run();
            }
        });

        btnAddCustomer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddCustomerUI addCustomer = new AddCustomerUI();
                addCustomer.run();
            }
        });

        btnManageAccount.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ManageAccountUI manage = new ManageAccountUI(mUser);
                manage.run();
            }
        });

        btnAddPurchase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddPurchaseUI addPurchase = new AddPurchaseUI();
                addPurchase.run();
            }
        });

        btnUpdatePurchase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdatePurchaseUI updatePurchase = new UpdatePurchaseUI();
                updatePurchase.run();
            }
        });

        btnLogout.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    link = new Socket("localhost", 1000);
                    input = new Scanner(link.getInputStream());
                    output = new PrintWriter(link.getOutputStream(), true);

                    output.println(MessageModel.LOGOUT);
                    output.println(mAccessToken);
                    int res = input.nextInt();
                    System.out.println("Sent LOGOUT " + mAccessToken + " received " + res);

                    if (res != MessageModel.LOGOUT_SUCCESS)
                        JOptionPane.showMessageDialog(null, "Invalid token for logout!");
                    else {
                        JOptionPane.showMessageDialog(null, "Logout successfully = " + accessToken);
                        view.setVisible(false);
                    }
                    mAccessToken = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void run() {
        view.setVisible(true);
    }

}
