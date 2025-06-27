package controller;

import model.Appointment;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/appointmentServlet")  // Đảm bảo bạn đã thêm dòng này
public class AppointmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Tạo danh sách dữ liệu giả lập
        List<Appointment> appointments = new ArrayList<>();

        Appointment a1 = new Appointment();
        a1.setId(1);
        a1.setCustomerName("Nguyen Van A");
        a1.setDate("2025-06-20");
        a1.setTime("09:00");
        a1.setService("Facial");

        Appointment a2 = new Appointment();
        a2.setId(2);
        a2.setCustomerName("Tran Thi B");
        a2.setDate("2025-06-21");
        a2.setTime("11:00");
        a2.setService("Massage");

        Appointment a3 = new Appointment();
        a3.setId(3);
        a3.setCustomerName("Le Van C");
        a3.setDate("2025-06-22");
        a3.setTime("15:30");
        a3.setService("Body Scrub");

        appointments.add(a1);
        appointments.add(a2);
        appointments.add(a3);

        // Gửi dữ liệu đến JSP
        request.setAttribute("appointmentList", appointments);
        request.getRequestDispatcher("appointments.jsp").forward(request, response);
    }
}
