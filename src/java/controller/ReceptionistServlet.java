/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author PhucHe
 */
@WebServlet("/receptionist")
public class ReceptionistServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Format current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String currentDate = dateFormat.format(new Date());
        
        // Set attribute for JSP
        request.setAttribute("currentDate", currentDate);
        response.setContentType("text/html;charset=UTF-8");



        // Forward to JSP
        request.getRequestDispatcher("/WEB-INF/views/receptionist.jsp").forward(request, response);
    }
}