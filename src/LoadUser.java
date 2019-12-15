//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LoadUser {
    public JFrame view;

    public JButton btnLoadUser = new JButton("Load");

    public JTextField txtUsername = new JTextField(20);

    Socket link;
    Scanner input;
    PrintWriter output;
    String mLoggedUser;

    public LoadUser(String loggedUser) {
        this.view = new JFrame();
        this.mLoggedUser = loggedUser;

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Load User");
        view.setSize(350, 150);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JLabel l = new JLabel("Username ");
        JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
        p.add(l);
        JTextField field = txtUsername;
        l.setLabelFor(field);
        p.add(field);
        view.getContentPane().add(p);

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnLoadUser);

        view.getContentPane().add(panelButtons);

        btnLoadUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String username = txtUsername.getText();
                UserModel user = null;

                if (username.length() == 0) {
                    JOptionPane.showMessageDialog(null, "Username cannot be null!");
                    return;
                }

                // do client/server

                try {
                    Socket link = new Socket("localhost", 1000);
                    Scanner input = new Scanner(link.getInputStream());
                    PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                    output.println(MessageModel.GET_USER);
                    output.println(username);

                    String password = input.nextLine();

                    if (password.equals("null")) {
                        JOptionPane.showMessageDialog(null, "User does NOT exist!");
                        return;
                    }

                    String fullName = input.nextLine();

                    int userType = Integer.parseInt(input.nextLine());

                    int customerID = 0;
                    if (userType == 3) {
                        customerID = Integer.parseInt(input.nextLine());
                    }

                    user = new UserModel(username, password, fullName, userType, customerID);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }


                view.dispose();
                UpdateUserUI update = new UpdateUserUI(4, user, mLoggedUser);
                update.run();
            }
        });
    }

    public void run() {
        view.setVisible(true);
    }
}