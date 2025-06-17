package controller;

import data.CustomerDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Date;
import model.Customer;

public class EditCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id")); // Lấy id từ request
        CustomerDAO dao = new CustomerDAO();
        
        Customer customer = dao.getCustomerById(id);
        request.setAttribute("customer", customer);
        
        // Forward đến trang editCustomer.jsp (phải tạo trang này trong webapp)
        request.getRequestDispatcher("editCustomer.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        String name = request.getParameter("name");
        String gender = request.getParameter("gender");
        Date dob = Date.valueOf(request.getParameter("dob"));

        Customer customer = new Customer(id, name, gender, dob);
        CustomerDAO dao = new CustomerDAO();
        dao.updateCustomer(customer);

        // Redirect về trang danh sách khách hàng sau khi cập nhật
        response.sendRedirect("getCustomerListServlet");
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý sửa thông tin khách hàng";
    }
}
