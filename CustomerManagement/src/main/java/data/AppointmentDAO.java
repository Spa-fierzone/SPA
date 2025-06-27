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
}
