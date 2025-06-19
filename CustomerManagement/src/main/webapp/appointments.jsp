<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <title>All Appointments</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen p-8">
    <div class="bg-white shadow-md rounded-lg p-6 max-w-6xl mx-auto">
        <div class="flex justify-between items-center mb-6">
            <a href="${pageContext.request.contextPath}/customerList.jsp" class="text-blue-600 hover:underline">
                <i class="fas fa-arrow-left"></i> Back to Customers
            </a>
            <h2 class="text-3xl font-bold text-purple-600">All Appointments</h2>
        </div>

        <div class="mb-4 text-right">
            <a href="${pageContext.request.contextPath}/addAppointment.jsp">
                <button class="bg-purple-600 text-white px-4 py-2 rounded hover:bg-purple-700">
                    Add Appointment
                </button>
            </a>
        </div>

        <table class="w-full border border-gray-300">
            <thead class="bg-purple-600 text-white">
                <tr>
                    <th class="px-4 py-2">No.</th>
                    <th class="px-4 py-2">Customer Name</th>
                    <th class="px-4 py-2">Date</th>
                    <th class="px-4 py-2">Time</th>
                    <th class="px-4 py-2">Service</th>
                    <th class="px-4 py-2">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${appointmentList}" var="appt" varStatus="status">
                    <tr class="border-b hover:bg-gray-50">
                        <td class="px-4 py-2">${status.index + 1}</td>
                        <td class="px-4 py-2">${appt.customerName}</td>
                        <td class="px-4 py-2">${appt.date}</td>
                        <td class="px-4 py-2">${appt.time}</td>
                        <td class="px-4 py-2">${appt.service}</td>
                        <td class="px-4 py-2 space-x-2">
                            <a href="${pageContext.request.contextPath}/editAppointment.jsp?id=${appt.id}" class="text-blue-600 hover:underline">Edit</a>
                            <a href="${pageContext.request.contextPath}/deleteAppointmentServlet?id=${appt.id}" class="text-red-600 hover:underline"
                               onclick="return confirm('Are you sure you want to delete this appointment?');">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</body>
</html>
