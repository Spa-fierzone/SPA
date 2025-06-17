package controller;

import data.UserDB;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

public class RegisterServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (var out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet RegisterServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet RegisterServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserDB userDB = new UserDB();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Kiểm tra user đã tồn tại chưa
        for (User u : userDB.getAll()) {
            if (u.getUsername().equals(username)) {
                request.setAttribute("userExisted", username);
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return; // quan trọng, để không tiếp tục xử lý sau khi đã forward
            }
        }
        
        // Thêm user mới
        userDB.add(new User(username, password));
        request.setAttribute("registrationSuccess", "registrationSuccess");
        request.getRequestDispatcher("register.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet xử lý đăng ký user";
    }
}
