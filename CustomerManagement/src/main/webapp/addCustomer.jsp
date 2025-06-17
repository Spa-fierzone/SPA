<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Add Customer</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css"></link>
</head>
<body class="bg-gray-100 text-center font-sans">
    <h2 class="text-3xl font-bold text-green-600 my-8">Add New Customer</h2>
    <form action="AddCustomerServlet" method="POST" class="w-full max-w-md mx-auto bg-white p-8 rounded-lg shadow-lg">
        <label class="block text-left text-gray-700 font-bold mb-2">Name:</label>
        <input type="text" name="name" required class="w-full p-2 mb-4 border border-gray-300 rounded">

        <label class="block text-left text-gray-700 font-bold mb-2">Gender:</label>
        <select name="gender" class="w-full p-2 mb-4 border border-gray-300 rounded">
            <option value="Male">Male</option>
            <option value="Female">Female</option>
        </select>

        <label class="block text-left text-gray-700 font-bold mb-2">DOB:</label>
        <input type="date" name="dob" required class="w-full p-2 mb-4 border border-gray-300 rounded">

        <input type="submit" value="Add Customer" class="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 cursor-pointer">
    </form>
    <div class="mt-8">
        <p class="text-red-500 font-bold">${errorString}</p>
        <a href="customerList.jsp">
            <button class="bg-green-600 text-white py-2 px-4 rounded hover:bg-green-700">Back to Customer List</button>
        </a>
    </div>
</body>
</html>
