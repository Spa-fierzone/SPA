package controller;

import data.CustomerDAO;
import data.AppointmentDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/kpiServlet")
public class KpiServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int totalCustomers = CustomerDAO.getTotalCustomers();
        int totalAppointments = AppointmentDAO.getTotalAppointments();
        double totalRevenue = AppointmentDAO.getTotalRevenue();

        // ➕ Số liệu mở rộng
        int newCustomersThisMonth = CustomerDAO.getNewCustomersThisMonth();
        int completedAppointments = AppointmentDAO.getCompletedAppointments();
        double revenueThisMonth = AppointmentDAO.getRevenueThisMonth();

        // Đặt dữ liệu lên request
        request.setAttribute("totalCustomers", totalCustomers);
        request.setAttribute("totalAppointments", totalAppointments);
        request.setAttribute("totalRevenue", totalRevenue);

        request.setAttribute("newCustomersThisMonth", newCustomersThisMonth);
        request.setAttribute("completedAppointments", completedAppointments);
        request.setAttribute("revenueThisMonth", revenueThisMonth);

        // Forward đến kpi.jsp
        request.getRequestDispatcher("kpi.jsp").forward(request, response);
    }
}
