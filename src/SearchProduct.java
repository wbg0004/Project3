//package edu.auburn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SearchProduct {
    public JFrame view;

    public JButton btnSearchProduct = new JButton("Search");

    public JTextField txtProductName = new JTextField(20);
    public JTextField txtPrice = new JTextField(20);

    Socket link;
    Scanner input;
    PrintWriter output;

    public SearchProduct() {
        this.view = new JFrame();

        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        view.setTitle("Search for Product");
        view.setSize(400, 150);
        view.getContentPane().setLayout(new BoxLayout(view.getContentPane(), BoxLayout.PAGE_AXIS));

        String[] labels = {"Product Name ", "Price "};
        JTextField[] textFields = {txtProductName, txtPrice};

        for (int i = 0; i < labels.length; i++) {
            JLabel l = new JLabel(labels[i]);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 5));
            p.add(l);
            JTextField field = textFields[i];
            l.setLabelFor(field);
            p.add(field);
            view.getContentPane().add(p);
        }


        JPanel panelButtons = new JPanel(new FlowLayout());
        panelButtons.add(btnSearchProduct);
        view.getContentPane().add(panelButtons);

        btnSearchProduct.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (txtPrice.getText().length() == 0 && txtProductName.getText().length() == 0) {
                    JOptionPane.showMessageDialog(null, "Must include either Product Name or Price!");
                    return;
                }

                String productName = txtProductName.getText();
                String productPrice = txtPrice.getText();

                // do client/server

                try {
                    Socket link = new Socket("localhost", 1000);
                    Scanner input = new Scanner(link.getInputStream());
                    PrintWriter output = new PrintWriter(link.getOutputStream(), true);

                    if (productName.length() > 0) {
                        output.println(MessageModel.SEARCH_PRODUCT_NAME);
                        output.println(productName);
                    }

                    else if (productPrice.length() > 0) {
                        output.println(MessageModel.SEARCH_PRODUCT_PRICE);
                        output.println(productPrice);
                    }

                    int size = Integer.parseInt(input.nextLine());

                    String[][] products = new String[size][7];

                    for (int i = 0; i < size; i++) {
                        products[i][0] = input.nextLine();
                        products[i][1] = input.nextLine();
                        products[i][2] = input.nextLine();
                        products[i][3] = input.nextLine();
                        products[i][4] = input.nextLine();
                        products[i][5] = input.nextLine();
                        products[i][6] = input.nextLine();
                    }

                    String[] columns = {"Product ID", "Product Name", "Price", "Tax Rate", "Quantity", "Vendor", "Description"};
                    JTable productTable = new JTable(products, columns);

                    JScrollPane scrollPane = new JScrollPane(productTable);
                    JPanel panel = new JPanel();
                    panel.add(scrollPane);
                    JFrame f = new JFrame();
                    f.getContentPane().add(panel, BorderLayout.CENTER);
                    f.setSize(600,500);
                    f.setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        });
    }

    public void run() {
        view.setVisible(true);
    }
}