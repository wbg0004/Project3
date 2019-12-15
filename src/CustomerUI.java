//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CustomerUI {
    public JFrame view;

    public JButton btnViewPurchaseHistory = new JButton("View Purchase History");
    public JButton btnSearchProduct = new JButton("Search for Product");
    public JButton btnAddPurchase = new JButton("Add Purchase");

    public JButton btnManageAccount = new JButton("Manage Account");
    public JButton btnLogout = new JButton("Logout");

    public JLabel loginLabel = new JLabel("Logged in as - Customer");

    Socket link;
    Scanner input;
    PrintWriter output;
    int mAccessToken;
    UserModel mUser;

    public CustomerUI(int accessToken, UserModel user) {
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
        panelButtons.add(btnViewPurchaseHistory);
        panelButtons.add(btnSearchProduct);
        panelButtons.add(btnAddPurchase);

        JPanel accPanel = new JPanel(new FlowLayout());
        accPanel.add(btnManageAccount);
        accPanel.add(btnLogout);

        view.getContentPane().add(panelButtons);

        view.getContentPane().add(accPanel);

        btnViewPurchaseHistory.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Socket link = new Socket("localhost", 1000);
                    Scanner input = new Scanner(link.getInputStream());
                    PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                    output.println(MessageModel.PURCHASE_HISTORY);

                    output.println(mUser.mCustomerID);

                    int size = Integer.parseInt(input.nextLine());

                    String[][] purchases = new String[size][7];

                    for (int i = 0; i < size; i++) {
                        purchases[i][0] = input.nextLine();
                        purchases[i][1] = input.nextLine();
                        purchases[i][2] = input.nextLine();
                        purchases[i][3] = input.nextLine();
                        purchases[i][4] = input.nextLine();
                        purchases[i][5] = input.nextLine();
                        purchases[i][6] = input.nextLine();
                    }

                    String[] columns = {"Product Name", "Purchase ID", "Quantity", "Cost", "Tax", "Total", "Date"};
                    JTable salesTable = new JTable(purchases, columns);

                    JScrollPane scrollPane = new JScrollPane(salesTable);
                    JPanel panel = new JPanel();
                    panel.add(scrollPane);
                    JFrame f = new JFrame();
                    f.getContentPane().add(panel, BorderLayout.CENTER);
                    f.setSize(600,500);
                    f.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnSearchProduct.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                SearchProduct search = new SearchProduct();
                search.run();
            }
        });

        btnAddPurchase.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddPurchaseUI addPurchase = new AddPurchaseUI();
                addPurchase.run();
            }
        });

        btnManageAccount.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                ManageAccountUI manage = new ManageAccountUI(mUser);
                manage.run();
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
