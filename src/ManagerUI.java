//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ManagerUI {
    public JFrame view;

    public JButton btnAddProduct = new JButton("Add Product");
    public JButton btnUpdateProduct = new JButton("Update Product");
    public JButton btnViewSalesReport = new JButton("View Sales Report");

    public JButton btnManageAccount = new JButton("Manage Account");
    public JButton btnLogout = new JButton("Logout");

    public JLabel loginLabel = new JLabel("Logged in as - Manager");

    Socket link;
    Scanner input;
    PrintWriter output;
    int mAccessToken;
    UserModel mUser;

    public ManagerUI(int accessToken, UserModel user) {
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
        panelButtons.add(btnAddProduct);
        panelButtons.add(btnUpdateProduct);
        panelButtons.add(btnViewSalesReport);

        JPanel accPanel = new JPanel(new FlowLayout());
        accPanel.add(btnManageAccount);
        accPanel.add(btnLogout);

        view.getContentPane().add(panelButtons);

        view.getContentPane().add(accPanel);

        btnAddProduct.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddProductUI addProduct = new AddProductUI();
                addProduct.run();
            }
        });

        btnUpdateProduct.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateProductUI updateProduct = new UpdateProductUI();
                updateProduct.run();
            }
        });

        btnViewSalesReport.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try {
                    Socket link = new Socket("localhost", 1000);
                    Scanner input = new Scanner(link.getInputStream());
                    PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                    output.println(MessageModel.PURCHASE_REPORT);

                    int size = Integer.parseInt(input.nextLine());

                    String[][] sales = new String[size][4];

                    for (int i = 0; i < size; i++) {
                        sales[i][0] = input.nextLine();
                        sales[i][1] = input.nextLine();
                        sales[i][2] = input.nextLine();
                        sales[i][3] = input.nextLine();
                    }

                    String[] columns = {"ProductID", "Product Name", "Quantity Sold", "Total Cost"};
                    JTable salesTable = new JTable(sales, columns);

                    JScrollPane scrollPane = new JScrollPane(salesTable);
                    JPanel panel = new JPanel();
                    panel.add(scrollPane);
                    JFrame f = new JFrame();
                    f.getContentPane().add(panel, BorderLayout.CENTER);
                    f.setSize(550,500);
                    f.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
