import java.io.PrintWriter;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class StoreServer {
    static String dbfile = "/Users/willgarrison/Documents/Software Engineering/Fall 2019/COMP 3700/StoreManagement.db";

    public static void main(String[] args) {
        HashMap<Integer, UserModel> activeUsers = new HashMap<Integer, UserModel>();

        int totalActiveUsers = 0;

        int port = 1000;

        if (args.length > 0) {
            System.out.println("Running arguments: ");
            for (String arg : args)
                System.out.println(arg);
            port = Integer.parseInt(args[0]);
            dbfile = args[1];
        }

        try {
            ServerSocket server = new ServerSocket(port);

            System.out.println("Server is listening at port = " + port);

            while (true) {
                Socket pipe = server.accept();
                PrintWriter out = new PrintWriter(pipe.getOutputStream(), true);
                Scanner in = new Scanner(pipe.getInputStream());

                int command = Integer.parseInt(in.nextLine());
                if (command == MessageModel.LOGIN) {
                    String username = in.nextLine();
                    String password = in.nextLine();

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM User WHERE UserName = \"" + username + "\"";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            if (rs.getString("Password").equals(password)) {
                                out.println(MessageModel.LOGIN_SUCCESS);
                                String fullName = rs.getString("FullName");
                                String userType = rs.getString("UserType");
                                String customerID = rs.getString("CustomerID");
                                if (customerID == null) {
                                    customerID = "0";
                                }
                                out.println(username);
                                out.println(password);
                                out.println(fullName);
                                out.println(userType);
                                out.println(customerID);

                                UserModel user = new UserModel(username, password, fullName, Integer.parseInt(userType), Integer.parseInt(customerID));

                                int key = totalActiveUsers;
                                while (activeUsers.containsKey(key)) {
                                    key++;
                                }
                                activeUsers.put(key, user);
                                out.println(key);
                                totalActiveUsers++;
                            }
                            else {
                                out.println(MessageModel.LOGIN_WRONG_PASS);
                            }
                        }
                        else {
                            out.println(MessageModel.LOGIN_WRONG_USER);
                        }
                    }
                    catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.LOGOUT) {
                    try {
                        int key = Integer.parseInt(in.nextLine());
                        out.println(MessageModel.LOGOUT_SUCCESS);
                        activeUsers.remove(key);
                        totalActiveUsers--;
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (command == MessageModel.GET_PRODUCT) {
                    String str = in.nextLine();
                    System.out.println("GET product with id = " + str);
                    int productID = Integer.parseInt(str);

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Product WHERE ProductID = " + productID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            out.println(rs.getString("Name")); // send back product name!
                            out.println(rs.getDouble("Price")); // send back product price!
                            out.println(rs.getDouble("TaxRate")); // send back product tax rate!
                            out.println(rs.getDouble("Quantity")); // send back product quantity!
                            out.println(rs.getString("Vendor")); // send back product vendor!
                            out.println(rs.getString("Description")); // send back product description!
                        }
                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }

                else if (command == MessageModel.PUT_PRODUCT || command == MessageModel.ADD_PRODUCT) {
                    String id = in.nextLine();  // read all information from client
                    String name = in.nextLine();
                    String price = in.nextLine();
                    String taxRate = in.nextLine();
                    String quantity = in.nextLine();
                    String vendor = in.nextLine();
                    String description = in.nextLine();

                    if (command == MessageModel.PUT_PRODUCT) {
                        System.out.println("PUT command with ProductID = " + id);
                    }
                    else {
                        System.out.println("ADD command with ProductID = " + id);
                    }

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Product WHERE ProductID = " + id;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_PRODUCT) {
                            rs.close();
                            stmt.execute("DELETE FROM Product WHERE ProductID = " + id);
                        }
                        else if (duplicate && command == MessageModel.ADD_PRODUCT) {
                            out.println(MessageModel.DUPLICATE_PRODUCT);
                            conn.close();
                            continue;
                        }

                        sql = "INSERT INTO Product VALUES (" + id + ",\"" + name + "\","
                                + price + "," + taxRate + "," + quantity + ",\"" + vendor
                                + "\",\"" + description + "\")";
                        if (command == MessageModel.PUT_PRODUCT) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }

                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.GET_CUSTOMER) {
                    String str = in.nextLine();
                    System.out.println("GET customer with id = " + str);
                    int customerID = Integer.parseInt(str);

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Customer WHERE CustomerID = " + customerID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            out.println(rs.getString("Name")); // send back customer name!
                            out.println(rs.getString("Address")); // send back customer address!
                            out.println(rs.getString("Phone")); // send back customer phone number!
                            out.println(rs.getString("PaymentInfo")); // send back customer payment info!
                        }
                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PUT_CUSTOMER || command == MessageModel.ADD_CUSTOMER) {
                    String id = in.nextLine();  // read all information from client
                    String name = in.nextLine();
                    String address = in.nextLine();
                    String phone = in.nextLine();
                    String paymentInfo = in.nextLine();

                    if (command == MessageModel.PUT_CUSTOMER) {
                        System.out.println("PUT command with CustomerID = " + id);
                    }
                    else {
                        System.out.println("ADD command with CustomerID = " + id);
                    }

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Customer WHERE CustomerID = " + id;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_CUSTOMER) {
                            rs.close();
                            stmt.execute("DELETE FROM Customer WHERE CustomerID = " + id);
                        }
                        else if (duplicate && command == MessageModel.ADD_CUSTOMER) {
                            out.println(MessageModel.DUPLICATE_CUSTOMER);
                            conn.close();
                            continue;
                        }

                        sql = "INSERT INTO Customer VALUES (" + id + ",\"" + name + "\",\""
                                + address + "\",\"" + phone
                                + "\",\"" + paymentInfo + "\")";
                        if (command == MessageModel.PUT_CUSTOMER) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }
                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.GET_PURCHASE) {
                    String str = in.nextLine();
                    System.out.println("GET purchase with id = " + str);
                    int purchaseID = Integer.parseInt(str);

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Purchase WHERE PurchaseID = " + purchaseID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        String customerID;
                        String productID;

                        if (rs.next()) {
                            customerID = rs.getString("CustomerID");
                            productID = rs.getString("ProductID");
                            out.println(productID); // send back ID of product being purchased!
                            out.println(customerID); // send back ID of customer making purchase!
                            out.println(rs.getString("Quantity")); // send back quantity of item purchased!
                            out.println(rs.getString("Cost")); // send back cost of purchase without tax!
                            out.println(rs.getString("TaxCost")); // send back tax cost of purchase!
                            out.println(rs.getString("TotalCost")); // send back total cost of purchase!
                            out.println(rs.getString("DateOf")); // send back purchase date!

                            rs = stmt.executeQuery("SELECT * FROM Customer WHERE CustomerID = " + customerID);
                            if (rs.next()) {
                                out.println(rs.getString("Name")); // send back name of customer making purchase!
                                out.println(rs.getString("Address")); // send back address of customer making purchase!
                                out.println(rs.getString("Phone")); // send back phone number of customer making purchase!
                                out.println(rs.getString("PaymentInfo")); // send back payment info of customer making purchase!
                            }
                            else {
                                out.println("null");
                            }

                            rs = stmt.executeQuery("SELECT * FROM Product WHERE ProductID = " + productID);
                            if (rs.next()) {
                                out.println(rs.getString("Name")); // send back name of product being purchased!
                                out.println(rs.getString("Price")); // send back price of product being purchased!
                                out.println(rs.getString("Quantity")); // send back total quantity of product in store!
                                out.println(rs.getString("TaxRate")); // send back the tax rate of the product!
                                out.println(rs.getString("Vendor")); // send back the vendor of the product!
                                out.println(rs.getString("Description")); // send back the description of the product!
                            }
                            else {
                                out.println("null");
                            }
                        }

                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PUT_PURCHASE || command == MessageModel.ADD_PURCHASE) {
                    String purchaseID = in.nextLine();  // read all information from client
                    String productID = in.nextLine();
                    String customerID = in.nextLine();
                    String quantity = in.nextLine();
                    String cost = in.nextLine();
                    String taxCost = in.nextLine();
                    String totalCost = in.nextLine();
                    String dateOf = in.nextLine();

                    if (command == MessageModel.PUT_PURCHASE) {
                        System.out.println("PUT command with PurchaseID = " + purchaseID);
                    }
                    else {
                        System.out.println("ADD command with PurchaseID = " + purchaseID);
                    }

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM Purchase WHERE PurchaseID = " + purchaseID;
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_PURCHASE) {
                            rs.close();
                            stmt.execute("DELETE FROM Purchase WHERE PurchaseID = " + purchaseID);
                        }
                        else if (duplicate && command == MessageModel.ADD_PURCHASE) {
                            out.println(MessageModel.DUPLICATE_PURCHASE);
                            conn.close();
                            continue;
                        }

                        sql = "INSERT INTO Purchase VALUES (" + purchaseID + "," + productID + "," + customerID
                                + "," + quantity + "," + cost + "," + taxCost + ","
                                + totalCost + ",\"" + dateOf + "\")";
                        if (command == MessageModel.PUT_PURCHASE) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }
                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.GET_USER) {
                    String str = in.nextLine();
                    System.out.println("GET user with UserName = " + str);
                    String username = str;

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT * FROM User WHERE UserName = \"" + username + "\"";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        if (rs.next()) {
                            out.println(rs.getString("Password")); // send back user password!
                            out.println(rs.getString("FullName")); // send back user name!
                            out.println(rs.getString("UserType")); // send back user type!
                            String customerID = rs.getString("CustomerID");
                            if (customerID != null && !customerID.equals("null")) {
                                out.println(customerID); // send back customer ID if user is a customer!
                            }
                            else {
                                out.println("0"); // send back customer ID if user is a customer!
                            }
                        }
                        else
                            out.println("null");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PUT_USER || command == MessageModel.ADD_USER) {
                    String oldUser = "";
                    String oldPass = "";
                    if (command == MessageModel.PUT_USER) {
                        oldUser = in.nextLine();  // read all information from client
                        oldPass = in.nextLine();
                    }

                    UserModel user = new UserModel(in.nextLine(), in.nextLine(), in.nextLine(), Integer.parseInt(in.nextLine()), Integer.parseInt(in.nextLine()));

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "";
                        Statement stmt;
                        ResultSet rs;
                        if (command == MessageModel.PUT_USER) {
                            sql = "SELECT * FROM User WHERE UserName = \"" + oldUser + "\"";
                            stmt = conn.createStatement();
                            rs = stmt.executeQuery(sql);

                            if (!rs.getString("Password").equals(oldPass)) {
                                out.println(MessageModel.UPDATE_USER_WRONG_PASS);
                            }

                            oldUser = in.nextLine();
                        }
                        else {
                            oldUser = user.mUsername;
                        }

                        sql = "SELECT * FROM User WHERE UserName = \"" + oldUser + "\"";
                        stmt = conn.createStatement();
                        rs = stmt.executeQuery(sql);

                        if (command == MessageModel.PUT_USER) {
                            System.out.println("PUT command with UserName = " + oldUser);
                        }
                        else {
                            System.out.println("ADD command with UserName = " + oldUser);
                        }

                        boolean duplicate = rs.next();
                        if (duplicate && command == MessageModel.PUT_USER) {
                            rs.close();
                            stmt.execute("DELETE FROM User WHERE UserName = \"" + oldUser + "\"");
                        }
                        else if (duplicate && command == MessageModel.ADD_USER) {
                            out.println(MessageModel.DUPLICATE_USER);
                            conn.close();
                            continue;
                        }

                        if (user.mCustomerID != 0) {
                            sql = "INSERT INTO User VALUES (\"" + user.mUsername + "\",\"" + user.mPassword + "\","
                                    + user.mUserType + ",\"" + user.mFullname
                                    + "\"," + user.mCustomerID + ")";
                        }
                        else {
                            sql = "INSERT INTO User VALUES (\"" + user.mUsername + "\",\"" + user.mPassword + "\","
                                    + user.mUserType + ",\"" + user.mFullname
                                    + "\"," + "NULL" + ")";
                        }
                        if (command == MessageModel.PUT_USER) {
                            System.out.println("SQL for PUT: " + sql);
                        }
                        else {
                            System.out.println("SQL for ADD: " + sql);
                        }
                        stmt.execute(sql);
                        out.println(MessageModel.OPERATION_OK);

                    } catch (Exception e) {
                        out.println(MessageModel.OPERATION_FAILED);
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PURCHASE_REPORT) {

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String sql = "SELECT Prod.ProductID, Prod.Name, SUM(Quantity) as Quantity, SUM(Cost) as Cost FROM"
                                + " (SELECT ProductID, Quantity, Cost FROM Purchase) Pur"
                                + " JOIN"
                                + " (SELECT ProductID, Name FROM Product) Prod"
                                + " ON Prod.ProductID = Pur.ProductID"
                                + " GROUP BY Prod.ProductID";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        int size = 0;
                        ArrayList<String> productIDList = new ArrayList<String>();
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> quantityList = new ArrayList<String>();
                        ArrayList<String> costList = new ArrayList<String>();

                        while (rs.next()) {
                            size++;
                            productIDList.add(rs.getString("ProductID"));
                            nameList.add(rs.getString("Name"));
                            quantityList.add(rs.getString("Quantity"));
                            costList.add(rs.getString("Cost"));
                        }

                        out.println(size);
                        for (int j = 0; j < size; j++) {
                            out.println(productIDList.get(j)); // send back Product ID!
                            out.println(nameList.get(j)); // send back Product Name!
                            out.println(quantityList.get(j)); // send back Quantity!
                            out.println(costList.get(j)); // send back Cost!
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.PURCHASE_HISTORY) {

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String customerID = in.nextLine();

                        String sql = "SELECT Name, PurchaseID, Quantity, Cost, TaxCost, TotalCost, DateOf FROM"
                                + " (SELECT * FROM Purchase WHERE CustomerID = " + customerID + ") t2"
                                + " JOIN"
                                + " (SELECT Name, ProductID FROM Product) t1"
                                + " WHERE t1.ProductID = t2.ProductID";
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        int size = 0;
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> purchaseIDList = new ArrayList<String>();
                        ArrayList<String> quantityList = new ArrayList<String>();
                        ArrayList<String> costList = new ArrayList<String>();
                        ArrayList<String> taxCostList = new ArrayList<String>();
                        ArrayList<String> totalCostList = new ArrayList<String>();
                        ArrayList<String> dateList = new ArrayList<String>();

                        while (rs.next()) {
                            size++;
                            nameList.add(rs.getString("Name"));
                            purchaseIDList.add(rs.getString("PurchaseID"));
                            quantityList.add(rs.getString("Quantity"));
                            costList.add(rs.getString("Cost"));
                            taxCostList.add(rs.getString("TaxCost"));
                            totalCostList.add(rs.getString("TotalCost"));
                            dateList.add(rs.getString("DateOf"));
                        }

                        out.println(size);
                        for (int j = 0; j < size; j++) {
                            out.println(nameList.get(j)); // send back Name of Product Purchase!
                            out.println(purchaseIDList.get(j)); // send back ID of Product Purchased!
                            out.println(quantityList.get(j)); // send back Quantity of Product Purchased!
                            out.println(costList.get(j)); // send back Cost of Purchase!
                            out.println(taxCostList.get(j)); // send back Tax Cost of Purchase!
                            out.println(totalCostList.get(j)); // send back Total Cost of Purchase!
                            out.println(dateList.get(j)); // send back Date of Purchase!
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else if (command == MessageModel.SEARCH_PRODUCT_NAME || command == MessageModel.SEARCH_PRODUCT_PRICE) {

                    Connection conn = null;
                    try {
                        String url = "jdbc:sqlite:" + dbfile;
                        conn = DriverManager.getConnection(url);

                        String param = in.nextLine();

                        String sql = "";

                        if (command == MessageModel.SEARCH_PRODUCT_PRICE) {
                            sql = "SELECT * FROM Product WHERE Price = " + param;
                        }
                        else {
                            sql = "SELECT * FROM Product WHERE Name LIKE '%" + param + "%'";
                        }
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql);

                        int size = 0;
                        ArrayList<String> productIDList = new ArrayList<String>();
                        ArrayList<String> nameList = new ArrayList<String>();
                        ArrayList<String> priceList = new ArrayList<String>();
                        ArrayList<String> taxList = new ArrayList<String>();
                        ArrayList<String> quantityList = new ArrayList<String>();
                        ArrayList<String> vendorList = new ArrayList<String>();
                        ArrayList<String> descriptionList = new ArrayList<String>();

                        while (rs.next()) {
                            size++;
                            productIDList.add(rs.getString("ProductID"));
                            nameList.add(rs.getString("Name"));
                            priceList.add(rs.getString("Price"));
                            taxList.add(rs.getString("TaxRate"));
                            quantityList.add(rs.getString("Quantity"));
                            vendorList.add(rs.getString("Vendor"));
                            descriptionList.add(rs.getString("Description"));
                        }

                        out.println(size);
                        for (int j = 0; j < size; j++) {
                            out.println(productIDList.get(j)); // send back Product IDs!
                            out.println(nameList.get(j)); // send back Product Names!
                            out.println(priceList.get(j)); // send back Product Prices!
                            out.println(taxList.get(j)); // send back Product Tax Rates!
                            out.println(quantityList.get(j)); // send back Product Quantity!
                            out.println(vendorList.get(j)); // send back Product Vendors!
                            out.println(descriptionList.get(j)); // send back Product Descriptions!
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    conn.close();
                }
                else {
                    out.println(0); // logout unsuccessful!
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}