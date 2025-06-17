package controller;

import data.CustomerDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author ADMIN
 */
public class DeleteCustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Lấy id khách hàng từ tham số request
        int id = Integer.parseInt(request.getParameter("id"));
        
        CustomerDAO customerDAO = new CustomerDAO();
        boolean deleted = customerDAO.deleteCustomer(id);
        
        // Redirect về trang danh sách khách hàng
        response.sendRedirect("getCustomerListServlet");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu không dùng POST thì có thể để trống hoặc gọi doGet()
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý xóa khách hàng";
    }
}
