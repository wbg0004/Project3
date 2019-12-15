//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class AdminUI {
    public JFrame view;

    public JButton btnAddUser = new JButton("Add User");
    public JButton btnUpdateUser = new JButton("Update User");

    public JButton btnManageAccount = new JButton("Manage Account");
    public JButton btnLogout = new JButton("Logout");

    public JLabel loginLabel = new JLabel("Logged in as - Admin");

    Socket link;
    Scanner input;
    PrintWriter output;
    int mAccessToken;
    UserModel mUser;

    public AdminUI(int accessToken, UserModel user) {
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
        panelButtons.add(btnAddUser);
        panelButtons.add(btnUpdateUser);

        JPanel accPanel = new JPanel(new FlowLayout());
        accPanel.add(btnManageAccount);
        accPanel.add(btnLogout);

        view.getContentPane().add(panelButtons);

        view.getContentPane().add(accPanel);

        btnUpdateUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                LoadUser load = new LoadUser(mUser.mUsername);
                load.run();
            }
        });

        btnAddUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                AddUserUI addUser = new AddUserUI();
                addUser.run();
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
