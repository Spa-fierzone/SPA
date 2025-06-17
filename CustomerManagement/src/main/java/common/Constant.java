package common;

public class Constant {

    public static final String CUSTOMER_SELECT_ALL = "SELECT * FROM Customer";
    public static final String CUSTOMER_ADD = "INSERT INTO Customer(Name, Gender, DOB) VALUES (?, ?, ?)";
    public static final String CUSTOMER_UPDATE = "UPDATE Customer SET Name = ?, Gender = ?, DOB = ? WHERE ID = ?";
    public static final String CUSTOMER_DELETE = "DELETE FROM Customer WHERE ID = ?";

    public static final String USER_GET_ALL = "SELECT * FROM Users";  // example table name
    public static final String USER_ADD = "INSERT INTO Users(Username, Password) VALUES (?, ?)";
}