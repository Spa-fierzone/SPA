<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>KPI Dashboard</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 min-h-screen flex items-center justify-center">
    <div class="bg-white p-8 rounded shadow-lg w-full max-w-3xl">
        <h1 class="text-3xl font-bold text-green-600 mb-6 text-center">KPI Dashboard</h1>

        <div class="grid grid-cols-2 gap-6 text-center">
            <div class="bg-green-100 p-4 rounded shadow">
                <h2 class="text-xl font-semibold text-green-800">Total Customers</h2>
                <p class="text-2xl mt-2 font-bold">${totalCustomers}</p>
            </div>
            <div class="bg-blue-100 p-4 rounded shadow">
                <h2 class="text-xl font-semibold text-blue-800">Total Appointments</h2>
                <p class="text-2xl mt-2 font-bold">${totalAppointments}</p>
            </div>
            <div class="bg-yellow-100 p-4 rounded shadow">
                <h2 class="text-xl font-semibold text-yellow-800">Total Revenue</h2>
                <p class="text-2xl mt-2 font-bold">$${totalRevenue}</p>
            </div>
            <div class="bg-purple-100 p-4 rounded shadow">
                <h2 class="text-xl font-semibold text-purple-800">New Customers This Month</h2>
                <p class="text-2xl mt-2 font-bold">${newCustomersThisMonth}</p>
            </div>
            <div class="bg-pink-100 p-4 rounded shadow">
                <h2 class="text-xl font-semibold text-pink-800">Completed Appointments</h2>
                <p class="text-2xl mt-2 font-bold">${completedAppointments}</p>
            </div>
            <div class="bg-orange-100 p-4 rounded shadow">
                <h2 class="text-xl font-semibold text-orange-800">Revenue This Month</h2>
                <p class="text-2xl mt-2 font-bold">$${revenueThisMonth}</p>
            </div>
        </div>
    </div>
</body>
</html>
