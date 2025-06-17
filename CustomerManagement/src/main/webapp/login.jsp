<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <title>System</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            margin: 0;
            height: 100vh;
            overflow: hidden;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            background-color: #f0f2f5;
        }

        .background-image {
            position: fixed;
            top: 0;
            left: 0;
            width: 100vw;
            height: 100vh;
            object-fit: cover;
            z-index: 1;
            filter: brightness(0.7);
        }

        .logo-top-left {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 120px;
            height: 120px;
            object-fit: cover;
            border-radius: 50%;
            background-color: rgba(255, 255, 255, 0.9);
            padding: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.15);
            z-index: 20;
        }

        .login-container {
            position: relative;
            z-index: 10;
            background-color: rgba(255, 255, 255, 0.95);
            padding: 3rem 3.5rem;
            border-radius: 0.75rem;
            box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            max-width: 500px;
            width: 90%;
            text-align: left;
        }

        .login-container h2 {
            color: #22c55e; 
            font-weight: 700;
            font-size: 2.25rem; /* 36px */
            margin-bottom: 1.5rem;
        }

        form label {
            color: #22c55e;
            font-weight: 600;
        }

        form input[type="text"],
        form input[type="password"] {
            width: 100%;
            padding: 12px 14px;
            margin-top: 6px;
            margin-bottom: 18px;
            border-radius: 6px;
            border: 1px solid #ccd0d5;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }
        form input[type="text"]:focus,
        form input[type="password"]:focus {
            outline: none;
            border-color: #22c55e;
            box-shadow: 0 0 5px #1877f2;
        }

        .checkbox-container {
            display: flex;
            align-items: center;
            margin-bottom: 24px;
            color: #22c55e;
            font-weight: 500;
        }

        .checkbox-container input[type="checkbox"] {
            margin-right: 8px;
            width: 16px;
            height: 16px;
        }

        button[type="submit"] {
            width: 100%;
            background-color: #22c55e;
            color: white;
            padding: 14px;
            border-radius: 8px;
            font-weight: 700;
            font-size: 1.125rem;
            border: none;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        button[type="submit"]:hover {
            background-color: #606770;
        }

        p.register-text {
            margin-top: 1.5rem;
            color: #606770;
            font-size: 1rem;
            text-align: center;
        }
        p.register-text a {
            color: #22c55e;
            font-weight: 600;
            text-decoration: none;
        }
        p.register-text a:hover {
            text-decoration: underline;
        }

        /* Error modal style */
        .error-modal {
            position: fixed;
            inset: 0;
            background: rgba(0,0,0,0.6);
            display: flex;
            justify-content: center;
            align-items: center;
            z-index: 50;
        }
        .error-modal-content {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            max-width: 400px;
            width: 90%;
            box-shadow: 0 8px 20px rgba(0,0,0,0.25);
            text-align: center;
        }
        .error-modal-content h3 {
            color: #e03131;
            margin-bottom: 1rem;
            font-size: 1.5rem;
            font-weight: 700;
        }
        .error-modal-content p {
            margin-bottom: 1.5rem;
            color: #333;
        }
        .error-modal-content a {
            color: #1877f2;
            font-weight: 600;
            text-decoration: none;
        }
        .error-modal-content a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <img src="images/spa.jpg" alt="Full Background Image" class="background-image">

    <!-- Logo fixed top-left -->
    <img src="images/spa1.jpg" alt="Spa Logo" class="logo-top-left">

    <div class="login-container">
        <h2>Login</h2>

        <c:if test="${not empty error}">
            <div class="error-modal">
                <div class="error-modal-content">
                    <h3>Login failed!</h3>
                    <p>${error}</p>
                    <a href="login.jsp">Try again</a>
                </div>
            </div>
        </c:if>

        <form action="loginServlet" method="post">
            <label for="username">Username:</label>
            <input type="text" id="username" name="username" value="${username}" required>

            <label for="password">Password:</label>
            <input type="password" id="password" name="password" value="${password}" required>

            <div class="checkbox-container">
                <input type="checkbox" id="remember" name="remember">
                <label for="remember">Remember me</label>
            </div>

            <button type="submit">Login</button>
        </form>

        <p class="register-text">
            Don't have an account? 
            <a href="register.jsp">Register here</a>
        </p>
    </div>
</body>
</html>
