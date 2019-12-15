//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ManageAccountUI {
    public JFrame view;

    public JButton btnChangeUser = new JButton("Change Username");
    public JButton btnChangePass = new JButton("Change Password");
    public JButton btnChangeName = new JButton("Change Name");

    Socket link;
    Scanner input;
    PrintWriter output;
    int mAccessToken;
    UserModel mUser;

    public ManageAccountUI(UserModel user) {
        this.view = new JFrame();
        this.mUser = user;

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Manage Account");
        view.setSize(500, 300);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout());
        JLabel title = new JLabel("Manage Account");

        title.setFont(title.getFont().deriveFont(24.0f));
        titlePanel.add(title);

        view.getContentPane().add(titlePanel);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnChangeUser);
        panelButtons.add(btnChangePass);
        panelButtons.add(btnChangeName);

        view.getContentPane().add(panelButtons);

        btnChangeUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateUserUI update = new UpdateUserUI(0, mUser, mUser.mUsername);
                update.run();
            }
        });

        btnChangePass.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateUserUI update = new UpdateUserUI(1, mUser, mUser.mUsername);
                update.run();
            }
        });

        btnChangeName.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                UpdateUserUI update = new UpdateUserUI(2, mUser, mUser.mUsername);
                update.run();
            }
        });
    }

    public void run() {
        view.setVisible(true);
    }
}