package data;

import model.Appointment;

import java.sql.*;
import java.util.*;

public class AppointmentDAO {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/spa_db";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "your_password";

    private static final String SELECT_ALL =
        "SELECT a.id, a.date, a.time, a.service, c.name AS customerName " +
        "FROM appointments a JOIN customers c ON a.customer_id = c.id";

    // Lấy danh sách lịch hẹn kèm tên khách hàng
    public List<Appointment> getAllAppointmentsWithCustomerNames() {
        List<Appointment> list = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Appointment appt = new Appointment();
                appt.setId(rs.getInt("id"));
                appt.setDate(rs.getString("date"));
                appt.setTime(rs.getString("time"));
                appt.setService(rs.getString("service"));
                appt.setCustomerName(rs.getString("customerName"));
                list.add(appt);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // Tổng số lịch hẹn
    public static int getTotalAppointments() {
        int count = 0;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Appointments");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error AppointmentDAO.getTotalAppointments");
            e.printStackTrace();
        }
        return count;
    }

    // Tổng doanh thu từ tất cả lịch hẹn
    public static double getTotalRevenue() {
        double total = 0;
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT SUM(price) FROM Appointments");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("Error AppointmentDAO.getTotalRevenue");
            e.printStackTrace();
        }
        return total;
    }

   
    public static int getCompletedAppointments() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM Appointments WHERE status = 'Completed'";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error AppointmentDAO.getCompletedAppointments");
            e.printStackTrace();
        }
        return count;
    }

    
    public static double getRevenueThisMonth() {
        double total = 0;
        String sql = "SELECT SUM(price) FROM Appointments " +
                     "WHERE MONTH(date) = MONTH(CURRENT_DATE()) " +
                     "AND YEAR(date) = YEAR(CURRENT_DATE())";
        try (Connection conn = DBContext.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("Error AppointmentDAO.getRevenueThisMonth");
            e.printStackTrace();
        }
        return total;
    }
}
