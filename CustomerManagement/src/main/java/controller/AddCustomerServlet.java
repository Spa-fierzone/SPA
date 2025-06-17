package controller;

import data.CustomerDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.LocalDate;
import model.Customer;

public class AddCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CustomerDAO customerDAO = new CustomerDAO();
        
        String name = request.getParameter("name");
        String gender = request.getParameter("gender");
        String dateString = request.getParameter("dob");
        
        try {
            LocalDate localDate = LocalDate.parse(dateString);
            Date sqlDate = Date.valueOf(localDate);
            Customer customer = new Customer(name, gender, sqlDate);
            boolean success = customerDAO.addCustomer(customer);
            
            if (success) {
                response.sendRedirect("getCustomerListServlet"); // đổi tên servlet nếu cần
            } else {
                request.setAttribute("error", "Failed to add customer.");
                request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Invalid date or input.");
            request.getRequestDispatcher("addCustomer.jsp").forward(request, response);
        }
    }
    @Override
    public String getServletInfo() {
        return "Servlet to add a new customer";
    }
}