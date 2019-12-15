import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class LoginUI {
    public JFrame view;

    public JButton btnLogin = new JButton("Login");
    public JButton btnLogout = new JButton("Logout");

    public JTextField txtUsername = new JTextField(20);
    public JTextField txtPassword = new JPasswordField(20);

    Socket link;
    Scanner input;
    PrintWriter output;

    int accessToken;

    public LoginUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Login");
        view.setSize(400, 200);

        Container pane = view.getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));

        JPanel line = new JPanel();
        line.add(new JLabel("Username"));
        line.add(txtUsername);
        pane.add(line);

        line = new JPanel();
        line.add(new JLabel("Password"));
        line.add(txtPassword);
        pane.add(line);

        btnLogin.setSize(20, 10);
        btnLogout.setSize(20, 10);

        pane.add(btnLogin);
        pane.add(btnLogout);

        btnLogout.setVisible(false);
        btnLogin.addActionListener(new LoginActionListener());

        btnLogout.addActionListener(new LogoutActionListener());

    }

    public static void main(String[] args) {
        int port = 1000;
        LoginUI ui = new LoginUI();
        ui.view.setVisible(true);

    }

    private class LogoutActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            try {
                link = new Socket("localhost", 1000);
                input = new Scanner(link.getInputStream());
                output = new PrintWriter(link.getOutputStream(), true);

                output.println("LOGOUT");
                output.println(accessToken);
                int res = input.nextInt();
                System.out.println("Sent LOGOUT " + accessToken + " received " + res);

                if (res == 0)
                    JOptionPane.showMessageDialog(null, "Invalid token for logout!");
                else
                    JOptionPane.showMessageDialog(null, "Logout successfully = " + accessToken);

                    accessToken = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private class LoginActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            if (username.length() == 0 || password.length() == 0) {
                JOptionPane.showMessageDialog(null, "Username or password cannot be null!");
                return;
            }
            try {
                try {
                    link = new Socket("localhost", 1000);
                    input = new Scanner(link.getInputStream());
                    output = new PrintWriter(link.getOutputStream(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                output.println(MessageModel.LOGIN);
                output.println(username);
                output.println(password);
                accessToken = input.nextInt();
                System.out.println("Sent " + username + "/" + password + " received " + accessToken);

                if (accessToken != MessageModel.LOGIN_SUCCESS) {
                    JOptionPane.showMessageDialog(null, "Invalid username or password! Access denied!");
                }
                else {
                    input.nextLine();

                    String mUsername = input.nextLine();
                    String mPassword = input.nextLine();
                    String fullname = input.nextLine();
                    int userType = Integer.parseInt(input.nextLine());
                    int customerID;
                    try {
                        customerID = Integer.parseInt(input.nextLine());
                    }
                    catch (Exception e) {
                        customerID = 0;
                    }

                    int key = Integer.parseInt(input.nextLine());
                    UserModel user = new UserModel(mUsername, mPassword, fullname, userType, customerID);

                    JOptionPane.showMessageDialog(null, "Access granted with access token = " + key);

                    if (user.mUserType == 0) {
                        AdminUI aUI = new AdminUI(key, user);
                        aUI.run();
                    }
                    if (user.mUserType == 1) {
                        ManagerUI mUI = new ManagerUI(key, user);
                        mUI.run();
                    }
                    if (user.mUserType == 2) {
                        CashierUI cUI = new CashierUI(key, user);
                        cUI.run();
                    }
                    else if (user.mUserType == 3) {
                        CustomerUI cUI = new CustomerUI(key, user);
                        cUI.run();
                    }
                    view.dispose();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
