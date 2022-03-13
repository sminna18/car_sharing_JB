
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:file:../task/src/carsharing/db/";

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        Statement statement = null;

        try {
            Class.forName(JDBC_DRIVER);

            connection = DriverManager.getConnection(DB_URL + args[1]);
            connection.setAutoCommit(true);
            statement = connection.createStatement();

//            statement.executeUpdate("DROP TABLE IF EXISTS CUSTOMER");
//            statement.executeUpdate("DROP TABLE IF EXISTS CAR");
//            statement.executeUpdate("DROP TABLE IF EXISTS COMPANY");

            String sql = "CREATE TABLE IF NOT EXISTS COMPANY" +
                    "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                    " name VARCHAR(255) NOT NULL UNIQUE, " +
                    " PRIMARY KEY ( id ))";

            statement.executeUpdate(sql);

            sql =   "CREATE TABLE IF NOT EXISTS CAR" +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " name VARCHAR(255) NOT NULL UNIQUE, " +
                    " company_id INTEGER NOT NULL," +
                    " FOREIGN KEY (company_id)" +
                    " REFERENCES COMPANY(id));";

            statement.executeUpdate(sql);

            sql =   "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                    "(id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " name VARCHAR(255) NOT NULL UNIQUE, " +
                    " rented_car_id INTEGER," +
                    " FOREIGN KEY (rented_car_id)" +
                    " REFERENCES CAR(id));";

            statement.executeUpdate(sql);

//            statement.executeUpdate("INSERT INTO COMPANY (name) VALUES ('BMW');");
//            statement.executeUpdate("INSERT INTO COMPANY (name) VALUES ('Mers');");
//            statement.executeUpdate("INSERT INTO CAR (name, company_id) VALUES ('520i', 1);");
//            statement.executeUpdate("INSERT INTO CUSTOMER (name) VALUES ('Nik');");

            menu(statement);

            statement.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createACompany(Statement statement) throws SQLException {
        System.out.println("Enter the company name:");
        Scanner scan =  new Scanner(System.in);
        String company_name = scan.nextLine();
        if (company_name.length() <= 255) {
            try {
                statement.executeUpdate("INSERT INTO COMPANY (name) VALUES ('" + company_name + "');");
                System.out.println("The company was created!");
            }
            catch (Throwable e) {
                System.out.println("This company already exists!");
            }
        }
        else
            System.out.println("Too long name!");
        System.out.println();
    }

    public static void createACar(Statement statement, int company_id) throws SQLException {
        System.out.println("Enter the car name:");
        Scanner scan =  new Scanner(System.in);
        String car_name = scan.nextLine();
        if (car_name.length() <= 255) {
            try {statement.executeUpdate("INSERT INTO CAR (name, company_id) VALUES ('" + car_name + "', " + company_id + ");");}
            catch (Throwable e) {System.out.println("This car already exists!");}
        }
        else
            System.out.println("Too long name!");
        System.out.println();
    }

    public static void createACustomer(Statement statement) throws SQLException {
        System.out.println("Enter the customer name:");
        Scanner scan =  new Scanner(System.in);
        String customer_name = scan.nextLine();
        if (customer_name.length() <= 255) {
            try {
                statement.executeUpdate("INSERT INTO CUSTOMER (name) VALUES ('" + customer_name + "');");
                System.out.println("This car already exists!");
            }
            catch (Throwable e) {System.out.println("This customer already exists!");}
        }
        else
            System.out.println("Too long name!");
        System.out.println();
    }

    public static void companyList(Statement statement, int menu_num, int customer_id) throws SQLException {
        Scanner scan = new Scanner(System.in);
        int company_id;
        ResultSet resultSet = statement.executeQuery("SELECT * FROM COMPANY");
        ArrayList<String> task = new ArrayList<>();
        int count = 0;
        while(resultSet.next()) {
            task.add(resultSet.getString("name"));
            count++;
        }
        if (count == 0) {
            System.out.println("The company list is empty!");
            System.out.println();
            return;
        }
        else {
            for (int i = 1; i <= count; i++) {
                System.out.println(i + ". " + task.get(i - 1));
            }
            System.out.println("0. Back");
        }
        System.out.println();
        company_id = scan.nextInt();
        if (company_id > 0 && company_id <= count && menu_num == 1) {
            company_menu(statement, task.get(company_id - 1), company_id);
        }
        else if (menu_num == 2) {
            System.out.println("Choose a car:");
            rent_car(statement, company_id, customer_id);
        }
    }

    public static void rent_car(Statement statement, int company_id, int customer_id) throws SQLException {
        Scanner scan = new Scanner(System.in);
        ResultSet resultSet = statement.executeQuery("SELECT * FROM CAR WHERE company_id = '" + company_id + "';");
        ArrayList<String> task = new ArrayList<>();
        int count = 0;
        while(resultSet.next()) {
            task.add(resultSet.getString("name"));
            count++;
        }
        if (count == 0) {
            System.out.println("The company list is empty!");
            System.out.println();
            return;
        }
        else {
            for (int i = 1; i <= count; i++) {
                System.out.println(i + ". " + task.get(i - 1));
            }
            System.out.println("0. Back");
        }
        System.out.println();
        int car_id = scan.nextInt();
        ResultSet resultSet_2 = statement.executeQuery("SELECT * FROM CAR WHERE name = '" + task.get(car_id - 1) + "';");
        ArrayList<String> task_2 = new ArrayList<>();
        resultSet_2.next();
        task_2.add(resultSet_2.getString("id"));
        int count_2 = 0;
        ResultSet resultSet_3 = statement.executeQuery("SELECT * FROM CUSTOMER WHERE rented_car_id = " + task_2.get(0) + ";");
        ArrayList<String> task_3 = new ArrayList<>();
        while (resultSet_3.next()) {
            task_3.add(resultSet_3.getString("rented_car_id"));
            count_2++;
        }
        if (count_2 != 0) {
            System.out.println("Car is busy!");
            System.out.println();
        }
        else if (car_id > 0 && car_id <= count) {
            System.out.println("You rented '" + task.get(car_id - 1) + "'");
            statement.executeUpdate("UPDATE CUSTOMER SET rented_car_id = " + task_2.get(0) + " WHERE id = " + customer_id + ";");
        }
    }

    public static void customerList(Statement statement) throws SQLException {
        Scanner scan = new Scanner(System.in);
        int s;
        ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMER");
        ArrayList<String> task = new ArrayList<>();
        int count = 0;
        while(resultSet.next()) {
            task.add(resultSet.getString("name"));
            count++;
        }
        if (count == 0) {
            System.out.println("The customer list is empty!");
            System.out.println();
            return;
        }
        else {
            for (int i = 1; i <= count; i++) {
                System.out.println(i + ". " + task.get(i - 1));
            }
            System.out.println("0. Back");
        }
        System.out.println();
        s = scan.nextInt();
        if (s > 0 && s <= count)
            customer_menu(statement, s);

    }

    public static void carList(Statement statement, String company_name, int company_id) throws SQLException {
        Scanner scan = new Scanner(System.in);
        int s;
        ResultSet resultSet = statement.executeQuery("SELECT * FROM CAR WHERE company_id = " + company_id + "");
//        ResultSet resultSet = statement.executeQuery("SELECT * FROM CAR");
        ArrayList<String> task = new ArrayList<>();
        int count = 0;
        while(resultSet.next()) {
            task.add(resultSet.getString("name"));
            count++;
        }
        if (count == 0) {
            System.out.println("The car list is empty!");
            System.out.println();
            return;
        }
        else
            for (int i = 1; i <= count; i++) {
                System.out.println(i + ". " + task.get(i - 1));
            }
        System.out.println();

    }

    public static void menu(Statement statement) throws SQLException {
        int i = 1;
        int s;
        Scanner scan = new Scanner(System.in);
        while(i > 0) {
            if (i == 1)
                System.out.println("1. Log in as a manager\n2. Log in as a customer\n3. Create a customer\n0. Exit");
            else
                System.out.println("1. Company list \n2. Create a company \n0. Back");
            s = scan.nextInt();
            if (i == 1) {
                if (s == 1)
                    i = 2;
                if (s == 2)
                    customerList(statement);
                if (s == 3)
                    createACustomer(statement);
                else if (s == 0)
                    i = 0;
            }
            else {
                if (s == 1)
                    companyList(statement, 1, 0);
                else if (s == 2)
                    createACompany(statement);
                else if (s == 0)
                    i = 1;
            }
        }
    }

    public static void company_menu(Statement statement, String company_name, int company_id) throws SQLException {
        int i = 1;
        int s;

        System.out.println(company_name + " company:");
        Scanner scan = new Scanner(System.in);
        while(i > 0) {
            System.out.println("1. Car list\n2. Create a car\n0. Back");
            s = scan.nextInt();
            if (s == 1)
                carList(statement, company_name, company_id);
            else if (s == 2)
                createACar(statement, company_id);
            else if (s == 0)
                i = 0;
        }
    }

    public static void customer_menu(Statement statement, int customer_id) throws SQLException {
        int i = 1;
        int s;

//        System.out.println(customer_name + " company:");
        Scanner scan = new Scanner(System.in);
        while(i > 0) {

            System.out.println("1. Rent a car\n2. Return a rented car\n3. My rented car\n0. Back");
            s = scan.nextInt();
            if (s == 1) {
                ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE id = " + customer_id + "AND rented_car_id > 0;");

                ArrayList<String> task = new ArrayList<>();
                int count = 0;
                while (resultSet.next()) {
                    task.add(resultSet.getString("rented_car_id"));
                    count++;
                }
                if (count != 0) {
                    System.out.println("You've already rented a car!");
                    System.out.println();
                }
                else {
                    System.out.println("Choose a company:");
                    companyList(statement, 2, customer_id);
                }
            } else if (s == 2) {

                ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE id = " + customer_id + "AND rented_car_id > 0;");

                ArrayList<String> task = new ArrayList<>();
                int count = 0;
                while (resultSet.next()) {
                    task.add(resultSet.getString("rented_car_id"));
                    count++;
                }
                if (count == 0) {
                    System.out.println("You didn't rent a car!");
                    System.out.println();
                }
                else
                    statement.executeUpdate("UPDATE CUSTOMER SET rented_car_id = NULL WHERE id = " + customer_id + ";");
            } else if (s == 3) {


                ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMER WHERE id = " + customer_id + "AND rented_car_id > 0;");

                ArrayList<String> task = new ArrayList<>();
                int count = 0;
                while (resultSet.next()) {
                    task.add(resultSet.getString("rented_car_id"));
                    count++;
                }
//                System.out.println("/////" + task.get(0));
                if (count == 0) {
                    System.out.println("You didn't rent a car!");
                    System.out.println();
                } else {
                    ResultSet resultSet_2 = statement.executeQuery("SELECT * FROM CAR WHERE id = " + task.get(0) + ";");
                    ArrayList<String> task_2 = new ArrayList<>();
                    resultSet_2.next();
                    task_2.add(resultSet_2.getString("company_id"));
                    task_2.add(resultSet_2.getString("name"));
                    ResultSet resultSet_3 = statement.executeQuery("SELECT * FROM COMPANY WHERE id = " + task_2.get(0) + ";");
                    ArrayList<String> task_3 = new ArrayList<>();
                    resultSet_3.next();
                    task_3.add(resultSet_3.getString("name"));
                    System.out.println("Your rented car:\n" + task_2.get(1));
                    System.out.println("Company:\n" + task_3.get(0));
                }
            }
            else if (s == 0)
                i = 0;
        }
    }
}

