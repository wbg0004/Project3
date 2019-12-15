import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class UpdateCustomerUI {

    public JFrame view;

    public JButton btnLoad = new JButton("Load Customer");
    public JButton btnSave = new JButton("Save Customer");

    public JTextField txtCustomerID = new JTextField(20);
    public JTextField txtName = new JTextField(20);
    public JTextField txtAddress = new JTextField(20);
    public JTextField txtPhone = new JTextField(20);
    public JTextField txtPaymentInfo = new JTextField(20);


    public UpdateCustomerUI() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Update Customer Information");
        view.setSize(400, 450);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnLoad);
        panelButtons.add(btnSave);
        view.getContentPane().add(panelButtons);

        String[] labels = {"Customer ID ", "Name ", "Address ", "Phone ", "Payment Info "};
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


        btnLoad.addActionListener(new LoadButtonListener());

        btnSave.addActionListener(new SaveButtonListener());

    }

    public void run() {
        view.setVisible(true);
    }

    class LoadButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CustomerModel customer = new CustomerModel();
            String id = txtCustomerID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }

            try {
                customer.mCustomerID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "CustomerID is invalid!");
                return;
            }

            // do client/server

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.GET_CUSTOMER);
                output.println(customer.mCustomerID);

                customer.mName = input.nextLine();

                if (customer.mName.equals("null")) {
                    JOptionPane.showMessageDialog(null, "Customer does NOT exist!");
                    return;
                }

                txtName.setText(customer.mName);

                customer.mAddress = input.nextLine();
                if (!customer.mAddress.equals("null")) {
                    txtAddress.setText(customer.mAddress);
                }

                customer.mPhone = input.nextLine();
                if (!customer.mPhone.equals("null")) {
                    txtPhone.setText(customer.mPhone);
                }

                customer.mPayInfo = input.nextLine();
                if (!customer.mPayInfo.equals("null")) {
                    txtPaymentInfo.setText(customer.mPayInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            CustomerModel customer = new CustomerModel();
            String id = txtCustomerID.getText();

            if (id.length() == 0) {
                JOptionPane.showMessageDialog(null, "CustomerID cannot be null!");
                return;
            }

            try {
                customer.mCustomerID = Integer.parseInt(id);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "CustomerID is invalid!");
                return;
            }

            String name = txtName.getText();
            if (name.length() == 0) {
                JOptionPane.showMessageDialog(null, "Customer name cannot be empty!");
                return;
            }

            customer.mName = name;

            String address = txtAddress.getText();
            customer.mAddress = address;

            String phone = txtPhone.getText();
            customer.mPhone = phone;

            String paymentInfo = txtPaymentInfo.getText();
            customer.mPayInfo = paymentInfo;

            // all customer info is ready! Send to Server!

            try {
                Socket link = new Socket("localhost", 1000);
                Scanner input = new Scanner(link.getInputStream());
                PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                output.println(MessageModel.PUT_CUSTOMER);
                output.println(customer.mCustomerID);
                output.println(customer.mName);
                output.println(customer.mAddress);
                output.println(customer.mPhone);
                output.println(customer.mPayInfo);

                if (input.nextInt() == (MessageModel.OPERATION_OK)) {
                    JOptionPane.showMessageDialog(null, "Customer saved successfully!");
                }
                else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Unable to save customer!", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
