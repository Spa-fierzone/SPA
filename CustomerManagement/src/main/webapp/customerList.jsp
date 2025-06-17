<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Customer List</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css" />
    <script src="https://cdn.tailwindcss.com"></script>
    <style>
        body {
            margin: 0;
            height: 100vh;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            align-items: center;
            background: url('${pageContext.request.contextPath}/images/spa2.jpg') no-repeat center center fixed;
            background-size: cover;
            font-family: 'Roboto', sans-serif;
        }
        .content {
            position: relative;
            z-index: 10;
            background-color: rgba(255, 255, 255, 0.9);
            padding: 2rem;
            border-radius: 0.5rem;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            width: 90%;
            max-width: 1200px;
            margin: 20px auto;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 10px;
            border: 1px solid #ddd;
            text-align: left;
        }
        thead {
            background: green;
            color: white;
        }
        tbody {
            display: block;
            max-height: 400px;
            overflow-y: auto;
        }
        thead, tbody tr {
            display: table;
            width: 100%;
            table-layout: fixed;
        }
        .avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #ddd;
        }
        .spacer {
            height: 30px;
            margin-top: -20px;
        }
        a.action-link {
            margin: 4px 0;
            display: inline-block;
            font-weight: 500;
            cursor: pointer;
        }
        a.action-link.edit {
            color: #3b82f6;
        }
        a.action-link.edit:hover {
            color: #1e40af;
        }
        a.action-link.delete {
            color: #ef4444;
        }
        a.action-link.delete:hover {
            color: #b91c1c;
        }
    </style>
</head>
<body>
    <div class="content">
        <div class="flex justify-between items-center p-4 bg-white shadow-md rounded-lg">
            <a href="${pageContext.request.contextPath}/loginServlet" class="text-blue-600 hover:text-blue-800">
                <i class="fas fa-arrow-left"></i> Back
            </a>
            <h2 class="text-3xl font-bold text-green-600">Customer List</h2>
            <a href="${pageContext.request.contextPath}/logoutServlet" class="text-red-600 hover:text-red-800">
                Logout <i class="fas fa-sign-out-alt"></i>
            </a>
        </div>

        <div class="mt-4">
            <a href="${pageContext.request.contextPath}/addCustomer.jsp" class="inline-block mb-4">
                <button class="bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700 transition duration-200">
                    Add New Customer
                </button>
            </a>
        </div>

        <!-- Bảng danh sách khách hàng -->
        <div class="overflow-x-auto w-full flex justify-center">
            <table class="min-w-full bg-white shadow-lg rounded-lg">
                <thead>
                    <tr>
                        <th class="py-3 px-4">No.</th>
                        <th class="py-3 px-4">Avatar</th>
                        <th class="py-3 px-4">ID</th>
                        <th class="py-3 px-4">Name</th>
                        <th class="py-3 px-4">Gender</th>
                        <th class="py-3 px-4">DOB</th>
                        <th class="py-3 px-4">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${customerList}" var="customer" varStatus="status">
                        <tr class="border-b hover:bg-gray-100">
                            <td class="py-3 px-4">${status.index + 1}</td>
                            <td class="py-3 px-4">
                                <img src="${pageContext.request.contextPath}/images/${customer.id}.jpg"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/images/avatar.jpg';"
                                     alt="Avatar" class="avatar" />
                            </td>
                            <td class="py-3 px-4">${customer.id}</td>
                            <td class="py-3 px-4">${customer.name}</td>
                            <td class="py-3 px-4">${customer.gender}</td>
                            <td class="py-3 px-4">${customer.dob}</td>
                            <td class="py-3 px-4 flex flex-col items-center">
                                <a href="${pageContext.request.contextPath}/editCustomerServlet?id=${customer.id}" class="action-link edit">Edit</a>
                                <a href="${pageContext.request.contextPath}/deleteCustomerServlet?id=${customer.id}" class="action-link delete">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <div class="spacer"></div>
    </div>
</body>
</html>
