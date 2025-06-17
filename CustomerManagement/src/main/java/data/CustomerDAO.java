    package data;

    import common.Constant;
    import java.sql.Date;
    import java.sql.PreparedStatement;
    import java.sql.ResultSet;
    import java.util.ArrayList;
    import model.Customer;

    public class CustomerDAO {

        public ArrayList<Customer> getAllCustomers() {
            ArrayList<Customer> customers = new ArrayList<>();
            try (PreparedStatement ps = DBContext.getConnection()
                    .prepareStatement(Constant.CUSTOMER_SELECT_ALL)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int customerId = rs.getInt("ID");
                    String customerName = rs.getNString("Name");
                    String customerGender = rs.getNString("Gender");
                    Date customerDOB = rs.getDate("DOB");
                    customers.add(new Customer(customerId, customerName, customerGender, customerDOB));
                }
            } catch (Exception e) {
                System.out.println("Error CustomerDAO.getAllCustomers");
                e.printStackTrace();
            }
            return customers;
        }

        public Customer getCustomerById(int id) {
            String sql = "SELECT * FROM Customer WHERE ID = ?";
            try (PreparedStatement ps = DBContext.getConnection().prepareStatement(sql)) {
                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int customerId = rs.getInt("ID");
                    String customerName = rs.getNString("Name");
                    String customerGender = rs.getNString("Gender");
                    Date customerDOB = rs.getDate("DOB");
                    return new Customer(customerId, customerName, customerGender, customerDOB);
                }
            } catch (Exception e) {
                System.out.println("Error CustomerDAO.getCustomerById");
                e.printStackTrace();
            }
            return null;
        }

        public boolean addCustomer(Customer c) {
            boolean check = false;
            try (PreparedStatement ps = DBContext
                    .getConnection()
                    .prepareStatement(Constant.CUSTOMER_ADD)) {
                ps.setString(1, c.getName());
                ps.setString(2, c.getGender());
                ps.setDate(3, c.getDob());
                int result = ps.executeUpdate();
                check = (result > 0);
            } catch (Exception e) {
                System.out.println("Error CustomerDAO.addCustomer");
                e.printStackTrace();
            }
            return check;
        }

        public boolean updateCustomer(Customer c) {
            boolean check = false;
            try (PreparedStatement ps = DBContext
                    .getConnection()
                    .prepareStatement(Constant.CUSTOMER_UPDATE)) {
                ps.setString(1, c.getName());
                ps.setString(2, c.getGender());
                ps.setDate(3, c.getDob());
                ps.setInt(4, c.getId());
                int result = ps.executeUpdate();
                check = (result > 0);
            } catch (Exception e) {
                System.out.println("Error CustomerDAO.updateCustomer");
                e.printStackTrace();
            }
            return check;
        }

        public boolean deleteCustomer(int customerId) {
            boolean check = false;
            try (PreparedStatement ps = DBContext
                    .getConnection()
                    .prepareStatement(Constant.CUSTOMER_DELETE)) {
                ps.setInt(1, customerId);
                int result = ps.executeUpdate();
                check = (result > 0);
            } catch (Exception e) {
                System.out.println("Error CustomerDAO.deleteCustomer");
                e.printStackTrace();
            }
            return check;
        }
        
    }
