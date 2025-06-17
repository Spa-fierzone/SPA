<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Edit Customer</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css"></link>
</head>
<body class="bg-gray-100 text-center font-sans">
    <h2 class="text-3xl font-bold text-green-600 my-8">Edit Customer</h2>

    <c:if test="${not empty errorMessage}">
        <div class="text-red-600 font-bold mb-4">${errorMessage}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/editCustomerServlet" method="post" class="w-full max-w-lg mx-auto bg-white p-8 rounded-lg shadow-lg">
        <input type="hidden" name="id" value="${customer.id}">

        <label class="block text-left text-gray-700 font-bold mb-2">Name:</label>
        <input type="text" name="name" value="${customer.name}" required class="w-full p-2 mb-4 border border-gray-300 rounded">

        <label class="block text-left text-gray-700 font-bold mb-2">Gender:</label>
        <select name="gender" class="w-full p-2 mb-4 border border-gray-300 rounded">
            <option value="Male" ${customer.gender == 'Male' ? 'selected' : ''}>Male</option>
            <option value="Female" ${customer.gender == 'Female' ? 'selected' : ''}>Female</option>
        </select>

        <label class="block text-left text-gray-700 font-bold mb-2">DOB:</label>
        <input type="date" name="dob" value="${dobFormatted}" required class="w-full p-2 mb-4 border border-gray-300 rounded">

        <input type="submit" value="Update Customer" class="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 cursor-pointer">
    </form>

    <div class="mt-8">
        <a href="${pageContext.request.contextPath}/customerList.jsp">
            <button class="bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700">Back to Customer List</button>
        </a>
    </div>
</body>
</html>
