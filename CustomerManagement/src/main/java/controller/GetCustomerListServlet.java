package controller;

import data.CustomerDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import model.Customer;

public class GetCustomerListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CustomerDAO dao = new CustomerDAO();
        ArrayList<Customer> customerList = dao.getAllCustomers();
        // Đặt danh sách khách hàng vào request scope
        request.setAttribute("customerList", customerList);
        // Chuyển đến trang JSP để hiển thị
        request.getRequestDispatcher("customerList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet lấy danh sách khách hàng";
    }
}
