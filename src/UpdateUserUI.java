import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UpdateUserUI {

    public JFrame view;

    public JButton btnSave = new JButton("Save");

    public JTextField txtUserName = new JTextField(20);
    public JTextField txtPassword = new JPasswordField(20);
    public JTextField txtFullName = new JTextField(20);

    String[] userTypes = {"Admin","Manager", "Cashier","Customer"};
    final JComboBox<String> dropUserTypes = new JComboBox<String>(userTypes);

    public JTextField txtOldPass = new JPasswordField(20);

    UserModel mUser;
    int mContext;
    String mLoggedUser;

    public UpdateUserUI(int context, UserModel user, String loggedUser) {
        this.view = new JFrame();
        this.mUser = user;
        this.mContext = context;
        this.mLoggedUser = loggedUser;

        dropUserTypes.setSelectedIndex(mUser.mUserType);

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Update Account Information");
        view.setSize(400, 250);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnSave);

        String[] labels = {"New Username ", "New Password ", "New Name ", "Current Password "};
        JTextField[] textFields = {txtUserName, txtPassword, txtFullName, txtOldPass};

        if (mContext < 4) {
            JLabel l = new JLabel(labels[mContext]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JTextField field = textFields[mContext];
            l.setLabelFor(field);
            p.add(field);
            view.getContentPane().add(p);
        }
        else {
            JLabel l = new JLabel("User Type");
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JComboBox<String> field = dropUserTypes;
            field.setSelectedIndex(mUser.mUserType);
            l.setLabelFor(field);
            p.add(field);
            view.getContentPane().add(p);
        }
        JLabel l = new JLabel(labels[3]);
        JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
        p.add(l);
        JTextField field = textFields[3];
        l.setLabelFor(field);
        p.add(field);
        view.getContentPane().add(p);

        view.getContentPane().add(panelButtons);

        btnSave.addActionListener(new SaveButtonListener());

    }

    public void run() {
        view.setVisible(true);
    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            UserModel user = new UserModel(mUser.mUsername, mUser.mPassword, mUser.mFullname, mUser.mUserType, mUser.mCustomerID);
            String oldUserName = mUser.mUsername;

            String oldPass = txtOldPass.getText();

            if (oldPass.length() == 0) {
                JOptionPane.showMessageDialog(null, "Current password should not be null!");
                return;
            }

            if (mContext == 0) {
                user.mUsername = txtUserName.getText();
                if (user.mUsername.length() == 0) {
                    JOptionPane.showMessageDialog(null, "New Username cannot be null!");
                    return;
                }
            }

            if (mContext == 1) {
                user.mPassword = txtPassword.getText();
                if (user.mPassword.length() == 0) {
                    JOptionPane.showMessageDialog(null, "New Password cannot be null!");
                    return;
                }
            }

            if (mContext == 2) {
                user.mFullname = txtFullName.getText();
                if (user.mFullname.length() == 0) {
                    JOptionPane.showMessageDialog(null, "New Name cannot be null!");
                    return;
                }
            }

            if (mContext == 4) {
                user.mUserType = dropUserTypes.getSelectedIndex();
            }

            // all user info is ready! Send to Server!

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.PUT_USER);

                output.println(mLoggedUser);
                output.println(oldPass);

                output.println(user.mUsername);
                output.println(user.mPassword);
                output.println(user.mFullname);
                output.println(user.mUserType);
                output.println(user.mCustomerID);

                output.println(oldUserName);

                int result = input.nextInt();
                if (result == (MessageModel.UPDATE_USER_WRONG_PASS)) {
                    JOptionPane.showMessageDialog(null, "Invalid Password!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
                else if (result == MessageModel.OPERATION_OK) {
                    JOptionPane.showMessageDialog(null, "User saved successfully!");
                    view.dispose();
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to save User!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
